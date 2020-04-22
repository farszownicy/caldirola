package farszownicy.caldirola.dto

import farszownicy.caldirola.data_classes.TaskSlice
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TaskSliceDto (val parent: String,
                         val startTime: String,
                         val endTime: String
){
    constructor(taskSlice: TaskSlice):this(taskSlice.parent.id, taskSlice.startTime.toString(), taskSlice.endTime.toString())
}