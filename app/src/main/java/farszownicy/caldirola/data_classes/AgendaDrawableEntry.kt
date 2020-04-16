package farszownicy.caldirola.data_classes

import java.time.LocalDateTime
import java.util.*

abstract class AgendaDrawableEntry(
    var startTime: LocalDateTime,
    var endTime: LocalDateTime
)