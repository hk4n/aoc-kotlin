package day7

import utils.Files
import java.util.UUID

open class Node(val name: String, val size: Long = 0, val parentNode: Node?, var childNode: List<Node>, val id: UUID = UUID.randomUUID()) {
    override fun toString(): String {
        val type: String = when(this){
            is Directory -> "|"
            is File -> "-"
            else -> ""
        }
        return "$type $name $size"
    }
}

class Directory(name: String, parentNode: Node?, childNode: List<Node>) : Node(name, size = 0, parentNode, childNode )
class File(name: String, size: Long, parentNode: Node?) : Node(name, size, parentNode, emptyList())

class Day7 {

    fun parse(filename: String): Node {
        val root: Node = Directory(name = "/", parentNode = null, childNode = emptyList())
        Files.read("day7", filename)
            .fold(root) { node, command ->
                when {
                    // change to root
                    command == "$ cd /" -> root

                    // change directory back
                    (command == "$ cd ..") -> node.parentNode ?: node

                    // change directory
                    command.startsWith("$ cd") -> {
                        val (_, _, dir) = command.split(" ")
                        node.childNode.first { it.name == dir }
                    }

                    // list
                    command == "$ ls" -> node// next

                    // directory
                    command.startsWith("dir") -> {
                        node.childNode = node.childNode + Directory(
                            name = command.split(" ").last(),
                            parentNode = node,
                            childNode = emptyList())
                        node
                    }

                    // file
                    command[0].isDigit() -> {
                        val (size, name) = command.split(" ")
                        node.childNode = node.childNode + File(
                            name = name,
                            size = size.toLong(),
                            parentNode = node
                        )
                        node
                    }

                    // error
                    else -> throw RuntimeException("this should not happen!!!")
                }
            }
        return root
    }

    fun calculateDirectorySize(node: Node): List<Node> =
        when(node) {
            is Directory -> {
                val childDirs = node.childNode.filterIsInstance<Directory>()
                val childFiles = node.childNode.filterIsInstance<File>()

                val dirsWithSize = childDirs.flatMap(::calculateDirectorySize)

                val currentChildDirsWithSize = dirsWithSize.filter { it.id in childDirs.map(Node::id) }
                val currentSize = (currentChildDirsWithSize + childFiles).sumOf(Node::size)

                val currentDirWithSize = Node(node.name, currentSize,node.parentNode, node.childNode, node.id)

                dirsWithSize + currentDirWithSize
            }
            else -> throw RuntimeException("unknown node type! $node")
        }

    fun solveFirst(filename: String): Long {
        val root = parse(filename)

        return calculateDirectorySize(root)
            .filter { it.size <= 100000 }
            .sumOf(Node::size)
    }

    fun solveSecond(filename: String) {
        val root = parse(filename)

        val dirs = calculateDirectorySize(root)

        val totalSize = 70000000
        val updateSize = 30000000
        val rootWithSize = dirs.first { it.name == "/" }

        val unusedSpace = totalSize - rootWithSize.size

        println()
        println("total  $totalSize\nused   ${rootWithSize.size}\nunused $unusedSpace")

        val dirToDelete = dirs.filter { it.size + unusedSpace >= updateSize }.minBy(Node::size)
        println("directory to delete is: $dirToDelete")
    }
}

fun main() {
    val day = Day7()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    day.solveSecond("input")
}
