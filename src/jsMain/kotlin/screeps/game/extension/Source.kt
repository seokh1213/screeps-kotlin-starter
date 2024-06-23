package screeps.game.extension

import screeps.api.Source


fun Source.countAccessibleTiles() = getAccessibleAdjacentTiles().size

fun Source.getAccessibleAdjacentTiles(): List<Vector> {
    val terrain = room.getTerrain()
    return pos.getAdjacentTiles().filter { vector ->
        terrain[vector.x, vector.y].isAccessible()
    }
}
