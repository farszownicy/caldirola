package farszownicy.caldirola.utils.memory

import android.content.Context
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.dto.*
import farszownicy.caldirola.models.data_classes.*
import farszownicy.caldirola.utils.Constants
import java.time.DayOfWeek
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
fun saveLocationsToMemory(context:Context){
    val placeDtos = PlanManager.mPlaces.map{ place -> PlaceDto(place) }
    writeObjectToSharedPreferences(
        context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_LOCATIONS_LIST_KEY,
        placeDtos
    )
}

@ExperimentalTime
fun loadLocationsFromMemory(context:Context){
    val placeDtos = readObjectsFromSharedPreferences<ArrayList<PlaceDto>>(
        context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_LOCATIONS_LIST_KEY
    )
        ?:ArrayList()
    PlanManager.mPlaces = placeDtos.map{dto -> Place(dto.name) } as ArrayList
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
        dto.places,
        dto.doable
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

fun savePreferencesToMemory(context: Context){
    val dtos = PlanManager.illegalIntervals.map { interval -> IllegalIntervalDto(interval) }
    writeObjectToSharedPreferences(context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_INTERVALS_LIST_KEY,
        dtos
        )

    writeObjectToSharedPreferences(context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_MAX_TASKS_KEY,
        PlanManager.maxTasksPerDay
        )

    writeObjectToSharedPreferences(context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_MINUTES_BETWEEN_KEY,
        PlanManager.minutesBetweenTasks
    )
}

fun loadPreferencesFromMemory(context: Context){
    val dtos = readObjectsFromSharedPreferences<ArrayList<IllegalIntervalDto>>(
        context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_INTERVALS_LIST_KEY
    ) ?: ArrayList()

    PlanManager.illegalIntervals = dtos.map { dto ->
        IllegalInterval(if(dto.dayOfWeek == 0) null else DayOfWeek.of(dto.dayOfWeek),
            LocalDateTime.parse(dto.startTime),
            LocalDateTime.parse(dto.endTime),
            dto.title
        )
    } as ArrayList<IllegalInterval>

    PlanManager.maxTasksPerDay = readObjectsFromSharedPreferences<Int>(
        context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_MAX_TASKS_KEY
    ) ?: 0

    PlanManager.minutesBetweenTasks = readObjectsFromSharedPreferences<Int>(
        context,
        Constants.SHARED_PREF_CALENDAR_FILE_NAME,
        Constants.SHARED_PREF_MINUTES_BETWEEN_KEY
    ) ?: 0
}