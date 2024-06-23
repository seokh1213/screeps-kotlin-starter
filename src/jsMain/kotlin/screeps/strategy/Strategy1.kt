package screeps.strategy

import screeps.api.Room


object Strategy1 : Strategy {
    override fun condition(room: Room): Boolean {
        return false // TODO
    }

    override fun execute(room: Room) {
        Strategy0.execute(room) // TODO
    }
}
