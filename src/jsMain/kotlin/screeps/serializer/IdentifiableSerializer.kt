package screeps.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import screeps.api.Game
import screeps.api.Identifiable
import screeps.api.Source
import screeps.api.structures.Structure

abstract class BaseIdentifiableSerializer<T : Identifiable> : KSerializer<T> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Identifiable", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.id)
    }

    override fun deserialize(decoder: Decoder): T {
        val id = decoder.decodeString()
        return createInstance(id)
    }

    abstract fun createInstance(id: String): T
}

object SourceSerializer : BaseIdentifiableSerializer<Source>() {
    override fun createInstance(id: String): Source {
        return Game.getObjectById(id) ?: error("Source with id $id not found")
    }
}

object StructureSerializer : BaseIdentifiableSerializer<Structure>() {
    override fun createInstance(id: String): Structure {
        return Game.getObjectById(id) ?: error("Structure with id $id not found")
    }
}
