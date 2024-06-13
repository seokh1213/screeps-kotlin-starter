package starter

import screeps.api.Game
import screeps.api.Memory
import screeps.api.component1
import screeps.api.component2
import screeps.api.iterator
import screeps.utils.contains

fun garbageCollect() {
    for ((name, _) in Memory.creeps) {
        if (name !in Game.creeps) {
            console.log("Clearing non-existing creep memory for $name")
            Memory.deleteCreeps(name)
        }
    }
}

external class Reflect {
    companion object {
        fun deleteProperty(target: Any, propertyKey: String): Boolean
    }
}

fun Memory.deleteCreeps(name: String) {
    if (name in creeps) {
        Reflect.deleteProperty(this.creeps, name)
    }
}
