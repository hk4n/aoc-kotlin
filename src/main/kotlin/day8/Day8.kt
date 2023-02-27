package day8

import utils.Files
import utils.list.transpose

private const val VISIBLE = 1
private const val HIDDEN = 0

data class VisibleTrees(val lastVisibleTreeHeight: Int, val visibleTrees: List<Int>)
fun VisibleTrees.newVisibleTree(tree: Int) = VisibleTrees(
    lastVisibleTreeHeight = tree,
    visibleTrees = this.visibleTrees + VISIBLE)

fun VisibleTrees.newHiddenTree(tree: Int) = VisibleTrees(
    lastVisibleTreeHeight = lastVisibleTreeHeight,
    visibleTrees = this.visibleTrees + HIDDEN)



class Day8 {
    fun solveFirst(filename: String): Long {
        val isTreeVisible = { tree:Int, last:VisibleTrees -> (tree > last.lastVisibleTreeHeight) }
        val isLastTreeVisible = {last: VisibleTrees -> last.visibleTrees.isEmpty() || last.visibleTrees.last() == VISIBLE }
        val isLastTreeHidden = {last: VisibleTrees -> last.visibleTrees.isEmpty() || last.visibleTrees.last() == HIDDEN }

        fun visibleTrees(trees: List<List<Int>>): List<List<Int>> {
            val visibleTrees = trees.map {
                it.fold(VisibleTrees(-1, emptyList())) { acc, tree -> when {
                    isLastTreeVisible(acc) and isTreeVisible(tree, acc) -> acc.newVisibleTree(tree)
                    isLastTreeHidden(acc) and isTreeVisible(tree, acc) -> acc.newVisibleTree(tree)
                    else -> acc.newHiddenTree(tree)
                }
                }
            }

            return visibleTrees.map { it.visibleTrees }
        }

        val trees = Files.read("day8", filename)
            .map { it.toCharArray() }
            .map { it.map(Char::digitToInt) }

        val treesLeft = visibleTrees(trees)
        val treesTop = visibleTrees(trees.transpose()).transpose()
        val treesRight = visibleTrees(trees.map{ it.reversed() }).map { it.reversed() }
        val treesBottom = visibleTrees(trees.transpose().map { it.reversed() }).map { it.reversed() }.transpose()

        println("trees left")
        treesLeft.forEach{ println(it.joinToString("", "", ""))}
        println()

        println("trees right")
        treesRight.forEach{ println(it.joinToString("", "", ""))}
        println()

        println("trees top")
        treesTop.forEach{ println(it.joinToString("", "", ""))}
        println()

        println("trees bottom")
        treesBottom.forEach{ println(it.joinToString("", "", ""))}
        println()

        val visibleTreesPerSide = listOf(treesLeft, treesRight, treesTop, treesBottom)

       val visibleTrees = visibleTreesPerSide.fold(emptyList<List<Int>>()) {
           acc, lists ->  when {
               acc.isEmpty() -> lists
               else /* merge visible trees */ -> {
                   val columns = acc.first().size
                   val rows = acc.size

                   Array(rows) { row ->
                       Array(columns) { column ->
                           if (acc[row][column] == VISIBLE || lists[row][column] == VISIBLE) VISIBLE
                           else HIDDEN
                       }.toList()
                   }.toList()
               }
               }
           }

        visibleTrees.forEach{ println(it.joinToString("", "", ""))}

        return visibleTrees.flatten().sum().toLong()
    }

    data class TreeView(val noOfTrees: Int, val tree: Int)
    fun solveSecond(filename: String): Long {

        fun viewOfThreesByRow(tree: List<List<Int>>): List<List<TreeView>> =
            tree.map { row ->
                row.mapIndexed { index, tree ->
                    val left = row.take(index).reversed() // reversed to be able to go left to right
                    val right = row.drop(index.inc())

                    //  stop at the first tree that is same height or taller
                    val countTrees = { trees: List<Int> ->
                        trees.fold(TreeView(0, -1)) { acc, t ->
                            when {
                                acc.noOfTrees == 0 -> TreeView(1, t) // first hit
                                tree <= t && tree > acc.tree -> TreeView(acc.noOfTrees.inc(), t)
                                tree <= t && tree <= acc.tree -> TreeView(acc.noOfTrees, t)// stop
                                tree > t && tree <= acc.tree -> TreeView(acc.noOfTrees, acc.tree)// stop
                                else -> TreeView(acc.noOfTrees.inc(), t)
                            }
                        }
                    }

                    val leftCount = countTrees(left).noOfTrees
                    val rightCount = countTrees(right).noOfTrees

                    TreeView(leftCount + rightCount, tree)
                }
            }

        val trees = Files.read("day8", filename)
            .map { it.toCharArray() }
            .map { it.map(Char::digitToInt) }

        val treesLeft = viewOfThreesByRow(trees)
        val treesTop = viewOfThreesByRow(trees.transpose()).transpose()

        // TODO! merge left and top and calculate the scenic score for each tree
        //       and filter out the highest scenic score


        return TODO("Provide the return value")
    }
}

fun main() {
    val day = Day8()

    println("first puzzle")
    //println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("example_input"))
}
