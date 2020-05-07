package farszownicy.caldirola.dto

import farszownicy.caldirola.models.data_classes.TaskSlice
import kotlinx.serialization.Serializable

@Serializable
data class TaskSliceDto (val parent: String,
                         val startTime: String,
                         val endTime: String
){
    constructor(taskSlice: TaskSlice):this(taskSlice.parent.id, taskSlice.startTime.toString(), taskSlice.endTime.toString())
}