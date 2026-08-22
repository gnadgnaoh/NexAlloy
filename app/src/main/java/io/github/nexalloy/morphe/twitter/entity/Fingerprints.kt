package io.github.nexalloy.morphe.twitter.entity

import io.github.nexalloy.morphe.AccessFlags
import io.github.nexalloy.morphe.Fingerprint
import io.github.nexalloy.morphe.InstructionLocation.MatchAfterImmediately
import io.github.nexalloy.morphe.Opcode
import io.github.nexalloy.morphe.findFieldDirect
import io.github.nexalloy.morphe.findMethodDirect
import io.github.nexalloy.morphe.methodCall
import io.github.nexalloy.morphe.opcode
import io.github.nexalloy.morphe.string

internal object TweetObjectFingerprint : Fingerprint(
    filters = listOf(
        string("https://x.com/%1\$s/status/%2\$d"),
    ),
)

internal object TweetNamesFingerprint : Fingerprint(
    classFingerprint = TweetObjectFingerprint,
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    parameters = listOf(),
    returnType = "Ljava/lang/String;",
    filters = listOf(
        methodCall(
            opcode = Opcode.INVOKE_VIRTUAL,
            definingClass = "this",
            returnType = "Ljava/lang/String;"
        ),
        opcode(opcode = Opcode.MOVE_RESULT_OBJECT, location = MatchAfterImmediately()),
        methodCall(
            opcode = Opcode.INVOKE_VIRTUAL,
            definingClass = "this",
            returnType = "Ljava/lang/String;",
            location = MatchAfterImmediately()
        ),
        opcode(opcode = Opcode.MOVE_RESULT_OBJECT, location = MatchAfterImmediately()),
        methodCall(
            opcode = Opcode.INVOKE_STATIC,
            parameters = listOf("Ljava/lang/String;", "Ljava/lang/String;"),
            returnType = "Ljava/lang/String;"
        ),
        opcode(opcode = Opcode.MOVE_RESULT_OBJECT, location = MatchAfterImmediately()),
        opcode(opcode = Opcode.RETURN_OBJECT, location = MatchAfterImmediately())
    )
)

internal object TweetMediaEntityClassFingerprint : Fingerprint(
    strings = listOf("EntityList{mEntities="),
)

internal object LongTweetObjectFingerprint : Fingerprint(
    strings = listOf("NoteTweet(id=", ", text="),
)

internal object QuotedViewSetAccessibilityFingerprint : Fingerprint(
    definingClass = "Lcom/twitter/tweetview/core/QuoteView;",
    name = "setAccessibility",
)

internal object MediaOptionSheetMediaListVideoDownloaderImplDownloadMethodFingerprint : Fingerprint(
    returnType = "Z",
    strings = listOf("url", "video_download"),
) {
    init {
        classMatcher {
            className(
                "com.twitter.tweetview.core.ui.mediaoptionssheet",
                org.luckypray.dexkit.query.enums.StringMatchType.StartsWith
            )
        }
    }
}

internal object ExtMediaGetImageMethodFinder : Fingerprint(
    definingClass = "Lcom/twitter/model/json/unifiedcard/JsonAppStoreData;",
    strings = listOf("type", "id"),
)

internal object TweetInfoObjectFingerprint : Fingerprint(
    filters = listOf(
        string("flags"),
        string("lang"),
        string("supplemental_language"),
    ),
    custom = { paramCount = 2 }
) {
    init {
        classMatcher { className("tdbh", org.luckypray.dexkit.query.enums.StringMatchType.Contains) }
    }
}


internal val tweetUsernameMethodResolved = findMethodDirect {
    TweetNamesFingerprint.instructionMatches[2].instruction.methodRef!!
}

internal val tweetProfileNameMethodResolved = findMethodDirect {
    TweetNamesFingerprint.instructionMatches[0].instruction.methodRef!!
}

