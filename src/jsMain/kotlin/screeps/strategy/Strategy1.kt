package screeps.strategy

import screeps.api.Room


object Strategy1 : Strategy {
    override fun condition(room: Room): Boolean {
        TODO()
    }

    override fun execute(room: Room) {
        console.log("Executing Stage 1 Strategy")
    }
}
