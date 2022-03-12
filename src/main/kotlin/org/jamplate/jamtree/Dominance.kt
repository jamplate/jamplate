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
     * ## **CONTAIN** [(opposite)][PART]
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
     * @see Relation.PARENT
     * @see Intersection.CONTAINER
     * @see Intersection.AHEAD
     * @see Intersection.BEHIND
     * @since 0.2.0 ~2021.01.10
     */
    CONTAIN {
        override val opposite: Dominance get() = PART
    },

    /**
     * ## **EXACT** [(opposite)][EXACT]
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
     * @see Relation.SELF
     * @see Intersection.SAME
     * @since 0.2.0 ~2021.01.10
     */
    EXACT {
        override val opposite: Dominance get() = EXACT
    },

    /**
     * ## **SHARE** [(opposite)][SHARE]
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
     * @see Relation.CLASH
     * @see Intersection.OVERFLOW
     * @see Intersection.UNDERFLOW
     * @since 0.2.0 ~2021.01.10
     */
    SHARE {
        override val opposite: Dominance get() = SHARE
    },

    /**
     * ## **PART** [(opposite)][CONTAIN]
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
     * @see Relation.CHILD
     * @see Intersection.FRAGMENT
     * @see Intersection.START
     * @see Intersection.END
     * @since 0.2.0 ~2021.01.10
     */
    PART {
        override val opposite: Dominance get() = CONTAIN
    },

    /**
     * ## **NONE** [(opposite)][NONE]
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
     * @see Relation.NEXT
     * @see Relation.PREVIOUS
     * @see Intersection.FRONT
     * @see Intersection.BACK
     * @see Intersection.AFTER
     * @see Intersection.BEFORE
     * @since 0.2.0 ~2021.01.10
     */
    NONE {
        override val opposite: Dominance get() = NONE
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
 * then the [part][Dominance.PART] will be returned.
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
fun computeDominance(i: Int, j: Int, s: Int, e: Int): Dominance {
    require(i >= 0 || s >= 0 || i <= j || s <= e) { "Illegal Indices" }
    return when {
        i == s && j == e -> Dominance.EXACT
        s < i && j < e || i == s && j < e || s < i && j == e -> Dominance.CONTAIN
        i < s && e < j || i == s /* && e < j */ || i < s && j == e -> Dominance.PART
        i < s && s < j /* && j < e */ || s < i && i < e /* && e < j */ -> Dominance.SHARE
        /* i < j && j == s && s < e || s < e && e == i && i < j || j < s || e < i */ else -> Dominance.NONE
    }
}
