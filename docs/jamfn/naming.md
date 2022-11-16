### 3rd-party Shortcuts

#### Provide Local Statement

```kotlin
withLocal(LocalX provides Y) { Z() }
```

```kotlin
withLocalX(Y) { Z() }
```

#### Get Current Local Statement

```kotlin
LocalX.current
```

```kotlin
currentX
```

#### Creating new scope

```kotlin
XJamfn(*Y, parent = this) { Z() }
```

```kotlin
createSubXJamfn(*Y) { Z() }
```

#### Creating new scope just for result

```kotlin
withLocalX(/* default-value */) { Z() }
```

```kotlin
createSubForX { Z() }
```
