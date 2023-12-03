package `2023`.day3

import utils.Files
import utils.Position
import utils.graphs.DirectedGraph
import utils.graphs.algorithms.Node
import utils.graphs.algorithms.NodeType

typealias CharNode = Node<Char>

// used to extract x and y from position in the createGraph method
private operator fun Position.component1(): Int = this.x
private operator fun Position.component2(): Int = this.y

data class NodeContainer(val nodes: List<CharNode>, val maxRow: Int, val maxColumn: Int, val symbols: List<Char>)

fun List<String>.parse(): NodeContainer {
    val symbols: List<Char> = this
        .flatMap { line -> line.toCharArray().toList() }
        .filterNot { c -> c in '0' .. '9' || c == '.' }
        .distinct()

    val maxRow = this.size - 1
    val maxColumn = this[0].length - 1

    val nodes = this.mapIndexed { i, s ->
        s.mapIndexed { j, c ->
            when {
                i == 0 && j == 0 -> CharNode(c, Position(j, i), 0.0, type = NodeType.start)
                i == maxRow && j == maxColumn -> CharNode(c, Position(i, j), 0.0, type = NodeType.stop)
                c in '0'..'9' -> CharNode(c, Position(j, i))
                c == '.' -> CharNode(c, Position(j, i))
                symbols.contains(c) -> CharNode(c, Position(j, i))
                else -> error("Guess we have something wrong here '$c'")
            }
        }
    }.flatten()

    return NodeContainer(nodes, maxRow, maxColumn, symbols)
}

fun createGraph(nodes: List<CharNode>): DirectedGraph<CharNode> {
    val graph = DirectedGraph<CharNode>()

    nodes.forEach { currentNode ->
        val (x,y) = currentNode.position
        val south = Position(x, y+1)
        val north = Position(x, y-1)
        val west = Position(x-1, y)
        val east = Position(x+1, y)

        val southEast = Position(x+1, y+1)
        val southWest = Position(x-1, y+1)
        val northEast = Position(x+1, y-1)
        val northWest = Position(x-1, y-1)

        val neighbourPositions = listOf(south, north, west, east, southEast, southWest, northEast, northWest)

        val neighbours = nodes.filter { neighbourPositions.contains(it.position) }

        neighbours.forEach { neighbour ->
            graph.addEdge(currentNode, neighbour, neighbour.weight)
            graph.addEdge(neighbour, currentNode, currentNode.weight)
        }
    }

    return graph
}

data class NextMoveContainer(val nextRow: Int, val nextNode: CharNode?)
fun getNextMove(currentRow: Int, currentNode: CharNode?, nodeContainer: NodeContainer, graph: DirectedGraph<CharNode>): NextMoveContainer {
    return when {
         (currentNode == null) -> NextMoveContainer(currentRow, null) // last node, this will not be used

        (currentNode.position.x == nodeContainer.maxColumn) -> {
            // step row by 1 and start x from 0
            val nextRow = currentRow + 1
            val nextNode = nodeContainer.nodes.find { it.position.y == nextRow && it.position.x == 0 }

            NextMoveContainer(nextRow, nextNode)
            }
        else  -> {
            // step x with one and find neighbour node
            val nextX = currentNode.position.x + 1
            val nextNode = graph.getNeighbors(currentNode)
                .map { it.vertex }
                .find { it.position.y == currentRow && it.position.x == nextX }

            NextMoveContainer(currentRow, nextNode)
        }
    }
}

tailrec fun rowWiseTraversal(currentRow: Int,
                             nodeContainer: NodeContainer,
                             graph: DirectedGraph<CharNode>, currentNode: CharNode?,
                             valueAcc: List<CharNode>,
                             acc: List<AccContainer>,
                             valueRule: (List<CharNode>, List<AccContainer>, NodeContainer, DirectedGraph<CharNode>) -> ValueAccContainer): List<AccContainer> {

    val (nextRow, nextNode) = getNextMove(currentRow, currentNode, nodeContainer, graph)

    return when {
        currentNode == null -> acc // the end of the recurse

        currentNode.data in '0'..'9' ->
            rowWiseTraversal(nextRow, nodeContainer, graph, nextNode, valueAcc + currentNode, acc, valueRule)

        currentNode.data == '.' || nodeContainer.symbols.contains(currentNode.data) -> {
            val (nextValueAcc, nextAcc) = valueRule(valueAcc, acc, nodeContainer, graph)
            rowWiseTraversal(nextRow, nodeContainer, graph, nextNode, nextValueAcc, nextAcc, valueRule)
        }
        else -> error("seems like some logic is missing for node $currentNode")
    }
}

data class AccContainer(val value: Long, val nodes: List<CharNode>, val gear: CharNode)
data class ValueAccContainer(val nextValueAcc: List<CharNode>, val nextAcc: List<AccContainer>)
fun allSymbols(valueAcc: List<Node<Char>>, acc: List<AccContainer>,
               nodeContainer: NodeContainer, graph: DirectedGraph<CharNode>): ValueAccContainer {
    // if valueAcc is not empty, construct integer and add to acc and reset valueAcc
    return when {
        valueAcc.isNotEmpty() -> {
            // get all neighbours for all nodes, check if any of them are adjacent to a symbol
            val adjacentSymbol = valueAcc.flatMap { node -> graph.getNeighbors(node).map { it.vertex } }
                .find { nodeContainer.symbols.contains(it.data) }

            if (adjacentSymbol != null) {
                // convert
                val value = valueAcc.map { it.data }.joinToString(separator = "").toLong()
                ValueAccContainer(listOf(), acc + AccContainer(value, valueAcc, adjacentSymbol))
            } else {
                ValueAccContainer(listOf(), acc)
            }
        }
        else -> ValueAccContainer(listOf(), acc)
    }
}

fun onlyGear(valueAcc: List<Node<Char>>, acc: List<AccContainer>,
             nodeContainer: NodeContainer, graph: DirectedGraph<CharNode>): ValueAccContainer {
    return when {
        valueAcc.isNotEmpty() -> {
            val gear = valueAcc.flatMap { node -> graph.getNeighbors(node).map { it.vertex } }
                    .find { it.data == '*' }

            if (gear != null) {
                // convert
                val value = valueAcc.map { it.data }.joinToString(separator = "").toLong()
                ValueAccContainer(listOf(), acc + AccContainer(value, valueAcc, gear))
            } else {
                ValueAccContainer(listOf(), acc)
            }
        }
        else -> ValueAccContainer(listOf(), acc)
    }
}

class Day3 {
    fun solveFirst(filename: String): Long {
        val nodeContainer = Files.read("day3", filename, "2023") .parse()
        val graph = createGraph(nodeContainer.nodes)
        val start = nodeContainer.nodes.find { it.type == NodeType.start } ?: error("no start found!")

        return rowWiseTraversal(0, nodeContainer, graph, start, listOf(), listOf(), ::allSymbols)
            .sumOf { it.value }
    }

    fun solveSecond(filename: String): Long {
        val nodeContainer = Files.read("day3", filename, "2023") .parse()
        val graph = createGraph(nodeContainer.nodes)
        val start = nodeContainer.nodes.find { it.type == NodeType.start } ?: error("no start found!")

        return rowWiseTraversal(0, nodeContainer, graph, start, listOf(), listOf(), ::onlyGear)
            .groupBy { it.gear }
            .filter { it.value.size > 1 }
            .map { it.value.fold(1L) { acc, accContainer -> acc * accContainer.value } }
            .sum()
    }
}

fun main() {
    val day = Day3()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
