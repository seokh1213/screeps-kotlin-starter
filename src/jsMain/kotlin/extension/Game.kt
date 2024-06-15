package extension

import screeps.api.Creep
import screeps.api.Record
import screeps.api.values

fun Record<String, Creep>.filter(predicate: (Creep) -> Boolean) = values.filter(predicate)
