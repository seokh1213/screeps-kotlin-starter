package screeps.strategy

import screeps.api.CARRY
import screeps.api.CreepMemory
import screeps.api.FIND_MY_CREEPS
import screeps.api.Game
import screeps.api.MOVE
import screeps.api.Room
import screeps.api.ScreepsReturnCode
import screeps.api.Source
import screeps.api.WORK
import screeps.api.options
import screeps.api.structures.StructureSpawn
import screeps.creeps.role.Role
import screeps.game.extension.countAccessibleTiles
import screeps.game.extension.findAvailableSources
import screeps.game.extension.spawn
import screeps.game.extension.whenSuccess
import screeps.game.role
import screeps.taks.HarvestTask
import screeps.taks.TaskQueue
import screeps.utils.unsafe.jsObject

object Strategy0 : Strategy {
    override fun condition(room: Room): Boolean {
        val maxWorkers = room.calculateMaxWorkers()
        val currentWorkers = room.findWorkers().size
        return currentWorkers >= maxWorkers
    }

    override fun execute(room: Room) {
        // spawn worker
        room.spawnWorkers()

        // assign task to creep
        val tasks = getTasks(room)
        val workers = room.findWorkers()

        console.log("Tasks.size(): ", tasks.size())
        console.log("Workers.size(): ", workers.size)
    }

    private fun getTasks(room: Room): TaskQueue {
        // harvest source
        return TaskQueue(
            room.findAvailableSources().map {
                HarvestTask(it)
            }
        )
    }

    private fun Room.spawnWorkers() {
        val spawn = spawn ?: return
        val currentWorkers = findWorkers().size
        val maxWorkers = calculateMaxWorkers()
        if (currentWorkers < maxWorkers && spawn.spawning == null) {
            val name = "Worker${Game.time}"
            spawn.spawnWorker(name).whenSuccess {
                console.log("Spawning new worker: $name")
            }
        }
    }

    private fun Room.calculateMaxWorkers(): Int {
        return findAvailableSources().sumOf(Source::countAccessibleTiles)
    }

    private fun Room.findWorkers() = find(FIND_MY_CREEPS).filter { it.memory.role == Role.WORKER }
}

private fun StructureSpawn.spawnWorker(name: String? = null): ScreepsReturnCode {
    return spawnCreep(arrayOf(WORK, CARRY, MOVE), name ?: "Worker${Game.time}", options {
        memory = jsObject<CreepMemory> { role = Role.WORKER }
    })
}

