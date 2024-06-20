package starter

import `object`.manager.LogicChain
import `object`.manager.ResourceManager
import `object`.manager.RoadManager
import `object`.manager.ScenarioManager
import `object`.manager.SpawnManager
import `object`.manager.WorkManager
import `object`.utils.GarbageCollector

object GameEngine {
    private val prepareActions = LogicChain.of(
        ScenarioManager::prepare,
        ResourceManager::prepare,
        SpawnManager::prepare,
        RoadManager::prepare,
        WorkManager::prepare
    )

    fun loop() {


        GarbageCollector.garbageCollect()
    }
}
