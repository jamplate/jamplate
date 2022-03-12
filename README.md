## Jamtree

A general purpose compiler heart.

> This is a component of the compiler of the [jamplate](https://jamplate.org) programming language.

### 1 Tree

A point in space with connections to other trees.

Each tree can have four trees connected to it: 
`top`, `left`, `right` and `bottom`.

A tree connections can be visualized in the following graph:

```
    A
    |
    B - C - D - E
        |       |
        F - G   H - I
```

#### 1.1 The connection between a tree and its children
As in the graph above, a tree's parent is not always on top of it.
In fact, only the first child can be connected to its parent.
This also means, a tree can only be connected to its first child.

#### 1.2 How to reach the parent of a tree?
The way to reach the parent of a tree is to reach the leftmost sibling
of it and then reach the parent.

#### 1.3 How many connections a tree can have at a time?
As in section `1.1`, a tree can have at most `3` connections at a time.
Because it must not have a tree `left` to it and another on `top` of it.

#### 1.4 How are the connections get managed?
The connections are managed automatically and the 
user need to only introduce them to each other.

#### 1.5 How are the connections get corrupted?
The trees must not be corrupted. If so, this is a bug in this library.
The user must not be concerned about the tree's structure and management.

#### 1.6 Is this library thread-safe?
Unfortunately, no. But, it might be in the future.

### 2 Structure

An imaginary component describing trees connected together.

When saying the `structure` of a tree, it refers to the tree itself 
and the trees connected to it.
