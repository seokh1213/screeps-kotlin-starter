package screeps.game

import screeps.api.CreepMemory
import screeps.api.RoomMemory
import screeps.creeps.role.Role
import screeps.utils.memory.memory


var CreepMemory.role by memory(Role.UNASSIGNED)

var RoomMemory.stage: Int by memory { 0 }


