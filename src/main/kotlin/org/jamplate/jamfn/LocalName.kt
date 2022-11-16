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
 * A name wrapper to resolve naming collisions and
 * allow simply calling [distinct].
 */
private class NameValue(val value: String)

private val DefaultNameValue = NameValue("")

/**
 * A local holding the debug name of the current component.
 */
private val LocalName = jamfnLocalOf { DefaultNameValue }

/**
 * The current component's name.
 */
@JamfnPrimaryCompanion
val Jamfn.currentName: String
    get() = LocalName.current.value

/**
 * Return the current component's name joined with
 * its parents' names.
 *
 * A typical fullname would look like this `::Comp1::Comp2::Comp3`
 *
 * The first `::` is because the root name is
 */
@JamfnPrimaryCompanion
val Jamfn.currentFullname: String
    get() {
        return generateSequence(this) { it.currentParent }
            .map { with(it) { LocalName.current } }
            .toList()
            .reversed()
            .distinct()
            .joinToString("::") { it.value }
    }

/**
 * Execute [block] in a sub-scope with [name].
 */
@JamfnCompanion
fun <R : Jamfn> R.withLocalName(
    name: String,
    block: R.() -> Unit = {}
): R {
    return withLocal(LocalName provides NameValue(name)).apply(block)
}
