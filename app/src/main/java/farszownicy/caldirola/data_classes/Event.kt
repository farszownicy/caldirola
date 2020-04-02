package farszownicy.caldirola.data_classes

import java.util.*

class Event (
    var id: String = "-1",
    var name: String = "",
    var description: String = "",
    startTime: Calendar  = Calendar.getInstance(),
    endTime: Calendar = Calendar.getInstance(),
    var Location: Place? = null
                 ) : AgendaDrawableEntry(startTime, endTime)