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
import org.jamplate.jamcore.Precedence.*
import org.jamplate.jamcore.Relation.*
import org.jetbrains.annotations.Contract

/**
 * SyntaxTree is an IST (Index Syntax Tree), a
 * tree specifically for syntax analysis. It has
 * indices specifying where it exists in some text
 * source.
 *
 * The behaviour of the basic object operations
 * (i.e. [equals], [hashCode], and [toString]) is
 * not specified by this interface and can differ
 * depending on the implementation.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.11.12
 */
interface SyntaxTree<T> : Tree<T>, SyntaxRange

//

/**
 * Return a fantom tree for the given [range] in
 * this tree.
 *
 * @param range the range to compute.
 * @return a fantom tree with the computed neighboring trees.
 * @throws IllegalTreeException if [range] does not fit in the structure of this tree.
 * @throws CorruptedTreeError
 * if noticed a prof that the structure of this tree is
 * corrupted while trying to compute.
 * @since 0.4.0 ~2022.11.14
 */
infix fun <T, R : SyntaxTree<T>> R.fantom(range: SyntaxRange): FantomTree<T?, R> {
    return compute(range).getOrThrow()
}

/**
 * Find the neighboring trees of a tree with [range] in the structure of this tree.
 *
 * @param range the range to compute.
 * @return a fantom tree with the computed neighboring trees.
 * @since 0.4.0 ~2022.11.14
 */
@Contract(value = "_,_,_->new", pure = true)
infix fun <T, R : SyntaxTree<T>> R.compute(range: SyntaxRange): Result<FantomTree<T?, R>> {
    return when (Relation(this, range)) {
        Parent -> this computeParent range
        Self -> this computeSelf range
        Child -> this computeChild range
        Next -> this computeNext range
        Previous -> this computePrevious range
        Clash -> Result.failure(TreeClashException("compute", this))
    }
}

@Contract(value = "_,_,_->new", pure = true)
internal infix fun <T, R : SyntaxTree<T>> R.computeSelf(range: SyntaxRange): Result<FantomTree<T?, R>> {
    require(Relation(this, range) == Self) {
        "Illegal Relation: Expected: $Self Actual: ${Relation(this, range)}"
    }

    val top = this.top
    val left = this.left
    val right = this.right
    val bottom = this.bottom

    if (top != null && left != null)
        return Result.failure(CorruptedTreeError("computeSelf: flipped T", top, this, left))

    return when (Precedence(this, range)) {
        Lower -> when {
            bottom != null -> when (Intersection(bottom, range)) {
                Intersection.Container, Intersection.Ahead, Intersection.Behind -> {
                    // this |> tree |> bottom
                    Result.success(FantomTree(
                        top = this,
                        bottom = bottom
                    ))
                }
                Intersection.Same -> when (Precedence(bottom, range)) {
                    Lower -> bottom computeSelf range
                    Higher -> {
                        // this |> tree |> bottom
                        Result.success(FantomTree(
                            top = this,
                            bottom = bottom
                        ))
                    }
                    Equal ->
                        Result.failure(TreeTakeoverException("computeSelf: takeover bottom", bottom))
                }
                Intersection.Overflow, Intersection.Underflow, Intersection.After, Intersection.Front, Intersection.Before, Intersection.Back, Intersection.Fragment, Intersection.Start, Intersection.End ->
                    Result.failure(CorruptedTreeError("computeSelf: tree outside its top", this, bottom))
            }
            else -> {
                // this |> tree [!bottom]
                Result.success(FantomTree(
                    top = this
                ))
            }
        }
        Higher -> when {
            top != null -> when (Intersection(top, range)) {
                Intersection.Fragment, Intersection.Start, Intersection.End -> {
                    // top |> (tree |> this) -> right
                    Result.success(FantomTree(
                        bottom = this,
                        top = top,
                        right = right
                    ))
                }
                Intersection.Same -> when (Precedence(top, range)) {
                    Higher -> top computeSelf range
                    Lower -> {
                        // top |> (tree |> this) -> right
                        Result.success(FantomTree(
                            bottom = this,
                            top = top,
                            right = right
                        ))
                    }
                    Equal ->
                        Result.failure(TreeTakeoverException("computeSelf: takeover top", top))
                }
                Intersection.Overflow, Intersection.Underflow, Intersection.After, Intersection.Front, Intersection.Before, Intersection.Back, Intersection.Container, Intersection.Ahead, Intersection.Behind ->
                    Result.failure(CorruptedTreeError("offerSelf: tree outside its top", top, this))
            }
            else -> {
                // left -> (tree |> this) -> right [!top]
                Result.success(FantomTree(
                    bottom = this,
                    left = left,
                    right = right,
                ))
            }
        }
        Equal ->
            Result.failure(TreeTakeoverException("computeSelf: takeover self", this))
    }
}

