package farszownicy.caldirola.models.data_classes

import java.time.DayOfWeek
import java.time.LocalDateTime

class IllegalInterval constructor(
    var dayOfWeek: DayOfWeek?,
    var startTime: LocalDateTime,
    var endTime: LocalDateTime
)