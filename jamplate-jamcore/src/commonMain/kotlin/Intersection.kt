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

/*
    This is enumerations of the scenarios of two indices.
    The main formulas are as the following:

    |    ([~])  | i == s <= e == j |
    |   (-[~]-) | i <  s <= e <  j |
    |    ([~]-) | i == s <= e <  j |
    |   (-[~])  | i <  s <= e == j |
    |  (-[-)-]  | i <  s <  j <  e |
    |  (-)[-]   | i <  j == s <  e |
    | (~)-[~]   | i <= j <  s <= e |
*/

/**
 * ## **Intersection**
 * An enumeration of possible intersections between sources.
 *
 * <br></br>
 *
 *      SAME
 *      i == s <= j == e
 *      ...|---|...
 *      ...|---|...
 *
 *      FRAGMENT | CONTAINER
 *      i < s <= e < j
 *      .<------->.
 *      ...|---|...
 *
 *      START | AHEAD
 *      i == s <= e < j
 *      .|------->.
 *      .|--->.....
 *
 *      END | BEHIND
 *      i < s <= e == j
 *      .<-------|.
 *      .....<---|.
 *
 *      OVERFLOW | UNDERFLOW
 *      i < s < j < e
 *      .<----|....
 *      ....|---->.
 *
 *      FRONT | BACK
 *      i < j == s < e
 *      .<---|.....
 *      .....|--->.
 *
 *      AFTER | BEFORE
 *      i <= j < s <= e
 *      .<--|......
 *      ......|-->.
 *
 * <br><br>
 *
 * @author LSafer
 * @since 0.2.0 ~2021.01.09
 */
enum class Intersection(
    /**
     * How dominant this intersection over the opposite intersection.
     *
     * @since 0.2.0 ~2021.01.10
     */
    val dominance: Dominance,
    /**
     * The direction from the opposite intersection to this intersection.
     *
     * @since 0.2.0 ~2021.05.15
     */
    val relation: Relation
) {
    /**
     * ## **CONTAINER** [(opposite)][Fragment]
     * When the bounds of the source are contained in the bounds of the other source but
     * do not touch.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      ...|---|...
     *      .<------->.
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (s < i <= j < e)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {s < i} & {j < e}
     *
     * <br><br>
     *
     * @see Dominance.Contain
     * @see Relation.Parent
     * @since 0.2.0 ~2021.01.09
     */
    Container(Dominance.Contain, Relation.Parent) {
        override val opposite: Intersection get() = Fragment
    },

    /**
     * ## **AHEAD** [(opposite)][Start]
     * When the source is contained at the very start of the other source.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .|--->.....
     *      .|------->.
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (s == i <= j < e)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i == s} & {j < e}
     *
     * @see Dominance.Contain
     * @see Relation.Parent
     * @since 0.2.0 ~2021.01.09
     */
    Ahead(Dominance.Contain, Relation.Parent) {
        override val opposite: Intersection get() = Start
    },

    /**
     * ## **BEHIND** [(opposite)][End]
     * When the source is contained at the very end of the other source.
     *
     * <br><br>
     *
     * ### Visual:
     *      .....<---|.
     *      .<-------|.
     *
     * <br><br>
     *
     * ### Logical:
     *      (s < i <= j == e)
     *
     * <br><br>
     *
     * ### Math:
     *      {s < i} & {j == e}
     *
     * <br><br>
     *
     * @see Dominance.Contain
     * @see Relation.Parent
     * @since 0.2.0 ~2021.01.09
     */
    Behind(Dominance.Contain, Relation.Parent) {
        override val opposite: Intersection get() = End
    },

    /**
     * ## **SAME** [(opposite)][Same]
     * When the source has the exact bounds as the other source.
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
     * ### Math
     *
     *      {i == s} & {j == e}
     *
     * <br><br>
     *
     * @see Dominance.Exact
     * @see Relation.Self
     * @since 0.2.0 ~2021.01.09
     */
    Same(Dominance.Exact, Relation.Self) {
        override val opposite: Intersection get() = Same
    },

    /**
     * ## **OVERFLOW** [(opposite)][Underflow]
     * When the first fragment of the source is before the other source but the second
     * fragment is in it. (without any bound been exactly at another bound)
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<----|....
     *      ....|---->.
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i < s < j < e)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i < s} & {s < j} & {j < e}
     *
     * <br><br>
     *
     * @see Dominance.Share
     * @see Relation.Clash
     * @since 0.2.0 ~2021.01.09
     */
    Overflow(Dominance.Share, Relation.Clash) {
        override val opposite: Intersection get() = Underflow
    },

    /**
     * ## **UNDERFLOW** [(opposite)][Overflow]
     * When the first fragment of the source is in the other source but the second
     * fragment is after it. (without any bound been exactly at another bound)
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      ....|---->.
     *      .<----|....
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (s < i < e < j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {s < i} & {i < e} & {e < j}
     *
     * <br><br>
     *
     * @see Dominance.Share
     * @see Relation.Clash
     * @since 0.2.0 ~2021.01.09
     */
    Underflow(Dominance.Share, Relation.Clash) {
        override val opposite: Intersection get() = Overflow
    },

    /**
     * ## **FRAGMENT** [(opposite)][Container]
     * When the source has its bounds containing the bounds of the other source but do not
     * touch.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<------->.
     *      ...|---|...
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i < s <= e < j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i < s} & {e < j}
     *
     * <br><br>
     *
     * @see Dominance.Part
     * @see Relation.Child
     * @since 0.2.0 ~2021.01.09
     */
    Fragment(Dominance.Part, Relation.Child) {
        override val opposite: Intersection get() = Container
    },

    /**
     * ## **START** [(opposite)][Ahead]
     * When the source contains the other source at its start.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .|------->.
     *      .|--->.....
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i == s <= e < j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i == s} & {e < j}
     *
     * <br><br>
     *
     * @see Dominance.Part
     * @see Relation.Child
     * @since 0.2.0 ~2021.01.09
     */
    Start(Dominance.Part, Relation.Child) {
        override val opposite: Intersection get() = Ahead
    },

    /**
     * ## **END** [(opposite)][Behind]
     * When the source contains the other source at its end.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<-------|.
     *      .....<---|.
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i < s <= e == j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i < s} & {j == e}
     *
     * <br><br>
     *
     * @see Dominance.Part
     * @see Relation.Child
     * @since 0.2.0 ~2021.01.09
     */
    End(Dominance.Part, Relation.Child) {
        override val opposite: Intersection get() = Behind
    },

    /**
     * ## **FRONT** [(opposite)][Back]
     * When the source is followed immediately by the other source.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<---|.....
     *      .....|--->.
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i < j == s < e)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i < j} & {j == s} & {s < e}
     *
     * <br><br>
     *
     * @see Dominance.None
     * @see Relation.Next
     * @since 0.2.0 ~2021.01.09
     */
    Front(Dominance.None, Relation.Next) {
        override val opposite: Intersection get() = Back
    },

    /**
     * ## **AFTER** [opposite][Before]
     * When the other source is after the source but not immediately.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<--|......
     *      ......|-->.
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i <= j < s <= e)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {j < s}
     *
     * <br><br>
     *
     * @see Dominance.None
     * @see Relation.Next
     * @since 0.2.0 ~2021.01.09
     */
    After(Dominance.None, Relation.Next) {
        override val opposite: Intersection get() = Before
    },

    /**
     * ## **BACK** [(opposite)][Front]
     * When the source has the other source immediately before it.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .....|--->.
     *      .<---|.....
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (s < e == i < j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {s < e} & {e == i} & {i < j}
     *
     * <br><br>
     *
     * @see Dominance.None
     * @see Relation.Previous
     * @since 0.2.0 ~2021.01.09
     */
    Back(Dominance.None, Relation.Previous) {
        override val opposite: Intersection get() = Front
    },

    /**
     * **BEFORE** [opposite][After]
     * When the other source is before the source but not immediately.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      ......|-->.
     *      .<--|......
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (s <= e < i <= j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {e < i}
     *
     * <br><br>
     *
     * @see Dominance.None
     * @see Relation.Previous
     * @since 0.2.0 ~2021.01.09
     */
    Before(Dominance.None, Relation.Previous) {
        override val opposite: Intersection get() = After
    };

    /**
     * The opposite intersection of this intersection.
     *
     * @since 0.2.0 ~2021.01.09
     */
    abstract val opposite: Intersection
}