/**
 * Assuming the relation between this and [range] is [Child],
 * Find the neighboring trees of a tree with [range] in the structure of this tree.
 *
 * @param range the range to compute.
 * @return a fantom tree with the computed neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [range] is not [Child].
 * @since 0.4.0 ~2022.11.14
 */
@Contract(value = "_,_,_->new", pure = true)
internal infix fun <T, R : SyntaxTree<T>> R.computeChild(range: SyntaxRange): Result<FantomTree<T?, R>> {
    require(Relation(this, range) == Child) {
        "Illegal Relation: Expected: $Child Actual: ${Relation(this, range)}"
    }

    val bottom = this.bottom

    return when {
        bottom != null -> when (Intersection(bottom, range)) {
            Intersection.Same -> bottom computeSelf range
            Intersection.Fragment, Intersection.Start, Intersection.End -> bottom computeChild range
            Intersection.Before, Intersection.Back -> {
                // this |> tree -> bottom
                Result.success(FantomTree(
                    right = bottom,
                    top = this
                ))
            }
            Intersection.Front, Intersection.After -> {
                var newLeft: R = bottom
                var newBottom: R? = null
                var newRight: R? = null

                for (it in generateSequence(bottom.right) { it.right })
                    when (Intersection(it, range)) {
                        Intersection.Same -> return it computeSelf range
                        Intersection.Fragment, Intersection.Start, Intersection.End -> return it computeChild range
                        Intersection.Front, Intersection.After -> newLeft = it
                        Intersection.Container, Intersection.Ahead, Intersection.Behind -> newBottom = newBottom ?: it
                        Intersection.Back, Intersection.Before -> newRight = it
                        Intersection.Overflow, Intersection.Underflow ->
                            return Result.failure(TreeClashException("computeChild: clash with child", it))
                    }

                // newLeft -> (tree |> newBottom) -> newRight
                Result.success(FantomTree(
                    left = newLeft,
                    bottom = newBottom,
                    right = newRight
                ))
            }
            Intersection.Container, Intersection.Ahead, Intersection.Behind -> {
                val newRight = generateSequence(bottom.right) { it.right }.firstOrNull {
                    when (Dominance(it, range)) {
                        Contain -> false
                        None -> true
                        Share ->
                            return Result.failure(TreeClashException("computeChild: tree clash with child", it))
                        Part, Exact ->
                            return Result.failure(CorruptedTreeError("computeChild: tree can fit in sibling", this, it, bottom))
                    }
                }

                // this |> (tree |> bottom) -> newRight
                Result.success(FantomTree(
                    top = this,
                    bottom = bottom,
                    right = newRight,
                ))
            }
            Intersection.Underflow, Intersection.Overflow ->
                Result.failure(TreeClashException("computeChild: clash with bottom", bottom))
        }
        else -> {
            // this |> tree [!bottom]
            Result.success(FantomTree(
                top = this
            ))
        }
    }
}

/**
 * Assuming the relation between this and [range] is [Parent],
 * Find the neighboring trees of a tree with [range] in the structure of this tree.
 *
 * @param range the range to compute.
 * @return a fantom tree with the computed neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [range] is not [Parent].
 * @since 0.4.0 ~2022.11.14
 */
