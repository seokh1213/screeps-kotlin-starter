package starter


import extension.analyzeRoom
import extension.availableSources
import extension.blueprintBuilt
import extension.countAccessibleDirections
import extension.getCornerDirections
import extension.getDirections
import extension.isAccessible
import extension.x
import extension.y
import model.MAX_CONTAINER_COUNT
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
import screeps.api.STRUCTURE_CONTAINER
import screeps.api.STRUCTURE_EXTENSION
import screeps.api.STRUCTURE_ROAD
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
    val terrain = room.getTerrain()
    val sourceList = room.memory.availableSources.flatMap { source ->
        List(source.countAccessibleDirections()) { source }
    }
    val upgraderCreepMaxCount = sourceList.size

    // spawn
    if (Game.creeps.values.count { it.memory.role == Role.HARVESTER } < 4) {
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
    } else if (Game.creeps.values.count { it.memory.role == Role.BUILDER } < 4) {
        val newName = "Builder${Game.time}"
        val result = spawn.spawnCreep(arrayOf(WORK, CARRY, MOVE), newName, options {
            memory = jsObject<CreepMemory> { role = Role.BUILDER }
        })

        if (result == OK) {
            console.log("Spawning new upgrader: $newName")
        }
    }

    // build blueprint
    // TODO: should check CPU
    // TODO: max construction site count: 100
    if (!room.memory.blueprintBuilt) {
        // extension
        val cornerDirections = spawn.pos.getCornerDirections()
        spawn.pos.getDirections()
            .filter { (x, y) -> terrain[x, y].isAccessible() && (x to y) !in cornerDirections }
            .forEach { (x, y) ->
                room.createConstructionSite(x, y, STRUCTURE_EXTENSION)
            }

        // container (storage) : 5
        spawn.pos.getDirections(3)
            .filter { (x, y) -> terrain[x, y].isAccessible() }
            .take(MAX_CONTAINER_COUNT)
            .forEach { (x, y) ->
                room.createConstructionSite(x, y, STRUCTURE_CONTAINER)
            }

        // road
        val roadDirections = spawn.pos.getDirections(2)

        roadDirections.filter { (x, y) -> terrain[x, y].isAccessible() }
            .forEach { vector ->
                room.createConstructionSite(vector.x, vector.y, STRUCTURE_ROAD)
            }

        sourceList.forEach { source ->
            val shortestPath = roadDirections.map { vector ->
                source.pos.findPathTo(vector.x, vector.y, opts = jsObject { ignoreCreeps = true })
            }.minByOrNull { it.size }

            shortestPath?.forEach {
                room.createConstructionSite(it.x, it.y, STRUCTURE_ROAD)
            }
        }


        room.memory.blueprintBuilt = true
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
