package utils.graphs.algorithms

import utils.DistanceMethods
import utils.Position
import utils.graphs.DirectedGraph
import java.util.*

data class InternalNode<T>(
    val f: Double,
    val g: Double,
    val node: Node<T>,
    val parent: InternalNode<T>?
): Comparable<InternalNode<T>> {
    // used to ensure the priority queue is ordered smallest value first
    override fun compareTo(other: InternalNode<T>): Int = when {
        f > other.f -> 1
        f < other.f -> -1
        else -> 0
    }

    override fun equals(other: Any?): Boolean = when(other) {
        is InternalNode<*> -> other.node.position == node.position
        else -> super.equals(other)
    }

    override fun hashCode(): Int {
        return node.position.hashCode()
    }
}

typealias Constraint<T> = (Node<T>) -> (DirectedGraph.Neighbor<Node<T>>) -> Boolean

class AStarShortestPath<T>(
    private val graph: DirectedGraph<Node<T>>,
    private val heuristicsMethods: DistanceMethods,
    private val constraint: Constraint<T> = { node -> { neighbor -> true } }
) {
    fun g(from: InternalNode<T>, to: DirectedGraph.Neighbor<Node<T>>): Double = from.g + to.weight
    fun h(from: Node<T>, to: Node<T>): Double = from.position.distance(to.position, heuristicsMethods)
    fun f(current: InternalNode<T>, neighbour: DirectedGraph.Neighbor<Node<T>>, to: Node<T>): Double = g(current, neighbour) + h(neighbour.vertex, to)

    private fun createInternalNode(currentNode: InternalNode<T>, neighbour: DirectedGraph.Neighbor<Node<T>>, endNode: Node<T>): InternalNode<T> =
        InternalNode(f(currentNode, neighbour, endNode), g(currentNode, neighbour), neighbour.vertex, currentNode)

    fun findShortestPath(start: Node<T>, end: Node<T>): HashMap<Position, InternalNode<T>>? {

        tailrec fun recurse(
            openNodes: PriorityQueue<InternalNode<T>>,
            endNode: Node<T>,
            closedNodes: HashMap<Position, InternalNode<T>>
        ): HashMap<Position, InternalNode<T>>? {

            if (openNodes.isEmpty())
                return null // did not find any path

            val currentNode = openNodes.poll()

            if (currentNode.node.type == NodeType.stop) {
                closedNodes[currentNode.node.position] = currentNode
                return closedNodes // found path
            }

            val neighbours = this.graph.getNeighbors(currentNode.node)
                .filter(constraint(currentNode.node))
                //.filter { constraint.apply(currentNode.node)(it) }

            // create open nodes from neighbours
            val newOpenNodes = neighbours
                .map { createInternalNode(currentNode, it, endNode)}
                .filterNot { closedNodes.contains(it.node.position) } // remove nodes that's already visited
                .mapNotNull { newNode ->
                    // remove open node if worse than the new node
                    val existingNode = openNodes.find { it == newNode }
                    when {
                        existingNode == null -> newNode
                        newNode.f < existingNode.f -> { // replace if newNode is better
                            openNodes.remove(existingNode)
                            newNode
                        }
                        else -> null// keep the old node
                    }
                }

            openNodes.addAll(newOpenNodes)

            closedNodes[currentNode.node.position] = currentNode
            return recurse(openNodes, endNode, closedNodes)
        }

        // initial setup
        val startNode = InternalNode(h(start, end), 0.0, start, null)
        val openQueue = PriorityQueue<InternalNode<T>>()
        openQueue.add(startNode)

        // let's find the shortest path to the end node
        return recurse(openQueue, end, hashMapOf())
    }
}

fun List<InternalNode<*>>.shortestPath(): List<InternalNode<*>> {
    fun recurseCollectRoute(
        node: InternalNode<*>,
        nodes: List<InternalNode<*>>,
        acc: List<InternalNode<*>>
    ): List<InternalNode<*>> {
        return when(node.parent) {
            null -> acc + node
            else -> recurseCollectRoute(node.parent, nodes, acc + node)
        }
    }
    return recurseCollectRoute(this.first { it.node.type == NodeType.stop }, this, emptyList())
}

fun <T> HashMap<Position, InternalNode<T>>.shortestPath(): List<InternalNode<*>> =
    this.values.toList().shortestPath()