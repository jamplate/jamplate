/*
 *	Copyright 2022 cufy.org
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
