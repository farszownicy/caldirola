package farszownicy.caldirola.models.data_classes

import java.time.DayOfWeek
import java.time.LocalDateTime

class IllegalInterval constructor(
    var dayOfWeek: DayOfWeek?,
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    var title: String
) : AgendaDrawableEntry(startTime, endTime)