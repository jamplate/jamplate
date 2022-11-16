## Jamtree

### Tree

A point in space with connections to other trees.

Each tree can have four trees connected to it:
`top`, `left`, `right` and `bottom`.

A tree connections can be visualized in the
following graph:

```
    A
    |
    B - C - D - E
        |       |
        F - G   H - I
```

#### The connection between a tree and its children

As in the graph above, a tree's parent is not
always on top of it. In fact, only the first child
can be connected to its parent. This also means, a
tree can only be connected to its first child.

#### How to reach the parent of a tree?

The way to reach the parent of a tree is to reach
the leftmost sibling of it and then reach the
parent.

#### How many connections a tree can have at a time?

A tree can have at most `3` connections at a time.
Because it must not have a tree `left` to it and
another on `top` of it.

### Common Types Of Trees

#### Manual Tree

Manual Trees are trees that can be positioned
manually. However, the attaching mechanism isn't.

#### Fantom Tree

Fantom Trees are trees that attaches to another
trees without other trees knowing it. It is a way
to pass data.

#### Index Syntax Trees

Index Syntax Trees (IST) are trees associated with
an offset, length and weight and with this data
can know its correct position in the text or
between other trees.

### Managed Index Syntax Tree (MIST)

Managed Index Syntax Tree (MIST) is a concept of
an automatically sorted tree to reduce the work
of sorting and managing indices of text fragments
in a larger text fragments.
MIST is not opinionated on how syntax should be or
for what should be used.
MIST, in simpler words, is just a utility or a
tool.

#### How are the connections get managed?

The connections are managed automatically and the
user need to only introduce them to each other.

#### How are the connections get corrupted?

The trees must not be corrupted. If so, this is a
bug in this library. The user must not be
concerned about the tree's structure and
management.

### Different types of trees removal

Because trees are two-dimensional, there is
various ways of removing a tree from its
structure.

#### Pop

Popping a tree is the process of de-attaching it
and only it from its tree structure.

The following is an example of popping the tree `X`:

```
    A
    |
    B - X - C
        |
        D - E
        |
        F
```

```
    A
    |
    B - D - E - C
        |
        F

    X
```

As you can see, `X` is the only tree de-attached
and its children got inlined to its place.

#### Clear

Clearing a tree is the process of de-attaching its
children from it.

The following is an example of clearing the tree `X`:

```
    A
    |
    B - X - C
        |
        D - E
        |
        F
```

```
    A
    |
    B - X - C
    
    D - E
    |
    F
```

As you can see, `X` is not de-attached. Only its
children were.

#### Remove

Removing a tree is the process of de-attaching it
and its children.

The following is an example of removing the tree `X`:

```
    A
    |
    B - X - C
        |
        D - E
        |
        F
```

```
    A
    |
    B - C
    
    X
    |
    D - E
    |
    F
```

As you can see, `X` and its children were 
de-attached altogether.
