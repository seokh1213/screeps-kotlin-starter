package screeps.game.extension

import screeps.api.BODYPART_COST
import screeps.api.BodyPartConstant
import screeps.api.FIND_MY_STRUCTURES
import screeps.api.RESOURCE_ENERGY
import screeps.api.ScreepsReturnCode
import screeps.api.get
import screeps.api.options
import screeps.api.structures.StructureExtension
import screeps.api.structures.StructureSpawn
import screeps.game.CreepMemoryBuilder
import screeps.taks.SpawnTask

fun StructureSpawn.canSpawn(body: Array<BodyPartConstant>): Boolean {
    if (spawning != null) {
        return false
    }

    val cost = body.sumOf { BODYPART_COST[it] ?: 0 }

    val spawnAvailableEnergy = store.getUsedCapacity(RESOURCE_ENERGY) ?: 0
    if (cost <= spawnAvailableEnergy) {
        return true
    }

    val extensions = room.find(FIND_MY_STRUCTURES).filterIsInstance<StructureExtension>()
    val extensionsAvailableEnergy = extensions.sumOf { it.store.getUsedCapacity(RESOURCE_ENERGY) ?: 0 }

    return cost <= (spawnAvailableEnergy + extensionsAvailableEnergy)
}


fun StructureSpawn.spawnCreep(context: SpawnTask.SpawnContext): ScreepsReturnCode {
    return spawnCreep(context.body, context.name, options { memory = CreepMemoryBuilder { role = context.role } })
}
