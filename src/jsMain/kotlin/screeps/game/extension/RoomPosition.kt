package screeps.game.extension

import screeps.api.RoomPosition
import kotlin.math.abs

typealias Vector = Pair<Int, Int>

val Vector.x get() = first
val Vector.y get() = second

fun RoomPosition.getAdjacentTiles(scaleFactor: Int = 1): List<Vector> =
    (-scaleFactor..scaleFactor).flatMap { dx ->
        (-scaleFactor..scaleFactor).mapNotNull { dy ->
            if (abs(dx) == scaleFactor || abs(dy) == scaleFactor) {
                Vector(x + dx, y + dy)
            } else {
                null
            }
        }
    }

fun RoomPosition.getCornerTiles(scaleFactor: Int = 1): List<Vector> =
    listOf(
        Vector(x - scaleFactor, y - scaleFactor), // Top-left corner
        Vector(x + scaleFactor, y - scaleFactor), // Top-right corner
        Vector(x + scaleFactor, y + scaleFactor), // Bottom-right corner
        Vector(x - scaleFactor, y + scaleFactor)  // Bottom-left corner
    )