@Contract(value = "_,_,_->new", pure = true)
internal infix fun <T, R : SyntaxTree<T>> R.computeParent(range: SyntaxRange): Result<FantomTree<T?, R>> {
    require(Relation(this, range) == Parent) {
        "Illegal Relation: Expected: $Parent Actual: ${Relation(this, range)}"
    }

    val parent = this.parent

    return when {
        parent != null -> when (Intersection(parent, range)) {
            Intersection.Same -> parent computeSelf range
            Intersection.Container, Intersection.Ahead, Intersection.Behind -> parent computeParent range
            Intersection.Fragment, Intersection.Start, Intersection.End -> parent computeChild range
            Intersection.Overflow, Intersection.Underflow ->
                Result.failure(TreeClashException("computeParent: clash with top", parent))
            Intersection.Back, Intersection.Before, Intersection.Front, Intersection.After ->
                Result.failure(CorruptedTreeError("computeParent: tree outside its parent", parent, this))
        }
        else -> {
            val newBottom = generateSequence(this) { it.left }.last {
                when (Dominance(it, range)) {
                    Contain -> true
                    None -> false
                    Share ->
                        return Result.failure(TreeClashException("computeParent: clash with sibling", it))
                    Part, Exact ->
                        return Result.failure(CorruptedTreeError("computeParent: tree can fit in sibling", it, this))
                }
            }
            val newLeft = generateSequence(left) { it.left }.firstOrNull {
                when (Dominance(it, range)) {
                    Contain -> false
                    None -> true
                    Share ->
                        return Result.failure(TreeClashException("computeParent: clash with sibling", it))
                    Part, Exact ->
                        return Result.failure(CorruptedTreeError("computeParent: tree can fit in sibling", it, this))
                }
            }
            val newRight = generateSequence(right) { it.right }.firstOrNull {
                when (Dominance(it, range)) {
                    Contain -> false
                    None -> true
                    Share ->
                        return Result.failure(TreeClashException("computeParent: clash with sibling", it))
                    Part, Exact ->
                        return Result.failure(CorruptedTreeError("offerParent: tree can fit in sibling", it, this))
                }
            }

            // newLeft -> (tree |> newBottom) -> newRight [!top]
            Result.success(FantomTree(
                right = newRight,
                left = newLeft,
                bottom = newBottom
            ))
        }
    }
}

/**
 * Assuming the relation between this and [range] is [Previous],
 * Find the neighboring trees of a tree with [range] in the structure of this tree.
 *
 * @param range the range to compute.
 * @return a fantom tree with the computed neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [range] is not [Previous].
 * @since 0.4.0 ~2022.11.14
 */
@Contract(value = "_,_,_->new", pure = true)
internal infix fun <T, R : SyntaxTree<T>> R.computePrevious(range: SyntaxRange): Result<FantomTree<T?, R>> {
    require(Relation(this, range) == Previous) {
        "Illegal Relation: Expected: $Previous Actual: ${Relation(this, range)}"
    }

    val top = this.top
    val left = this.left

    if (top != null && left != null)
        return Result.failure(CorruptedTreeError("computePrevious: flipped T", top, this, left))

    return when {
        top != null -> when (Intersection(top, range)) {
            Intersection.Fragment, Intersection.Start -> top computeChild range
            Intersection.Back, Intersection.Before -> top computePrevious range
            Intersection.Overflow, Intersection.Underflow ->
                Result.failure(TreeClashException("computePrevious: clash with top", top))
            Intersection.Same, Intersection.Container, Intersection.Ahead, Intersection.Behind, Intersection.After, Intersection.Front, Intersection.End ->
                Result.failure(CorruptedTreeError("computePrevious: tree outside its top", top, this))
        }
        left != null -> when (Intersection(left, range)) {
            Intersection.Same -> left computeSelf range
            Intersection.Container, Intersection.Ahead, Intersection.Behind -> left computeParent range
            Intersection.Fragment, Intersection.Start, Intersection.End -> left computeChild range
            Intersection.Back, Intersection.Before -> left computePrevious range
            Intersection.After, Intersection.Front -> {
                // left -> tree -> this [!top]
                Result.success(FantomTree(
                    left = left,
                    right = this
                ))
            }
            Intersection.Overflow, Intersection.Underflow ->
                Result.failure(TreeClashException("computePrevious: clash with left", left))
        }
        else -> {
            // tree -> this [!top !left]
            Result.success(FantomTree(
                right = this
            ))
        }
    }
}

