package farszownicy.caldirola.dto

import farszownicy.caldirola.models.data_classes.Place
import farszownicy.caldirola.models.data_classes.Task
import farszownicy.caldirola.utils.Constants.PRIORITY_LOW
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class TaskDto constructor(
    var id: String = "-1",
    var name: String,
    var description: String,
    var deadline: String,
    var duration: Int = 0,
    var priority: String = PRIORITY_LOW,
    var prerequisites: List<String> = mutableListOf(), //Ids
    var divisible: Boolean = false,
    var minSliceSize: Int = 0,
    var places: List<Place> = mutableListOf(),
    var doable: Boolean
){
    @ExperimentalTime
    constructor(task: Task) : this(task.id, task.name, task.description, task.deadline.toString(),
        task.duration.inMinutes.toInt(), task.priority, task.prerequisites.map { t -> t.id },
        task.divisible, task.minSliceSize, task.places, task.doable){
    }
}