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

import org.jetbrains.annotations.Contract

/**
 * A tree that can be manually mutated.
 *
 * This is a more manual form of [ManagedTree]
 * with the difference being the positioning of
 * trees. The position of manual trees are set
 * manually vs. managed trees with its positions
 * being set depending on the offset, length and
 * weight.
 *
 * A common thing between manual and managed trees
 * is the binding mechanism. Just like managed
 * trees, the binding mechanism of manual trees is
 * internally configured with public functions to
 * tell when and where to position the trees.
 *
 * This class does not implement [equals] and
 * [hashCode] functions.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.11.15
 */
data class ManualTree<T>(
    override val value: T
) : Tree<T> {
    @TreeImplementationApi
    override var _top: ManualTree<T>? = null; internal set

    @TreeImplementationApi
    override var _left: ManualTree<T>? = null; internal set

    @TreeImplementationApi
    override var _right: ManualTree<T>? = null; internal set

    @TreeImplementationApi
    override var _bottom: ManualTree<T>? = null; internal set

    override fun hashCode(): Int =
        System.identityHashCode(this)

    override fun equals(other: Any?): Boolean =
        this === other
}

//

/**
 * Unset this tree from the current bottom tree of it.
 * Set the bottom tree of this tree to the given [tree]
 * Unset the given [tree] from the current top tree of it.
 * Set the top tree of the given [tree] to this tree.
 *
 * @param tree the tree to be at the bottom of this tree.
 * @since 0.4.0 ~2022.11.14
 */
@OptIn(TreeImplementationApi::class)
@Contract(mutates = "this,param")
private infix fun <T> ManualTree<T>.bottom(tree: ManualTree<T>?) {
    this._bottom?._top = null
    this._bottom = tree
    tree?._top?._bottom = null
    tree?._top = this
}

/**
 * Unset this tree from the current top tree of it.
 * Set the top tree of this tree to the given [tree]
 * Unset the given [tree] from the current bottom tree of it.
 * Set the bottom tree of the given [tree] to this tree.
 *
 * @param tree the tree to be at the top of this tree.
 * @since 0.4.0 ~2022.11.14
 */
@OptIn(TreeImplementationApi::class)
@Contract(mutates = "this,param")
private infix fun <T> ManualTree<T>.top(tree: ManualTree<T>?) {
    this._top?._bottom = null
    this._top = tree
    tree?._bottom?._top = null
    tree?._bottom = this
}

/**
 * Unset this tree from the current left tree of it.
 * Set the left tree of this tree to the given [tree]
 * Unset the given [tree] from the current right tree of it.
 * Set the right tree of the given [tree] to this tree.
 *
 * @param tree the tree to be at the left of this tree.
 * @since 0.4.0 ~2022.11.14
 */
@OptIn(TreeImplementationApi::class)
@Contract(mutates = "this,param")
private infix fun <T> ManualTree<T>.left(tree: ManualTree<T>?) {
    this._left?._right = null
    this._left = tree
    tree?._right?._left = null
    tree?._right = this
}

/**
 * Unset this tree from the current right tree of it.
 * Set the right tree of this tree to the given [tree]
 * Unset the given [tree] from the current left tree of it.
 * Set the left tree of the given [tree] to this tree.
 *
 * @param tree the tree to be at the right of this tree.
 * @since 0.4.0 ~2022.11.14
 */
@OptIn(TreeImplementationApi::class)
@Contract(mutates = "this,param")
private infix fun <T> ManualTree<T>.right(tree: ManualTree<T>?) {
    this._right?._left = null
    this._right = tree
    tree?._left?._right = null
    tree?._left = this
}

//

/**
 * Make [tree] be the previous tree of this.
 *
 * [tree] will be [removed][ManualTree.remove]
 * from its structure then put to the proper place
 * in the structure of this tree.
 *
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to set [tree].
 */
@Contract(mutates = "this,param")
infix fun <T> ManualTree<T>.pushPrevious(tree: ManualTree<T>) {
    val top = this.top
    val left = this.left

    if (top != null && left != null)
        throw CorruptedTreeError("previous: flipped T", top, this, left)

    when {
        top != null -> {
            // top |> tree -> this [!left]
            tree.remove()
            top bottom tree
            this left tree
        }
        left != null -> {
            // left -> tree -> this [!top]
            tree.remove()
            left right tree
            this left tree
        }
        else -> {
            // tree -> this [!top !left]
            tree.remove()
            this left tree
        }
    }
}

/**
 * Make [tree] be the next tree of this.
 *
 * [tree] will be [removed][ManualTree.remove]
 * from its structure then put to the proper place
 * in the structure of this tree.
 *
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to set [tree].
 */
