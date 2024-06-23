package screeps.taks

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import screeps.api.Source
import screeps.api.structures.Structure
import screeps.serializer.SourceSerializer
import screeps.serializer.StructureSerializer
import screeps.utils.PriorityItem
import screeps.utils.PriorityQueue

enum class TaskType(val priority: Int) {
    HARVEST(1),
    BUILD(2)
}

@Serializable
sealed interface Task : PriorityItem {
    val taskType: TaskType

    companion object {
        fun serialize(task: Task): String {
            return Json.encodeToString(task)
        }

        fun deserialize(serialized: String): Task? {
            return try {
                Json.decodeFromString(serialized)
            } catch (e: Exception) {
                null
            }
        }
    }
}

@Serializable
data class BuildTask(@Serializable(with = StructureSerializer::class) val target: Structure) : Task {
    override val priority: Int = TaskType.BUILD.priority
    override val taskType: TaskType = TaskType.BUILD
}

@Serializable
data class HarvestTask(@Serializable(with = SourceSerializer::class) val source: Source) : Task {
    override val priority: Int = TaskType.HARVEST.priority
    override val taskType: TaskType = TaskType.HARVEST
}

class TaskQueue() : PriorityQueue<Task>() {
    constructor(vararg items: Collection<Task>) : this() {
        items.forEach { enqueueAll(it) }
    }
}
