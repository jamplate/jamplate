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
package org.jamplate.jamcore.regex

import org.jamplate.jamcore.BufferRange
import java.util.regex.Matcher

/**
 * Return a sequence of the remaining matches.
 *
 * Important Note: the returned result IS A SEQUENCE!
 * sequences don't evaluate until needed. Manipulating
 * the matcher while collecting will change the sequence.
 */
fun Matcher.asSequence(): Sequence<BufferRange> {
    return sequence {
        while (find()) {
            val start = start()
            val end = end()

            yield(BufferRange(
                offset = start.toULong(),
                length = end.toULong() - start.toULong()
            ))
        }
    }
}

/**
 * Return a list of the remaining matches.
 */
fun Matcher.toList(): List<BufferRange> {
    return asSequence().toList()
}
