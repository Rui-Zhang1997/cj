package cjcollections

data class Node(val key: String, val value: Int, var prev: Node? = null, var next: Node? = null)
class SITuple(val key: String, val value: Int) {
    override fun toString(): String {
        return "${key}: ${value}"
    }
}

class SortedLinkedList {
    var root: Node? = null
    var head: Node? = null
    var size: Int = 0
    fun insert(node: Node) {
        if (head == null) {
            root = node
            head = node
        } else if (head!!.value <= node.value) {
            head!!.next = node
            node.prev = head
            head = node
        } else if (root!!.value >= node.value) {
            root!!.prev = node
            node.next = root!!
            root = node
        } else {
            var n: Node? = root!!
            while (n != null && n.value <= node.value) {
                n = n.next
            }
            n = n!!.prev
            node.next = n?.next
            node.prev = n
            if (n?.next != null) {
                n.next!!.prev = node
            }
            n!!.next = node
            size += 1
        }
    }

    fun insertMap(map: Map<String, Int>) {
        for (key in map.keys) {
            this.insert(Node(key, map.get(key)!!))
        }
    }

    fun toSITuple(maxFirst: Boolean = false): List<SITuple> {
        val sortedMap: MutableList<SITuple> = mutableListOf<SITuple>()
        val it: (Node) -> Node? = if (maxFirst) { n: Node -> n.prev } else { n: Node -> n.next }
        var n: Node? = if (maxFirst) head else root
        var i: Int = 0
        while (n != null) {
            sortedMap.add(SITuple(n.key, n.value))
            n = it(n)
        }
        return sortedMap
    }
}