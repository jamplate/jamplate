package org.jamplate.jamfn

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JamfnTest {
    @Test
    fun `Simple HelloWorld Program`() {
        val out = StringBuilder()

        @JamfnComponent
        fun Jamfn.HelloWorld() {
            withLocalName("HelloWorld") {
                out.append("Hello World")
            }
        }

        Jamfn().whileSustained {
            HelloWorld()
        }

        assertEquals("Hello World", out.toString())
    }

    @Test
    fun `Simple Locals Program`() {
        val out = StringBuilder()

        val LocalMessage = jamfnLocalOf {
            "Default Message"
        }

        @JamfnComponent
        fun Jamfn.MessagePrinter() {
            withLocalName("MessagePrinter") {
                val currentMessage = LocalMessage.current

                out.append(currentMessage)
            }
        }

        Jamfn().whileSustained {
            withLocal(LocalMessage provides "Hello World") {
                MessagePrinter()
            }
        }

        assertEquals("Hello World", out.toString())
    }

    @Test
    fun `Simple use of sustain`() {
        val out = StringBuilder()

        val message = "Hello World"
        var index = 0

        Jamfn().whileSustained {
            if (index < message.length) {
                out.append(message[index])
                index++
                sustain
            } else {
                out.append("\n")
            }
        }

        assertEquals("Hello World\n", out.toString())
    }

    @Test
    fun `Simple use of withLocalName`() {
        Jamfn().whileSustained {
            assertEquals("", currentName)
            assertEquals("", currentFullname)

            withLocalName("Comp1") {
                assertEquals("Comp1", currentName)
                assertEquals("::Comp1", currentFullname)

                withLocalName("Comp2") {
                    assertEquals("Comp2", currentName)
                    assertEquals("::Comp1::Comp2", currentFullname)
                }
            }
        }
    }
}
