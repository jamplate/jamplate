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
 * ## **Precedence**
 * An enumeration of possible precedence scenarios.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.03.11
 */
enum class Precedence {
    /**
     * ## **HIGHER** [(opposite)][Lower]
     * When the second area can contain the first area.
     *
     * @since 0.4.0 ~2022.03.11
     */
    Higher {
        override val opposite: Precedence get() = Lower
    },

    /**
     * ## **LOWER** [(opposite)][Higher]
     * When the second area can be contained in the first area.
     *
     * @since 0.4.0 ~2022.03.11
     */
    Lower {
        override val opposite: Precedence get() = Higher
    },

    /**
     * ## **EQUAL** [(opposite)][Equal]
     * When the second source has equal precedence to the first area.
     *
     * @since 0.4.0 ~2022.03.11
     */
    Equal {
        override val opposite: Precedence get() = Equal
    };

    /**
     * The opposite precedence of this.
     */
    abstract val opposite: Precedence
}

/**
 * Calculate the precedence between the first area and the second.
 *
 * The first area is the area to compare the second area with. The returned
 * precedence will be describing the feelings of the first area
 * about the second area.
 *
 * For example: if the second area is higher than the first area,
 * then [higher][Precedence.Higher] will be returned.
 *
 * @param k the weight of the first area.
 * @param w the weight of the second area.
 * @return the precedence of the second area over the first area.
 * @since 0.4.0 ~2022.03.11
 */
@Contract(pure = true)
fun Precedence(k: Int, w: Int): Precedence {
    return when {
        k > w -> Precedence.Higher
        k < w -> Precedence.Lower
        else -> Precedence.Equal
    }
}

/**
 * Compute the precedence between [range] and a range with [w].
 *
 * @param range the first range.
 * @param w the weight of the second area.
 * @return the precedence of [w] over the first range.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun Precedence(range: SyntaxRange, w: Int): Precedence =
    Precedence(range.weight, w)

/**
 * Compute the precedence between [range] and the [other] range.
 *
 * @param range the first range.
 * @param other the second range.
 * @return the precedence of the second range over the first range.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun Precedence(range: SyntaxRange, other: SyntaxRange): Precedence =
    Precedence(range.weight, other.weight)

/**
 * Check if the precedence computation of the
 * given arguments result to this precedence.
 */
@Contract(pure = true)
operator fun Precedence.invoke(k: Int, w: Int): Boolean =
    this == Precedence(k, w)

/**
 * Check if the precedence computation of the
 * given arguments result to this precedence.
 */
@Contract(pure = true)
operator fun Precedence.invoke(range: SyntaxRange, w: Int): Boolean =
    this == Precedence(range, w)

/**
 * Check if the precedence computation of the
 * given arguments result to this precedence.
 */
@Contract(pure = true)
operator fun Precedence.invoke(range: SyntaxRange, other: SyntaxRange): Boolean =
    this == Precedence(range, other)
