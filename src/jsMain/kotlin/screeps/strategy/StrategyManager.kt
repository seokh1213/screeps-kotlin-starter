package screeps.strategy

import screeps.api.Room
import screeps.game.stage
import screeps.strategy.strategy0.Strategy0
import screeps.strategy.strategy1.Strategy1

typealias Stage = Int

object StrategyManager {
    private val strategies = listOf(
        Strategy0,
        Strategy1
    )

    fun executeStrategy(room: Room) {
        val stage = room.determineStage()
        strategies[stage].execute(room)
    }

    private fun Room.determineStage(): Stage {
        val previousStage = memory.stage
        val currentStage = strategies.withIndex()
            .firstOrNull { (_, strategy) -> !strategy.condition(this) }
            ?.let { (index, _) -> index }
            ?: previousStage

        return currentStage.also {
            if (previousStage != currentStage) {
                memory.stage = currentStage
                console.log("Stage changed from $previousStage to $currentStage")
            }
        }
    }
}

