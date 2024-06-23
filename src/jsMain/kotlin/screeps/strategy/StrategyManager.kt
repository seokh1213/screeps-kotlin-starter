package screeps.strategy

import screeps.api.Room
import screeps.game.stage

typealias Stage = Int

object StrategyManager {
    private val strategies = listOf(
        Strategy0,
        Strategy1
    )

    fun executeStrategy(room: Room) {
        strategies[room.determineStage()].execute(room)
    }

    private fun Room.determineStage(): Stage {
        val previousStage = memory.stage
        val currentStage = strategies.subList(previousStage).withIndex()
            .firstOrNull { (_, strategy) -> !strategy.condition(this) }
            ?.let { (index, _) -> return previousStage + index }
            ?: previousStage
        return currentStage.also { memory.stage = it }
    }
}

private fun <T> List<T>.subList(startIndex: Int) = subList(startIndex, size)
