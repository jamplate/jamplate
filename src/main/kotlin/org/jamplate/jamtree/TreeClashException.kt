package org.jamplate.jamtree

import org.jetbrains.annotations.Contract

/**
 * An exception to indicate that a provided tree is clashing with another tree and the
 * operation cannot continue because of that.
 *
 * @author LSafer
 * @since 0.4.0 ~2021.05.14
 */
open class TreeClashException(
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
        private const val serialVersionUID: Long = 8158864603104733166L
    }
}

/**
 * Throw [TreeClashException].
 *
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->fail", pure = true)
fun treeClash(vararg trees: Tree<*>?): Nothing =
    throw TreeClashException(null, null, *trees)

/**
 * Throw [TreeClashException].
 *
 * @param message the detail message.
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_,_->fail", pure = true)
fun treeClash(message: String?, vararg trees: Tree<*>?): Nothing =
    throw TreeClashException(message, null, *trees)

/**
 * Throw [TreeClashException].
 *
 * @param message the detail message.
 * @param cause the cause exception.
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_,_,_->fail", pure = true)
fun treeClash(message: String?, cause: Throwable?, vararg trees: Tree<*>): Nothing =
    throw TreeClashException(message, cause, *trees)
