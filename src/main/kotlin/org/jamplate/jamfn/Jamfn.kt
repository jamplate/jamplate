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
package org.jamplate.jamfn

/**
 * Jamplate function scope.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.11.13
 */
interface Jamfn {
    /**
     * The current value provided for this local.
     * Or the result of invoking the default value
     * function of it.
     * If the value isn't provided, the default
     * function will be invoked everytime this
     * value is requested.
     */
    val <T> JamfnLocal<T>.current: T

    /**
     * The parent function scope.
     */
    @JamfnPrimaryCompanion
    val currentParent: Jamfn?

    /**
     * Create sub-instance of this scope.
     *
     * @param values local values to be provided.
     */
    @JamfnImplementationApi
    fun _withLocal(vararg values: JamfnLocalValue<*>): Jamfn
}

/**
 * Create a new jamfn scope with the given local
 * values.
 */
fun Jamfn(
    vararg values: JamfnLocalValue<*>,
    parent: Jamfn? = null,
    block: Jamfn.() -> Unit = {}
): Jamfn {
    return JamfnImpl(parent, *values).apply(block)
}

/**
 * Create a new jamfn scope with this scope as its
 * parent and with the given local values.
 */
@JamfnCompanion
fun Jamfn.createSubJamfn(
    vararg values: JamfnLocalValue<*>,
    block: Jamfn.() -> Unit = {}
): Jamfn {
    return Jamfn(*values, parent = this, block = block)
}

/**
 * Execute [block] in a sub-scope with the given [values].
 */
@Suppress("UNCHECKED_CAST")
@OptIn(JamfnImplementationApi::class)
@JamfnCompanion
inline fun <R : Jamfn> R.withLocal(
    vararg values: JamfnLocalValue<*>,
    block: R.() -> Unit = {}
): R {
    val sub = _withLocal(*values) as R
    return sub.apply(block)
}

private class JamfnImpl(
    val parent: Jamfn?,
    vararg values: JamfnLocalValue<*>
) : Jamfn {
    val locals = values.associate { it.local to it.value }

    @Suppress("UNCHECKED_CAST")
    @JamfnImplementationApi
    override val <T> JamfnLocal<T>.current: T
        get() = locals[this] as T
            ?: parent?.let { with(it) { current } }
            ?: defaultValue()

    override val currentParent: Jamfn? get() = parent

    @JamfnImplementationApi
    override fun _withLocal(vararg values: JamfnLocalValue<*>): Jamfn {
        return JamfnImpl(this, *values)
    }
}
