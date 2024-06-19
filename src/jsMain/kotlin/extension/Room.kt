package extension

import screeps.api.FIND_SOURCES
import screeps.api.Game
import screeps.api.Room
import screeps.api.RoomMemory
import screeps.api.Source
import screeps.utils.memory.memory
import screeps.utils.memory.memoryWithSerializer
import kotlin.reflect.KMutableProperty0

var RoomMemory.blueprintBuilt: Boolean by memory { false }
var RoomMemory.availableSourceCount: Int by memory { -1 }
var RoomMemory.availableSources: List<Source> by memoryWithSerializer(
    { emptyList() },
    {
        it.fold("") { acc, source ->
            "$acc${source.id};"
        }
    },
    {
        it.split(";").dropLast(1).mapNotNull { id ->
            Game.getObjectById(id)
        }
    }
)

fun Room.analyzeRoom() {
    val availableSources = findAvailableSources()
    memory::availableSourceCount.whenChanged(availableSources.size) {
        memory.availableSources = availableSources
    }
}

fun Room.findAvailableSources(): List<Source> {
    return find(FIND_SOURCES).filter { source -> source.energy > 0 }
}

private inline fun <T> KMutableProperty0<T>.whenChanged(newValue: T, crossinline block: () -> Unit) {
    if (get() != newValue) {
        set(newValue)
        block()
    }
}
