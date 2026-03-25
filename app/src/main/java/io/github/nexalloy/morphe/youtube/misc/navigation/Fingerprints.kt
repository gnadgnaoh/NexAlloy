package io.github.nexalloy.morphe.youtube.misc.navigation

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.ResourceType
import io.github.nexalloy.morphe.accessFlags
import io.github.nexalloy.morphe.findClassDirect
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.findMethodListDirect
import io.github.nexalloy.morphe.fingerprint
import io.github.nexalloy.morphe.parameters
import io.github.nexalloy.morphe.resourceLiteral
import io.github.nexalloy.morphe.resourceMappings
import io.github.nexalloy.morphe.returns

// val actionBarSearchResultsFingerprint = fingerprint {
//    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
//    returns("Landroid/view/View;")
//    literal { actionBarSearchResultsViewMicId }
//}

val toolbarContainerId get() = resourceMappings["id", "toolbar_container"]

object ToolbarLayoutFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.CONSTRUCTOR),
    filters = listOf(
        resourceLiteral(ResourceType.ID, "toolbar_container")
    )
)

/**
 * Matches to https://android.googlesource.com/platform/frameworks/support/+/9eee6ba/v7/appcompat/src/android/support/v7/widget/Toolbar.java#963
 */
object AppCompatToolbarBackButtonFingerprint : Fingerprint(
    definingClass = "Landroid/support/v7/widget/Toolbar;",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Landroid/graphics/drawable/Drawable;",
    parameters = listOf()
)

/**
 * Matches to the class found in [pivotBarConstructorFingerprint].
 */
val initializeButtonsFingerprint = fingerprint {
    classFingerprint(pivotBarConstructorFingerprint)
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    strings("FEvideo_picker")
}

val getNavigationEnumMethod = findMethodDirect {
    initializeButtonsFingerprint().invokes.findMethod {
        matcher {
            declaredClass(navigationEnumClass(this@findMethodDirect).name)
            accessFlags(AccessFlags.STATIC)
        }
    }.single()
}

/**
 * Matches to the Enum class that looks up ordinal -> instance.
 */
val navigationEnumFingerprint = fingerprint {
    accessFlags(AccessFlags.STATIC, AccessFlags.CONSTRUCTOR)
    strings(
        "PIVOT_HOME",
        "TAB_SHORTS",
        "CREATION_TAB_LARGE",
        "PIVOT_SUBSCRIPTIONS",
        "TAB_ACTIVITY",
        "VIDEO_LIBRARY_WHITE",
        "INCOGNITO_CIRCLE",
    )
}

val navigationEnumClass = findClassDirect { navigationEnumFingerprint().declaredClass!! }

val pivotBarButtonsCreateDrawableViewFingerprint = findMethodDirect {
    findMethod {
        matcher {
            accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
            returns("Landroid/view/View;")
            declaredClass {
                descriptor =
                    "Lcom/google/android/libraries/youtube/rendering/ui/pivotbar/PivotBar;"
            }
        }
    }.single {
        it.paramTypes.firstOrNull()?.descriptor == "Landroid/graphics/drawable/Drawable;"
    }
}

object PivotBarButtonsCreateResourceViewFingerprint : Fingerprint(
    definingClass = "Lcom/google/android/libraries/youtube/rendering/ui/pivotbar/PivotBar;",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "Landroid/view/View;",
    parameters = listOf("L", "Z", "I", "L")
)

// fun indexOfSetViewSelectedInstruction(method: Method) = method.indexOfFirstInstruction {
//    opcode == Opcode.INVOKE_VIRTUAL && getReference<MethodReference>()?.name == "setSelected"
//}

val pivotBarButtonsViewSetSelectedFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("I", "Z")
    classMatcher {
        descriptor = "Lcom/google/android/libraries/youtube/rendering/ui/pivotbar/PivotBar;"
    }
    methodMatcher { addInvoke { name = "setSelected" } }
}

val pivotBarButtonsViewSetSelectedSubFingerprint = findMethodDirect {
    pivotBarButtonsViewSetSelectedFingerprint().invokes.single { it.name == "setSelected" }
}

val pivotBarConstructorFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
    strings("com.google.android.apps.youtube.app.endpoint.flags")
}

val getNavIconResIdFingerprint = findMethodListDirect {
    // two matches in versions 20.24.xx-20.26.xx,
    // one match in versions <=v20.20.xx and >=v20.28.xx
    val navigationEnumClass = navigationEnumClass()
    findMethod {
        matcher {
            paramTypes(navigationEnumClass.name, "boolean")
            returnType = "int"
        }
    }
}
