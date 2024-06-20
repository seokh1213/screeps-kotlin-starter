package `object`.manager

typealias ManagerAction = (Manager.Context) -> Manager.Context

interface Manager {
    fun prepare(context: Context): Context
    fun process(context: Context): Context
    fun finish(context: Context): Context

    interface Context
}


class LogicChain private constructor(
    private val actions: List<ManagerAction>
) {
    companion object {
        fun of(vararg actions:  ManagerAction): LogicChain {
            return LogicChain(actions.toList())
        }
    }

    fun chain(initialContext: Manager.Context): Manager.Context {
        return actions.fold(initialContext) { context, action ->
            action.invoke(context)
        }
    }

}
