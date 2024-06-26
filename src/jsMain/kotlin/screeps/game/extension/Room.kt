package screeps.game.extension

import screeps.api.FIND_MY_SPAWNS
import screeps.api.FIND_SOURCES
import screeps.api.FIND_STRUCTURES
import screeps.api.Game
import screeps.api.Room
import screeps.api.Source
import screeps.api.structures.StructureKeeperLair
import screeps.api.values

val Room.spawn
    get() = find(FIND_MY_SPAWNS).firstOrNull()
        ?: Game.spawns.values.minByOrNull { Game.map.getRoomLinearDistance(it.room.name, name) }
        ?: Game.spawns.values.firstOrNull()

fun Room.findSourceKeepersLairs() = find(FIND_STRUCTURES).filterIsInstance<StructureKeeperLair>()

fun Room.findAvailableSources() = find(FIND_SOURCES).filter { source -> source.energy > 0 }


fun Room.findSourceKeeperSources(): List<Source> {
    val sourceKeepersLairs = findSourceKeepersLairs()
    val sources = findAvailableSources()

    return sourceKeepersLairs.flatMap { lair ->
        sources.filter { source ->
            lair.pos.inRangeTo(source.pos, 5)
        }
    }
}


fun Room.findAvailableSourcesExcludingSourceKeepers(): List<Source> {
    val allSources = findAvailableSources()
    val sourceKeeperSources = findSourceKeeperSources().map { it.id }.toSet()
    return allSources.filterNot { source ->
        sourceKeeperSources.contains(source.id)
    }
}
