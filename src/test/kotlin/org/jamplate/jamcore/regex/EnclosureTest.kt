package org.jamplate.jamcore.regex

import org.jamplate.jamcore.BufferRange
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class EnclosureTest {
    @Test
    fun `Nested enclosures calculation`() {
        //           3 2 1 0 0 1 2 3
        //           0123456789 1234
        val input = "( { [ < > ] } )"
        val open = Pattern.compile("[({\\[<]")
        val close = Pattern.compile("[)}\\]>]")

        val actual = computeEnclosure(
            opens = open.matcher(input)
                .useAnchoringBounds(true)
                .useTransparentBounds(true)
                .toList(),
            closes = close.matcher(input)
                .useAnchoringBounds(true)
                .useTransparentBounds(true)
                .toList()
        ).toList()

        val expected = listOf(
            Enclosure(
                BufferRange(6uL, 1uL),
                BufferRange(8uL, 1uL)
            ),
            Enclosure(
                BufferRange(4uL, 1uL),
                BufferRange(10uL, 1uL)
            ),
            Enclosure(
                BufferRange(2uL, 1uL),
                BufferRange(12uL, 1uL)
            ),
            Enclosure(
                BufferRange(0uL, 1uL),
                BufferRange(14uL, 1uL)
            )
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `Nested adjacent enclosures calculation`() {
        //           6 0 0 1 1 2 2 3 3 4 4 5 5 6
        //           0123456789 123456789 123456
        val input = "< ( ) ( ) [ ] [ ] { } { } >"
        val open = Pattern.compile("[({\\[<]")
        val close = Pattern.compile("[)}\\]>]")

        val actual = computeEnclosure(
            opens = open.matcher(input)
                .useAnchoringBounds(true)
                .useTransparentBounds(true)
                .toList(),
            closes = close.matcher(input)
                .useAnchoringBounds(true)
                .useTransparentBounds(true)
                .toList()
        ).toList()

        val expected = listOf(
            // 0
            Enclosure(
                BufferRange(2uL, 1uL),
                BufferRange(4uL, 1uL),
            ),
            // 1
            Enclosure(
                BufferRange(6uL, 1uL),
                BufferRange(8uL, 1uL)
            ),
            // 2
            Enclosure(
                BufferRange(10uL, 1uL),
                BufferRange(12uL, 1uL)
            ),
            // 3
            Enclosure(
                BufferRange(14uL, 1uL),
                BufferRange(16uL, 1uL)
            ),
            // 4
            Enclosure(
                BufferRange(18uL, 1uL),
                BufferRange(20uL, 1uL),
            ),
            // 5
            Enclosure(
                BufferRange(22uL, 1uL),
                BufferRange(24uL, 1uL),
            ),
            // 6
            Enclosure(
                BufferRange(0uL, 1uL),
                BufferRange(26uL, 1uL),
            )
        )

        assertEquals(expected, actual)
    }
}
