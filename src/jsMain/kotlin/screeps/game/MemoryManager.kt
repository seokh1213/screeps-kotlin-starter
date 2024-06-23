package screeps.game


import screeps.api.Game
import screeps.api.Memory
import screeps.api.MutableRecord
import screeps.api.keys
import screeps.utils.contains

object MemoryManager {
    fun cleanupMemory() {
        cleanupCreepMemory()
        cleanupRoomMemory()
    }

    private fun cleanupCreepMemory() {
        for (name in Memory.creeps.keys) {
            if (name !in Game.creeps) {
                console.log("Clearing non-existing creep memory for $name")
                Memory.creeps.delete(name)
            }
        }
    }

    private fun cleanupRoomMemory() {
        for (name in Memory.rooms.keys) {
            if (name !in Game.rooms) {
                console.log("Clearing non-existing room memory for $name")
                Memory.rooms.delete(name)
            }
        }
    }
}

private fun <V> MutableRecord<String, V>.delete(name: String) {
    if (name in this) {
        delete(this, name)
    }
}

@Suppress("UNUSED_PARAMETER")
private fun delete(obj: dynamic, name: String) {
    js("delete obj[name]")
}
