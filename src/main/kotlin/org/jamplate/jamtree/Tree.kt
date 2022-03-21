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

import org.jamplate.jamtree.Dominance.*
import org.jamplate.jamtree.Intersection.*
import org.jamplate.jamtree.Precedence.*
import org.jamplate.jamtree.Relation.*
import org.jamplate.jamtree.Side.*
import org.jetbrains.annotations.Contract
import java.io.Serializable

/**
 * A tree is a point in a background structure of values that hold the variables of component.
 *
 * The background structure is working like magic and the user cannot interact directly
 * with it.
 *
 * A tree structure can only be modified throw the [offer] method on any
 * tree in it. Any tree that get [offered][offer] into a structure of another
 * tree will be removed from its previous structure.
 *
 * A structure cannot have any clashing trees or trees that does not fit their
 * parent or trees that breaks the order of their neighboring trees in it and all
 * the trees in it will be organized implicitly.
 *
 * The tree class is not thead safe and multiple threads modifying the same tree structure
 * can make that structure corrupted. The corruption due to two thread modifying the same
 * tree structure is undefined. Aside from that, two or more threads just reading the tree
 * structure is totally fine. Also, one thread modifying a tree structure while the other
 * threads just reading it will not corrupt the structure and will only confuse the other
 * threads because random trees will be moved around while those threads are reading.
 *
 *  Two identical trees in one structure is allowed only if the two trees does not have the
 * same [weight]. The tree with the higher weight can fit inside the
 * tree with the lower weight.
 *
 * @author LSafer
 * @since 0.2.0 ~2020.12.25
 */
data class Tree<T>(
    /**
     * The payload of this tree.
     */
    var value: T,
    /**
     * The first index of this tree.
     *
     * @since 0.4.0 ~2022.03.20
     */
    val offset: Int = 0,
    /**
     * The length of this tree.
     *
     * @since 0.4.0 ~2022.03.20
     */
    val length: Int = 0,
    /**
     * The weight of this tree.
     */
    val weight: Int = 0
) : Iterable<Tree<T>>, Serializable {
    /**
     * The tree above this tree. Must be null when [left] isn't.
     */
    var top: Tree<T>? = null; internal set

    /**
     * The tree to the left of this tree. Must be null when [top] isn't.
     */
    var left: Tree<T>? = null; internal set

    /**
     * The tree to the right of this tree.
     */
    var right: Tree<T>? = null; internal set

    /**
     * The tree below this tree.
     */
    var bottom: Tree<T>? = null; internal set

    override fun iterator(): Iterator<Tree<T>> =
        generateSequence(bottom) { it.next }.iterator()

    companion object {
        private const val serialVersionUID: Long = 6655340677472661213L
    }
}

//

/**
 * One past the last index of this range.
 *
 * @since 0.4.0 ~2022.03.20
 */
val Tree<*>.terminal: Int get() = this.offset + this.length

//

/**
 * The tree before this tree in the parent.
 */
val <T> Tree<T>.previous: Tree<T>? get() = this.left

/**
 * The tree after this tree in this parent.
 */
val <T> Tree<T>.next: Tree<T>? get() = this.right

/**
 * The tree containing this tree.
 */
val <T> Tree<T>.parent: Tree<T>? get() = this.left?.parent ?: this.top

/**
 * The first tree contained in this tree.
 */
val <T> Tree<T>.child: Tree<T>? get() = this.bottom

//

/**
 * The first tree in the parent of this tree.
 */
val <T> Tree<T>.head: Tree<T> get() = this.previous?.head ?: this

/**
 * The last tree in the parent of this tree.
 */
val <T> Tree<T>.tail: Tree<T> get() = this.next?.tail ?: this

/**
 * The root tree of this tree.
 */
val <T> Tree<T>.root: Tree<T> get() = this.parent?.root ?: this

//

/**
 * Return a sequence of all the trees related to
 * this tree excluding this tree.
 *
 * @return a sequence of the trees related to this.
 * @since 0.4.0 ~2022.03.18
 */
@Contract(pure = true)
fun <T> Tree<T>.hierarchy(): Sequence<Tree<T>> =
    this.leftHierarchy() +
            this.topHierarchy() +
            this.rightHierarchy() +
            this.bottomHierarchy()

/**
 * Return a sequence of all trees related to the
 * `bottom` of this tree excluding this tree.
 *
 * @return a sequence of all trees to the bottom of this tree.
 * @since 0.4.0 ~2022.03.18
 */
@Contract(pure = true)
fun <T> Tree<T>.bottomHierarchy(): Sequence<Tree<T>> =
    generateSequence(this.bottom) { it.bottom }
        .flatMap { sequenceOf(it) + it.rightHierarchy() }

