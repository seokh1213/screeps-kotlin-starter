package screeps.game.extension

import screeps.api.TERRAIN_MASK_WALL
import screeps.api.TerrainMaskConstant


fun TerrainMaskConstant.isAccessible() = this != TERRAIN_MASK_WALL

