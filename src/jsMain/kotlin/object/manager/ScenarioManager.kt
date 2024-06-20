package `object`.manager

object ScenarioManager : Manager {
    private var stage: Int = 0

    fun getCurrentStage(): Int {
        return stage
    }

    fun measureDevelopmentStage() {
        // TODO
    }

    override fun prepare(context: Manager.Context): Manager.Context {
        measureDevelopmentStage()
        return context
    }

    override fun process(context: Manager.Context): Manager.Context {
        // TODO
        return context
    }

    override fun finish(context: Manager.Context): Manager.Context {
        // TODO
        return context
    }
}