/**
 * Assuming the relation between this and [range] is [Next],
 * Find the neighboring trees of a tree with [range] in the structure of this tree.
 *
 * @param range the range to compute.
 * @return a fantom tree with the computed neighboring trees.
 * @throws IllegalArgumentException if the relation between this and [range] is not [Next].
 * @since 0.4.0 ~2022.11.14
 */
@Contract(value = "_,_,_->new", pure = true)
internal infix fun <T, R : SyntaxTree<T>> R.computeNext(range: SyntaxRange): Result<FantomTree<T?, R>> {
    require(Relation(this, range) == Next) {
        "Illegal Relation: Expected: $Next Actual: ${Relation(this, range)}"
    }

    val top = this.top
    val right = this.right

    return when {
        top != null -> when (Intersection(top, range)) {
            Intersection.Fragment, Intersection.End -> top computeChild range
            Intersection.Front, Intersection.After -> top computeNext range
            Intersection.Overflow, Intersection.Underflow ->
                Result.failure(TreeClashException("computeNext: clash with top", top))
            Intersection.Same, Intersection.Container, Intersection.Ahead, Intersection.Behind, Intersection.Before, Intersection.Back, Intersection.Start ->
                Result.failure(CorruptedTreeError("computeNext: tree outside its top", top, this))
        }
        right != null -> when (Intersection(right, range)) {
            Intersection.Same -> right computeSelf range
            Intersection.Container, Intersection.Ahead, Intersection.Behind -> right computeParent range
            Intersection.Fragment, Intersection.Start, Intersection.End -> right computeChild range
            Intersection.Front, Intersection.After -> right computeNext range
            Intersection.Back, Intersection.Before -> {
                // this -> tree -> right [!top]
                Result.success(FantomTree(
                    left = this,
                    right = right
                ))
            }
            Intersection.Overflow, Intersection.Underflow ->
                Result.failure(TreeClashException("computeNext: clash with right", right))
        }
        else -> {
            // this -> tree [!top !right]
            Result.success(FantomTree(
                right = this
            ))
        }
    }
}

//

/**
 * Check if this tree can be a direct parent of [range].
 */
fun SyntaxTree<*>.canBeDirectParentOf(range: BufferRange): Boolean {
    return canBeDirectParentOf(SyntaxRange(range))
}

/**
 * Check if this tree can be a direct parent of [range].
 */
fun SyntaxTree<*>.canBeDirectParentOf(range: SyntaxRange): Boolean {
    return when (Dominance(this, range)) {
        Exact -> {
            when (Precedence(this, range)) {
                Higher, Equal -> false
                else -> this.children.all { it.fitNotAsParentOf(range) }
            }
        }
        Part -> this.children.all { it.fitNotAsParentOf(range) }
        None, Share, Contain -> false
    }
}

/**
 * Check if this tree and its structure can be
 * merged with the given [tree].
 */
fun SyntaxTree<*>.canWholeMerge(tree: SyntaxTree<*>): Boolean {
    return when (Dominance(this, tree)) {
        Contain -> tree.children.all { it.canWholeMerge(this) }
        Part -> this.children.all { it.canWholeMerge(tree) }
        None -> true
        Share -> false
        Exact -> when (Precedence(this, tree)) {
            Higher -> tree.children.all { it.canWholeMerge(this) }
            Lower -> this.children.all { it.canWholeMerge(tree) }
            Equal -> false
        }
    }
}
