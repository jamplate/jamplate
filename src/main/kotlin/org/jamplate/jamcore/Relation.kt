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
     * ## **PARENT** [(opposite)][Child]
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
     * @see Dominance.Contain
     * @see Intersection.Container
     * @see Intersection.Ahead
     * @see Intersection.Behind
     * @since 0.2.0 ~2021.05.14
     */
    Parent(Dominance.Contain) {
        override val opposite: Relation get() = Child
    },

    /**
     * ## **CHILD** [(opposite)][Parent]
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
     * @see Dominance.Part
     * @see Intersection.Fragment
     * @see Intersection.Start
     * @see Intersection.End
     * @since 0.2.0 ~2021.05.14
     */
    Child(Dominance.Part) {
        override val opposite: Relation get() = Parent
    },

    /**
     * ## **PREVIOUS** [(opposite)][Next]
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
     * @see Dominance.None
     * @see Intersection.Back
     * @see Intersection.Before
     * @since 0.2.0 ~2021.05.14
     */
    Previous(Dominance.None) {
        override val opposite: Relation get() = Next
    },

    /**
     * ## **NEXT** [(opposite)][Previous]
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
     * @see Dominance.None
     * @see Intersection.Front
     * @see Intersection.After
     * @since 0.2.0 ~2021.05.14
     */
    Next(Dominance.None) {
        override val opposite: Relation get() = Previous
    },

    /**
     * ## **SELF** [(opposite)][Self]
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
     * @see Intersection.Same
     * @see Dominance.Exact
     */
    Self(Dominance.Exact) {
        override val opposite: Relation get() = Self
    },

    /**
     * ## **CLASH** [(opposite)][Clash]
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
     * @see Dominance.Share
     * @see Intersection.Overflow
     * @see Intersection.Underflow
     */
    Clash(Dominance.Share) {
        override val opposite: Relation get() = Clash
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
 * then [child][Relation.Child] will be returned.
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
fun Relation(i: ULong, j: ULong, s: ULong, e: ULong): Relation {
    require(i <= j || s <= e) { "Illegal Indices" }
    return when {
        e < i || s < e && e == i && i < j -> Relation.Previous
        j < s || i < j && j == s && s < e -> Relation.Next
        s <= i && j < e || s < i && j == e -> Relation.Parent
        i <= s && e < j || i < s && j == e -> Relation.Child
        i == s /* && j == e */ -> Relation.Self
        /* i < s && s < j && j < e || s < i && i < e && e < j */ else -> Relation.Clash
    }
}

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
fun Relation(range: BufferRange, s: ULong, e: ULong): Relation =
    Relation(range.offset, range.terminal, s, e)

/**
 * Compute the relation between [range] and the [other] range.
 *
 * @param range the first range.
 * @param other the second range.
 * @return the relation of the second range over the first range.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun Relation(range: BufferRange, other: BufferRange): Relation =
    Relation(range.offset, range.terminal, other.offset, other.terminal)

/**
 * Check if the relation computation of the given
 * arguments result to this relation.
 */
@Contract(pure = true)
operator fun Relation.invoke(i: ULong, j: ULong, s: ULong, e: ULong): Boolean =
    this == Relation(i, j, s, e)

/**
 * Check if the relation computation of the given
 * arguments result to this relation.
 */
@Contract(pure = true)
operator fun Relation.invoke(range: BufferRange, s: ULong, e: ULong): Boolean =
    this == Relation(range, s, e)

/**
 * Check if the relation computation of the given
 * arguments result to this relation.
 */
@Contract(pure = true)
operator fun Relation.invoke(range: BufferRange, other: BufferRange): Boolean =
    this == Relation(range, other)
