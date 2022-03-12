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
 * ## **Precedence**
 * An enumeration of possible precedence scenarios.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.03.11
 */
enum class Precedence {
    /**
     * ## **HIGHER** [(opposite)][LOWER]
     * When the second area can contain the first area.
     *
     * @since 0.4.0 ~2022.03.11
     */
    HIGHER {
        override val opposite: Precedence get() = LOWER
    },

    /**
     * ## **LOWER** [(opposite)][HIGHER]
     * When the second area can be contained in the first area.
     *
     * @since 0.4.0 ~2022.03.11
     */
    LOWER {
        override val opposite: Precedence get() = HIGHER
    },

    /**
     * ## **EQUAL** [(opposite)][EQUAL]
     * When the second source has equal precedence to the first area.
     *
     * @since 0.4.0 ~2022.03.11
     */
    EQUAL {
        override val opposite: Precedence get() = EQUAL
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
 * then [higher][Precedence.HIGHER] will be returned.
 *
 * @param k the weight of the first area.
 * @param w the weight of the second area.
 * @return the precedence of the second area over the first area.
 * @since 0.4.0 ~2022.03.11
 */
@Contract(pure = true)
fun computePrecedence(k: Int, w: Int): Precedence {
    return when {
        k > w -> Precedence.HIGHER
        k < w -> Precedence.LOWER
        else -> Precedence.EQUAL
    }
}
