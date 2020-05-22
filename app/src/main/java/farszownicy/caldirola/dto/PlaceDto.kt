package farszownicy.caldirola.dto

import farszownicy.caldirola.models.data_classes.Place
import kotlinx.serialization.Serializable

class PlaceDto(
    var name:String = ""
){
    constructor(place: Place): this(place.name)
}