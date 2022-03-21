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

import org.jamplate.jamtree.*
import org.jamplate.jamtree.Side.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

fun assertComputation(tree: Tree<*>, other: Tree<*>, vararg computation: Pair<Side?, Tree<*>?>) {
    assertEquals(computation.toMap(), buildMap {
        putAll(tree compute other)
        entries.removeIf { it.value == null }
    })
}

fun assertIntersection(range: IntRange, other: IntRange, intersection: Intersection) {
    assertIntersection(range.first, range.last + 1, other.first, other.last + 1, intersection)
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

class TreeTest {
    @Test
    fun `relation computations checks`() {
        val document = "ABC0123"
        val reference = document.indices
        val letters = 0..2
        val numbers = 3..6
        val b = 1..1
        val bc = 1..2
        val c0 = 2..3

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

    @Test
    fun `multiple trees with same bounds but different weight`() {
        val root = Tree("root", 0, 10, 0)
        val a = Tree("a", 2, 1, 0)
        val b = Tree("b", 3, 3, -1)
        val c = Tree("c", 3, 3, 0)
        val d = Tree("d", 3, 3, 1)
        val e = Tree("e", 3, 1, 0)
        val f = Tree("f", 5, 1, 0)
        val g = Tree("g", 6, 1, 0)

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

        assertComputation(root, a, TOP to root)
        root offer a

        assertComputation(root, b, LEFT to a)
        root offer b

        assertComputation(root, g, LEFT to b)
        root offer g

        assertComputation(root, e, TOP to b)
        root offer e

        assertComputation(root, f, LEFT to e)
        root offer f

        assertComputation(f, d, BOTTOM to e, TOP to b)
        f offer d

        assertComputation(f, c, BOTTOM to d, TOP to b)
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
        val root = Tree("root", 0, 10)
        val a = Tree("a", 8, 1)
        val b = Tree("b", 6, 2)
        val c = Tree("c", 3, 3)
        val d = Tree("d", 0, 2)
        val e = Tree("e", 5, 1)
        val f = Tree("f", 4, 1)
        val g = Tree("g", 0, 1)

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

        assertComputation(a, b, RIGHT to a)
        a.offer(b)

        assertComputation(a, c, RIGHT to b)
        a.offer(c)

        assertComputation(a, d, RIGHT to c)
        a.offer(d)

        assertComputation(a, e, TOP to c)
        a.offer(e)

        assertComputation(a, f, RIGHT to e, TOP to c)
        a.offer(f)

        assertComputation(a, g, TOP to d)
        a.offer(g)

        assertSame(b, a.left)
        assertSame(c, a.left!!.left)
        assertSame(d, a.left!!.left!!.left)
        assertSame(e, a.left!!.left!!.bottom!!.right)
        assertSame(f, a.left!!.left!!.bottom)
        assertSame(g, a.left!!.left!!.left!!.bottom)

        assertComputation(f, root, BOTTOM to d)
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
    fun `hierarchy sequence collects all trees except receiver`() {
        val root = Tree("root", 0, 10)
        val a = Tree("a", 8, 1)
        val b = Tree("b", 6, 2)
        val c = Tree("c", 3, 3)
        val d = Tree("d", 0, 2)
        val e = Tree("e", 7, 1)
        val f = Tree("f", 3, 1)
        val g = Tree("g", 6, 1)

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

        val hierarchy = f.hierarchy().toList()

        hierarchy.groupBy { it }.forEach { (tree, occurrences) ->
            assertSame(1, occurrences.size, "Tree yield twice: $tree")
        }

        assertEquals(
            setOf(root, a, b, c, d, e, g),
            hierarchy.toSet()
        )
    }
}
