package org.jamplate.jamtree

import org.jetbrains.annotations.Contract

/**
 * An exception indicating that a tree is clashing with another tree by having the same bounds
 * as that other tree.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.03.12
 */
open class TreeTakeoverException(
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
     * The trees cause the exception. The last one is the illegal.
     *
     * @since 0.4.0 ~2022.03.12
     */
    override vararg val trees: Tree<*>?
) : IllegalTreeException() {
    companion object {
        private const val serialVersionUID: Long = 2459133353401711363L
    }
}

/**
 * Throw [TreeTakeoverException].
 *
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->fail", pure = true)
fun treeTakeover(vararg trees: Tree<*>?): Nothing =
    throw TreeTakeoverException(null, null, *trees)

/**
 * Throw [TreeTakeoverException].
 *
 * @param message the detail message.
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_,_->fail", pure = true)
fun treeTakeover(message: String?, vararg trees: Tree<*>?): Nothing =
    throw TreeTakeoverException(message, null, *trees)

/**
 * Throw [TreeTakeoverException].
 *
 * @param message the detail message.
 * @param cause the cause exception.
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_,_,_->fail", pure = true)
fun treeTakeover(message: String?, cause: Throwable?, vararg trees: Tree<*>): Nothing =
    throw TreeTakeoverException(message, cause, *trees)
