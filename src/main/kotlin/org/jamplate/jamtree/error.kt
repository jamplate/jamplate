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
    constructor(vararg trees: Tree<*>) :
            this(null, null, *trees)

    constructor(message: String?, vararg trees: Tree<*>) :
            this(message, null, *trees)

    companion object {
        private const val serialVersionUID: Long = -9083763689617894655L
    }
}

/**
 * An exception indicating that an illegal tree was provided.
 *
 * @author LSafer
 * @since 0.4.0 ~2021.05.14
 */
open class IllegalTreeException(
    /**
     * The detail message.
     *
     * @since 0.4.0 ~2021.05.14
     */
    override val message: String? = null,
    /**
     * The cause exception.
     *
     * @since 0.4.0 ~2021.05.14
     */
    override val cause: Throwable? = null,
    /**
     * The trees cause the exception. The last one is the illegal.
     *
     * @since 0.4.0 ~2021.05.14
     */
    open vararg val trees: Tree<*>? = emptyArray()
) : IllegalArgumentException() {
    constructor(vararg trees: Tree<*>) :
            this(null, null, *trees)

    constructor(message: String?, vararg trees: Tree<*>) :
            this(message, null, *trees)

    companion object {
        private const val serialVersionUID: Long = -5671343990702472395L
    }
}

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
    constructor(vararg trees: Tree<*>) :
            this(null, null, *trees)

    constructor(message: String?, vararg trees: Tree<*>) :
            this(message, null, *trees)

    companion object {
        private const val serialVersionUID: Long = 8158864603104733166L
    }
}

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
    constructor(vararg trees: Tree<*>) :
            this(null, null, *trees)

    constructor(message: String?, vararg trees: Tree<*>) :
            this(message, null, *trees)

    companion object {
        private const val serialVersionUID: Long = 2459133353401711363L
    }
}
