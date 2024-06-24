package screeps.strategy

import screeps.api.BodyPartConstant
import screeps.api.CARRY
import screeps.api.Creep
import screeps.api.ERR_FULL
import screeps.api.ERR_NOT_IN_RANGE
import screeps.api.FIND_MY_CREEPS
import screeps.api.Game
import screeps.api.MOVE
import screeps.api.OK
import screeps.api.RESOURCE_ENERGY
import screeps.api.Room
import screeps.api.Source
import screeps.api.WORK
import screeps.api.structures.StructureSpawn
import screeps.creeps.role.Role
import screeps.game.extension.canSpawn
import screeps.game.extension.countAccessibleTiles
import screeps.game.extension.findAvailableSourcesExcludingSourceKeepers
import screeps.game.extension.getAccessibleAdjacentTiles
import screeps.game.extension.getAdjacentTiles
import screeps.game.extension.isAccessible
import screeps.game.extension.spawn
import screeps.game.extension.spawnCreep
import screeps.game.extension.whenSuccess
import screeps.game.extension.x
import screeps.game.extension.y
import screeps.game.role
import screeps.game.taskId
import screeps.taks.HarvestTask
import screeps.taks.SpawnTask
import screeps.taks.TaskQueue
import screeps.utils.ListQueue

object Strategy0 : Strategy {
    override fun condition(room: Room): Boolean {
        val maxWorkers = room.calculateMaxWorkers()
        val currentWorkers = room.findWorkers().size
        return currentWorkers >= maxWorkers
    }

    override fun execute(room: Room) {
        val tasks = getTasks(room)
        val (assignedWorkers, unassignedWorkers) = room.findWorkers().partition {
            it.memory.taskId.isNotEmpty()
        }.let { (assigned, unassigned) ->
            assigned.associateBy { it.memory.taskId } to ListQueue(unassigned)
        }

        tasks.forEach { task ->
            when (task) {
                is SpawnTask -> {
                    val spawn = room.spawn ?: return@forEach
                    task.run(spawn)
                }

                is HarvestTask -> {
                    val worker = assignedWorkers[task.taskId] ?: unassignedWorkers.dequeue() ?: return@forEach
                    worker.memory.taskId = task.taskId

                    task.run(worker)
                }

                else -> {
                    //skip
                }
            }
        }

    }

    /**
     * spawn + harvest
     */
    private fun getTasks(room: Room): TaskQueue {
        return TaskQueue {
            task {
                room.getSpawnTask()
            }

            tasks {
                room.getHarvestTasks()
            }
        }
    }

    private fun Room.getSpawnTask(): SpawnTask? {
        val spawn = spawn ?: return null
        val currentWorkers = findWorkers().size
        val maxWorkers = calculateMaxWorkers()

        val body = arrayOf<BodyPartConstant>(WORK, CARRY, MOVE)
        if (!(currentWorkers < maxWorkers && spawn.canSpawn(body))) {
            return null
        }

        val name = "Worker${Game.time}"
        return SpawnTask(
            taskId = "spawn-worker-$name",
            context = SpawnTask.SpawnContext(name, body, Role.WORKER)
        )
    }

    private fun Room.getHarvestTasks(): List<HarvestTask> {
        return findAvailableSourcesExcludingSourceKeepers()
            .flatMap { source ->
                source.getAccessibleAdjacentTiles()
                    .map { (x, y) -> HarvestTask(source.id + "_" + x + y, source) }
            }
    }

    private fun Room.calculateMaxWorkers(): Int {
        return findAvailableSourcesExcludingSourceKeepers().sumOf(Source::countAccessibleTiles)
    }

    private fun Room.findWorkers() = find(FIND_MY_CREEPS).filter { it.memory.role == Role.WORKER }

    private fun SpawnTask.run(spawn: StructureSpawn) {
        spawn.spawnCreep(context).whenSuccess {
            console.log("Spawned worker ${context.name}")
        }
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
}

