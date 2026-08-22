package io.github.nexalloy.revanced.instagram.dm

import app.morphe.extension.shared.Logger
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.findMethodDirect
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.result.ClassData
import org.luckypray.dexkit.result.FieldData
import org.luckypray.dexkit.result.MethodData

private const val DIRECT_THREAD_KEY = "com.instagram.model.direct.DirectThreadKey"
private const val STRING = "java.lang.String"

private val INSTANCE_FIELD_WRITES: Set<Int> = setOf(
    Opcode.IPUT,
    Opcode.IPUT_WIDE,
    Opcode.IPUT_OBJECT,
    Opcode.IPUT_BOOLEAN,
    Opcode.IPUT_BYTE,
    Opcode.IPUT_CHAR,
    Opcode.IPUT_SHORT,
).mapTo(mutableSetOf()) { it.opCode }

private fun DexKitBridge.messageParserClass(): ClassData {
    val candidates = findMethod {
        matcher {
            usingStrings("item_id", "user_id", "text", "timestamp", "hide_in_thread", "thread_key")
        }
    }
    if (candidates.isEmpty()) throw Exception("Direct message deserializer not found")

    val preferred = candidates.firstOrNull { it.declaredClass?.methods?.any(::isJsonEntryPoint) == true }
    return (preferred ?: candidates.first()).declaredClass
        ?: throw Exception("Direct message deserializer has no declaring class")
}

private fun isJsonEntryPoint(method: MethodData) =
    method.name == "parseFromJson" || method.name == "unsafeParseFromJson"

private fun DexKitBridge.messageField(key: String): FieldData =
    messageParserClass().methods.firstNotNullOfOrNull { method ->
        val instructions = runCatching { method.instructions }.getOrNull()
            ?: return@firstNotNullOfOrNull null
        val keyIndex = instructions.indexOfFirst { it.string == key }
        if (keyIndex < 0) return@firstNotNullOfOrNull null

        instructions.drop(keyIndex + 1)
            .firstOrNull { it.opcode in INSTANCE_FIELD_WRITES }
            ?.fieldRef
    } ?: throw Exception("No field stored for direct message JSON key '$key'")

internal val messageItemIdField = findFieldDirect { messageField("item_id") }

internal val messageClientContextField = findFieldDirect { messageField("client_context") }

internal val messageUserIdField = findFieldDirect { messageField("user_id") }

internal val messageSentByViewerField = findFieldDirect { messageField("is_sent_by_viewer") }

internal val messageHideInThreadField = findFieldDirect { messageField("hide_in_thread") }

internal val messageParseMethod = findMethodDirect {
    messageParserClass().methods.first(::isJsonEntryPoint)
}

private fun DexKitBridge.messageRowDelete(): MethodData {
    findMethod {
        matcher {
            usingStrings("server_item_id=='", "client_item_id=='")
            paramTypes(DIRECT_THREAD_KEY, STRING, STRING)
            returnType = "void"
        }
    }.firstOrNull()?.let { return it }

    return findMethod {
        matcher {
            usingStrings("Both message ID and client context is null.")
            paramTypes(DIRECT_THREAD_KEY, STRING, STRING)
            returnType = "void"
        }
    }.first()
}

internal val messageRowDeleteMethod = findMethodDirect { messageRowDelete() }

private fun DexKitBridge.threadRemoveMessage(): MethodData {
    val rowDelete = messageRowDelete()
    val diskJobs = findMethod { matcher { addInvoke(rowDelete.descriptor) } }
    Logger.printInfo { "SaveDeletedMessages: disk delete jobs -> ${diskJobs.size}" }

    val removals = LinkedHashMap<String, MethodData>()
    diskJobs.forEach { job ->
        val jobClass = job.declaredClass ?: return@forEach
        jobClass.methods.filter { it.isConstructor }.forEach { constructor ->
            runCatching {
                findMethod { matcher { addInvoke(constructor.descriptor) } }
            }.getOrNull()?.forEach { creator ->
                val params = creator.paramTypeNames
                val looksLikeRemoval = creator.returnTypeName == "void" &&
                    params.any { it == DIRECT_THREAD_KEY } &&
                    params.count { it == STRING } >= 2
                if (looksLikeRemoval) removals.putIfAbsent(creator.descriptor, creator)
            }
        }
    }

    Logger.printInfo {
        "SaveDeletedMessages: removal methods -> " +
            removals.values.joinToString { "${it.className}.${it.name}" }
    }
    return removals.values.firstOrNull() ?: throw Exception("Thread removal method not found")
}

internal val threadRemoveMessageMethod = findMethodDirect { threadRemoveMessage() }

internal val listRemoveMessageMethod = findMethodDirect {
    val removal = threadRemoveMessage()
    findMethod {
        matcher {
            addCaller(removal.descriptor)
            paramTypes(STRING, "java.util.List")
            returnType = "boolean"
        }
    }.first()
}

private fun DexKitBridge.messageClassName(): String {
    val base = messageField("item_id").declaredClassName
    return findClass { matcher { superClass(base) } }.firstOrNull()?.name ?: base
}

internal val threadStateLookupMethod = findMethodDirect {
    val removal = threadRemoveMessage()
    findMethod {
        matcher {
            addCaller(removal.descriptor)
            declaredClass(removal.className)
            paramTypes(DIRECT_THREAD_KEY)
        }
    }.first()
}

internal val threadFindMessageMethod = findMethodDirect {
    val removal = threadRemoveMessage()
    findMethod {
        matcher {
            addCaller(removal.descriptor)
            paramTypes(STRING)
            returnType = messageClassName()
        }
    }.first()
}
