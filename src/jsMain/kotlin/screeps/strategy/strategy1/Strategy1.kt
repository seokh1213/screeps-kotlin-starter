package screeps.strategy.strategy1

import screeps.api.Room
import screeps.strategy.Strategy
import screeps.strategy.strategy0.Strategy0


object Strategy1 : Strategy {
    override fun condition(room: Room): Boolean {
        return false // TODO
    }

    override fun execute(room: Room) {
        Strategy0.execute(room) // TODO
    }
}
