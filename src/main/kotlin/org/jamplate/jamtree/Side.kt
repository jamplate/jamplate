package org.jamplate.jamtree

/**
 * An enumeration of the sides of a tree.
 *
 * @author LSafer
 * @since 0.4.0 ~2022.03.12
 */
enum class Side {
    /**
     * The top side.
     *
     * @see Relation.PARENT
     * @see Dominance.CONTAIN
     * @see Intersection.CONTAINER
     * @see Intersection.AHEAD
     * @see Intersection.BEHIND
     */
    TOP {
        override val opposite: Side get() = BOTTOM
    },

    /**
     * The left side.
     *
     * @see Relation.PREVIOUS
     * @see Dominance.NONE
     * @see Intersection.BACK
     * @see Intersection.BEFORE
     */
    LEFT {
        override val opposite: Side get() = RIGHT
    },

    /**
     * The right side.
     *
     * @see Relation.NEXT
     * @see Dominance.NONE
     * @see Intersection.FRONT
     * @see Intersection.AFTER
     */
    RIGHT {
        override val opposite: Side get() = LEFT
    },

    /**
     * The bottom side.
     *
     * @see Relation.CHILD
     * @see Dominance.PART
     * @see Intersection.FRAGMENT
     * @see Intersection.START
     * @see Intersection.END
     */
    BOTTOM {
        override val opposite: Side get() = TOP
    };

    /**
     * The opposite side of this side.
     *
     * @since 0.4.0 ~2022.03.12
     */
    abstract val opposite: Side
}
