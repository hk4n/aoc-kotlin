package day12

import utils.DistanceMethods
import utils.Files
import utils.Position
import utils.graphs.algorithms.AStarShortestPath
import utils.graphs.algorithms.Constraint
import utils.graphs.DirectedGraph
import utils.graphs.algorithms.Node
import utils.graphs.algorithms.NodeType
import utils.graphs.algorithms.shortestPath

typealias StringNode = Node<String>

fun List<String>.parse(): List<StringNode> =
    this.mapIndexed { i, s ->
        s.mapIndexed { j, c ->
            when(c) {
                in 'a'..'z' -> StringNode(c.toString(), Position(i, j), c.code.toDouble())
                'S' -> StringNode("S", Position(i, j), 'a'.code.toDouble(), type = NodeType.start)
                'E' -> StringNode("E", Position(i, j), 'z'.code.toDouble(), type = NodeType.stop)
                else -> error("not allowed weight")
            }
        }
    }.flatten()

fun createGraph(nodes: List<StringNode>): DirectedGraph<StringNode> {
    val graph = DirectedGraph<StringNode>()

    nodes.forEach { currentNode ->
        val (x,y) = currentNode.position
        val up = Position(x, y+1)
        val down = Position(x, y-1)
        val left = Position(x-1, y)
        val right = Position(x+1, y)
        val positions = listOf(up, down, left, right)

        val neighbours = nodes.filter { positions.contains(it.position) }

        neighbours.forEach { neighbour ->
            graph.addEdge(currentNode, neighbour, neighbour.weight)
            graph.addEdge(neighbour, currentNode, currentNode.weight)
        }
    }

    return graph
}

// used to extract x and y from position in the createGraph method
private operator fun Position.component1(): Int = this.x
private operator fun Position.component2(): Int = this.y

val day12constraint: Constraint<String> = { node -> { neighbor -> neighbor.weight - node.weight <= 1 } }

class Day12 {
    fun solveFirst(filename: String) {
        val nodes = Files.read("day12", filename).parse()
        val graph = createGraph(nodes)

        val start = nodes.find { it.type == NodeType.start } ?: error("no start found!")
        val end = nodes.find { it.type == NodeType.stop } ?: error("no end found!")

        val aStar = AStarShortestPath(graph, DistanceMethods.MANHATTAN, day12constraint)
        val paths = aStar.findShortestPath(start, end)

        val shortest = paths?.shortestPath()
        println("no of steps ${shortest?.let { it.size - 1 }}")
    }

    fun solveSecond(filename: String) {
        val nodes = Files.read("day12", filename).parse()
        val graph = createGraph(nodes)
        val aStar = AStarShortestPath(graph, DistanceMethods.MANHATTAN, day12constraint)

        val start = nodes.find { it.type == NodeType.start } ?: error("no start found!")
        val end = nodes.find { it.type == NodeType.stop } ?: error("no end found!")
        val aNodes = nodes.filter { it.data == "a" }.filter { graph.getNeighbors(it).size <= 3 }

        val startNodes = aNodes + start

        val shortestPath = startNodes
            .mapNotNull { aStar.findShortestPath(it, end) }
            .map { it.shortestPath() }.minOfOrNull { it.size - 1 }

        println("no of steps $shortestPath")
    }
}

fun main() {
    val day = Day12()

    println("first puzzle")
    day.solveFirst("input")

    println("second puzzle")
    day.solveSecond("input")
}
