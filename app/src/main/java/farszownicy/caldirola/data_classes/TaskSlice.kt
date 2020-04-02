package farszownicy.caldirola.data_classes

import java.util.*

class TaskSlice constructor(val parent: Task,
                            startTime: Calendar,
                            endTime: Calendar)
    : AgendaDrawableEntry(startTime, endTime)