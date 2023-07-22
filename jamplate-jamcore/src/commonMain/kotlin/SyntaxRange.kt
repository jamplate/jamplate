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
 * A syntax range is the indices of some text and
 * its elevation.
 *
 * The behaviour of the basic object operations
 * (i.e. [equals], [hashCode], and [toString]) is
 * not specified by this interface and can differ
 * depending on the implementation.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.11.14
 */
interface SyntaxRange : BufferRange {
    /**
     * The weight of the range.
     */
    val weight: Int
}

/**
 * Construct a new syntax range.
 *
 * The returned instances from this function
 * implements [toString], [equals] and [hashCode]
 * and are considered data classes. However, they
 * recognize other instances created by this
 * function only.
 *
 * @param offset the range's offset.
 * @param length the range's length.
 * @param weight the range's weight.
 * @return a new syntax range.
 * @since 0.4.0 ~2022.11.14
 */
fun SyntaxRange(offset: ULong, length: ULong, weight: Int = 0): SyntaxRange {
    return SyntaxRangeImpl(offset, length, weight)
}

/**
 * Construct a new syntax range from another
 * syntax range.
 *
 * This function uses the [SyntaxRange] constructor.
 */
fun SyntaxRange(range: SyntaxRange): SyntaxRange {
    return SyntaxRange(range.offset, range.length, range.weight)
}

/**
 * Construct a new syntax range from a buffer
 * range.
 *
 * This function uses the [SyntaxRange] constructor.
 */
fun SyntaxRange(range: BufferRange, weight: Int = 0): SyntaxRange {
    return SyntaxRange(range.offset, range.length, weight)
}

/**
 * Return a range with the values of this plus the
 * given values.
 *
 * This function uses the [SyntaxRange] constructor.
 */
fun SyntaxRange.plus(
    offset: ULong = 0uL,
    length: ULong = 0uL,
    weight: Int = 0,
): SyntaxRange {
    return SyntaxRange(
        offset = this.offset + offset,
        length = this.length + length,
        weight = this.weight + weight
    )
}

/**
 * Return a range with the values of this minus the
 * given values.
 *
 * This function uses the [SyntaxRange] constructor.
 */
fun SyntaxRange.minus(
    offset: ULong = 0uL,
    length: ULong = 0uL,
    weight: Int = 0,
): SyntaxRange {
    return SyntaxRange(
        offset = this.offset - offset,
        length = this.length - length,
        weight = this.weight - weight
    )
}

/**
 * Return a range with the given values.
 *
 * This function uses the [SyntaxRange] constructor.
 */
fun SyntaxRange.copy(
    offset: ULong = this.offset,
    length: ULong = this.length,
    weight: Int = this.weight,
): SyntaxRange {
    return SyntaxRange(
        offset = offset,
        length = length,
        weight = weight
    )
}

//

/**
 * Check if this range fits with [range] but not as a parent.
 */
fun SyntaxRange.fitNotAsParentOf(range: BufferRange): Boolean {
    return fitNotAsParentOf(SyntaxRange(range))
}

/**
 * Check if this range fits with [range] but not as a parent.
 */
fun SyntaxRange.fitNotAsParentOf(range: SyntaxRange): Boolean {
    return when (Dominance(this, range)) {
        Dominance.Exact -> when (Precedence(this, range)) {
            Precedence.Higher -> true
            Precedence.Lower, Precedence.Equal -> false
        }
        Dominance.Contain, Dominance.None -> true
        Dominance.Share, Dominance.Part -> false
    }
}

//

internal data class SyntaxRangeImpl(
    override val offset: ULong,
    override val length: ULong,
    override val weight: Int
) : SyntaxRange {
    override fun toString(): String =
        "SyntaxRange(offset=$offset, length=$length, weight=$weight)"
}
