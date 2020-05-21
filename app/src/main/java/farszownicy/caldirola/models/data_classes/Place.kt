package farszownicy.caldirola.models.data_classes

import kotlinx.serialization.Serializable

@Serializable
data class Place(var name: String){
    override fun equals(other: Any?): Boolean {
        return name == (other as Place).name
    }
}