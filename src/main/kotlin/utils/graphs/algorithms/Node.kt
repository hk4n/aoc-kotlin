package utils.graphs.algorithms

import utils.Position

enum class NodeType { regular, start, stop }
data class Node<T>(
    val data: T,
    val position: Position,
    val weight: Double = 0.0,
    val type: NodeType = NodeType.regular
) {
    override fun equals(other: Any?): Boolean {
        return when(other) {
            is Node<*> -> position == other.position
            else -> false
        }
    }

    override fun hashCode(): Int {
        return position.hashCode()
    }
}