/**
 * Return a sequence of all trees related to the
 * `top` of this tree excluding this tree.
 *
 * @return a sequence of all trees to the top of this tree.
 * @since 0.4.0 ~2022.03.18
 */
@Contract(pure = true)
fun <T> Tree<T>.topHierarchy(): Sequence<Tree<T>> =
    generateSequence(this.top) { it.top }
        .flatMap { sequenceOf(it) + it.leftHierarchy() + it.rightHierarchy() }

/**
 * Return a sequence of all trees related to the
 * `left` of this tree excluding this tree.
 *
 * @return a sequence of all trees to the left of this tree.
 * @since 0.4.0 ~2022.03.18
 */
@Contract(pure = true)
fun <T> Tree<T>.leftHierarchy(): Sequence<Tree<T>> =
    generateSequence(this.left) { it.left }
        .flatMap { sequenceOf(it) + it.bottomHierarchy() + it.topHierarchy() }

/**
 * Return a sequence of all trees related to the
 * `right` of this tree excluding this tree.
 *
 * @return a sequence of all trees to the right of this tree.
 * @since 0.4.0 ~2022.03.18
 */
@Contract(pure = true)
fun <T> Tree<T>.rightHierarchy(): Sequence<Tree<T>> =
    generateSequence(this.right) { it.right }
        .flatMap { sequenceOf(it) + it.bottomHierarchy() }

//

