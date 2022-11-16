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
package org.jamplate.jamtree

import java.util.*

/**
 * Construct a new fantom tree with the given
 * arguments.
 *
 * @param top the tree on the top.
 * @param left the tree on the left.
 * @param right the tree on the right.
 * @param bottom the tree on the bottom.
 */
fun <T, R : Tree<out T>> FantomTree(
    top: R? = null,
    left: R? = null,
    right: R? = null,
    bottom: R? = null,
): FantomTree<T?, R> {
    return FantomTree(
        null,
        top,
        left,
        right,
        bottom
    )
}

/**
 * An immutable tree that points to other trees.
 *
 * Fantom trees implements [equals] and [hashCode]
 *
 * @author LSafer
 * @since 0.4.0
 */
class FantomTree<T, R : Tree<out T>>(
    override val value: T,
    /**
     * The tree to the top.
     */
    top: R? = null,
    /**
     * The tree to the left.
     */
    left: R? = null,
    /**
     * The tree to the right.
     */
    right: R? = null,
    /**
     * The tree to the bottom.
     */
    bottom: R? = null,
) : Tree<T> {
    @Suppress("UNCHECKED_CAST")
    @TreeImplementationApi
    override val _top: Tree<T>? = top as Tree<T>?

    @Suppress("UNCHECKED_CAST")
    @TreeImplementationApi
    override val _left: Tree<T>? = left as Tree<T>?

    @Suppress("UNCHECKED_CAST")
    @TreeImplementationApi
    override val _right: Tree<T>? = right as Tree<T>?

    @Suppress("UNCHECKED_CAST")
    @TreeImplementationApi
    override val _bottom: Tree<T>? = bottom as Tree<T>?

    @OptIn(TreeImplementationApi::class)
    override fun hashCode(): Int {
        return Objects.hash(value, _top, _left, _right, _bottom)
    }

    @OptIn(TreeImplementationApi::class)
    override fun equals(other: Any?): Boolean {
        return other is FantomTree<*, *> &&
                other._top == this._top &&
                other._left == this._left &&
                other._right == this._right &&
                other._bottom == this._bottom
    }

    @OptIn(TreeImplementationApi::class)
    override fun toString(): String {
        return "FantomTree(value=$value, top=$_top, left=$_left, right=$_right, bottom=$_bottom)"
    }
}

/**
 * The tree to the top of this tree.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(TreeImplementationApi::class)
val <T, R : Tree<T>> FantomTree<T, R>.top: R?
    get() = _top as R?

/**
 * The tree to the bottom of this tree.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(TreeImplementationApi::class)
val <T, R : Tree<T>> FantomTree<T, R>.bottom: R?
    get() = _bottom as R?

/**
 * The tree to the left of this tree.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(TreeImplementationApi::class)
val <T, R : Tree<T>> FantomTree<T, R>.left: R?
    get() = _left as R?

/**
 * The tree to the right of this tree.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(TreeImplementationApi::class)
val <T, R : Tree<T>> FantomTree<T, R>.right: R?
    get() = _right as R?
