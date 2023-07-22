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

import org.jamplate.jamcore.*
import org.jamplate.jamcore.Dominance.*
import org.jamplate.jamcore.Intersection.*
import org.jamplate.jamcore.Precedence.*
import org.jamplate.jamcore.Relation.*
import org.jamplate.jamtree.*
import org.jetbrains.annotations.Contract

/**
 * ManagedTree is a MIST (Managed Index Syntax
 * Tree), a point in a background structure of
 * values that hold the variables of component.
 *
 * The background structure is working like magic
 * and the user cannot interact directly with it.
 *
 * A tree structure can only be modified throw the
 * [offer] method on any tree in it. Any tree that
 * get [offered][offer] into a structure of
 * another tree will be removed from its previous
 * structure.
 *
 * A structure cannot have any clashing trees or
 * trees that does not fit their parent or trees
 * that breaks the order of their neighboring
 * trees in it and all the trees in it will be
 * organized implicitly.
 *
 * The tree class is not thead safe and multiple
 * threads modifying the same tree structure can
 * make that structure corrupted. The corruption
 * due to two thread modifying the same tree
 * structure is undefined. Aside from that, two or
 * more threads just reading the tree structure is
 * totally fine. Also, one thread modifying a tree
 * structure while the other threads just reading
 * it will not corrupt the structure and will only
 * confuse the other threads because random trees
 * will be moved around while those threads are
 * reading.
 *
 * Two identical trees in one structure is allowed
 * only if the two trees does not have the same
 * [weight]. The tree with the higher weight can
 * fit inside the tree with the lower weight.
 *
 * This class does not implement [equals] and
 * [hashCode] functions.
 *
 * @author LSafer
 * @since 0.2.0 ~2020.12.25
 */
data class ManagedTree<T>(
    override var value: T,
    override val offset: ULong,
    override val length: ULong,
    override val weight: Int = 0
) : SyntaxTree<T> {
    @TreeImplementationApi
    override var _top: ManagedTree<T>? = null; internal set

    @TreeImplementationApi
    override var _left: ManagedTree<T>? = null; internal set

    @TreeImplementationApi
    override var _right: ManagedTree<T>? = null; internal set

    @TreeImplementationApi
    override var _bottom: ManagedTree<T>? = null; internal set

    override fun hashCode(): Int =
        System.identityHashCode(this)

    override fun equals(other: Any?): Boolean =
        this === other
}

//

/**
 * Construct a new managed tree with the given arguments.
 *
 * @param value the value.
 * @param range the range providing offset and length.
 * @param weight the weight.
 * @since 0.4.0 ~2022.11.14
 */
fun <T> ManagedTree(value: T, range: BufferRange, weight: Int = 0): ManagedTree<T> {
    return ManagedTree(value, range.offset, range.length, weight)
}

/**
 * Construct a new managed tree with the given arguments.
 *
 * @param value the value.
 * @param range the range providing offset, length and weight.
 * @since 0.4.0 ~2022.11.14
 */
fun <T> ManagedTree(value: T, range: SyntaxRange): ManagedTree<T> {
    return ManagedTree(value, range.offset, range.length, range.weight)
}

//

/**
 * Unset this tree from the current bottom tree of it.
 * Set the bottom tree of this tree to the given [tree]
 * Unset the given [tree] from the current top tree of it.
 * Set the top tree of the given [tree] to this tree.
 *
 * @param tree the tree to be at the bottom of this tree.
 * @since 0.4.0 ~2022.03.12
 */
