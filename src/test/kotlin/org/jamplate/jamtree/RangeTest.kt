/*
 *	Copyright 2021 Cufy
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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

fun assertIntersection(range: Range, other: Range, intersection: Intersection) {
    assertIntersection(range.start, range.end, other.start, other.end, intersection)
}

fun assertIntersection(i: Int, j: Int, s: Int, e: Int, intersection: Intersection) {
    assertSame(
        intersection,
        computeIntersection(i, j, s, e),
        "Intersection of [$i, $j] with [$s, $e]"
    )
    assertSame(
        intersection.opposite,
        computeIntersection(s, e, i, j),
        "Intersection($s, $e, $i, $j)"
    )
    assertSame(
        intersection.dominance,
        computeDominance(i, j, s, e),
        "Dominance($i, $j, $s, $e)"
    )
    assertSame(
        intersection.dominance.opposite,
        computeDominance(s, e, i, j),
        "Dominance($s, $e, $i, $j)"
    )
    assertSame(
        intersection.relation,
        computeRelation(i, j, s, e),
        "Relation($i, $j, $s, $e)"
    )
    assertSame(
        intersection.relation.opposite,
        computeRelation(s, e, i, j),
        "Relation($s, $e, $i, $j)"
    )
}

fun String.substring(range: Range) = substring(range.start, range.end)

class RangeTest {
    @Test
    fun misc0() {
        val document = "ABC0123"
        val reference = Range(0, document.length)
        val letters = reference.subrange(0, 3)
        val numbers = reference.subrange(3, 4)
        val b = reference.subrange(1, 1)
        val bc = letters.subrange(1, 2)
        val c0 = reference.subrange(2, 2)

        assertEquals("ABC", document.substring(letters), "Wrong Slice")
        assertEquals("0123", document.substring(numbers), "Wrong Slice")
        assertEquals("B", document.substring(b), "Wrong Slice")
        assertEquals("BC", document.substring(bc), "Wrong Slice")
        assertEquals("C0", document.substring(c0), "Wrong Slice")

        assertIntersection(reference, reference, Intersection.SAME)
        assertIntersection(reference, letters, Intersection.START)
        assertIntersection(reference, numbers, Intersection.END)
        assertIntersection(reference, b, Intersection.FRAGMENT)
        assertIntersection(reference, bc, Intersection.FRAGMENT)
        assertIntersection(reference, c0, Intersection.FRAGMENT)

        assertIntersection(letters, letters, Intersection.SAME)
        assertIntersection(letters, numbers, Intersection.FRONT)
        assertIntersection(letters, b, Intersection.FRAGMENT)
        assertIntersection(letters, bc, Intersection.END)
        assertIntersection(letters, c0, Intersection.OVERFLOW)

        assertIntersection(numbers, numbers, Intersection.SAME)
        assertIntersection(numbers, b, Intersection.BEFORE)
        assertIntersection(numbers, bc, Intersection.BACK)
        assertIntersection(numbers, c0, Intersection.UNDERFLOW)

        assertIntersection(b, b, Intersection.SAME)
        assertIntersection(b, bc, Intersection.AHEAD)
        assertIntersection(b, c0, Intersection.FRONT)

        assertIntersection(bc, bc, Intersection.SAME)
        assertIntersection(bc, c0, Intersection.OVERFLOW)

        assertIntersection(c0, c0, Intersection.SAME)
    }
}
