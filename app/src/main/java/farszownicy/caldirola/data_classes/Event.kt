package farszownicy.caldirola.data_classes

import java.time.LocalDateTime

class Event (
    var id: String = "-1",
    var name: String = "",
    var description: String = "",
    startTime: LocalDateTime  = LocalDateTime.now(),
    endTime: LocalDateTime = LocalDateTime.now(),
    var Location: Place? = null
                 ) : AgendaDrawableEntry(startTime, endTime)