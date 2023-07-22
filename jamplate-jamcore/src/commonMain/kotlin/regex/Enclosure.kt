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
import org.jamplate.jamcore.terminal

/**
 * The ranges of an enclosure.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.11.13
 */
data class Enclosure(
    /**
     * The range of the opening anchor.
     */
    val open: BufferRange,
    /**
     * The range of the closing anchor.
     */
    val close: BufferRange,
    /**
     * The outer range of the enclosure.
     *
     * From the start of the opening anchor to the
     * end of the closing anchor.
     */
    val outer: BufferRange = BufferRange(
        offset = open.offset,
        length = close.terminal - open.offset
    ),
    /**
     * The inner range of the enclosure.
     *
     * From the end of the opening anchor to the
     * start of the closing anchor.
     */
    val inner: BufferRange = BufferRange(
        offset = open.terminal,
        length = close.offset - open.terminal
    )
)

/**
 * Return a sequence of ranges for valid enclosures
 * that might start at one of the [opensOrCloses]
 * anchors and end at another one of the
 * [opensOrCloses] anchors.
 *
 * A valid enclosure must have the end of its
 * opening anchor before the start of its closing
 * anchor.
 *
 * The finding will start at the last closing
 * anchor from the given [opensOrCloses] anchors
 * and search for a valid opening anchor for it
 * from the given [opensOrCloses] anchors
 * (reversed) and so on until reaching the first
 * closing anchor.
 *
 * Once an anchor is yielded, it won't be yielded
 * again.
 *
 * @param opensOrCloses the opening or closing anchors.
 * @return a sequence of valid enclosure opening and closing pairs.
 */
fun computeEnclosure(
    opensOrCloses: Iterable<BufferRange>
): Sequence<Enclosure> {
    return computeEnclosure(
        opens = opensOrCloses,
        closes = opensOrCloses
    )
}

/**
 * Return a sequence of ranges for valid enclosures
 * that might start at one of the [opens] anchors
 * and end at one of the [closes] anchors.
 *
 * A valid enclosure must have the end of its
 * opening anchor before the start of its closing
 * anchor.
 *
 * The finding will start at the last closing
 * anchor from the given [closes] anchors and
 * search for a valid opening anchor for it from
 * the given [opens] anchors (reversed) and so on
 * until reaching the first closing anchor.
 *
 * Once an anchor is yielded, it won't be yielded
 * again.
 *
 * @param opens the opening anchors.
 * @param closes the closing anchors.
 * @return a sequence of valid enclosure opening and closing pairs.
 */
fun computeEnclosure(
    opens: Iterable<BufferRange>,
    closes: Iterable<BufferRange>
): Sequence<Enclosure> {
    val opensQueue = opens.toMutableList().also { it.reverse() }

    return sequence {
        // in search for the first valid end
        for (close in closes) {
            for ((index, open) in opensQueue.withIndex()) {
                // check if the end of the start reached the start of the end
                if (open.terminal > close.offset)
                    continue

                // bingo!
                opensQueue.removeAt(index)
                yield(Enclosure(open, close))
                break
            }
        }
    }
}
