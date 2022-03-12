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
 * ## **Relation**
 * An enumeration of the possible directions to the relatives of a source.
 *
 * <br><br>
 *
 *      CHILD | PARENT
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
 *      NEXT | PREVIOUS
 *      i < j == s < e
 *      .<---|.....
 *      .....|--->.
 *
 *      i <= j < s <= e
 *      .<--|......
 *      ......|-->.
 *
 * @author LSafer
 * @since 0.2.0 ~2021.05.14
 */
enum class Relation(
    /**
     * How dominant this direction over the opposite direction.
     *
     * @since 0.2.0 ~2021.05.15
     */
    private val dominance: Dominance
) {
    /**
     * ## **PARENT** [(opposite)][CHILD]
     * When the other source can fit the source without being filled.
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
     * @see Dominance.CONTAIN
     * @see Intersection.CONTAINER
     * @see Intersection.AHEAD
     * @see Intersection.BEHIND
     * @since 0.2.0 ~2021.05.14
     */
    PARENT(Dominance.CONTAIN) {
        override val opposite: Relation get() = CHILD
    },

    /**
     * ## **CHILD** [(opposite)][PARENT]
     * When the other source fits inside the source but is not filling it.
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
     * @see Dominance.PART
     * @see Intersection.FRAGMENT
     * @see Intersection.START
     * @see Intersection.END
     * @since 0.2.0 ~2021.05.14
     */
    CHILD(Dominance.PART) {
        override val opposite: Relation get() = PARENT
    },

    /**
     * ## **PREVIOUS** [(opposite)][NEXT]
     * When the other source occurs before the source.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .....|--->.
     *      .<---|.....
     *
     *      ......|-->.
     *      .<--|......
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (s < e == i < j)
     *      (s <= e < i <= j)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {s < e} & {e == i} & {i < j}
     *      {e < i}
     *
     * <br><br>
     *
     * @see Dominance.NONE
     * @see Intersection.BACK
     * @see Intersection.BEFORE
     * @since 0.2.0 ~2021.05.14
     */
    PREVIOUS(Dominance.NONE) {
        override val opposite: Relation get() = NEXT
    },

    /**
     * ## **NEXT** [(opposite)][PREVIOUS]
     * When the other source occurs after the source.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<---|.....
     *      .....|--->.
     *
     *      .<--|......
     *      ......|-->.
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i < j == s < e)
     *      (i <= j < s <= e)
     *
     * <br><br>
     *
     * ### Math:
     *
     *      {i < j} & {j == s} & {s < e}
     *      {j < s}
     *
     * <br><br>
     *
     * @see Dominance.NONE
     * @see Intersection.FRONT
     * @see Intersection.AFTER
     * @since 0.2.0 ~2021.05.14
     */
    NEXT(Dominance.NONE) {
        override val opposite: Relation get() = PREVIOUS
    },

    /**
     * ## **SELF** [(opposite)][SELF]
     * When the source is the other source.
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
     * @see Intersection.SAME
     * @see Dominance.EXACT
     */
    SELF(Dominance.EXACT) {
        override val opposite: Relation get() = SELF
    },

    /**
     * ## **CLASH** [(opposite)][CLASH]
     * When the second source clashes with the first source.
     *
     * <br><br>
     *
     * ### Visual:
     *
     *      .<----|....
     *      ....|---->.
     *
     *      ....|---->.
     *      .<----|....
     *
     * <br><br>
     *
     * ### Logical:
     *
     *      (i < s < j < e)
     *      (s < i < e < j)
     *
     * ### Math:
     *
     *      {i < s} & {s < j} & {j < e}
     *      {s < i} & {i < e} & {e < j}
     *
     * <br><br>
     *
     * ### Math:
     *
     * @see Dominance.SHARE
     * @see Intersection.OVERFLOW
     * @see Intersection.UNDERFLOW
     */
    CLASH(Dominance.SHARE) {
        override val opposite: Relation get() = CLASH
    };

    /**
     * The opposite relation of this direction.
     *
     * @since 0.2.0 ~2021.05.15
     */
    abstract val opposite: Relation
}

/**
 * Calculate the relation between the area `[i, j)` to the area `[s, e)`.
 *
 * The first area is the area to compare the second area with. The returned
 * relation will be the relation describing the feelings of the first area
 * about the second area.
 *
 * For example: if the second area is contained in the middle of first area,
 * then [child][Relation.CHILD] will be returned.
 *
 * @param i the first index of the first area.
 * @param j one past the last index of the first area.
 * @param s the first index of the second area.
 * @param e one past the last index of the second area.
 * @return the relation between the first area and the second area.
 * @throws IllegalArgumentException
 * - if `i` is not in the range `[0, j]`
 * - if `s` is not in the range `[0, e]`
 * @since 0.2.0 ~2021.05.15
 */
@Contract(pure = true)
fun computeRelation(i: Int, j: Int, s: Int, e: Int): Relation {
    require(i >= 0 || s >= 0 || i <= j || s <= e) { "Illegal Indices" }
    return when {
        e < i || s < e && e == i && i < j -> Relation.PREVIOUS
        j < s || i < j && j == s && s < e -> Relation.NEXT
        s <= i && j < e || s < i && j == e -> Relation.PARENT
        i <= s && e < j || i < s && j == e -> Relation.CHILD
        i == s /* && j == e */ -> Relation.SELF
        /* i < s && s < j && j < e || s < i && i < e && e < j */ else -> Relation.CLASH
    }
}
