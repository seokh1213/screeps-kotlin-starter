package starter

import screeps.api.Creep
import screeps.api.ERR_NOT_IN_RANGE
import screeps.api.FIND_MY_CONSTRUCTION_SITES
import screeps.api.FIND_MY_STRUCTURES
import screeps.api.FIND_SOURCES
import screeps.api.FIND_STRUCTURES
import screeps.api.Game
import screeps.api.RESOURCE_ENERGY
import screeps.api.Room
import screeps.api.STRUCTURE_EXTENSION
import screeps.api.STRUCTURE_ROAD
import screeps.api.STRUCTURE_SPAWN
import screeps.api.Source
import screeps.api.StoreOwner
import screeps.api.compareTo
import screeps.api.get
import screeps.api.structures.Structure
import screeps.api.structures.StructureController


enum class Role {
    UNASSIGNED,
    HARVESTER,
    BUILDER,
    UPGRADER
}

fun Creep.upgrade(controller: StructureController, source: Source) {
    if (!memory.building && store[RESOURCE_ENERGY] < store.getCapacity(RESOURCE_ENERGY)) {
        if (harvest(source) == ERR_NOT_IN_RANGE) {
            moveTo(source.pos)
        }
        return
    }

    if (memory.building && store[RESOURCE_ENERGY] == 0) {
        memory.building = false
        return
    }

    memory.building = true
    if (upgradeController(controller) == ERR_NOT_IN_RANGE) {
        moveTo(controller.pos)
    }
}

fun Structure.isCriticalDamaged(): Boolean {
    return (hits.toDouble() / hitsMax) < 0.5
}

fun Structure.isDamaged(): Boolean {
    return (hits.toDouble() / hitsMax) < 0.8
}

fun Creep.build(assignedRoom: Room = this.room) {
    if (memory.building && store[RESOURCE_ENERGY] == 0) {
        memory.building = false
        say("🔄 harvest")
    }
    if (!memory.building && store[RESOURCE_ENERGY] == store.getCapacity()) {
        memory.building = true
        say("🚧 build")
    }

    if (memory.building) {
        val repairTarget = (if (memory.repairTargetId.isNotEmpty()) Game.getObjectById<Structure>(memory.repairTargetId) else null)
            ?.takeIf{ it.structureType == STRUCTURE_ROAD && it.isDamaged()}
            ?: assignedRoom.find(FIND_STRUCTURES)
                .filter { it.structureType == STRUCTURE_ROAD && it.isCriticalDamaged() }
                .minByOrNull { it.hits }
                ?.also {
                    memory.repairTargetId = it.id
                }

        if (repairTarget != null && repairTarget.isDamaged()) {
            if (repair(repairTarget) == ERR_NOT_IN_RANGE) {
                moveTo(repairTarget.pos)
            }
        } else {
            val target = assignedRoom.find(FIND_MY_CONSTRUCTION_SITES)
                .minByOrNull { it.pos.getRangeTo(pos) }
            if (target != null) {
                if (build(target) == ERR_NOT_IN_RANGE) {
                    moveTo(target.pos)
                }
            } else {
                moveTo(Game.spawns["Spawn1"]!!)
            }
        }
    } else {
        val sources = room.find(FIND_SOURCES)
        if (harvest(sources[0]) == ERR_NOT_IN_RANGE) {
            moveTo(sources[0].pos)
        }
    }
}

fun Creep.harvest(fromRoom: Room = this.room, toRoom: Room = this.room) {
    if (store[RESOURCE_ENERGY] < store.getCapacity()) {
        val sources = fromRoom.find(FIND_SOURCES)
        if (harvest(sources.last()) == ERR_NOT_IN_RANGE) {
            moveTo(sources.last().pos)
        }
    } else {
        val targets = toRoom.find(FIND_MY_STRUCTURES)
            .filter { (it.structureType == STRUCTURE_EXTENSION || it.structureType == STRUCTURE_SPAWN) }
            .map { it.unsafeCast<StoreOwner>() }
            .filter { it.store[RESOURCE_ENERGY] < it.store.getCapacity(RESOURCE_ENERGY) }

        if (targets.isNotEmpty()) {
            if (transfer(targets[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                moveTo(targets[0].pos)
            }
        } else {
            moveTo(Game.spawns["Spawn1"]!!)
        }
    }
}