/**
 * Compute the dominance between [tree] and the [other] tree.
 *
 * @param tree the first tree.
 * @param other the second tree.
 * @return the dominance of the second tree over the first tree.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeDominance(tree: Tree<*>, other: Tree<*>): Dominance =
    computeDominance(tree.offset, tree.terminal, other.offset, other.terminal)

/**
 * Compute the relation between [tree] and the [other] tree.
 *
 * @param tree the first tree.
 * @param other the second tree.
 * @return the relation of the second tree over the first tree.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeRelation(tree: Tree<*>, other: Tree<*>): Relation =
    computeRelation(tree.offset, tree.terminal, other.offset, other.terminal)

/**
 * Compute the intersection between [tree] and the [other] tree.
 *
 * @param tree the first tree.
 * @param other the second tree.
 * @return the intersection of the second tree over the first tree.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeIntersection(tree: Tree<*>, other: Tree<*>): Intersection =
    computeIntersection(tree.offset, tree.terminal, other.offset, other.terminal)

/**
 * Compute the precedence between [tree] and the [other] tree.
 *
 * @param tree the first tree.
 * @param other the second tree.
 * @return the precedence of the second tree over the first tree.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computePrecedence(tree: Tree<*>, other: Tree<*>): Precedence =
    computePrecedence(tree.weight, other.weight)

//

/**
 * Compute the dominance between [tree] and a tree with [s] and [e].
 *
 * @param tree the first tree.
 * @param s the first index of the second area.
 * @param e one past the last index of the second area.
 * @return the dominance of [s] and [e] over the first tree.
 * @throws IllegalArgumentException if `s` is not in the range `[0, e]`.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeDominance(tree: Tree<*>, s: Int, e: Int): Dominance =
    computeDominance(tree.offset, tree.terminal, s, e)

/**
 * Compute the relation between [tree] and a tree with [s] and [e].
 *
 * @param tree the first tree.
 * @param s the first index of the second area.
 * @param e one past the last index of the second area.
 * @return the relation of [s] and [e] over the first tree.
 * @throws IllegalArgumentException if `s` is not in the range `[0, e]`.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeRelation(tree: Tree<*>, s: Int, e: Int): Relation =
    computeRelation(tree.offset, tree.terminal, s, e)

/**
 * Compute the intersection between [tree] and a tree with [s] and [e].
 *
 * @param tree the first tree.
 * @param s the first index of the second area.
 * @param e one past the last index of the second area.
 * @return the intersection of [s] and [e] over the first tree.
 * @throws IllegalArgumentException if `s` is not in the range `[0, e]`.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computeIntersection(tree: Tree<*>, s: Int, e: Int): Intersection =
    computeIntersection(tree.offset, tree.terminal, s, e)

/**
 * Compute the precedence between [tree] and a tree with [w].
 *
 * @param tree the first tree.
 * @param w the weight of the second area.
 * @return the precedence of [w] over the first tree.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(pure = true)
fun computePrecedence(tree: Tree<*>, w: Int): Precedence =
    computePrecedence(tree.weight, w)

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
@Contract(mutates = "this,param")
internal infix fun <T> Tree<T>.bottom(tree: Tree<T>?) {
    this.bottom?.top = null
    this.bottom = tree
    tree?.top?.bottom = null
    tree?.top = this
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
@Contract(mutates = "this,param")
internal infix fun <T> Tree<T>.top(tree: Tree<T>?) {
    this.top?.bottom = null
    this.top = tree
    tree?.bottom?.top = null
    tree?.bottom = this
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
@Contract(mutates = "this,param")
internal infix fun <T> Tree<T>.left(tree: Tree<T>?) {
    this.left?.right = null
    this.left = tree
    tree?.right?.left = null
    tree?.right = this
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
@Contract(mutates = "this,param")
internal infix fun <T> Tree<T>.right(tree: Tree<T>?) {
    this.right?.left = null
    this.right = tree
    tree?.left?.right = null
    tree?.left = this
}

//

/**
 * Offer the [tree] to the structure of this tree. [tree] will be removed from
 * its structure then put to the proper place in the structure of this tree.
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
infix fun <T> Tree<T>.offer(tree: Tree<T>) {
    when (computeRelation(this, tree)) {
        PARENT -> this offerParent tree
        SELF -> this offerSelf tree
        CHILD -> this offerChild tree
        NEXT -> this offerNext tree
        PREVIOUS -> this offerPrevious tree
        CLASH -> treeClash("offer", this, tree)
    }
}

/**
 * Assuming the relation between this and [tree] is [SELF],
 * Offer the [tree] to the structure of this tree. [tree] will be removed from
 * its structure then put to the proper place in the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [SELF].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
infix fun <T> Tree<T>.offerSelf(tree: Tree<T>) {
    require(computeRelation(this, tree) == SELF) {
        "Illegal Relation: Expected: $SELF Actual: ${computeRelation(this, tree)}"
    }

    val top = this.top
    val left = this.left
    val right = this.right
    val bottom = this.bottom

    if (top != null && left != null)
        corruptedTree("offerSelf: flipped T", top, this, left)

    when (computePrecedence(this, tree)) {
        LOWER -> when {
            bottom != null -> when (computeIntersection(bottom, tree)) {
                CONTAINER, AHEAD, BEHIND -> {
                    // this |> tree |> bottom
                    tree.pop()
                    this bottom tree
                    bottom top tree
                }
                SAME -> when (computePrecedence(bottom, tree)) {
                    LOWER -> bottom offerSelf tree
                    HIGHER -> {
                        // this |> tree |> bottom
                        tree.pop()
                        this bottom tree
                        bottom top tree
                    }
                    EQUAL ->
                        treeTakeover("offerSelf: takeover bottom", bottom, tree)
                }
                OVERFLOW, UNDERFLOW, AFTER, FRONT, BEFORE, BACK, FRAGMENT, START, END ->
                    corruptedTree("offerSelfLower: tree outside its top", this, bottom)
            }
            else -> {
                // this |> tree [!bottom]
                tree.pop()
                this bottom tree
            }
        }
        HIGHER -> when {
            top != null -> when (computeIntersection(top, tree)) {
                FRAGMENT, START, END -> {
                    // top |> (tree |> this) -> right
                    tree.pop()
                    this top tree
                    top bottom tree
                    right?.left(tree)
                }
                SAME -> when (computePrecedence(top, tree)) {
                    HIGHER -> top offerSelf tree
                    LOWER -> {
                        // top |> (tree |> this) -> right
                        tree.pop()
                        this top tree
                        top bottom tree
                        right?.left(tree)
                    }
                    EQUAL ->
                        treeTakeover("offerSelfHigher", top, tree)
                }
                OVERFLOW, UNDERFLOW, AFTER, FRONT, BEFORE, BACK, CONTAINER, AHEAD, BEHIND ->
                    corruptedTree("offerSelfHigher: tree outside its top", top, this)
            }
            else -> {
                // left -> (tree |> this) -> right [!top]
                tree.pop()
                this top tree
                left?.right(tree)
                right?.left(tree)
            }
        }
        EQUAL -> treeTakeover("offerSelf", this, tree)
    }
}

/**
 * Assuming the relation between this and [tree] is [CHILD],
 * Offer the [tree] to the structure of this tree. [tree] will be removed from
 * its structure then put to the proper place in the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [CHILD].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
infix fun <T> Tree<T>.offerChild(tree: Tree<T>) {
    require(computeRelation(this, tree) == CHILD) {
        "Illegal Relation: Expected: $CHILD Actual: ${computeRelation(this, tree)}"
    }

    val bottom = this.bottom

    when {
        bottom != null -> when (computeIntersection(bottom, tree)) {
            SAME -> bottom offerSelf tree
            FRAGMENT, START, END -> bottom offerChild tree
            BEFORE, BACK -> {
                // this |> tree -> bottom
                tree.pop()
                this bottom tree
                bottom left tree
            }
            FRONT, AFTER -> {
                var newLeft: Tree<T> = bottom
                var newBottom: Tree<T>? = null
                var newRight: Tree<T>? = null

                for (it in generateSequence(bottom.right) { it.right })
                    when (computeIntersection(it, tree)) {
                        SAME -> return it offerSelf tree
                        FRAGMENT, START, END -> return it offerChild tree
                        FRONT, AFTER -> newLeft = it
                        CONTAINER, AHEAD, BEHIND -> newBottom = newBottom ?: it
                        BACK, BEFORE -> newRight = it
                        OVERFLOW, UNDERFLOW ->
                            treeClash("offerChild: clash with child", it, tree)
                    }

                // newLeft -> (tree |> newBottom) -> newRight
                tree.pop()
                newLeft right tree
                newBottom?.top(tree)
                newRight?.left(tree)
            }
            CONTAINER, AHEAD, BEHIND -> {
                val newRight = generateSequence(bottom.right) { it.right }.firstOrNull {
                    when (computeDominance(it, tree)) {
                        CONTAIN -> false
                        NONE -> true
                        SHARE ->
                            treeClash("offerEnd: tree clash with child", it, tree)
                        PART, EXACT ->
                            corruptedTree("offerEnd: tree can fit in sibling", this, it, bottom)
                    }
                }

                // this |> (tree |> bottom) -> newRight
                tree.pop()
                this bottom tree
                bottom top tree
                newRight?.left(tree)
            }
            UNDERFLOW, OVERFLOW ->
                treeClash("offerChild: clash with bottom", bottom, tree)
        }
        else -> {
            // this |> tree [!bottom]
            tree.pop()
            this bottom tree
        }
    }
}

/**
 * Assuming the relation between this and [tree] is [PARENT],
 * Offer the [tree] to the structure of this tree. [tree] will be removed from
 * its structure then put to the proper place in the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [PARENT].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
infix fun <T> Tree<T>.offerParent(tree: Tree<T>) {
    require(computeRelation(this, tree) == PARENT) {
        "Illegal Relation: Expected: $PARENT Actual: ${computeRelation(this, tree)}"
    }

    val parent = this.parent

    when {
        parent != null -> when (computeIntersection(parent, tree)) {
            SAME -> parent offerSelf tree
            CONTAINER, AHEAD, BEHIND -> parent offerParent tree
            FRAGMENT, START, END -> parent offerChild tree
            OVERFLOW, UNDERFLOW ->
                treeClash("offerParent: clash with top", parent, tree)
            BACK, BEFORE, FRONT, AFTER ->
                corruptedTree("offerParent: tree outside its parent", parent, this)
        }
        else -> {
            val newBottom = generateSequence(this) { it.left }.last {
                when (computeDominance(it, tree)) {
                    CONTAIN -> true
                    NONE -> false
                    SHARE ->
                        treeClash("offerParent: clash with sibling", it, tree)
                    PART, EXACT ->
                        corruptedTree("offerParent: tree can fit in sibling", it, this)
                }
            }
            val newLeft = generateSequence(this.left) { it.left }.firstOrNull {
                when (computeDominance(it, tree)) {
                    CONTAIN -> false
                    NONE -> true
                    SHARE ->
                        treeClash("offerParent: clash with sibling", it, tree)
                    PART, EXACT ->
                        corruptedTree("offerParent: tree can fit in sibling", it, this)
                }
            }
            val newRight = generateSequence(this.right) { it.right }.firstOrNull {
                when (computeDominance(it, tree)) {
                    CONTAIN -> false
                    NONE -> true
                    SHARE ->
                        treeClash("offerParent: clash with sibling", it, tree)
                    PART, EXACT ->
                        corruptedTree("offerParent: tree can fit in sibling", it, this)
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
 * Assuming the relation between this and [tree] is [PREVIOUS],
 * Offer the [tree] to the structure of this tree. [tree] will be removed from
 * its structure then put to the proper place in the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [PREVIOUS].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
infix fun <T> Tree<T>.offerPrevious(tree: Tree<T>) {
    require(computeRelation(this, tree) == PREVIOUS) {
        "Illegal Relation: Expected: $PREVIOUS Actual: ${computeRelation(this, tree)}"
    }

    val top = this.top
    val left = this.left

    if (top != null && left != null)
        corruptedTree("offerPrevious: flipped T", top, this, left)

    when {
        top != null -> when (computeIntersection(top, tree)) {
            FRAGMENT, START -> top offerChild tree
            BACK, BEFORE -> top offerPrevious tree
            OVERFLOW, UNDERFLOW ->
                treeClash("offerPrevious: clash with top", top, tree)
            SAME, CONTAINER, AHEAD, BEHIND, AFTER, FRONT, END ->
                corruptedTree("offerPrevious: tree outside its top", top, this)
        }
        left != null -> when (computeIntersection(left, tree)) {
            SAME -> left offerSelf tree
            CONTAINER, AHEAD, BEHIND -> left offerParent tree
            FRAGMENT, START, END -> left offerChild tree
            BACK, BEFORE -> left offerPrevious tree
            AFTER, FRONT -> {
                // left -> tree -> this [!top]
                tree.pop()
                this left tree
                left right tree
            }
            OVERFLOW, UNDERFLOW ->
                treeClash("offerPrevious: clash with left", left, tree)
        }
        else -> {
            // tree -> this [!top !left]
            tree.pop()
            this left tree
        }
    }
}

/**
 * Assuming the relation between this and [tree] is [NEXT],
 * Offer the [tree] to the structure of this tree. [tree] will be removed from
 * its structure then put to the proper place in the structure of this tree.
 *
 * If failed to insert the given [tree] because of an [IllegalTreeException] or
 * [CorruptedTreeError], then the method will exit without anything changed.
 *
 * @param tree the tree to be inserted.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [NEXT].
 * @throws IllegalTreeException if [tree] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to insert [tree].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this,param")
infix fun <T> Tree<T>.offerNext(tree: Tree<T>) {
    require(computeRelation(this, tree) == NEXT) {
        "Illegal Relation: Expected: $NEXT Actual: ${computeRelation(this, tree)}"
    }

    val top = this.top
    val right = this.right

    when {
        top != null -> when (computeIntersection(top, tree)) {
            FRAGMENT, END -> top offerChild tree
            FRONT, AFTER -> top offerNext tree
            OVERFLOW, UNDERFLOW ->
                treeClash("offerNext: clash with top", top, tree)
            SAME, CONTAINER, AHEAD, BEHIND, BEFORE, BACK, START ->
                corruptedTree("offerNext: tree outside its top", top, this)
        }
        right != null -> when (computeIntersection(right, tree)) {
            SAME -> right offerSelf tree
            CONTAINER, AHEAD, BEHIND -> right offerParent tree
            FRAGMENT, START, END -> right offerChild tree
            FRONT, AFTER -> right offerNext tree
            BACK, BEFORE -> {
                // this -> tree -> right [!top]
                tree.pop()
                this right tree
                right left tree
            }
            OVERFLOW, UNDERFLOW ->
                treeClash("offerNext: clash with right", right, tree)
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
 * Find the neighboring trees of [tree] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If [tree] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param tree the tree to compute
 * @return the mappings of the neighboring trees.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->new", pure = true)
infix fun Tree<*>.compute(tree: Tree<*>): Map<Side?, Tree<*>?> =
    compute(tree.offset, tree.terminal, tree.weight)

/**
 * Assuming the relation between this and [tree] is [SELF],
 * Find the neighboring trees of [tree] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If [tree] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param tree the tree to compute
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [SELF].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->new", pure = true)
infix fun Tree<*>.computeSelf(tree: Tree<*>): Map<Side?, Tree<*>?> =
    computeSelf(tree.offset, tree.terminal, tree.weight)

/**
 * Assuming the relation between this and [tree] is [CHILD],
 * Find the neighboring trees of [tree] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If [tree] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param tree the tree to compute
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [CHILD].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->new", pure = true)
infix fun Tree<*>.computeChild(tree: Tree<*>): Map<Side?, Tree<*>?> =
    computeChild(tree.offset, tree.terminal, tree.weight)

/**
 * Assuming the relation between this and [tree] is [PARENT],
 * Find the neighboring trees of [tree] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If [tree] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param tree the tree to compute
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [PARENT].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->new", pure = true)
infix fun Tree<*>.computeParent(tree: Tree<*>): Map<Side?, Tree<*>?> =
    computeParent(tree.offset, tree.terminal, tree.weight)

/**
 * Assuming the relation between this and [tree] is [PREVIOUS],
 * Find the neighboring trees of [tree] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If [tree] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param tree the tree to compute
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [PREVIOUS].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->new", pure = true)
infix fun Tree<*>.computePrevious(tree: Tree<*>): Map<Side?, Tree<*>?> =
    computePrevious(tree.offset, tree.terminal, tree.weight)

/**
 * Assuming the relation between this and [tree] is [NEXT],
 * Find the neighboring trees of [tree] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If [tree] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param tree the tree to compute
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [tree] is not [NEXT].
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->new", pure = true)
infix fun Tree<*>.computeNext(tree: Tree<*>): Map<Side?, Tree<*>?> =
    computeNext(tree.offset, tree.terminal, tree.weight)

//

/**
 * Find the neighboring trees of a tree with [i], [j] and [k] in the structure of this tree.
 * <br><br>
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If the [i], [j] and [k] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param i the first index of that tree.
 * @param j one past the last index of that tree.
 * @param k the weight of that tree.
 * @return the mappings of the neighboring trees.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_,_,_->new", pure = true)
fun Tree<*>.compute(i: Int, j: Int, k: Int): Map<Side?, Tree<*>?> {
    return when (computeRelation(this, i, j)) {
        PARENT -> this.computeParent(i, j, k)
        SELF -> this.computeSelf(i, j, k)
        CHILD -> this.computeChild(i, j, k)
        NEXT -> this.computeNext(i, j, k)
        PREVIOUS -> this.computePrevious(i, j, k)
        CLASH -> mapOf(null to this)
    }
}

/**
 * Assuming the relation between this and [i], [j] and [k] is [SELF],
 * Find the neighboring trees of a tree with [i], [j] and [k] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If the [i], [j] and [k] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param i the first index of that tree.
 * @param j one past the last index of that tree.
 * @param k the weight of that tree.
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [i], [j] and [k] is not [SELF].
 * @since 0.4.0 ~2022.03.12
 */
