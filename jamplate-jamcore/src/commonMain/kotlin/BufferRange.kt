/*
 *	Copyright 2020-2022 cufy.org
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *	    http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package org.jamplate.jamcore

/**
 * A buffer range is a range of elements in a buffer.
 *
 * The behaviour of the basic object operations
 * (i.e. [equals], [hashCode], and [toString]) is
 * not specified by this interface and can differ
 * depending on the implementation.
 *
 * @author LSafer
 * @since 0.4.0
 */
interface BufferRange {
    /**
     * The first index of range.
     */
    val offset: ULong

    /**
     * The length of the range.
     */
    val length: ULong
}

/**
 * Construct a new buffer range.
 *
 * The returned instances from this function
 * implements [toString], [equals] and [hashCode]
 * and are considered data classes. However, they
 * recognize other instances created by this
 * function only.
 *
 * @param offset the range's offset.
 * @param length the range's length.
 * @return a new buffer range.
 * @since 0.4.0 ~2022.11.14
 */
fun BufferRange(offset: ULong, length: ULong): BufferRange {
    return BufferRangeImpl(offset, length)
}

/**
 * Construct a new buffer range from another
 * buffer range.
 *
 * This function uses the [BufferRange] constructor.
 */
fun BufferRange(range: BufferRange): BufferRange {
    return BufferRange(range.offset, range.length)
}

/**
 * Return a range with the values of this plus the
 * given values.
 *
 * This function uses the [BufferRange] constructor.
 */
fun BufferRange.plus(
    offset: ULong = 0uL,
    length: ULong = 0uL
): BufferRange {
    return BufferRange(
        offset = this.offset + offset,
        length = this.length + length
    )
}

/**
 * Return a range with the values of this minus
 * the given values.
 *
 * This function uses the [BufferRange] constructor.
 */
fun BufferRange.minus(
    offset: ULong = 0uL,
    length: ULong = 0uL
): BufferRange {
    return BufferRange(
        offset = this.offset - offset,
        length = this.length - length
    )
}

/**
 * Return a range with the given values.
 *
 * This function uses the [BufferRange] constructor.
 */
fun BufferRange.copy(
    offset: ULong = this.offset,
    length: ULong = this.length
): BufferRange {
    return BufferRange(
        offset = offset,
        length = length
    )
}

/**
 * One past the last index of this range.
 */
val BufferRange.terminal: ULong
    get() = this.offset + this.length

/**
 * Return a native range.
 */
val BufferRange.range: ULongRange
    get() = offset until terminal

/**
 * Return a signed native range.
 */
val BufferRange.signedRange: LongRange
    get() = offset.toLong() until terminal.toLong()

/**
 * Return an int native range.
 */
val BufferRange.intRange: UIntRange
    get() = offset.toUInt() until terminal.toUInt()

/**
 * Return a signed int native range.
 */
val BufferRange.signedIntRange: IntRange
    get() = offset.toInt() until terminal.toInt()

internal data class BufferRangeImpl(
    override val offset: ULong,
    override val length: ULong
) : BufferRange {
    override fun toString(): String =
        "BufferRange(offset=$offset, length=$length)"
}
