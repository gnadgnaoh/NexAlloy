package io.github.nexalloy.morphe.youtube.misc.litho.filter

import app.morphe.extension.youtube.patches.components.Filter
import app.morphe.extension.youtube.patches.components.LithoFilterPatch
import app.morphe.extension.youtube.shared.ConversionContext
import de.robv.android.xposed.XC_MethodReplacement.returnConstant
import io.github.nexalloy.patch
import io.github.nexalloy.new
import io.github.nexalloy.morphe.youtube.misc.playservice.is_19_25_or_greater
import io.github.nexalloy.morphe.youtube.misc.playservice.is_20_05_or_greater
import java.nio.ByteBuffer

lateinit var addLithoFilter: (Filter) -> Unit
    private set

val LithoFilter = patch(
    description = "Hooks the method which parses the bytes into a ComponentContext to filter components.",
) {
    addLithoFilter = { filter ->
        LithoFilterPatch.addFilter(filter)
    }

    //region Pass the buffer into extension.
    ProtobufBufferReferenceFingerprint.hookMethod {
        before { param ->
            LithoFilterPatch.setProtoBuffer(param.args[1] as ByteBuffer)
        }
    }

    //endregion

    // region Hook the method that parses bytes into a ComponentContext.

    // Return an EmptyComponent instead of the original component if the filterState method returns true.
    ComponentCreateFingerprint.hookMethod {
        val identifierField = ::identifierFieldData.field
        val pathBuilderField = ::pathBuilderFieldData.field
        val emptyComponentClazz = ::emptyComponentClass.clazz
        after { param ->
            val conversion = param.args[1]
            val contextWrapper = object : ConversionContext.ContextInterface {
                override fun patch_getPathBuilder() = pathBuilderField.get(conversion) as StringBuilder
                override fun patch_getIdentifier() = identifierField.get(conversion) as? String ?: ""
                override fun toString() = conversion.toString()
            }

            if (LithoFilterPatch.isFiltered(contextWrapper, null, null, null)) {
                param.result = emptyComponentClazz.new()
            }
        }
    }

    //endregion

    // region Change Litho thread executor to 1 thread to fix layout issue in unpatched YouTube.

    ::lithoThreadExecutorFingerprint.hookMethod {
        before {
            it.args[0] = LithoFilterPatch.getExecutorCorePoolSize(it.args[0] as Int)
            it.args[1] = LithoFilterPatch.getExecutorMaxThreads(it.args[1] as Int)
        }
    }

    // endregion

    // region A/B test of new Litho native code.

    // Turn off native code that handles litho component names.  If this feature is on then nearly
    // all litho components have a null name and identifier/path filtering is completely broken.
    //
    // Flag was removed in 20.05. It appears a new flag might be used instead (45660109L),
    // but if the flag is forced on then litho filtering still works correctly.
    if (is_19_25_or_greater && !is_20_05_or_greater) {
        LithoComponentNameUpbFeatureFlagFingerprint.hookMethod(returnConstant(false))
    }

    // Turn off a feature flag that enables native code of protobuf parsing (Upb protobuf).
    // If this is enabled, then the litho protobuffer hook will always show an empty buffer
    // since it's no longer handled by the hooked Java code.
    ::featureFlagCheck.hookMethod {
        before {
            if (it.args[0] == 45419603L)
                it.result = false
        }
    }

    // endregion
}