@Suppress("LiftReturnOrAssignment")
@Contract(value = "_,_,_->new", pure = true)
fun Tree<*>.computeSelf(i: Int, j: Int, k: Int): Map<Side?, Tree<*>?> {
    require(computeRelation(this, i, j) == SELF) {
        "Illegal Relation: Expected: $SELF Actual: ${computeRelation(this, i, j)}"
    }

    val top = this.top
    val left = this.left
    val right = this.right
    val bottom = this.bottom

    if (top != null && left != null)
        return emptyMap()

    when (computePrecedence(this, k)) {
        LOWER -> when {
            bottom != null -> when (computeIntersection(bottom, i, j)) {
                CONTAINER, AHEAD, BEHIND -> {
                    // this |> tree |> bottom
                    return mapOf(
                        TOP to this,
                        BOTTOM to bottom
                    )
                }
                SAME -> when (computePrecedence(bottom, k)) {
                    LOWER -> return bottom.computeSelf(i, j, k)
                    HIGHER -> {
                        // this |> tree |> bottom
                        return mapOf(
                            TOP to this,
                            BOTTOM to bottom
                        )
                    }
                    EQUAL -> return mapOf(null to bottom)
                }
                OVERFLOW, UNDERFLOW, AFTER, FRONT, BEFORE, BACK, FRAGMENT, START, END ->
                    return emptyMap()
            }
            else -> {
                // this |> tree [!bottom]
                return mapOf(
                    TOP to this
                )
            }
        }
        HIGHER -> when {
            top != null -> when (computeIntersection(top, i, j)) {
                FRAGMENT, START, END -> {
                    // top |> (tree |> this) -> right
                    return mapOf(
                        RIGHT to right,
                        TOP to top,
                        BOTTOM to this
                    )
                }
                SAME -> when (computePrecedence(top, k)) {
                    HIGHER -> return top.computeSelf(i, j, k)
                    LOWER -> {
                        // top |> (tree |> this) -> right
                        return mapOf(
                            RIGHT to right,
                            TOP to top,
                            BOTTOM to this
                        )
                    }
                    EQUAL -> return mapOf(null to top)
                }
                OVERFLOW, UNDERFLOW, AFTER, FRONT, BEFORE, BACK, CONTAINER, AHEAD, BEHIND ->
                    return emptyMap()
            }
            else -> {
                // left -> (tree |> this) -> right [!top]
                return mapOf(
                    RIGHT to right,
                    LEFT to left,
                    BOTTOM to this
                )
            }
        }
        EQUAL -> return mapOf(null to this)
    }
}

