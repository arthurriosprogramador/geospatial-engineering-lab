package model

import model.BoundingBox2D
import model.Point2D

/**
 * An item stored in the R-Tree, containing an envelope and user payload.
 */
data class RTreeEntry<T>(
    val boundingBox: BoundingBox2D,
    val value: T
)

/**
 * 2D R-Tree implementation for spatial indexing.
 *
 * @param maxEntries Maximum number of entries per node before a split occurs (branching factor M).
 */
class RTree2D<T>(private val maxEntries: Int = 16) {

    init {
        require(maxEntries >= 2) {
            "Maximum entries should be more than 1."
        }
    }

    private var root: Node<T> = LeafNode(maxEntries)

    fun insert(entry: RTreeEntry<T>) {
        val splitNode = root.insert(entry)
        if (splitNode != null) {
            val newRoot = InternalNode<T>(maxEntries)
            newRoot.children.add(root)
            newRoot.children.add(splitNode)
            newRoot.recalculateBoundingBox()
            root = newRoot
        }
    }

    fun search(queryBox: BoundingBox2D): List<T> {
        val results = mutableListOf<T>()
        root.search(queryBox, results)
        return results
    }

    fun insert(point: Point2D, value: T) {
        val box = BoundingBox2D(point.x, point.y, point.x, point.y)
        insert(RTreeEntry(box, value))
    }

    internal sealed class Node<T>(val maxEntries: Int) {
        var boundingBox: BoundingBox2D = BoundingBox2D(
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY
        )

        abstract fun insert(entry: RTreeEntry<T>): Node<T>?
        abstract fun search(queryBox: BoundingBox2D, results: MutableList<T>)
        abstract fun recalculateBoundingBox()
    }

    internal class LeafNode<T>(maxEntries: Int) : Node<T>(maxEntries) {
        val entries = mutableListOf<RTreeEntry<T>>()

        override fun insert(entry: RTreeEntry<T>): Node<T>? {
            entries.add(entry)
            recalculateBoundingBox()

            return if (entries.size > maxEntries) {
                split()
            } else {
                null
            }
        }

        override fun search(queryBox: BoundingBox2D, results: MutableList<T>) {
            if (!boundingBox.intersects(queryBox)) return

            for ((bb, value) in entries) {
                if (bb.intersects(queryBox)) {
                    results.add(value)
                }
            }
        }

        override fun recalculateBoundingBox() {
            if (entries.isEmpty()) return
            var minX = Double.POSITIVE_INFINITY
            var minY = Double.POSITIVE_INFINITY
            var maxX = Double.NEGATIVE_INFINITY
            var maxY = Double.NEGATIVE_INFINITY

            for (e in entries) {
                val b = e.boundingBox
                if (b.minX < minX) minX = b.minX
                if (b.minY < minY) minY = b.minY
                if (b.maxX > maxX) maxX = b.maxX
                if (b.maxY > maxY) maxY = b.maxY
            }

            boundingBox = BoundingBox2D(minX, minY, maxX, maxY)
        }

        private fun split() : LeafNode<T> {
            val spanX = boundingBox.maxX - boundingBox.minX
            val spanY = boundingBox.maxY - boundingBox.minY

            if (spanX > spanY) {
                entries.sortBy { it.boundingBox.minX }
            } else {
                entries.sortBy { it.boundingBox.minY }
            }

            val mid = entries.size / 2
            val sibling = LeafNode<T>(maxEntries)
            val siblingEntries = entries.subList(mid, entries.size)
            sibling.entries.addAll(siblingEntries)
            siblingEntries.clear()

            this.recalculateBoundingBox()
            sibling.recalculateBoundingBox()
            return sibling
        }
    }

    internal class InternalNode<T>(maxEntries: Int) : Node<T>(maxEntries) {
        val children = mutableListOf<Node<T>>()

        override fun insert(entry: RTreeEntry<T>): Node<T>? {
            val bestChild = chooseBestChild(entry.boundingBox)
            val newSibling = bestChild.insert(entry)

            if (newSibling != null) {
                children.add(newSibling)
            }

            recalculateBoundingBox()

            return if (children.size > maxEntries) {
                split()
            } else {
                null
            }
        }

        override fun search(queryBox: BoundingBox2D, results: MutableList<T>) {
            if (!boundingBox.intersects(queryBox)) return

            for (child in children) {
                child.search(queryBox, results)
            }
        }

        override fun recalculateBoundingBox() {
            if (children.isEmpty()) return
            var minX = Double.POSITIVE_INFINITY
            var minY = Double.POSITIVE_INFINITY
            var maxX = Double.NEGATIVE_INFINITY
            var maxY = Double.NEGATIVE_INFINITY

            for (c in children) {
                val b = c.boundingBox
                if (b.minX < minX) minX = b.minX
                if (b.minY < minY) minY = b.minY
                if (b.maxX > maxX) maxX = b.maxX
                if (b.maxY > maxY) maxY = b.maxY
            }
            boundingBox = BoundingBox2D(minX, minY, maxX, maxY)
        }

        private fun chooseBestChild(targetBox: BoundingBox2D): Node<T> {
            return children.minByOrNull { child ->
                val expandedMinX = minOf(child.boundingBox.minX, targetBox.minX)
                val expandedMinY = minOf(child.boundingBox.minY, targetBox.minY)
                val expandedMaxX = maxOf(child.boundingBox.maxX, targetBox.maxX)
                val expandedMaxY = maxOf(child.boundingBox.maxY, targetBox.maxY)

                val originalArea = (child.boundingBox.maxX - child.boundingBox.minX) *
                        (child.boundingBox.maxY - child.boundingBox.minY)
                val expandedArea = (expandedMaxX - expandedMinX) * (expandedMaxY - expandedMinY)

                expandedArea - originalArea
            } ?: children.first()
        }

        private fun split(): InternalNode<T> {
            val spanX = boundingBox.maxX - boundingBox.minX
            val spanY = boundingBox.maxY - boundingBox.minY

            if (spanX > spanY) {
                children.sortBy { it.boundingBox.minX }
            } else {
                children.sortBy { it.boundingBox.minY }
            }

            val mid = children.size / 2
            val sibling = InternalNode<T>(maxEntries)
            val siblingChildren = children.subList(mid, children.size)
            sibling.children.addAll(siblingChildren)
            siblingChildren.clear()

            this.recalculateBoundingBox()
            sibling.recalculateBoundingBox()
            return sibling
        }
    }
}