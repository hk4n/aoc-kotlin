package utils.graphs;

class DirectedGraph<T> {
    private val adjacencyList = mutableMapOf<T, MutableList<Neighbor<T>>>()

    fun addVertex(vertex: T) {
        adjacencyList.computeIfAbsent(vertex) { mutableListOf() }
    }

    fun addEdge(from: T, to: T, weight: Double) {
        addVertex(from)
        addVertex(to)
        val neighbor = Neighbor(to, weight)
        adjacencyList[from]?.takeUnless { adjacencyList[from]?.contains(neighbor)?: false }?.add(neighbor)
    }

    fun getNeighbors(vertex: T): List<Neighbor<T>> {
        return adjacencyList[vertex]?.toList() ?: emptyList()
    }

    data class Neighbor<T>(val vertex: T, val weight: Double)
}
