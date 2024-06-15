package extension

import screeps.api.RoomPosition

typealias Vector = Pair<Int, Int>

val Vector.x get() = first
val Vector.y get() = second

val DIRECTIONS = listOf(
    Vector(-1, -1), Vector(+0, -1), Vector(+1, -1),
    Vector(-1, +0), /*   self    */ Vector(+1, +0),
    Vector(-1, +1), Vector(+0, +1), Vector(+1, +1)
)

val RoomPosition.directions: List<Vector>
    get() = DIRECTIONS.map { (dx, dy) -> Vector(x + dx, y + dy) }
