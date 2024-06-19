package `object`.utils

import screeps.api.Game
import screeps.api.Memory
import screeps.api.MutableRecord
import screeps.api.keys
import screeps.utils.contains

external class Reflect {
    companion object {
        fun deleteProperty(target: Any, propertyKey: String): Boolean
    }
}

object GarbageCollector {
    fun garbageCollect() {
        for (name in Memory.creeps.keys) {
            if (name !in Game.creeps) {
                console.log("Clearing non-existing creep memory for $name")
                Memory.creeps.delete(name)
            }
        }

        for (name in Memory.rooms.keys) {
            if (name !in Game.rooms) {
                console.log("Clearing non-existing room memory for $name")
                Memory.rooms.delete(name)
            }
        }
    }


    private fun <K, V> MutableRecord<K, V>.delete(name: String) {
        if (name in this) {
            Reflect.deleteProperty(this, name)
        }
    }
}
