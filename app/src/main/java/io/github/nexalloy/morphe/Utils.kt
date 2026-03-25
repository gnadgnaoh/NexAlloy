package io.github.nexalloy.morphe

import org.luckypray.dexkit.result.InstructionData
import org.luckypray.dexkit.result.MethodData


/**
 * Get the index of matching instruction,
 * starting from the end of the method and searching down.
 *
 * @return -1 if the instruction is not found.
 */
fun MethodData.indexOfFirstInstructionReversedOrThrow(targetOpcode: Opcode): Int = indexOfFirstInstructionReversedOrThrow {
    opcode == targetOpcode.ordinal
}

/**
 * Get the index of matching instruction,
 * starting from [startIndex] and searching down.
 *
 * @param startIndex Optional starting index to search down from. Searching includes the start index.
 * @return The index of the instruction.
 * @see indexOfFirstInstructionReversed
 */
fun MethodData.indexOfFirstInstructionReversedOrThrow(startIndex: Int? = null, filter: InstructionData.() -> Boolean): Int {
    val index = indexOfFirstInstructionReversed(startIndex, filter)

    if (index < 0) {
        throw Exception("Could not find instruction index")
    }

    return index
}

/**
 * Get the index of matching instruction,
 * starting from and [startIndex] and searching down.
 *
 * @param startIndex Optional starting index to search down from. Searching includes the start index.
 * @return -1 if the instruction is not found.
 * @see indexOfFirstInstructionReversedOrThrow
 */
fun MethodData.indexOfFirstInstructionReversed(startIndex: Int? = null, targetOpcode: Opcode): Int =
    indexOfFirstInstructionReversed(startIndex) {
        opcode == targetOpcode.ordinal
    }


/**
 * Get the index of matching instruction,
 * starting from and [startIndex] and searching down.
 *
 * @param startIndex Optional starting index to search down from. Searching includes the start index.
 * @return -1 if the instruction is not found.
 * @see indexOfFirstInstructionReversedOrThrow
 */
fun MethodData.indexOfFirstInstructionReversed(startIndex: Int? = null, filter: InstructionData.() -> Boolean): Int {
    var instructions = this.instructions
    if (startIndex != null) {
        instructions = instructions.take(startIndex + 1)
    }

    return instructions.indexOfLast(filter)
}
