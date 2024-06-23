package screeps.game

import screeps.api.Game
import screeps.api.values
import screeps.strategy.StrategyManager


object GameEngine {
    fun run() {
        MemoryManager.cleanupMemory()

        Game.rooms.values.forEach { room ->
            StrategyManager.executeStrategy(room)
        }
    }
}
