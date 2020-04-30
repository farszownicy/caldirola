package farszownicy.caldirola.dto

import farszownicy.caldirola.data_classes.Place
import farszownicy.caldirola.data_classes.Task
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.time.ExperimentalTime

@Serializable
data class TaskDto constructor(
    var id: String = "-1",
    var name: String,
    var description: String,
    var deadline: String,
    var duration: Int = 0,
    var priority: Int = 0,
    var prerequisites: List<String> = mutableListOf(), //Ids
    var divisible: Boolean = false,
    var minSliceSize: Int = 0,
    var places: List<Place> = mutableListOf()
){
    @ExperimentalTime
    constructor(task: Task) : this(task.id, task.name, task.description, task.deadline.toString(),
        task.duration.inMinutes.toInt(), task.priority, task.prerequisites.map { t -> t.id },
        task.divisible, task.minSliceSize, task.places){
    }
}