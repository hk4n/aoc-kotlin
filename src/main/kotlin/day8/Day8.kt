package day8

import utils.Files
import utils.list.combineWith
import utils.list.transpose

private const val VISIBLE = 1
private const val HIDDEN = 0

fun Int.atLeastOne(): Int = if (this == 0) 1 else this

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

        val visibleTreesPerSide = listOf(treesLeft, treesRight, treesTop, treesBottom)

        val visibleTrees = visibleTreesPerSide.fold(emptyList<List<Int>>()) { acc, treesToMerge ->
            acc.combineWith(treesToMerge) { row, column, a, b ->
                if (a[row][column] == VISIBLE || b[row][column] == VISIBLE) VISIBLE
                else HIDDEN
            }
        }

        visibleTrees.forEach{ println(it.joinToString("", "", ""))}

        return visibleTrees.flatten().sum().toLong()
    }

    data class TreeView(val scenicScore: Int, val tree: Int) {
        override fun toString(): String = "($scenicScore, $tree)"
    }

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
                                acc.scenicScore == 0 -> TreeView(1, t) // first hit
                                tree <= t && tree > acc.tree -> TreeView(acc.scenicScore.inc(), t)
                                tree <= t && tree <= acc.tree -> TreeView(acc.scenicScore, t)// stop
                                tree > t && tree <= acc.tree -> TreeView(acc.scenicScore, acc.tree)// stop
                                else -> TreeView(acc.scenicScore.inc(), t)
                            }
                        }
                    }

                    val leftScore = countTrees(left).scenicScore.atLeastOne()
                    val rightScore = countTrees(right).scenicScore.atLeastOne()

                    TreeView(leftScore * rightScore, tree)
                }
            }

        val trees = Files.read("day8", filename)
            .map { it.toCharArray() }
            .map { it.map(Char::digitToInt) }

        val treesLeft = viewOfThreesByRow(trees)
        val treesTop = viewOfThreesByRow(trees.transpose()).transpose()

        val mergedTrees = treesLeft.combineWith(treesTop) { row, column, a, b ->
            val aCell = a[row][column]
            val bCell = b[row][column]
            TreeView(aCell.scenicScore.atLeastOne() * bCell.scenicScore.atLeastOne(), aCell.tree)
        }

        val bestTree = mergedTrees
            .fold(TreeView(0, -1)){highestScoreTree, treeViews ->
                (treeViews + highestScoreTree).filter { tree -> tree.scenicScore >= highestScoreTree.scenicScore }
                    .sortedByDescending(TreeView::scenicScore)
                    .first()
            }

        println(bestTree)
        return bestTree.scenicScore.toLong()
    }
}

fun main() {
    val day = Day8()

    println("first puzzle")
    println(day.solveFirst("input"))

    println("second puzzle")
    println(day.solveSecond("input"))
}
