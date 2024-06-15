package starter


import extension.analyzeRoom
import extension.availableSources
import extension.countAccessibleDirections
import screeps.api.BODYPART_COST
import screeps.api.BodyPartConstant
import screeps.api.CARRY
import screeps.api.Creep
import screeps.api.CreepMemory
import screeps.api.ERR_BUSY
import screeps.api.ERR_NOT_ENOUGH_ENERGY
import screeps.api.FIND_MY_CONSTRUCTION_SITES
import screeps.api.Game
import screeps.api.MOVE
import screeps.api.OK
import screeps.api.WORK
import screeps.api.component1
import screeps.api.component2
import screeps.api.get
import screeps.api.iterator
import screeps.api.options
import screeps.api.structures.StructureSpawn
import screeps.api.values
import screeps.utils.unsafe.jsObject

fun gameLoop() {
    // 1-room code
    // init
    val spawn = (Game.spawns.values.firstOrNull() ?: return)
    val room = spawn.room.also { it.analyzeRoom() }
    val sourceList = room.memory.availableSources.flatMap { source ->
        List(source.countAccessibleDirections()) { source }
    }
    val upgraderCreepMaxCount = sourceList.size

    // spawn
    if (Game.creeps.values.count { it.memory.role == Role.HARVESTER } < 2) {
        val newName = "Harvester${Game.time}"
        val result = spawn.spawnCreep(arrayOf(WORK, CARRY, MOVE), newName, options {
            memory = jsObject<CreepMemory> { role = Role.HARVESTER }
        })

        if (result == OK) {
            console.log("Spawning new harvester: $newName")
        }
    } else if (Game.creeps.values.count { it.memory.role == Role.UPGRADER } < upgraderCreepMaxCount) {
        val newName = "Upgrader${Game.time}"
        val result = spawn.spawnCreep(arrayOf(WORK, CARRY, MOVE), newName, options {
            memory = jsObject<CreepMemory> { role = Role.UPGRADER }
        })

        if (result == OK) {
            console.log("Spawning new upgrader: $newName")
        }
    }

    // creep
    var upgraderIndex = 0
    for ((_, creep) in Game.creeps) {
        when (creep.memory.role) {
            Role.HARVESTER -> creep.harvest()
            Role.BUILDER -> creep.build()
            Role.UPGRADER -> {
                creep.upgrade(spawn.room.controller!!, sourceList[upgraderIndex++])
            }
        }
    }
}


private fun spawnCreeps(
    creeps: Array<Creep>,
    spawn: StructureSpawn
) {

    val body = arrayOf<BodyPartConstant>(WORK, CARRY, MOVE)

    if (spawn.room.energyAvailable < body.sumOf { BODYPART_COST[it]!! }) {
        return
    }

    val role: Role = when {
        creeps.count { it.memory.role == Role.HARVESTER } < 2 -> Role.HARVESTER

        creeps.none { it.memory.role == Role.UPGRADER } -> Role.UPGRADER

        spawn.room.find(FIND_MY_CONSTRUCTION_SITES).isNotEmpty() &&
                creeps.count { it.memory.role == Role.BUILDER } < 2 -> Role.BUILDER

        else -> return
    }

    val newName = "${role.name}_${Game.time}"
    val code = spawn.spawnCreep(body, newName, options {
        memory = jsObject<CreepMemory> { this.role = role }
    })

    when (code) {
        OK -> console.log("spawning $newName with body $body")
        ERR_BUSY, ERR_NOT_ENOUGH_ENERGY -> run { } // do nothing
        else -> console.log("unhandled error code $code")
    }
}