/**
 * Assuming the relation between this and [i], [j] and [k] is [CHILD],
 * Find the neighboring trees of a tree with [i], [j] and [k] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If the [i], [j] and [k] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param i the first index of that tree.
 * @param j one past the last index of that tree.
 * @param k the weight of that tree.
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [i], [j] and [k] is not [CHILD].
 * @since 0.4.0 ~2022.03.12
 */
@Suppress("LiftReturnOrAssignment")
@Contract(value = "_,_,_->new", pure = true)
fun Tree<*>.computeChild(i: Int, j: Int, k: Int): Map<Side?, Tree<*>?> {
    require(computeRelation(this, i, j) == CHILD) {
        "Illegal Relation: Expected: $CHILD Actual: ${computeRelation(this, i, j)}"
    }

    val bottom = this.bottom

    when {
        bottom != null -> when (computeIntersection(bottom, i, j)) {
            SAME -> return bottom.computeSelf(i, j, k)
            FRAGMENT, START, END -> return bottom.computeChild(i, j, k)
            BEFORE, BACK -> {
                // this |> tree -> bottom
                return mapOf(
                    RIGHT to bottom,
                    TOP to this
                )
            }
            FRONT, AFTER -> {
                var newLeft: Tree<*> = bottom
                var newBottom: Tree<*>? = null
                var newRight: Tree<*>? = null

                for (it in generateSequence(bottom.right) { it.right })
                    when (computeIntersection(it, i, j)) {
                        SAME -> return it.computeSelf(i, j, k)
                        FRAGMENT, START, END -> return it.computeChild(i, j, k)
                        FRONT, AFTER -> newLeft = it
                        CONTAINER, AHEAD, BEHIND -> newBottom = newBottom ?: it
                        BACK, BEFORE -> newRight = it
                        OVERFLOW, UNDERFLOW -> return mapOf(null to it)
                    }

                // newLeft -> (tree |> newBottom) -> newRight
                return mapOf(
                    RIGHT to newRight,
                    BOTTOM to newBottom,
                    LEFT to newLeft
                )
            }
            CONTAINER, AHEAD, BEHIND -> {
                val newRight = generateSequence(bottom.right) { it.right }.firstOrNull {
                    when (computeDominance(it, i, j)) {
                        CONTAIN -> false
                        NONE -> true
                        SHARE -> return mapOf(null to it)
                        PART, EXACT -> return emptyMap()
                    }
                }

                // this |> (tree |> bottom) -> newRight
                return mapOf(
                    RIGHT to newRight,
                    BOTTOM to bottom,
                    TOP to this
                )
            }
            UNDERFLOW, OVERFLOW -> return mapOf(null to bottom)
        }
        else -> {
            // this |> tree [!bottom]
            return mapOf(
                TOP to this
            )
        }
    }
}