@OptIn(TreeImplementationApi::class)
@Contract(mutates = "this,param")
private infix fun <T> ManagedTree<T>.bottom(tree: ManagedTree<T>?) {
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
 * @since 0.4.0 ~2022.03.12
 */
@OptIn(TreeImplementationApi::class)
@Contract(mutates = "this,param")
private infix fun <T> ManagedTree<T>.top(tree: ManagedTree<T>?) {
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
 * @since 0.4.0 ~2022.03.12
 */
@OptIn(TreeImplementationApi::class)
@Contract(mutates = "this,param")
private infix fun <T> ManagedTree<T>.left(tree: ManagedTree<T>?) {
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
 * @since 0.4.0 ~2022.03.12
 */
@OptIn(TreeImplementationApi::class)
@Contract(mutates = "this,param")
private infix fun <T> ManagedTree<T>.right(tree: ManagedTree<T>?) {
    this._right?._left = null
    this._right = tree
    tree?._left?._right = null
    tree?._left = this
}

//

/**
 * Offer the [tree] to the structure of this tree.
 *
 * [tree] will be [popped][ManagedTree.pop] from
 * its structure then put to the proper place in
 * the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
infix fun <T> ManagedTree<T>.offer(tree: ManagedTree<T>) {
    when (Relation(this, tree)) {
        Parent -> this offerParent tree
        Self -> this offerSelf tree
        Child -> this offerChild tree
        Next -> this offerNext tree
        Previous -> this offerPrevious tree
        Clash -> throw TreeClashException("offer", this, tree)
    }
}

/**
 * Assuming the relation between this and [tree] is [Self],
 * Offer the [tree] to the structure of this tree.
 *
 * [tree] will be [popped][ManagedTree.pop] from
 * its structure then put to the proper place in
 * the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [Self].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
internal infix fun <T> ManagedTree<T>.offerSelf(tree: ManagedTree<T>) {
    require(Relation(this, tree) == Self) {
        "Illegal Relation: Expected: $Self Actual: ${Relation(this, tree)}"
    }

    val top = this.top
    val left = this.left
    val right = this.right
    val bottom = this.bottom

    if (top != null && left != null)
        throw CorruptedTreeError("offerSelf: flipped T", top, this, left)

    when (Precedence(this, tree)) {
        Lower -> when {
            bottom != null -> when (Intersection(bottom, tree)) {
                Container, Ahead, Behind -> {
                    // this |> tree |> bottom
                    tree.pop()
                    this bottom tree
                    bottom top tree
                }
                Same -> when (Precedence(bottom, tree)) {
                    Lower -> bottom offerSelf tree
                    Higher -> {
                        // this |> tree |> bottom
                        tree.pop()
                        this bottom tree
                        bottom top tree
                    }
                    Equal ->
                        throw TreeTakeoverException("offerSelf: takeover bottom", bottom, tree)
                }
                Overflow, Underflow, After, Front, Before, Back, Fragment, Start, End ->
                    throw CorruptedTreeError("offerSelfLower: tree outside its top", this, bottom)
            }
            else -> {
                // this |> tree [!bottom]
                tree.pop()
                this bottom tree
            }
        }
        Higher -> when {
            top != null -> when (Intersection(top, tree)) {
                Fragment, Start, End -> {
                    // top |> (tree |> this) -> right
                    tree.pop()
                    this top tree
                    top bottom tree
                    right?.left(tree)
                }
                Same -> when (Precedence(top, tree)) {
                    Higher -> top offerSelf tree
                    Lower -> {
                        // top |> (tree |> this) -> right
                        tree.pop()
                        this top tree
                        top bottom tree
                        right?.left(tree)
                    }
                    Equal ->
                        throw TreeTakeoverException("offerSelfHigher", top, tree)
                }
                Overflow, Underflow, After, Front, Before, Back, Container, Ahead, Behind ->
                    throw CorruptedTreeError("offerSelfHigher: tree outside its top", top, this)
            }
            else -> {
                // left -> (tree |> this) -> right [!top]
                tree.pop()
                this top tree
                left?.right(tree)
                right?.left(tree)
            }
        }
        Equal -> throw TreeTakeoverException("offerSelf", this, tree)
    }
}

/**
 * Assuming the relation between this and [tree] is [Child],
 * Offer the [tree] to the structure of this tree.
 *
 * [tree] will be [popped][ManagedTree.pop] from
 * its structure then put to the proper place in
 * the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [Child].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
internal infix fun <T> ManagedTree<T>.offerChild(tree: ManagedTree<T>) {
    require(Relation(this, tree) == Child) {
        "Illegal Relation: Expected: $Child Actual: ${Relation(this, tree)}"
    }

    val bottom = this.bottom

    when {
        bottom != null -> when (Intersection(bottom, tree)) {
            Same -> bottom offerSelf tree
            Fragment, Start, End -> bottom offerChild tree
            Before, Back -> {
                // this |> tree -> bottom
                tree.pop()
                this bottom tree
                bottom left tree
            }
            Front, After -> {
                var newLeft: ManagedTree<T> = bottom
                var newBottom: ManagedTree<T>? = null
                var newRight: ManagedTree<T>? = null

                for (it in generateSequence(bottom.right) { it.right })
                    when (Intersection(it, tree)) {
                        Same -> return it offerSelf tree
                        Fragment, Start, End -> return it offerChild tree
                        Front, After -> newLeft = it
                        Container, Ahead, Behind -> newBottom = newBottom ?: it
                        Back, Before -> newRight = it
                        Overflow, Underflow ->
                            throw TreeClashException("offerChild: clash with child", it, tree)
                    }

                // newLeft -> (tree |> newBottom) -> newRight
                tree.pop()
                newLeft right tree
                newBottom?.top(tree)
                newRight?.left(tree)
            }
            Container, Ahead, Behind -> {
                val newRight = generateSequence(bottom.right) { it.right }.firstOrNull {
                    when (Dominance(it, tree)) {
                        Contain -> false
                        None -> true
                        Share ->
                            throw TreeClashException("offerEnd: tree clash with child", it, tree)
                        Part, Exact ->
                            throw CorruptedTreeError("offerEnd: tree can fit in sibling", this, it, bottom)
                    }
                }

                // this |> (tree |> bottom) -> newRight
                tree.pop()
                this bottom tree
                bottom top tree
                newRight?.left(tree)
            }
            Underflow, Overflow ->
                throw TreeClashException("offerChild: clash with bottom", bottom, tree)
        }
        else -> {
            // this |> tree [!bottom]
            tree.pop()
            this bottom tree
        }
    }
}

/**
 * Assuming the relation between this and [tree] is [Parent],
 * Offer the [tree] to the structure of this tree.
 *
 * [tree] will be [popped][ManagedTree.pop] from
 * its structure then put to the proper place in
 * the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [Parent].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
internal infix fun <T> ManagedTree<T>.offerParent(tree: ManagedTree<T>) {
    require(Relation(this, tree) == Parent) {
        "Illegal Relation: Expected: $Parent Actual: ${Relation(this, tree)}"
    }

    val parent = this.parent

    when {
        parent != null -> when (Intersection(parent, tree)) {
            Same -> parent offerSelf tree
            Container, Ahead, Behind -> parent offerParent tree
            Fragment, Start, End -> parent offerChild tree
            Overflow, Underflow ->
                throw TreeClashException("offerParent: clash with top", parent, tree)
            Back, Before, Front, After ->
                throw CorruptedTreeError("offerParent: tree outside its parent", parent, this)
        }
        else -> {
            val newBottom = generateSequence(this) { it.left }.last {
                when (Dominance(it, tree)) {
                    Contain -> true
                    None -> false
                    Share ->
                        throw TreeClashException("offerParent: clash with sibling", it, tree)
                    Part, Exact ->
                        throw CorruptedTreeError("offerParent: tree can fit in sibling", it, this)
                }
            }
            val newLeft = generateSequence(left) { it.left }.firstOrNull {
                when (Dominance(it, tree)) {
                    Contain -> false
                    None -> true
                    Share ->
                        throw TreeClashException("offerParent: clash with sibling", it, tree)
                    Part, Exact ->
                        throw CorruptedTreeError("offerParent: tree can fit in sibling", it, this)
                }
            }
            val newRight = generateSequence(right) { it.right }.firstOrNull {
                when (Dominance(it, tree)) {
                    Contain -> false
                    None -> true
                    Share ->
                        throw TreeClashException("offerParent: clash with sibling", it, tree)
                    Part, Exact ->
                        throw CorruptedTreeError("offerParent: tree can fit in sibling", it, this)
                }
            }

            // newLeft -> (tree |> newBottom) -> newRight [!top]
            tree.pop()
            newBottom top tree
            newLeft?.right(tree)
            newRight?.left(tree)
        }
    }
}

/**
 * Assuming the relation between this and [tree] is [Previous],
 * Offer the [tree] to the structure of this tree.
 *
 * [tree] will be [popped][ManagedTree.pop] from
 * its structure then put to the proper place in
 * the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [Previous].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
internal infix fun <T> ManagedTree<T>.offerPrevious(tree: ManagedTree<T>) {
    require(Relation(this, tree) == Previous) {
        "Illegal Relation: Expected: $Previous Actual: ${Relation(this, tree)}"
    }

    val top = this.top
    val left = this.left

    if (top != null && left != null)
        throw CorruptedTreeError("offerPrevious: flipped T", top, this, left)

    when {
        top != null -> when (Intersection(top, tree)) {
            Fragment, Start -> top offerChild tree
            Back, Before -> top offerPrevious tree
            Overflow, Underflow ->
                throw TreeClashException("offerPrevious: clash with top", top, tree)
            Same, Container, Ahead, Behind, After, Front, End ->
                throw CorruptedTreeError("offerPrevious: tree outside its top", top, this)
        }
        left != null -> when (Intersection(left, tree)) {
            Same -> left offerSelf tree
            Container, Ahead, Behind -> left offerParent tree
            Fragment, Start, End -> left offerChild tree
            Back, Before -> left offerPrevious tree
            After, Front -> {
                // left -> tree -> this [!top]
                tree.pop()
                this left tree
                left right tree
            }
            Overflow, Underflow ->
                throw TreeClashException("offerPrevious: clash with left", left, tree)
        }
        else -> {
            // tree -> this [!top !left]
            tree.pop()
            this left tree
        }
    }
}

/**
 * Assuming the relation between this and [tree] is [Next],
 * Offer the [tree] to the structure of this tree.
 *
 * [tree] will be [popped][ManagedTree.pop] from
 * its structure then put to the proper place in
 * the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [Next].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
internal infix fun <T> ManagedTree<T>.offerNext(tree: ManagedTree<T>) {
    require(Relation(this, tree) == Next) {
        "Illegal Relation: Expected: $Next Actual: ${Relation(this, tree)}"
    }

    val top = this.top
    val right = this.right

    when {
        top != null -> when (Intersection(top, tree)) {
            Fragment, End -> top offerChild tree
            Front, After -> top offerNext tree
            Overflow, Underflow ->
                throw TreeClashException("offerNext: clash with top", top, tree)
            Same, Container, Ahead, Behind, Before, Back, Start ->
                throw CorruptedTreeError("offerNext: tree outside its top", top, this)
        }
        right != null -> when (Intersection(right, tree)) {
            Same -> right offerSelf tree
            Container, Ahead, Behind -> right offerParent tree
            Fragment, Start, End -> right offerChild tree
            Front, After -> right offerNext tree
            Back, Before -> {
                // this -> tree -> right [!top]
                tree.pop()
                this right tree
                right left tree
            }
            Overflow, Underflow ->
                throw TreeClashException("offerNext: clash with right", right, tree)
        }
        else -> {
            // this -> tree [!top !right]
            tree.pop()
            this right tree
        }
    }
}

//

/**
 * Offer the whole structure of the given [tree].
 */
@ExperimentalTreeApi
infix fun <T> ManagedTree<T>.offerWhole(tree: ManagedTree<T>) {
    tree.collect().toList().forEach { this offer it }
}

//

/**
 * Offer all the trees in this iterable into a
 * single structure.
 *
 * All trees in the iterable WILL be [popped][ManagedTree.pop].
 */
@ExperimentalTreeApi
fun <T> Iterable<ManagedTree<T>>.joinOffer() {
    val iterator = this.iterator()
    if (!iterator.hasNext())
        return
    val first = iterator.next()
    first.pop()
    while (iterator.hasNext()) {
        first offer iterator.next()
    }
}

//

/**
 * Remove the children of this tree.
 *
 * This method will result to the following structures:
 * - The original structure.
 * - A structure containing the children of this tree.
 *
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to clear this tree.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this")
fun <T> ManagedTree<T>.clear() {
    this bottom null
}

/**
 * Remove this tree and only this tree from its structure.
 *
 * This method will result to the following structures:
 * - The original structure.
 * - A structure containing only this tree.
 *
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to pop this tree.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this")
fun <T> ManagedTree<T>.pop() {
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
 * This method will result to the following structures:
 * - The original structure.
 * - A structure containing this tree and its children.
 *
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to remove this tree.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this")
fun <T> ManagedTree<T>.remove() {
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
