package farszownicy.caldirola.data_classes

import java.util.*

data class Event(
    var id: String = "-1",
    var startTime: Calendar  = Calendar.getInstance(),
    var endTime: Calendar = Calendar.getInstance(),
    var Location: Place? = null
                 ) : CalendarEntry()