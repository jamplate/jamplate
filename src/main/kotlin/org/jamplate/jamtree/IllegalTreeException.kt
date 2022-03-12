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
    companion object {
        private const val serialVersionUID: Long = -5671343990702472395L
    }
}

/**
 * Throw [IllegalTreeException].
 *
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_->fail", pure = true)
fun illegalTree(vararg trees: Tree<*>?): Nothing =
    throw IllegalTreeException(null, null, *trees)

/**
 * Throw [IllegalTreeException].
 *
 * @param message the detail message.
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_,_->fail", pure = true)
fun illegalTree(message: String?, vararg trees: Tree<*>?): Nothing =
    throw IllegalTreeException(message, null, *trees)

/**
 * Throw [IllegalTreeException].
 *
 * @param message the detail message.
 * @param cause the cause exception.
 * @param trees the trees caused the exception. The last one is most likely the cause.
 * @since 0.4.0 ~2022.03.12
 */
@Contract(value = "_,_,_->fail", pure = true)
fun illegalTree(message: String?, cause: Throwable?, vararg trees: Tree<*>): Nothing =
    throw IllegalTreeException(message, cause, *trees)
