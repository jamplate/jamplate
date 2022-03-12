package org.jamplate.jamtree

import org.jetbrains.annotations.Contract

/**
 * An error indicating that a tree is corrupted.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.03.12
 */
open class CorruptedTreeError(
    /**
     * The detail message.
     *
     * @since 0.4.0 ~2022.03.12
     */
    override val message: String? = null,
    /**
     * The cause exception.
     *
     * @since 0.4.0 ~2022.03.12
     */
    override val cause: Throwable? = null,
    /**
     * The trees caused the exception. The last one is most likely the cause.
     *
     * @since 0.4.0 ~2022.03.12
     */
    open vararg val trees: Tree<*>?
) : InternalError() {
    companion object {
        private const val serialVersionUID: Long = -9083763689617894655L
    }
}

/**
 * Throw [CorruptedTreeError].
 *
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->fail", pure = true)
fun corruptedTree(vararg trees: Tree<*>?): Nothing =
    throw CorruptedTreeError(null, null, *trees)

/**
 * Throw [CorruptedTreeError].
 *
 * @param message the detail message.
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_,_->fail", pure = true)
fun corruptedTree(message: String?, vararg trees: Tree<*>?): Nothing =
    throw CorruptedTreeError(message, null, *trees)

/**
 * Throw [CorruptedTreeError].
 *
 * @param message the detail message.
 * @param cause the cause exception.
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_,_,_->fail", pure = true)
fun corruptedTree(message: String?, cause: Throwable?, vararg trees: Tree<*>): Nothing =
    throw CorruptedTreeError(message, cause, *trees)
