package `object`.manager

object ScenarioManager : Manager {
    private var stage: Int = 0

    fun getCurrentStage(): Int {
        return stage
    }

    fun measureDevelopmentStage() {
        // TODO
    }

    override fun prepare() {
        measureDevelopmentStage()
    }

    override fun action() {
        // TODO
    }

    override fun finish() {
        // TODO
    }
}
