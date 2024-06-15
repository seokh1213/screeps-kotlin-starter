package extension

import screeps.api.TERRAIN_MASK_NONE
import screeps.api.TerrainMaskConstant


fun TerrainMaskConstant.isAccessible(): Boolean {
    return this == TERRAIN_MASK_NONE
}
