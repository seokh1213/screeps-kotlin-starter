package screeps.strategy

import screeps.api.Room


object Strategy1 : Strategy {
    override fun condition(room: Room): Boolean {
        return false
    }

    override fun execute(room: Room) {
        console.log("Executing Stage 1 Strategy")
    }
}