/**
 * Assuming the relation between this and [i], [j] and [k] is [PARENT],
 * Find the neighboring trees of a tree with [i], [j] and [k] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If the [i], [j] and [k] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param i the first index of that tree.
 * @param j one past the last index of that tree.
 * @param k the weight of that tree.
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [i], [j] and [k] is not [PARENT].
 * @since 0.4.0 ~2022.03.12
 */
@Suppress("LiftReturnOrAssignment")
@Contract(value = "_,_,_->new", pure = true)
fun Tree<*>.computeParent(i: Int, j: Int, k: Int): Map<Side?, Tree<*>?> {
    require(computeRelation(this, i, j) == PARENT) {
        "Illegal Relation: Expected: $PARENT Actual: ${computeRelation(this, i, j)}"
    }

    val parent = this.parent

    when {
        parent != null -> when (computeIntersection(parent, i, j)) {
            SAME -> return parent.computeSelf(i, j, k)
            CONTAINER, AHEAD, BEHIND -> return parent.computeParent(i, j, k)
            FRAGMENT, START, END -> return parent.computeChild(i, j, k)
            OVERFLOW, UNDERFLOW -> return mapOf(null to parent)
            BACK, BEFORE, FRONT, AFTER -> return emptyMap()
        }
        else -> {
            val newBottom = generateSequence(this) { it.left }.last {
                when (computeDominance(it, i, j)) {
                    CONTAIN -> true
                    NONE -> false
                    SHARE -> return mapOf(null to it)
                    PART, EXACT -> return emptyMap()
                }
            }
            val newLeft = generateSequence(this.left) { it.left }.firstOrNull {
                when (computeDominance(it, i, j)) {
                    CONTAIN -> false
                    NONE -> true
                    SHARE -> return mapOf(null to it)
                    PART, EXACT -> return emptyMap()
                }
            }
            val newRight = generateSequence(this.right) { it.right }.firstOrNull {
                when (computeDominance(it, i, j)) {
                    CONTAIN -> false
                    NONE -> true
                    SHARE -> return mapOf(null to it)
                    PART, EXACT -> return emptyMap()
                }
            }

            // newLeft -> (tree |> newBottom) -> newRight [!top]
            return mapOf(
                RIGHT to newRight,
                LEFT to newLeft,
                BOTTOM to newBottom
            )
        }
    }
}

