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

import java.util.concurrent.atomic.AtomicBoolean

/**
 * A local holding the sustain boolean flag.
 */
private val LocalSustain = jamfnLocalOf<AtomicBoolean> {
    error("LocalSustain not provided")
}

/**
 * Just accessing this value will trigger the
 * whole scope to rerun.
 */
@JamfnPrimaryCompanion
val Jamfn.sustain: Boolean
    get() {
        val sustain = LocalSustain.current
        sustain.set(true)
        return true
    }

/**
 * True, if [whileSustained] has been called.
 */
@JamfnPrimaryCompanion
val Jamfn.sustained: Boolean
    get() {
        val sustain = LocalSustain.current
        return sustain.get()
    }

/**
 * Run [block] in an enclosed sustaining scope.
 *
 * This function will keep invoking [block] as long
 * as [block] is calling [sustain] on it.
 *
 * The following code will print "Hello World"
 *
 * ```kotlin
 * val message = "Hello World"
 * var index = 0
 *
 * Jamfn().whileSustained {
 *      if (index < message.length) {
 *          print(message[index])
 *          index++
 *          sustain
 *      } else {
 *          println()
 *      }
 * }
 * ```
 */
@JamfnCompanion
fun <R : Jamfn> R.whileSustained(block: R.() -> Unit) {
    while (true) {
        val sustain = AtomicBoolean(false)
        val sub = withLocal(LocalSustain provides sustain)
        block(sub)
        if (!sustain.get())
            return
    }
}
