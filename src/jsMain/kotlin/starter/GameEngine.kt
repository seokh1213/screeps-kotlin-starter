package starter

import `object`.manager.RoadManager
import `object`.manager.ScenarioManager
import `object`.manager.SpawnManager
import `object`.manager.WorkManager
import `object`.utils.GarbageCollector

object GameEngine {
    private val managers = listOf(
        ScenarioManager,
        SpawnManager,
        RoadManager,
        WorkManager
    )

    fun loop() {
        managers.forEach { it.prepare() }
        managers.forEach { it.action() }
        managers.forEach { it.finish() }

        GarbageCollector.garbageCollect()
    }
}
