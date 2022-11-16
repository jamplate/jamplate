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
 * A function local is a way to pass contextual
 * arguments to a jamplate function.
 */
interface JamfnLocal<T> {
    /**
     * Obtain a default value of this local.
     */
    @JamfnImplementationApi
    fun defaultValue(): T

    /**
     * Return a local pair with this local and [value].
     */
    infix fun provides(value: T): JamfnLocalValue<T> {
        return JamfnLocalValue(this, value)
    }
}

/**
 * Create a new jamfn local.
 */
fun <T> jamfnLocalOf(defaultValue: () -> T): JamfnLocal<T> {
    return object : JamfnLocal<T> {
        @JamfnImplementationApi
        override fun defaultValue(): T {
            return defaultValue()
        }
    }
}

/**
 * An instance holding values from calling [JamfnLocal.provides].
 *
 * @author LSafer
 * @since 0.4.0 ~2022.11.13
 */
class JamfnLocalValue<T> internal constructor(
    /**
     * The providing local.
     */
    val local: JamfnLocal<T>,
    /**
     * The provided value.
     */
    val value: T
)

private class JamfnLocalImpl<T>(
    val defaultValueBlock: () -> T
) : JamfnLocal<T> {
    @JamfnImplementationApi
    override fun defaultValue(): T {
        return defaultValueBlock()
    }
}
