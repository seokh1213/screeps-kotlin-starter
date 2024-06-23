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
import screeps.utils.Queue

enum class TaskType(val priority: Int) {
    HARVEST(1),
    BUILD(2)
}

@Serializable
sealed interface Task : PriorityItem {
    val taskId: String
    val taskType: TaskType
    override val priority: Int get() = taskType.priority

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
data class BuildTask(
    override val taskId: String,
    @Serializable(with = StructureSerializer::class) val target: Structure
) : Task {
    override val taskType: TaskType = TaskType.BUILD
}

@Serializable
data class HarvestTask(
    override val taskId: String,
    @Serializable(with = SourceSerializer::class) val source: Source
) : Task {
    override val taskType: TaskType = TaskType.HARVEST
}

class TaskQueue : Queue<Task> by PriorityQueue() {
    fun tasks(block: () -> List<Task>) {
        enqueueAll(block())
    }

    companion object {
        operator fun invoke(block: TaskQueue.() -> Unit): TaskQueue {
            return TaskQueue().apply(block)
        }
    }
}
