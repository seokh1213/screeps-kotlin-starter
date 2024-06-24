package screeps.game

import screeps.api.CreepMemory
import screeps.api.RoomMemory
import screeps.creeps.role.Role
import screeps.utils.memory.memory
import screeps.utils.unsafe.jsObject

class CreepMemoryBuilder private constructor() {
    var taskId: String = ""
    var role: Role = Role.UNASSIGNED

    companion object {
        operator fun invoke(block: CreepMemoryBuilder.() -> Unit): CreepMemory {
            return CreepMemoryBuilder().apply(block).build()
        }
    }

    private fun build(): CreepMemory {
        return jsObject {
            role = role
            taskId = taskId
        }
    }
}

var CreepMemory.taskId by memory { "" }
var CreepMemory.role by memory(Role.UNASSIGNED)

var RoomMemory.stage: Int by memory { 0 }


