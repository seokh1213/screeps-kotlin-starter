package starter

import screeps.api.CreepMemory
import screeps.utils.memory.memory

var CreepMemory.repairTargetId: String by memory { "" }
var CreepMemory.building: Boolean by memory { false }
var CreepMemory.role by memory(Role.UNASSIGNED)

