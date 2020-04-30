package farszownicy.caldirola.utils.memory

import android.content.Context
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.data_classes.Task
import farszownicy.caldirola.data_classes.TaskSlice
import farszownicy.caldirola.dto.EventDto
import farszownicy.caldirola.dto.TaskDto
import farszownicy.caldirola.dto.TaskSliceDto
import farszownicy.caldirola.utils.Constants
import java.time.LocalDateTime
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

@ExperimentalTime
fun saveEventsToMemory(context: Context){
    val eventDtos = PlanManager.mEvents.map { event -> EventDto(event) }
    writeObjectToSharedPreferences(
        context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_EVENTS_LIST_KEY,
        eventDtos
    )
}

@ExperimentalTime
fun loadEventsFromMemory(context: Context){
     val eventDtos=
        readObjectsFromSharedPreferences<ArrayList<EventDto>>(
            context,
            Constants.SHARED_PREF_CALENDAR_FILE_NAME,
            Constants.SHARED_PREF_EVENTS_LIST_KEY
        )
            ?: ArrayList()
    PlanManager.mEvents = eventDtos.map { dto -> Event(
        dto.id,
        dto.name,
        dto.description,
        LocalDateTime.parse(dto.startTime),
        LocalDateTime.parse(dto.endTime),
        dto.Location
    ) } as ArrayList
}

@ExperimentalTime
fun saveTasksToMemory(context: Context){
    val taskDtos = PlanManager.mTasks.map { task -> TaskDto(task) }
    writeObjectToSharedPreferences(
        context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_TASKS_LIST_KEY,
        taskDtos
    )

    val taskSlicesDtos = PlanManager.mTaskSlices.map { taskSlice -> TaskSliceDto(taskSlice) }
    writeObjectToSharedPreferences(
        context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_TASKS_SLICES_LIST_KEY,
        taskSlicesDtos
    )
}

@ExperimentalTime
fun loadTasksFromMemory(context: Context){
    val taskDtos =
        readObjectsFromSharedPreferences<ArrayList<TaskDto>>(
            context,
            Constants.SHARED_PREF_CALENDAR_FILE_NAME,
            Constants.SHARED_PREF_TASKS_LIST_KEY
        )
            ?: ArrayList()

    val tasks = taskDtos.map { dto -> Task(
        dto.id,
        dto.name,
        dto.description,
        LocalDateTime.parse(dto.deadline),
        dto.duration.minutes,
        dto.priority,
        mutableListOf(),
        dto.divisible,
        dto.minSliceSize,
        dto.places
    ) }

    for (i in tasks.indices){
        //Dtos contain only ids of tasks
        tasks[i].prerequisites = tasks.filter { task -> taskDtos[i].prerequisites.contains(task.id) }
    }

    PlanManager.mTasks = tasks as ArrayList<Task>

    val taskSliceDtos =
        readObjectsFromSharedPreferences<ArrayList<TaskSliceDto>>(
            context,
            Constants.SHARED_PREF_CALENDAR_FILE_NAME,
            Constants.SHARED_PREF_TASKS_SLICES_LIST_KEY
        )
            ?: ArrayList()

    PlanManager.mTaskSlices =
        taskSliceDtos.map { dto -> TaskSlice(
            PlanManager.mTasks.find { task -> task.id == dto.parent }!!, //Strzeli nullPointer jeśli nie znajdzie rodzica
            LocalDateTime.parse(dto.startTime),
            LocalDateTime.parse(dto.endTime)
        ) } as ArrayList
}