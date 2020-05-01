package farszownicy.caldirola.models.data_classes

import java.time.LocalDateTime

abstract class AgendaDrawableEntry(
    var startTime: LocalDateTime,
    var endTime: LocalDateTime
)