/**
 * Assuming the relation between this and [i], [j] and [k] is [PREVIOUS],
 * Find the neighboring trees of a tree with [i], [j] and [k] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If the [i], [j] and [k] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param i the first index of that tree.
 * @param j one past the last index of that tree.
 * @param k the weight of that tree.
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [i], [j] and [k] is not [PREVIOUS].
 * @since 0.4.0 ~2022.03.12
 */
@Suppress("LiftReturnOrAssignment")
@Contract(value = "_,_,_->new", pure = true)
fun Tree<*>.computePrevious(i: Int, j: Int, k: Int): Map<Side?, Tree<*>?> {
    require(computeRelation(this, i, j) == PREVIOUS) {
        "Illegal Relation: Expected: $PREVIOUS Actual: ${computeRelation(this, i, j)}"
    }

    val top = this.top
    val left = this.left

    if (top != null && left != null)
        return emptyMap()

    when {
        top != null -> when (computeIntersection(top, i, j)) {
            FRAGMENT, START -> return top.computeChild(i, j, k)
            BACK, BEFORE -> return top.computePrevious(i, j, k)
            OVERFLOW, UNDERFLOW ->
                return mapOf(null to top)
            SAME, CONTAINER, AHEAD, BEHIND, AFTER, FRONT, END ->
                return emptyMap()
        }
        left != null -> when (computeIntersection(left, i, j)) {
            SAME -> return left.computeSelf(i, j, k)
            CONTAINER, AHEAD, BEHIND -> return left.computeParent(i, j, k)
            FRAGMENT, START, END -> return left.computeChild(i, j, k)
            BACK, BEFORE -> return left.computePrevious(i, j, k)
            AFTER, FRONT -> {
                // left -> tree -> this [!top]
                return mapOf(
                    LEFT to left,
                    RIGHT to this
                )
            }
            OVERFLOW, UNDERFLOW ->
                return mapOf(null to left)
        }
        else -> {
            // tree -> this [!top !left]
            return mapOf(
                RIGHT to this
            )
        }
    }
}

