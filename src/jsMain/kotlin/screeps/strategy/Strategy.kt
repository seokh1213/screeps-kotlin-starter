package screeps.strategy

import screeps.api.Room

interface Strategy {
    fun condition(room: Room): Boolean
    fun execute(room: Room)
}
