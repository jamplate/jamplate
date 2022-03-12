/*
 *	Copyright 2022 cufy.org
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
package org.jamplate.jamtree

import org.jetbrains.annotations.Contract
import java.io.Serializable

/**
 * The indices of a text component.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.03.11
 */
data class Range(
    /**
     * The position of this range.
     *
     * @since 0.4.0 ~2022.03.11
     */
    val start: Int = 0,
    /**
     * The length of this range.
     *
     * @since 0.4.0 ~2022.03.11
     */
    val length: Int = 0
) : Serializable, Comparable<Range> {
    init {
        require(start >= 0 && length >= 0) { "Invalid Range: <$start, $length>" }
    }

    override fun compareTo(other: Range): Int {
        return when {
            other.start < this.start -> 1
            other.start > this.start -> -1
            else -> this.length compareTo other.length
        }
    }

    companion object {
        private const val serialVersionUID: Long = 6861800167664946299L
    }
}

//

/**
 * One past the last index of this range.
 */
val Range.end: Int get() = start + length

//

/**
 * Compute the dominance between [range] and the [other] range
 *
 * @param range the first range.
 * @param other the second range.
 * @return the dominance of the second range over the first range.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeDominance(range: Range, other: Range): Dominance =
    computeDominance(range.start, range.end, other.start, other.end)

/**
 * Compute the relation between [range] and the [other] range.
 *
 * @param range the first range.
 * @param other the second range.
 * @return the relation of the second range over the first range.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeRelation(range: Range, other: Range): Relation =
    computeRelation(range.start, range.end, other.start, other.end)

/**
 * Compute the intersection between [range] and the [other] range.
 *
 * @param range the first range.
 * @param other the second range.
 * @return the intersection of the second range over the first range.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeIntersection(range: Range, other: Range): Intersection =
    computeIntersection(range.start, range.end, other.start, other.end)

//

/**
 * Compute the dominance between [range] and a range with [s] and [e].
 *
 * @param range the first range.
 * @param s the first index of the second area.
 * @param e one past the last index of the second area.
 * @return the dominance of [s] and [e] over the first range.
 * @throws IllegalArgumentException if `s` is not in the range `[0, e]`.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeDominance(range: Range, s: Int, e: Int): Dominance =
    computeDominance(range.start, range.end, s, e)

/**
 * Compute the relation between [range] and a range with [s] and [e].
 *
 * @param range the first range.
 * @param s the first index of the second area.
 * @param e one past the last index of the second area.
 * @return the relation of [s] and [e] over the first range.
 * @throws IllegalArgumentException if `s` is not in the range `[0, e]`.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeRelation(range: Range, s: Int, e: Int): Relation =
    computeRelation(range.start, range.end, s, e)

/**
 * Compute the intersection between [range] and a range with [s] and [e].
 *
 * @param range the first range.
 * @param s the first index of the second area.
 * @param e one past the last index of the second area.
 * @return the intersection of [s] and [e] over the first range.
 * @throws IllegalArgumentException if `s` is not in the range `[0, e]`.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeIntersection(range: Range, s: Int, e: Int): Intersection =
    computeIntersection(range.start, range.end, s, e)

//

/**
 * Get a slice of this range.
 *
 * @param start the position where the new range will have relative to this range.
 * @param length  the length of the new range.
 * @return a range that starts at [start] relative to this range and its length is [length].
 * @throws IllegalArgumentException
 * - if the given [start] or [length] is negative.
 * - if [start] plus [length] is greater than [Range.length].
 */
@Contract(value = "_,_->new", pure = true)
fun Range.subrange(start: Int, length: Int = this.length - start): Range {
    require(start >= 0 && length >= 0) { "Invalid Range: <$start, $length>" }
    require(start + length <= this.length) { "Range: <$start, $length> out <${this.start}, ${this.length}>" }
    return when {
        start == 0 && length == this.length -> this
        else -> Range(this.start + start, length)
    }
}
