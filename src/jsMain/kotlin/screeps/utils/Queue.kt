package screeps.utils

interface PriorityItem : Comparable<PriorityItem> {
    /**
     * Priority of the item. Lower value means higher priority.
     */
    val priority: Int

    override fun compareTo(other: PriorityItem): Int {
        return priority.compareTo(other.priority)
    }
}

interface Queue<T> : Iterable<T> {
    fun enqueueAll(items: Collection<T>)
    fun enqueue(item: T)
    fun dequeue(): T?
    fun peek(): T?
    fun isEmpty(): Boolean
    fun size(): Int
}

class ListQueue<T>() : Queue<T> {
    private val items = mutableListOf<T>()

    constructor(items: Collection<T>) : this() {
        this.items.addAll(items)
    }

    override fun enqueue(item: T) {
        items.add(item)
    }

    override fun enqueueAll(items: Collection<T>) {
        this.items.addAll(items)
    }

    override fun dequeue(): T? {
        return items.removeFirstOrNull()
    }

    override fun peek(): T? {
        return items.firstOrNull()
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun size(): Int {
        return items.size
    }

    override fun iterator(): Iterator<T> {
        return items.iterator()
    }
}

class PriorityQueue<T : PriorityItem> : Queue<T> {
    private val items = mutableListOf<T>()

    override fun enqueue(item: T) {
        items.add(item)
        items.sort()
    }

    override fun enqueueAll(items: Collection<T>) {
        this.items.addAll(items)
        this.items.sort()
    }

    override fun dequeue(): T? {
        return items.removeFirstOrNull()
    }

    override fun peek(): T? {
        return items.firstOrNull()
    }

    override fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun size(): Int {
        return items.size
    }

    override fun iterator(): Iterator<T> {
        return items.iterator()
    }
}

