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
     * ## **CONTAINER** [(opposite)][FRAGMENT]
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
     * @see Dominance.CONTAIN
     * @see Relation.PARENT
     * @since 0.2.0 ~2021.01.09
     */
    CONTAINER(Dominance.CONTAIN, Relation.PARENT) {
        override val opposite: Intersection get() = FRAGMENT
    },

    /**
     * ## **AHEAD** [(opposite)][START]
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
     * @see Dominance.CONTAIN
     * @see Relation.PARENT
     * @since 0.2.0 ~2021.01.09
     */
    AHEAD(Dominance.CONTAIN, Relation.PARENT) {
        override val opposite: Intersection get() = START
    },

    /**
     * ## **BEHIND** [(opposite)][END]
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
     * @see Dominance.CONTAIN
     * @see Relation.PARENT
     * @since 0.2.0 ~2021.01.09
     */
    BEHIND(Dominance.CONTAIN, Relation.PARENT) {
        override val opposite: Intersection get() = END
    },

    /**
     * ## **SAME** [(opposite)][SAME]
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
     * @see Dominance.EXACT
     * @see Relation.SELF
     * @since 0.2.0 ~2021.01.09
     */
    SAME(Dominance.EXACT, Relation.SELF) {
        override val opposite: Intersection get() = SAME
    },

    /**
     * ## **OVERFLOW** [(opposite)][UNDERFLOW]
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
     * @see Dominance.SHARE
     * @see Relation.CLASH
     * @since 0.2.0 ~2021.01.09
     */
    OVERFLOW(Dominance.SHARE, Relation.CLASH) {
        override val opposite: Intersection get() = UNDERFLOW
    },

    /**
     * ## **UNDERFLOW** [(opposite)][OVERFLOW]
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
     * @see Dominance.SHARE
     * @see Relation.CLASH
     * @since 0.2.0 ~2021.01.09
     */
    UNDERFLOW(Dominance.SHARE, Relation.CLASH) {
        override val opposite: Intersection get() = OVERFLOW
    },

    /**
     * ## **FRAGMENT** [(opposite)][CONTAINER]
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
     * @see Dominance.PART
     * @see Relation.CHILD
     * @since 0.2.0 ~2021.01.09
     */
    FRAGMENT(Dominance.PART, Relation.CHILD) {
        override val opposite: Intersection get() = CONTAINER
    },

    /**
     * ## **START** [(opposite)][AHEAD]
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
     * @see Dominance.PART
     * @see Relation.CHILD
     * @since 0.2.0 ~2021.01.09
     */
    START(Dominance.PART, Relation.CHILD) {
        override val opposite: Intersection get() = AHEAD
    },

    /**
     * ## **END** [(opposite)][BEHIND]
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
     * @see Dominance.PART
     * @see Relation.CHILD
     * @since 0.2.0 ~2021.01.09
     */
    END(Dominance.PART, Relation.CHILD) {
        override val opposite: Intersection get() = BEHIND
    },

    /**
     * ## **FRONT** [(opposite)][BACK]
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
     * @see Dominance.NONE
     * @see Relation.NEXT
     * @since 0.2.0 ~2021.01.09
     */
    FRONT(Dominance.NONE, Relation.NEXT) {
        override val opposite: Intersection get() = BACK
    },

    /**
     * ## **AFTER** [opposite][BEFORE]
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
     * @see Dominance.NONE
     * @see Relation.NEXT
     * @since 0.2.0 ~2021.01.09
     */
    AFTER(Dominance.NONE, Relation.NEXT) {
        override val opposite: Intersection get() = BEFORE
    },

    /**
     * ## **BACK** [(opposite)][FRONT]
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
     * @see Dominance.NONE
     * @see Relation.PREVIOUS
     * @since 0.2.0 ~2021.01.09
     */
    BACK(Dominance.NONE, Relation.PREVIOUS) {
        override val opposite: Intersection get() = FRONT
    },

    /**
     * **BEFORE** [opposite][AFTER]
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
     * @see Dominance.NONE
     * @see Relation.PREVIOUS
     * @since 0.2.0 ~2021.01.09
     */
    BEFORE(Dominance.NONE, Relation.PREVIOUS) {
        override val opposite: Intersection get() = AFTER
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
 * then [fragmnet][Intersection.FRAGMENT] will be returned.
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
fun computeIntersection(i: Int, j: Int, s: Int, e: Int): Intersection {
    require(i >= 0 || s >= 0 || i <= j || s <= e) { "Illegal Indices" }
    return when {
        e < i -> Intersection.BEFORE
        j < s -> Intersection.AFTER
        i == s -> when {
            /* i == s && */ j == e -> Intersection.SAME
            /* i == s && */ j < e -> Intersection.AHEAD
            /* i == s && e < j */ else -> Intersection.START
        }
        j == e -> when {
            s < i /* && j == e */ -> Intersection.BEHIND
            /* i < s && j == e */ else -> Intersection.END
        }
        /* s < e && */ e == i /* && i < j */ -> Intersection.BACK
        /* i < j && */ j == s /* && s < e */ -> Intersection.FRONT
        s < i -> when {
            /* s < i && */ j < e -> Intersection.CONTAINER
            /* s < i && i < e && e < j */ else -> Intersection.UNDERFLOW
        }
        /* i < s && */ e < j -> Intersection.FRAGMENT
        /* i < s && s < j && j < e */ else -> Intersection.OVERFLOW
    }
}
