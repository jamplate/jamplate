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

/**
 * A tree is a node in a (almost) 2D plane
 * connected to other trees (in some way).
 *
 * Trees can be (but not limited to) an immutable
 * result buffer, a manually mutable buffer or an
 * automatically mutable buffer.
 * And can be used (also not limited to) as a
 * temporary object to be passed to some operation
 * as an argument or a very important aging object
 * to be a primary source of truth.
 *
 * As a bonus requirement. It is required for any
 * tree implementation to have its related trees
 * all the same class as it.
 * The exception of this requirement is when they
 * inherit the same public class but are different
 * private subclasses.
 * This requirement was made to make tooling easier.
 *
 * The behaviour of the basic object operations
 * (i.e. [equals], [hashCode], and [toString]) is
 * not specified by this interface and can differ
 * depending on the implementation.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.11.12
 */
interface Tree<T> {
    /**
     * The payload of the tree.
     */
    val value: T

    /**
     * The tree above this tree. Must be null when [_left] isn't.
     */
    @Suppress("PropertyName")
    @TreeImplementationApi
    val _top: Tree<T>?

    /**
     * The tree to the left of this tree. Must be null when [_top] isn't.
     */
    @Suppress("PropertyName")
    @TreeImplementationApi
    val _left: Tree<T>?

    /**
     * The tree to the right of this tree.
     */
    @Suppress("PropertyName")
    @TreeImplementationApi
    val _right: Tree<T>?

    /**
     * The tree below this tree.
     */
    @Suppress("PropertyName")
    @TreeImplementationApi
    val _bottom: Tree<T>?
}

//

/**
 * The tree above this tree. Must be null when [left] isn't.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(TreeImplementationApi::class)
inline val <R : Tree<*>> R.top: R?
    get() = this._top as R?

/**
 * The tree below this tree.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(TreeImplementationApi::class)
inline val <R : Tree<*>> R.bottom: R?
    get() = this._bottom as R?

/**
 * The tree to the left of this tree. Must be null when [top] isn't.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(TreeImplementationApi::class)
inline val <R : Tree<*>> R.left: R?
    get() = this._left as R?

/**
 * The tree to the right of this tree.
 */
@Suppress("UNCHECKED_CAST")
@OptIn(TreeImplementationApi::class)
inline val <R : Tree<*>> R.right: R?
    get() = this._right as R?

//

/**
 * The tree before this tree in the parent.
 */
inline val <R : Tree<*>> R.previous: R?
    get() = this.left

/**
 * The tree after this tree in this parent.
 */
inline val <R : Tree<*>> R.next: R?
    get() = this.right

/**
 * The tree containing this tree.
 */
val <R : Tree<*>> R.parent: R?
    get() = this.previous?.parent ?: this.top

/**
 * The first tree contained in this tree.
 */
inline val <R : Tree<*>> R.child: R?
    get() = this.bottom

/**
 * A sequence of the children of this tree.
 */
inline val <R : Tree<*>> R.children: Sequence<R>
    get() = generateSequence(child) { it.next }

/**
 * A sequence of ALL the children of this tree.
 * Including children, grand-children and
 * grand-grand-children so on.
 *
 * This is a depth-first traversal.
 */
val <R : Tree<*>> R.hierarchy: Sequence<R>
    get() = children.flatMap { sequenceOf(it) + it.hierarchy }

/**
 * A sequence of ALL the children of this tree and
 * this tree. Including children, grand-children and
 * grand-grand-children so on.
 *
 * This is a depth-first traversal.
 */
val <R : Tree<*>> R.hierarchyInclusive: Sequence<R>
    get() = sequenceOf(this) + this.hierarchy

/**
 * The first tree in the parent of this tree.
 */
val <R : Tree<*>> R.head: R
    get() = this.previous?.head ?: this

/**
 * The last tree in the parent of this tree.
 */
val <R : Tree<*>> R.tail: R
    get() = this.next?.tail ?: this

/**
 * The root tree of this tree.
 */
val <R : Tree<*>> R.root: R
    get() = this.parent?.root ?: this

//

/**
 * Return the tree at [indices].
 *
 * Example:
 * ```
 *      A
 *      |
 *      B - C - D
 *          |
 *          E - F - G
 *              |
 *              H
 * ```
 * ```
 * A -> A
 * B -> A[0]
 * C -> A[1]
 * D -> A[2]
 * E -> A[1][0]
 * F -> A[1][1]
 * G -> A[1][2]
 * H -> A[1][1][0]
 * ```
 *
 * @throws IndexOutOfBoundsException if no such tree.
 * @throws IllegalArgumentException if [indices] is empty.
 */
