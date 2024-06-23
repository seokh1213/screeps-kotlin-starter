package screeps.game.extension

import screeps.api.OK
import screeps.api.ScreepsReturnCode

fun ScreepsReturnCode.whenSuccess(block: () -> Unit) {
    if (this == OK) {
        block()
    }
}
