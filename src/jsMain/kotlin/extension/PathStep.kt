package extension

import screeps.api.Room

operator fun Room.PathStep.component1() = x
operator fun Room.PathStep.component2() = y