operator fun <R : Tree<*>> R.get(vararg indices: Int): R {
    return get(indices.asList())
}

/**
 * Return the tree at [indices].
 *
 * Example:
 * ```
 *      A
 *      |
 *      B - C - D
 *          |
 *          E - F - G
 *              |
 *              H
 * ```
 * ```
 * A -> A
 * B -> A[0]
 * C -> A[1]
 * D -> A[2]
 * E -> A[1][0]
 * F -> A[1][1]
 * G -> A[1][2]
 * H -> A[1][1][0]
 * ```
 *
 * @throws IndexOutOfBoundsException if no such tree.
 * @throws IllegalArgumentException if [indices] is empty.
 */
fun <R : Tree<*>> R.get(indices: List<Int>): R {
    return getOrElse(indices) { throw IndexOutOfBoundsException("Tree doesn't contain element at indices $indices") }
}

/**
 * Return the tree at [indices], or the result of invoking [defaultValue] if no
 * such tree was found.
 *
 * @throws IllegalArgumentException if [indices] is empty.
 */
fun <R : Tree<*>> R.getOrElse(vararg indices: Int, defaultValue: () -> R): R {
    return getOrElse(indices.asList(), defaultValue)
}

/**
 * Return the tree at [indices], or the result of invoking [defaultValue] if no
 * such tree was found.
 *
 * @throws IllegalArgumentException if [indices] is empty.
 */
fun <R : Tree<*>> R.getOrElse(indices: List<Int>, defaultValue: () -> R): R {
    require(indices.isNotEmpty()) { "When accessing a tree with an index, at least one index should be provided" }

    val tree = this.children.elementAtOrNull(indices[0])

    tree ?: return defaultValue()

    if (indices.size > 1)
        return tree.getOrElse(indices.drop(1), defaultValue)

    return tree
}

/**
 * Return the tree at [indices], or null if no such tree was found.
 *
 * @throws IllegalArgumentException if [indices] is empty.
 */
fun <R : Tree<*>> R.getOrNull(vararg indices: Int): R? {
    return getOrNull(indices.asList())
}

/**
 * Return the tree at [indices], or null if no such tree was found.
 *
 * @throws IllegalArgumentException if [indices] is empty.
 */
fun <R : Tree<*>> R.getOrNull(indices: List<Int>): R? {
    require(indices.isNotEmpty()) { "When accessing a tree with an index, at least one index should be provided" }

    val tree = this.children.elementAtOrNull(indices[0])

    tree ?: return null

    if (indices.size > 1)
        return tree.getOrNull(indices.drop(1))

    return tree
}

/**
 * Find (breadth-first) the tree matching [predicate].
 */
fun <R : Tree<*>> R.find(predicate: (R) -> Boolean): R? {
    val children = this.children.toList()
    val chosen = children.firstOrNull(predicate)

    if (chosen != null)
        return chosen

    return children.firstNotNullOfOrNull { it.find(predicate) }
}

/**
 * Return the first (breadth-first) tree matching [predicate].
 *
 * @throws NoSuchElementException if no such tree was found.
 */
fun <R : Tree<*>> R.first(predicate: (R) -> Boolean): R {
    val children = this.children.toList()
    val chosen = children.firstOrNull(predicate)

    if (chosen != null)
        return chosen

    return children.firstNotNullOfOrNull { it.find(predicate) }
        ?: throw NoSuchElementException("Tree contains no element matching the predicate.")
}

//

/**
 * Collect the trees in the structure of this tree
 * with the given [block] being the function
 * determining the sequence's order.
 *
 * Important Note: the returned result IS A SEQUENCE!
 * sequences don't evaluate until needed. Manipulating
 * the trees while collecting will change the sequence.
 *
 * @param block the function determining the order.
 * @param inclusive pass false to include this tree.
 * @return a sequence of trees.
 * @since 0.4.0 ~2022.11.14
 */
fun <R : Tree<*>> R.collect(
    inclusive: Boolean = true,
    block: (R) -> List<R?> = { listOf(it.bottom, it.right, it.left, it.top) }
): Sequence<R> {
    // fifo; null: reduce back stack
    val queue: MutableList<R?> = block(this).filterNotNull().toMutableList()
    // lifo
    val backStack = mutableListOf(this)

    return sequence {
        if (inclusive)
            yield(this@collect)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            if (current == null) {
                backStack.removeLast()
                continue
            }

            yield(current)

            val dejaVu = backStack.last()

            val neighbors = block(current)
                .filterNotNull()
                .filter { it !== dejaVu }

            if (neighbors.isNotEmpty()) {
                backStack += current
                queue.addAll(0, neighbors + null)
            }
        }
    }
}
