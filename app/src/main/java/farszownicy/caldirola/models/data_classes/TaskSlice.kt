package farszownicy.caldirola.models.data_classes

import java.time.LocalDateTime

class TaskSlice constructor(val parent: Task,
                            startTime: LocalDateTime,
                            endTime: LocalDateTime)
    : AgendaDrawableEntry(startTime, endTime)