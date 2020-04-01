package farszownicy.caldirola.data_classes

import farszownicy.caldirola.utils.CalendarSerializer
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Event (
    var name: String = "",
    var description: String = "",
    var id: String = "-1",
    @Serializable(with = CalendarSerializer::class)
    var startTime: Calendar  = Calendar.getInstance(),
    @Serializable(with = CalendarSerializer::class)
    var endTime: Calendar = Calendar.getInstance(),
    var Location: Place? = null
                 )