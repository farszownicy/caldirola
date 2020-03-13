package farszownicy.caldirola.data_classes

import java.util.*

data class Event(
    var id: String = "-1",
    var startTime: Date  = Date(),
    var endTime: Date = Date(),
    var Location: Place? = null
                 ) : CalendarEntry()