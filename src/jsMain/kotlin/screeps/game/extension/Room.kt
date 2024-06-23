package screeps.game.extension

import screeps.api.FIND_MY_SPAWNS
import screeps.api.FIND_SOURCES
import screeps.api.Room

val Room.spawn
    get() = find(FIND_MY_SPAWNS).firstOrNull()

fun Room.findAvailableSources() = find(FIND_SOURCES).filter { source -> source.energy > 0 }

