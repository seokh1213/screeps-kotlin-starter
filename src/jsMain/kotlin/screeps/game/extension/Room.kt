package screeps.game.extension

import screeps.api.FIND_MY_SPAWNS
import screeps.api.FIND_SOURCES
import screeps.api.FIND_STRUCTURES
import screeps.api.Room
import screeps.api.Source
import screeps.api.structures.StructureKeeperLair

val Room.spawn
    get() = find(FIND_MY_SPAWNS).firstOrNull()

fun Room.findSourceKeepersLairs(): List<StructureKeeperLair> {
    return find(FIND_STRUCTURES).filterIsInstance<StructureKeeperLair>()
}

fun Room.findAvailableSources() = find(FIND_SOURCES).filter { source -> source.energy > 0 }


fun Room.findSourceKeeperSources(): List<Source> {
    val sourceKeeperSources = mutableListOf<Source>()
    val sourceKeepersLairs = findSourceKeepersLairs()
    val sources = find(FIND_SOURCES)

    sourceKeepersLairs.forEach { lair ->
        sources.forEach { source ->
            if (lair.pos.inRangeTo(source.pos, 5)) {
                sourceKeeperSources.add(source)
            }
        }
    }

    return sourceKeeperSources
}


fun Room.findAvailableSourcesExcludingSourceKeepers(): List<Source> {
    val allSources = findAvailableSources()
    val sourceKeeperSources = findSourceKeeperSources().map { it.id }.toSet()
    return allSources.filterNot { source ->
        sourceKeeperSources.contains(source.id)
    }
}
