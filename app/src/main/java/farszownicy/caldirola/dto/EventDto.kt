package farszownicy.caldirola.dto

import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.Place
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    var id: String = "-1",
    var name: String = "",
    var description: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var Location: Place? = null){
    constructor(event: Event): this(event.id, event.name, event.description, event.startTime.toString(), event.endTime.toString(), event.Location)
}