/**
 * Calculate the intersection between the areas `[i, j)` and `[s, e)`.
 *
 * The first area is the area to compare the second area with. The returned
 * intersection will be the intersection describing the feelings of the first area
 * about the second area.
 *
 * For example: if the second area is contained in the middle of first area,
 * then [fragmnet][Intersection.Fragment] will be returned.
 *
 * @param i the first index of the first area.
 * @param j one past the last index of the first area.
 * @param s the first index of the second area.
 * @param e one past the last index of the second area.
 * @return the intersection constant describing the intersection of the second area to
 * the first area.
 * @throws IllegalArgumentException
 * - if `i` is not in the range `[0, j]`
 * - if `s` is not in the range `[0, e]`
 * @since 0.2.0 ~2021.01.10
 */
@Contract(pure = true)
fun Intersection(i: ULong, j: ULong, s: ULong, e: ULong): Intersection {
    require(i <= j || s <= e) { "Illegal Indices" }
    return when {
        e < i -> Intersection.Before
        j < s -> Intersection.After
        i == s -> when {
            /* i == s && */ j == e -> Intersection.Same
            /* i == s && */ j < e -> Intersection.Ahead
            /* i == s && e < j */ else -> Intersection.Start
        }
        j == e -> when {
            s < i /* && j == e */ -> Intersection.Behind
            /* i < s && j == e */ else -> Intersection.End
        }
        /* s < e && */ e == i /* && i < j */ -> Intersection.Back
        /* i < j && */ j == s /* && s < e */ -> Intersection.Front
        s < i -> when {
            /* s < i && */ j < e -> Intersection.Container
            /* s < i && i < e && e < j */ else -> Intersection.Underflow
        }
        /* i < s && */ e < j -> Intersection.Fragment
        /* i < s && s < j && j < e */ else -> Intersection.Overflow
    }
}

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
fun Intersection(range: BufferRange, s: ULong, e: ULong): Intersection =
    Intersection(range.offset, range.terminal, s, e)

/**
 * Compute the intersection between [range] and the [other] range.
 *
 * @param range the first range.
 * @param other the second range.
 * @return the intersection of the second range over the first range.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun Intersection(range: BufferRange, other: BufferRange): Intersection =
    Intersection(range.offset, range.terminal, other.offset, other.terminal)

/**
 * Check if the intersection computation of the
 * given arguments result to this intersection.
 */
@Contract(pure = true)
operator fun Intersection.invoke(i: ULong, j: ULong, s: ULong, e: ULong): Boolean =
    this == Intersection(i, j, s, e)

/**
 * Check if the intersection computation of the
 * given arguments result to this intersection.
 */
@Contract(pure = true)
operator fun Intersection.invoke(range: BufferRange, s: ULong, e: ULong): Boolean =
    this == Intersection(range, s, e)

/**
 * Check if the intersection computation of the
 * given arguments result to this intersection.
 */
@Contract(pure = true)
operator fun Intersection.invoke(range: BufferRange, other: BufferRange): Boolean =
    this == Intersection(range, other)
