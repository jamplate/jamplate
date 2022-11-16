# Jamfn

A functional approach to process data.

> This component is highly inspired by React and Jetpack Compose

### Getting Started

With no jsx-like mechanism or a compiler plugin,
this component relays on scopes.
The following example is how to print `HelloWorld`
using jamfn.

```kotlin
@JamfnComponent
fun Jamfn.HelloWorld() {
    withLocalName("HelloWorld") {
        println("Hello World")
    }
}

fun main() {
    Jamfn().whileSustained {
        HelloWorld()
    }
}
```

####  Locals (Contexts)

To pass global variables to code downstream it is
better to use the Local mechanism.

The following is an example of how to create a new
local, pass a value to it, and consume the value.

```kotlin
val LocalMessage = jamfnLocalOf {
    // this is the default value of the local
    "Default Message"
}

@JamfnComponent
fun Jamfn.MessagePrinter() {
    withLocalName("MessagePrinter") {
        val currentMessage = LocalMessage.current

        println(currentMessage)
    }
}

fun main() {
    Jamfn().whileSustained {
        withLocal(LocalMessage provides "Hello World") {
            MessagePrinter()
        }
    }
}
```

#### The use of `sustain`

Sustaining a scope means making it rerun after
finish.

The following is an example of using sustain to
print `Hello World`:

```kotlin
fun main() {
    val message = "Hello World"
    var index = 0

    Jamfn().whileSustained {
        if (index < message.length) {
            print(message[index])
            index++
            sustain
        } else {
            println()
        }
    }
}
```

#### The use of withLocalName

The local name is the name of the scope.
`withLocalName` is a way to create a new scope
with a different name.
By default, a scope will inherit the name of its
parent.
Using `currentFullname` will return the name of
this scope and all the scopes above it.

The following example is a demonstration of how
scope names work.

```kotlin
fun main() {
    Jamfn().whileSustained {
        println(currentName) // prints ""
        println(currentFullname) // prints ""

        withLocalName("Comp1") {
            println(currentName) // prints "Comp1"
            println(currentFullname) // prints "::Comp1"

            withLocalName("Comp2") {
                println(currentName) // prints "Comp2"
                println(currentFullname) // prints "::Comp1::Comp2"
            }
        }
    }
}
```
