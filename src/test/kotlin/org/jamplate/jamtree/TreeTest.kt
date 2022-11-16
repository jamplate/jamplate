/*
 *	Copyright 2021-2022 cufy.org
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
package org.jamplate.test.org.jamplate.jamtree

import org.jamplate.jamcore.*
import org.jamplate.jamtree.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

fun assertIntersectionEqual(
    range: BufferRange,
    other: BufferRange,
    intersection: Intersection
) {
    assertSame(
        intersection,
        Intersection(range, other),
        "Intersection($range, $other)"
    )
    assertSame(
        intersection.opposite,
        Intersection(other, range),
        "Intersection($other, $range)"
    )
    assertSame(
        intersection.dominance,
        Dominance(range, other),
        "Dominance($range, $other)"
    )
    assertSame(
        intersection.dominance.opposite,
        Dominance(other, range),
        "Dominance($other, $range)"
    )
    assertSame(
        intersection.relation,
        Relation(range, other),
        "Relation($range, $other)"
    )
    assertSame(
        intersection.relation.opposite,
        Relation(other, range),
        "Relation($other, $range)"
    )
}

class TreeTest {
    @Test
    fun `relation computations checks`() {
        val document = "ABC0123"

        val reference = BufferRange(0uL, document.length.toULong())
        val letters = BufferRange(0uL, 3uL)
        val numbers = BufferRange(3uL, 4uL)
        val b = BufferRange(1uL, 1uL)
        val bc = BufferRange(1uL, 2uL)
        val c0 = BufferRange(2uL, 2uL)

        assertEquals("ABC", document.substring(letters.signedIntRange), "Wrong Slice")
        assertEquals("0123", document.substring(numbers.signedIntRange), "Wrong Slice")
        assertEquals("B", document.substring(b.signedIntRange), "Wrong Slice")
        assertEquals("BC", document.substring(bc.signedIntRange), "Wrong Slice")
        assertEquals("C0", document.substring(c0.signedIntRange), "Wrong Slice")

        assertIntersectionEqual(reference, reference, Intersection.Same)
        assertIntersectionEqual(reference, letters, Intersection.Start)
        assertIntersectionEqual(reference, numbers, Intersection.End)
        assertIntersectionEqual(reference, b, Intersection.Fragment)
        assertIntersectionEqual(reference, bc, Intersection.Fragment)
        assertIntersectionEqual(reference, c0, Intersection.Fragment)

        assertIntersectionEqual(letters, letters, Intersection.Same)
        assertIntersectionEqual(letters, numbers, Intersection.Front)
        assertIntersectionEqual(letters, b, Intersection.Fragment)
        assertIntersectionEqual(letters, bc, Intersection.End)
        assertIntersectionEqual(letters, c0, Intersection.Overflow)

        assertIntersectionEqual(numbers, numbers, Intersection.Same)
        assertIntersectionEqual(numbers, b, Intersection.Before)
        assertIntersectionEqual(numbers, bc, Intersection.Back)
        assertIntersectionEqual(numbers, c0, Intersection.Underflow)

        assertIntersectionEqual(b, b, Intersection.Same)
        assertIntersectionEqual(b, bc, Intersection.Ahead)
        assertIntersectionEqual(b, c0, Intersection.Front)

        assertIntersectionEqual(bc, bc, Intersection.Same)
        assertIntersectionEqual(bc, c0, Intersection.Overflow)

        assertIntersectionEqual(c0, c0, Intersection.Same)
    }

    @Test
    fun `multiple trees with same bounds but different weight`() {
        val root = ManagedTree("root", 0uL, 10uL, 0)
        val a = ManagedTree("a", 2uL, 1uL, 0)
        val b = ManagedTree("b", 3uL, 3uL, -1)
        val c = ManagedTree("c", 3uL, 3uL, 0)
        val d = ManagedTree("d", 3uL, 3uL, 1)
        val e = ManagedTree("e", 3uL, 1uL, 0)
        val f = ManagedTree("f", 5uL, 1uL, 0)
        val g = ManagedTree("g", 6uL, 1uL, 0)

        /*
              0123456789
        root: ----------
        a   :   -
        b   :    ---
        c   :    ---
        d   :    ---
        e   :    -
        f   :      -
        g   :       -
        */

        assertEquals(root fantom a, FantomTree(top = root))
        root offer a

        assertEquals(root fantom b, FantomTree(left = a))
        root offer b

        assertEquals(root fantom g, FantomTree(left = b))
        root offer g

        assertEquals(root fantom e, FantomTree(top = b))
        root offer e

        assertEquals(root fantom f, FantomTree(left = e))
        root offer f

        assertEquals(f fantom d, FantomTree(bottom = e, top = b))
        f offer d

        assertEquals(f fantom c, FantomTree(bottom = d, top = b))
        f offer c

        assertSame(
            a,
            root.bottom,
            ""
        )
        assertSame(
            b,
            root.bottom!!.next,
            "Ctx lost its place"
        )
        assertSame(
            g,
            root.bottom!!.next!!.next,
            ""
        )
        assertSame(
            c,
            root.bottom!!.next!!.bottom,
            "First overlap not placed currently"
        )
        assertSame(
            d,
            root.bottom!!.next!!.bottom!!.bottom,
            "Second overlap not placed currently"
        )
        assertSame(
            e,
            root.bottom!!.next!!.bottom!!.bottom!!.bottom!!,
            ""
        )
        assertSame(
            f,
            root.bottom!!.next!!.bottom!!.bottom!!.bottom!!.next!!,
            ""
        )
    }

    @Test
    fun `backward offerings and offering parent from deep bottom`() {
        val root = ManagedTree("root", 0uL, 10uL)
        val a = ManagedTree("a", 8uL, 1uL)
        val b = ManagedTree("b", 6uL, 2uL)
        val c = ManagedTree("c", 3uL, 3uL)
        val d = ManagedTree("d", 0uL, 2uL)
        val e = ManagedTree("e", 5uL, 1uL)
        val f = ManagedTree("f", 4uL, 1uL)
        val g = ManagedTree("g", 0uL, 1uL)

        /*
              0123456789
        root: ----------
        a   :         -
        b   :       --
        c   :    ---
        d   : --
        e   :      -
        f   :     -
        g   : -
        */

        assertEquals(a fantom b, FantomTree(right = a))
        a.offer(b)

        assertEquals(a fantom c, FantomTree(right = b))
        a.offer(c)

        assertEquals(a fantom d, FantomTree(right = c))
        a.offer(d)

        assertEquals(a fantom e, FantomTree(top = c))
        a.offer(e)

        assertEquals(a fantom f, FantomTree(right = e, top = c))
        a.offer(f)

        assertEquals(a fantom g, FantomTree(top = d))
        a.offer(g)

        assertSame(b, a.left)
        assertSame(c, a.left!!.left)
        assertSame(d, a.left!!.left!!.left)
        assertSame(e, a.left!!.left!!.bottom!!.right)
        assertSame(f, a.left!!.left!!.bottom)
        assertSame(g, a.left!!.left!!.left!!.bottom)

        assertEquals(f fantom root, FantomTree(bottom = d))
        f.offer(root)

        assertSame(a, root.bottom!!.right!!.right!!.right)
        assertSame(b, root.bottom!!.right!!.right)
        assertSame(c, root.bottom!!.right)
        assertSame(d, root.bottom)
        assertSame(e, root.bottom!!.right!!.bottom!!.right)
        assertSame(f, root.bottom!!.right!!.bottom)
        assertSame(g, root.bottom!!.bottom)
    }

    @Test
    fun `collect works with same supplied order`() {
        val root = ManagedTree("root", 0uL, 10uL)
        val a = ManagedTree("a", 8uL, 1uL)
        val b = ManagedTree("b", 6uL, 2uL)
        val c = ManagedTree("c", 3uL, 3uL)
        val d = ManagedTree("d", 0uL, 2uL)
        val e = ManagedTree("e", 7uL, 1uL)
        val f = ManagedTree("f", 3uL, 1uL)
        val g = ManagedTree("g", 6uL, 1uL)

        /*
              0123456789
        root: ----------
        a   :         -
        b   :       --
        c   :    ---
        d   : --
        e   :        -
        f   :    -
        g   :       -
        */

        root.offer(a)
        root.offer(b)
        root.offer(c)
        root.offer(d)
        root.offer(e)
        root.offer(f)
        root.offer(g)

        // bottom right left top
        /*
            f ^ c
            c > b
            b v g
            g > e
            b > a
            b < d
            d ^ root
        */

        val collected = f.collect(false).toList()

        assertEquals(
            listOf(c, b, g, e, a, d, root).map { it.value },
            collected.map { it.value }
        )
    }

    @Test
    fun `hierarchy works with the expected order`() {
        val root = ManagedTree("root", 0uL, 10uL)
        val a = ManagedTree("a", 0uL, 2uL)
        val b = ManagedTree("b", 3uL, 3uL)
        val c = ManagedTree("c", 3uL, 1uL)
        val d = ManagedTree("d", 6uL, 2uL)
        val e = ManagedTree("e", 6uL, 1uL)
        val f = ManagedTree("f", 7uL, 1uL)
        val g = ManagedTree("g", 8uL, 1uL)

        /*
              0123456789
        root: ----------
        a   : --
        b   :    ---
        c   :    -
        d   :       --
        e   :       -
        f   :        -
        g   :         -
        */

        root.offer(a)
        root.offer(b)
        root.offer(c)
        root.offer(d)
        root.offer(e)
        root.offer(f)
        root.offer(g)

        /*
        root v a
        a > b
        b v c
        b > d
        d v e
        e > f
        d > g
        */

        val hierarchy = root.hierarchy.toList()

        assertEquals(
            listOf(a, b, c, d, e, f, g).map { it.value },
            hierarchy.map { it.value }
        )
    }
}
