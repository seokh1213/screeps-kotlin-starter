package extension

import screeps.api.Source


fun Source.countAccessibleDirections(): Int {
    val terrain = room.getTerrain()
    return pos.getDirections()
        .count { vector -> terrain[vector.x, vector.y].isAccessible() }
}
