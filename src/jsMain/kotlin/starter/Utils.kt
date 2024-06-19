package starter

import screeps.api.Game
import screeps.api.Memory
import screeps.api.MutableRecord
import screeps.api.component1
import screeps.api.component2
import screeps.api.iterator
import screeps.utils.contains

fun garbageCollect() {
    for ((name, _) in Memory.creeps) {
        if (name !in Game.creeps) {
            console.log("Clearing non-existing creep memory for $name")
            Memory.creeps.delete(name)
        }
    }

    for ((name, _) in Memory.rooms) {
        if (name !in Game.rooms) {
            console.log("Clearing non-existing room memory for $name")
            Memory.rooms.delete(name)
        }
    }
}

external class Reflect {
    companion object {
        fun deleteProperty(target: Any, propertyKey: String): Boolean
    }
}

fun <K, V> MutableRecord<K, V>.delete(name: String) {
    if (name in this) {
        Reflect.deleteProperty(this, name)
    }
}