/**
 * Assuming the relation between this and [i], [j] and [k] is [NEXT],
 * Find the neighboring trees of a tree with [i], [j] and [k] in the structure of this tree.
 *
 * The following are the failure scenarios and what will be returned:
 * - If the structure of this tree is corrupted,
 *     An empty map will be returned.
 * - If the [i], [j] and [k] clashes with a tree,
 *     A null will be associated with that tree.
 *
 * @param i the first index of that tree.
 * @param j one past the last index of that tree.
 * @param k the weight of that tree.
 * @return the mappings of the neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [i], [j] and [k] is not [NEXT].
 * @since 0.4.0 ~2022.03.12
 */
@Suppress("LiftReturnOrAssignment")
@Contract(value = "_,_,_->new", pure = true)
fun Tree<*>.computeNext(i: Int, j: Int, k: Int): Map<Side?, Tree<*>?> {
    require(computeRelation(this, i, j) == NEXT) {
        "Illegal Relation: Expected: $NEXT Actual: ${computeRelation(this, i, j)}"
    }

    val top = this.top
    val right = this.right

    when {
        top != null -> when (computeIntersection(top, i, j)) {
            FRAGMENT, END -> return top.computeChild(i, j, k)
            FRONT, AFTER -> return top.computeNext(i, j, k)
            OVERFLOW, UNDERFLOW ->
                return mapOf(null to top)
            SAME, CONTAINER, AHEAD, BEHIND, BEFORE, BACK, START ->
                return emptyMap()
        }
        right != null -> when (computeIntersection(right, i, j)) {
            SAME -> return right.computeSelf(i, j, k)
            CONTAINER, AHEAD, BEHIND -> return right.computeParent(i, j, k)
            FRAGMENT, START, END -> return right.computeChild(i, j, k)
            FRONT, AFTER -> return right.computeNext(i, j, k)
            BACK, BEFORE -> {
                // this -> tree -> right [!top]
                return mapOf(
                    RIGHT to right,
                    LEFT to this
                )
            }
            OVERFLOW, UNDERFLOW ->
                return mapOf(null to right)
        }
        else -> {
            // this -> tree [!top !right]
            return mapOf(
                LEFT to this
            )
        }
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
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this")
fun <T> Tree<T>.clear() {
    this bottom null
}

/**
 * Remove this tree and only this tree from its structure.
 *
 * This method will result to the following structures:
 * - The original structure.
 * - A structure containing only this tree.
 *
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this")
fun <T> Tree<T>.pop() {
    val top = this.top
    val left = this.left
    val right = this.right
    val bottom = this.bottom

    if (top != null && left != null)
        corruptedTree("pop: flipped T", top, this, left)

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
 * @since 0.4.0 ~2022.03.12
 */
@Contract(mutates = "this")
fun <T> Tree<T>.remove() {
    val top = this.top
    val left = this.left
    val right = this.right

    if (top != null && left != null)
        corruptedTree("remove: flipped T", top, this, left)

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