internal val tweetUserIdMethodResolved = findMethodDirect {
    TweetObjectFingerprint().declaredClass!!.methods.last { it.returnTypeName == "long" }
}

internal val tweetMediaMethodResolved = findMethodDirect {
    val targetOps = listOf(
        Opcode.IGET_OBJECT.opCode,
        Opcode.IGET_OBJECT.opCode,
        Opcode.IGET_OBJECT.opCode,
        Opcode.IGET_OBJECT.opCode,
        Opcode.RETURN_OBJECT.opCode
    )
    TweetObjectFingerprint().declaredClass!!.methods.first { m ->
        m.instructions?.map { it.opcode } == targetOps
    }
}

internal val extMediaListFieldResolved = findFieldDirect {
    TweetMediaEntityClassFingerprint().declaredClass!!.fields.first { it.typeName.contains("List") }
}

internal val tweetNoteTweetMethodResolved = findMethodDirect {
    TweetObjectFingerprint().declaredClass!!.methods.firstOrNull {
        it.returnTypeName.contains("notetweet", ignoreCase = true)
    } ?: throw Exception("getNoteTweetMethod not found")
}

internal val longTextFieldResolved = findFieldDirect {
    LongTweetObjectFingerprint().declaredClass!!.fields.first { it.typeName == "java.lang.String" }
}

internal val tweetShortTextMethodResolved = findMethodDirect {
    val method = QuotedViewSetAccessibilityFingerprint()
    val instructions = method.instructions ?: emptyList()
    val newInstanceIndex = instructions.indexOfFirst { it.opcode == Opcode.NEW_INSTANCE.opCode }
        .let { if (it < 0) instructions.size else it }
    instructions.take(newInstanceIndex)
        .lastOrNull { it.opcode == Opcode.INVOKE_VIRTUAL_RANGE.opCode }
        ?.methodRef ?: throw Exception("getShortText anchor method not found")
}

internal val tweetLangFieldResolved = findFieldDirect {
    val match = TweetInfoObjectFingerprint.instructionMatches
        .firstOrNull { it.instruction.string == "lang" }
        ?: throw Exception("'lang' string literal not found in TweetInfo object")
    val instructions = TweetInfoObjectFingerprint().instructions ?: emptyList()
    instructions[match.index + 1].fieldRef ?: throw Exception("lang field reference not found")
}

internal val tweetInfoFieldResolved = findFieldDirect {
    val langField = tweetLangFieldResolved()
    val tweetInfoClassDescriptor = langField.declaredClass!!.descriptor
    TweetObjectFingerprint().declaredClass!!.fields.first { it.typeSign == tweetInfoClassDescriptor }
}

internal val extMediaVideoInfoFieldResolved = findFieldDirect {
    val instructions =
        MediaOptionSheetMediaListVideoDownloaderImplDownloadMethodFingerprint().instructions
            ?: emptyList()
    instructions.first { it.opcode == Opcode.IGET_OBJECT.opCode }.fieldRef
        ?: throw Exception("extMediaVideoInfoField not found")
}

internal val extMediaVideoVariantsFieldResolved = findFieldDirect {
    val instructions =
        MediaOptionSheetMediaListVideoDownloaderImplDownloadMethodFingerprint().instructions
            ?: emptyList()
    val firstIndex = instructions.indexOfFirst { it.opcode == Opcode.IGET_OBJECT.opCode }
    instructions.drop(firstIndex + 1).firstOrNull { it.opcode == Opcode.IGET_OBJECT.opCode }?.fieldRef
        ?: throw Exception("extMediaVideoVariantsField not found")
}

internal val extMediaImageFieldResolved = findFieldDirect {
    val instructions = ExtMediaGetImageMethodFinder().instructions ?: emptyList()
    instructions.lastOrNull { it.opcode == Opcode.IGET_OBJECT.opCode }?.fieldRef
        ?: throw Exception("extMediaImageField not found")
}

