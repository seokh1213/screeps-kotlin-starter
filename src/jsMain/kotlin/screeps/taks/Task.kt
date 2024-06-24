package screeps.taks

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import screeps.api.BodyPartConstant
import screeps.api.Source
import screeps.api.structures.Structure
import screeps.creeps.role.Role
import screeps.serializer.SourceSerializer
import screeps.serializer.StructureSerializer
import screeps.utils.PriorityItem
import screeps.utils.PriorityQueue
import screeps.utils.Queue

enum class TaskType(val priority: Int) {
    SPAWN(0),
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
    @Serializable(with = StructureSerializer::class) val target: Structure,
) : Task {
    override val taskType: TaskType = TaskType.BUILD
}

data class SpawnTask(
    override val taskId: String,
    val context: SpawnContext,
) : Task {
    override val taskType: TaskType = TaskType.SPAWN

    data class SpawnContext(val name: String, val body: Array<BodyPartConstant>, val role: Role) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class.js != other::class.js) return false

            other as SpawnContext

            if (name != other.name) return false
            if (!body.contentEquals(other.body)) return false
            if (role != other.role) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + body.contentHashCode()
            result = 31 * result + role.hashCode()
            return result
        }
    }
}

@Serializable
data class HarvestTask(
    override val taskId: String,
    @Serializable(with = SourceSerializer::class) val source: Source,
) : Task {
    override val taskType: TaskType = TaskType.HARVEST
}

class TaskQueue private constructor() : Queue<Task> by PriorityQueue() {
    companion object {
        inline operator fun invoke(block: Builder.() -> Unit): TaskQueue {
            return Builder().apply(block).build()
        }
    }

    class Builder {
        private val tasks = mutableListOf<Task>()

        fun tasks(block: () -> List<Task>) {
            tasks.addAll(block())
        }

        fun task(block: () -> Task?) {
            block()?.let { tasks.add(it) }
        }

        fun build(): TaskQueue {
            return TaskQueue().also { it.enqueueAll(tasks) }
        }
    }
}