@Contract(mutates = "this,param")
infix fun <T> ManualTree<T>.pushNext(tree: ManualTree<T>) {
    val right = this.right

    when {
        right != null -> {
            // this -> tree -> right
            tree.remove()
            this right tree
            right left tree
        }
        else -> {
            // this -> right [!right]
            tree.remove()
            this right tree
        }
    }
}

/**
 * Make [tree] be the first child tree of this.
 *
 * [tree] will be [removed][ManualTree.remove]
 * from its structure then put to the proper place
 * in the structure of this tree.
 *
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to set [tree].
 */
@Contract(mutates = "this,param")
infix fun <T> ManualTree<T>.pushChild(tree: ManualTree<T>) {
    val bottom = this.bottom

    when {
        bottom != null -> {
            // this |> tree -> bottom
            tree.remove()
            this bottom tree
            bottom left tree
        }
        else -> {
            // this |> tree [!bottom]
            tree.remove()
            this bottom tree
        }
    }
}

//

/**
 * Make all the trees in this iterable siblings.
 * The **first** tree will be the most **left** tree and
 * the **last** tree will be the most **right** tree.
 *
 * All trees in the iterable WILL be [removed][ManualTree.remove].
 */
fun <T> Iterable<ManualTree<T>>.joinPushNext() {
    val iterator = this.iterator()
    if (!iterator.hasNext())
        return
    val first = iterator.next()
    first.remove()
    while (iterator.hasNext()) {
        first pushNext iterator.next()
    }
}

/**
 * Make all the trees in this iterable siblings.
 * The **first** tree will be the most **right** tree and
 * the **last** tree will be the most **left** tree.
 *
 * All trees in the iterable WILL be [removed][ManualTree.remove].
 */
fun <T> Iterable<ManualTree<T>>.joinPushPrevious() {
    val iterator = this.iterator()
    if (!iterator.hasNext())
        return
    val first = iterator.next()
    first.remove()
    while (iterator.hasNext()) {
        first pushPrevious iterator.next()
    }
}

//

/**
 * Remove the children of this tree.
 *
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to clear this tree.
 * @since 0.4.0 ~2022.11.14
 */
@Contract(mutates = "this")
fun <T> ManualTree<T>.clear() {
    this bottom null
}

/**
 * Remove this tree and only this tree from its structure.
 *
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to pop this tree.
 * @since 0.4.0 ~2022.11.14
 */
@Contract(mutates = "this")
fun <T> ManualTree<T>.pop() {
    val top = this.top
    val left = this.left
    val right = this.right
    val bottom = this.bottom

    if (top != null && left != null)
        throw CorruptedTreeError("pop: flipped T", top, this, left)

    when {
        top != null -> {
            when {
                bottom != null -> {
                    //top |> bottom...tail -> right [!left]
                    top bottom bottom
                    bottom.tail right right
                }
                right != null -> {
                    //top |> right [!left !bottom]
                    top bottom right
                    right left null
                }
                else -> {
                    //top [!left !bottom !right]
                    top bottom null
                }
            }
        }
        left != null -> {
            when {
                bottom != null -> {
                    //left -> bottom...tail -> right [!top]
                    left right bottom
                    bottom top null
                    bottom.tail right right
                }
                right != null -> {
                    //left -> right [!top !bottom]
                    left right right
                }
                else -> {
                    //left [!top !bottom !right]
                    left right null
                }
            }
        }
        bottom != null -> {
            //bottom...tail -> right [!top !left]
            bottom top null
            bottom.tail right right
        }
        right != null -> {
            //right [!top !left !bottom]
            right left null
        }
    }
}

/**
 * Remove this tree and its children from its structure.
 *
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to remove this tree.
 * @since 0.4.0 ~2022.11.14
 */
@Contract(mutates = "this")
fun <T> ManualTree<T>.remove() {
    val top = this.top
    val left = this.left
    val right = this.right

    if (top != null && left != null)
        throw CorruptedTreeError("remove: flipped T", top, this, left)

    when {
        top != null -> {
            when {
                right != null -> {
                    //top |> right [!left]
                    top bottom right
                    right left null
                }
                else -> {
                    //top [!left !right]
                    top bottom null
                }
            }
        }
        left != null -> {
            when {
                right != null -> {
                    //left -> right [!top]
                    left right right
                }
                else -> {
                    //left [!top !right]
                    left right null
                }
            }
        }
        right != null -> {
            //right [!top !left]
            right left null
        }
    }
}
