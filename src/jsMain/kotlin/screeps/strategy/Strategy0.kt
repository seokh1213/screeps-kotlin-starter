package screeps.strategy

import screeps.api.CARRY
import screeps.api.Creep
import screeps.api.CreepMemory
import screeps.api.ERR_FULL
import screeps.api.ERR_NOT_IN_RANGE
import screeps.api.FIND_MY_CREEPS
import screeps.api.Game
import screeps.api.MOVE
import screeps.api.OK
import screeps.api.RESOURCE_ENERGY
import screeps.api.Room
import screeps.api.ScreepsReturnCode
import screeps.api.Source
import screeps.api.WORK
import screeps.api.options
import screeps.api.structures.StructureSpawn
import screeps.creeps.role.Role
import screeps.game.extension.countAccessibleTiles
import screeps.game.extension.findAvailableSourcesExcludingSourceKeepers
import screeps.game.extension.getAccessibleAdjacentTiles
import screeps.game.extension.getAdjacentTiles
import screeps.game.extension.isAccessible
import screeps.game.extension.spawn
import screeps.game.extension.whenSuccess
import screeps.game.extension.x
import screeps.game.extension.y
import screeps.game.role
import screeps.game.taskId
import screeps.taks.BuildTask
import screeps.taks.HarvestTask
import screeps.taks.TaskQueue
import screeps.utils.ListQueue
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
        val (assignedWorkers, unassignedWorkers) = room.findWorkers().partition {
            it.memory.taskId.isNotEmpty()
        }.let { (assigned, unassigned) ->
            assigned.associateBy { it.memory.taskId } to ListQueue(unassigned)
        }

        tasks.forEach { task ->
            when (task) {
                is HarvestTask -> {
                    val worker = assignedWorkers[task.taskId] ?: unassignedWorkers.dequeue() ?: return@forEach

                    worker.memory.taskId = task.taskId
                    task.run(worker)
                }

                is BuildTask -> {}
            }
        }

    }

    private fun getTasks(room: Room): TaskQueue {
        // harvest source
        return TaskQueue {
            tasks {
                room.findAvailableSourcesExcludingSourceKeepers()
                    .flatMap { source ->
                        source.getAccessibleAdjacentTiles().withIndex()
                            .map { (index, vector) -> HarvestTask(source.id + "_" + vector.x + vector.y, source) }
                    }
            }
        }
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
        return findAvailableSourcesExcludingSourceKeepers().sumOf(Source::countAccessibleTiles)
    }

    private fun Room.findWorkers() = find(FIND_MY_CREEPS).filter { it.memory.role == Role.WORKER }
}

private fun StructureSpawn.spawnWorker(name: String? = null): ScreepsReturnCode {
    return spawnCreep(arrayOf(WORK, CARRY, MOVE), name ?: "Worker${Game.time}", options {
        memory = jsObject<CreepMemory> { role = Role.WORKER }
    })
}


private fun HarvestTask.run(creep: Creep) {
    if (creep.store.getFreeCapacity() > 0) {
        val harvestResult = creep.harvest(source)
        if (harvestResult == OK) {
            return
        }

        if (harvestResult == ERR_NOT_IN_RANGE) {
            creep.moveTo(source)
            return
        }
    } else {
        val spawn = creep.room.spawn ?: return
        val transferResult = creep.transfer(spawn, RESOURCE_ENERGY)
        if (transferResult == OK) {
            return
        }

        if (transferResult == ERR_NOT_IN_RANGE) {
            creep.moveTo(spawn)
            return
        }

        if (transferResult == ERR_FULL) {
            // wait until spawn is not full
            val terrain = creep.room.getTerrain()
            val vector = spawn.pos.getAdjacentTiles(2).firstOrNull { vector ->
                terrain[vector.x, vector.y].isAccessible()
            } ?: return

            creep.moveTo(vector.x, vector.y)
        }
    }
}

