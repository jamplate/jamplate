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

import org.jetbrains.annotations.Contract

/**
 * ## **Dominance**
 * An enumeration of how dominant a relation is over another relation.
 *
 * <br><br>
 *
 *      EXACT
 *      i == s <= j == e
 *      ...|---|...
 *      ...|---|...
 *
 *      PART | CONTAIN
 *      i < s <= e < j
 *      .<------->.
 *      ...|---|...
 *
 *      i == s <= e < j
 *      .|------->.
 *      .|--->.....
 *
 *      i < s <= e == j
 *      .<-------|.
 *      .....<---|.
 *
 *      SHARE
 *      i < s < j < e
 *      .<----|....
 *      ....|---->.
 *
 *      s < i < e < j
 *      ....|---->.
 *      .<----|....
 *
 *      NONE
 *      i < j == s < e
 *      .<---|.....
 *      .....|--->.
 *
 *      s < e == i < j
 *      .....|--->.
 *      .<---|.....
 *
 *      i <= j < s <= e
 *      .<--|......
 *      ......|-->.
 *
 *      s <= e < i <= j
 *      ......|-->.
 *      .<--|......
 *
 * <br><br>
 *
 * @author LSafer
 * @since 0.2.0 ~2021.01.10
 */
enum class Dominance {
    /**
     * ## **CONTAIN** [(opposite)][Part]
     * Defines that a source that have the relation contains the other source in it (but
     * not exact).
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      ...|---|...
     *      .<------->.
     *
     *      .|--->.....
     *      .|------->.
     *
     *      .....<---|.
     *      .<-------|.
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (s < i <= j < e)
     *      (s == i <= j < e)
     *      (s < i <= j == e)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {s < i} & {j < e}
     *      {i == s} & {j < e}
     *      {s < i} & {j == e}
     *
     * <br><br>
     *
     * @see Relation.Parent
     * @see Intersection.Container
     * @see Intersection.Ahead
     * @see Intersection.Behind
     * @since 0.2.0 ~2021.01.10
     */
    Contain {
        override val opposite: Dominance get() = Part
    },

    /**
     * ## **EXACT** [(opposite)][Exact]
     * Defines that a source that have the relation has the same relation as the other
     * source.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      ...|---|...
     *      ...|---|...
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i == s <= j == e)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i == s} & {j == e}
     *
     * <br><br>
     *
     * @see Relation.Self
     * @see Intersection.Same
     * @since 0.2.0 ~2021.01.10
     */
    Exact {
        override val opposite: Dominance get() = Exact
    },

    /**
     * ## **SHARE** [(opposite)][Share]
     * Defines that a source that have the relation shares some (but not all) of the other
     * source and vice versa.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<----|....
     *      ....|---->.
     *
     *       ....|---->.
     *      .<----|....
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i < s < j < e)
     *      (s < i < e < j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i < s} & {s < j} & {j < e}
     *      {s < i} & {i < e} & {e < j}
     *
     * <br><br>
     *
     * @see Relation.Clash
     * @see Intersection.Overflow
     * @see Intersection.Underflow
     * @since 0.2.0 ~2021.01.10
     */
    Share {
        override val opposite: Dominance get() = Share
    },

    /**
     * ## **PART** [(opposite)][Contain]
     * Defines that a source that have the relation shares some (but not all) of the other
     * source. (one but not both)
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<------->.
     *      ...|---|...
     *
     *      .|------->.
     *      .|--->.....
     *
     *      .<-------|.
     *      .....<---|.
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i < s <= e < j)
     *      (i == s <= e < j)
     *      (i < s <= e == j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i < s} & {e < j}
     *      {i == s} & {e < j}
     *      {i < s} & {j == e}
     *
     * <br><br>
     *
     * @see Relation.Child
     * @see Intersection.Fragment
     * @see Intersection.Start
     * @see Intersection.End
     * @since 0.2.0 ~2021.01.10
     */
    Part {
        override val opposite: Dominance get() = Contain
    },

    /**
     * ## **NONE** [(opposite)][None]
     * Defines that a source that have the relation shares none of the other source.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<---|.....
     *      .....|--->.
     *
     *      .....|--->.
     *      .<---|.....
     *
     *      .<--|......
     *      ......|-->.
     *
     *      ......|-->.
     *      .<--|......
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i < j == s < e)
     *      (s < e == i < j)
     *      (i <= j < s <= e)
     *      (s <= e < i <= j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i < j} & {j == s} & {s < e}
     *      {s < e} & {e == i} & {i < j}
     *      {j < s}
     *      {e < i}
     *
     * <br><br>
     *
     * @see Relation.Next
     * @see Relation.Previous
     * @see Intersection.Front
     * @see Intersection.Back
     * @see Intersection.After
     * @see Intersection.Before
     * @since 0.2.0 ~2021.01.10
     */
    None {
        override val opposite: Dominance get() = None
    };

    /**
     * The opposite dominance of this dominance.
     *
     * @since 0.2.0 ~2021.01.10
     */
    abstract val opposite: Dominance
}

/**
 * Calculate the dominance of the area `[s, e)` over the area `[i, j)`.
 *
 * The first area is the area to compare the second area with. The returned
 * dominance will be the dominance describing the feelings of the first area
 * about the second area.
 *
 * For example: if the second area is contained in the middle of first area,
 * then the [part][Dominance.Part] will be returned.
 *
 * @param i the first index of the first area.
 * @param j one past the last index of the first area.
 * @param s the first index of the second area.
 * @param e one past the last index of the second area.
 * @return the dominance of the second area over the first area.
 * @throws IllegalArgumentException
 * - if `i` is not in the range `[0, j]`
 * - if `s` is not in the range `[0, e]`
 * @since 0.2.0 ~2021.01.10
 */
@Contract(pure = true)
fun Dominance(i: ULong, j: ULong, s: ULong, e: ULong): Dominance {
    require(i <= j || s <= e) { "Illegal Indices" }
    return when {
        i == s && j == e -> Dominance.Exact
        s < i && j < e || i == s && j < e || s < i && j == e -> Dominance.Contain
        i < s && e < j || i == s /* && e < j */ || i < s && j == e -> Dominance.Part
        i < s && s < j /* && j < e */ || s < i && i < e /* && e < j */ -> Dominance.Share
        /* i < j && j == s && s < e || s < e && e == i && i < j || j < s || e < i */ else -> Dominance.None
    }
}

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
fun Dominance(range: BufferRange, s: ULong, e: ULong): Dominance =
    Dominance(range.offset, range.terminal, s, e)

/**
 * Compute the dominance between [range] and the [other] range.
 *
 * @param range the first range.
 * @param other the second range.
 * @return the dominance of the second range over the first range.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun Dominance(range: BufferRange, other: BufferRange): Dominance =
    Dominance(range.offset, range.terminal, other.offset, other.terminal)

/**
 * Check if the dominance computation of the given
 * arguments result to this dominance.
 */
@Contract(pure = true)
operator fun Dominance.invoke(i: ULong, j: ULong, s: ULong, e: ULong): Boolean =
    this == Dominance(i, j, s, e)

/**
 * Check if the dominance computation of the given
 * arguments result to this dominance.
 */
@Contract(pure = true)
operator fun Dominance.invoke(range: BufferRange, s: ULong, e: ULong): Boolean =
    this == Dominance(range, s, e)

/**
 * Check if the dominance computation of the given
 * arguments result to this dominance.
 */
@Contract(pure = true)
operator fun Dominance.invoke(range: BufferRange, other: BufferRange): Boolean =
    this == Dominance(range, other)
