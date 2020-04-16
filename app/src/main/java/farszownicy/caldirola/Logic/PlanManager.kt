package farszownicy.caldirola.Logic

import android.util.Log
import farszownicy.caldirola.data_classes.AgendaDrawableEntry
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.data_classes.Task
import farszownicy.caldirola.data_classes.TaskSlice
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

object PlanManager {

    init{ }
    @ExperimentalTime
    var mEvents: ArrayList<Event> = ArrayList()
        set(events){
            events.sortBy{it.startTime}
            field = events
            updateAllEntries()
        }

    @ExperimentalTime
    var mTasks: ArrayList<Task> = ArrayList()
        set(tasks){
            tasks.sortBy{it.deadline}
            field = tasks
            distributeTasks()
        }

    var mTaskSlices: ArrayList<TaskSlice> = ArrayList()
    var mAllInsertedEntries: ArrayList<AgendaDrawableEntry> = ArrayList()

    @ExperimentalTime
    private fun updateAllEntries() {
        mAllInsertedEntries = ArrayList()
        mAllInsertedEntries.addAll(mEvents)
        mAllInsertedEntries.addAll(mTaskSlices)
        mAllInsertedEntries.sortBy{it.startTime}
    }

    @ExperimentalTime
    private fun distributeTasks() {
        for(task in mTasks) {
            if (!task.divisible)
                insertNonDivisibleTask(task)
            else
                insertDivisibleTask(task)
        }
    }

    @ExperimentalTime
    public fun addTask(task: Task): Boolean{
        mTasks.add(task)
        mTasks.sortBy { it.deadline }
        return if (!task.divisible)
            insertNonDivisibleTask(task)
        else
            insertDivisibleTask(task)
    }

    @ExperimentalTime
    public fun addEvent(event: Event): Boolean{
        if (canEventBeInserted(event)) {
            mEvents.add(event)
            mAllInsertedEntries.add(event)
            mAllInsertedEntries.sortBy{it.startTime}
            return true
        }
        return false
    }

    @ExperimentalTime
    private fun canEventBeInserted(event : Event): Boolean {
        var currTime = event.startTime
        var numOfAvailableMinutes = 0
        val eventDuration = differenceInMinutes(event.startTime, event.endTime)
        while(isTimeAvailable(currTime) && numOfAvailableMinutes < eventDuration) {
            currTime = currTime.plusMinutes(1)
            numOfAvailableMinutes += 1
        }

        return numOfAvailableMinutes >= eventDuration
    }

    @ExperimentalTime
    private fun insertDivisibleTask(task: Task): Boolean {
        var currTime = LocalDateTime.now().withHour(0).withMinute(0)
        var totalInsertedDuration = 0
        var sliceDuration: Int
        val insertedTaskSlices:  ArrayList<TaskSlice> = ArrayList()

        while(isBefore(currTime, task.deadline) && totalInsertedDuration < task.duration.inMinutes) {

            for(insertedEntry in mAllInsertedEntries){
                if(isBeforeOrEqual(insertedEntry.startTime, currTime) && isAfter(insertedEntry.endTime, currTime)) //jesli jakis event nachodzi na aktualny czas
                    currTime = insertedEntry.endTime //to przesun sie na czas konca tego eventu
            }

            val slotStartTime = currTime
            sliceDuration = 0

            while(isTimeAvailable(currTime) && totalInsertedDuration + sliceDuration < task.duration.inMinutes
                && isBefore(currTime, task.deadline)
            ) {
                currTime = currTime.plusMinutes(1)
                sliceDuration += 1
            }

            if(sliceDuration > 0) {
                val slotEndTime = slotStartTime.plusMinutes(sliceDuration.toLong())

                val slice = TaskSlice(task, slotStartTime, slotEndTime)
                insertedTaskSlices.add(slice)
//            mTaskSlices.add(slice)
//            mAllInsertedEntries.add(slice)
//            mAllInsertedEntries.sortBy{it.startTime}
                totalInsertedDuration += sliceDuration
            }
        }
        var result = true
        if(totalInsertedDuration < task.duration.inMinutes) {
            //abortTask(task)
            Log.d("LOG", "Nie udalo sie wcisnac calego taska ${task.name}.")
            result = false
            //Toast.makeText(this.context, "Podzielnego zadania ${task.name} nie da sie wcisnac do kalendarza.", Toast.LENGTH_SHORT).show()
        }
        //else{
        mTaskSlices.addAll(insertedTaskSlices)
        mAllInsertedEntries.addAll(insertedTaskSlices)
        mAllInsertedEntries.sortBy{it.startTime}
        return result
        //}
    }

    @ExperimentalTime
    private fun insertNonDivisibleTask(task: Task): Boolean {
        val startTime = findNextEmptySlotLasting(task.duration, task.deadline)
        if(startTime != null) {
            val endTime = startTime.plusMinutes(task.duration.inMinutes.toLong())
            val slice = TaskSlice(task, startTime, endTime)
            mTaskSlices.add(slice)
            mAllInsertedEntries.add(slice)
            mAllInsertedEntries.sortBy { it.startTime }
            return true
        }
        else {
            Log.d("LOG", "zadania ${task.name} nie da sie wcisnac do kalendarza")
            return false
//            Toast.makeText(
//                this.context,
//                "Niepodzielnego zadania ${task.name} nie da sie wcisnac do kalendarza.",
//                Toast.LENGTH_SHORT
//            ).show()
        }
    }

    @ExperimentalTime
    private fun findNextEmptySlotLasting(minutes: Duration, deadline: LocalDateTime): LocalDateTime? {
        var currTime = LocalDateTime.now().withHour(0).withMinute(0)
        var slotStartTime:LocalDateTime
        var slotEndTime:LocalDateTime

        val maxStartTime = deadline.plusMinutes((-minutes.inMinutes).toLong())

        while(isBefore(currTime, maxStartTime)) {
            for(insertedEntry in mAllInsertedEntries){
                val st = insertedEntry.startTime
                val et = insertedEntry.endTime
                if(isBeforeOrEqual(st, currTime) && isAfter(et, currTime)) //jesli jakis event nachodzi na aktualny czas
                    currTime = insertedEntry.endTime //to przesun sie na czas konca tego eventu
            }

            slotStartTime = currTime
            slotEndTime = currTime.plusMinutes((minutes.inMinutes).toLong())

            while(isTimeAvailable(currTime) && isBefore(currTime,slotEndTime)
                && isBefore(currTime, deadline))
                currTime = currTime.plusMinutes(1)

            if(areEqual(currTime, slotEndTime))
                return slotStartTime
        }
        return null
    }

    @ExperimentalTime
    private fun isTimeAvailable(currTime: LocalDateTime): Boolean {
        return !mEvents.any{isBeforeOrEqual(it.startTime,currTime) && isAfter(it.endTime,currTime)}
    }

    private fun isBeforeOrEqual(earlierDate: LocalDateTime, laterDate: LocalDateTime): Boolean {
        return differenceInMinutes(earlierDate, laterDate) >= 0
    }

    private fun isBefore(earlierDate: LocalDateTime, laterDate: LocalDateTime): Boolean {
        return differenceInMinutes(earlierDate, laterDate) > 0
    }

    private fun isAfter(firstDate: LocalDateTime, secDate: LocalDateTime): Boolean {
        return differenceInMinutes(firstDate, secDate) < 0
    }

    private fun areEqual(firstDate: LocalDateTime, secDate: LocalDateTime): Boolean {
        return differenceInMinutes(firstDate, secDate) == 0L
    }

    private fun isAfterOrEqual(earlierDate: LocalDateTime, laterDate: LocalDateTime): Boolean {
        return differenceInMinutes(earlierDate, laterDate) <= 0
    }

    private fun differenceInMinutes(earlierDate: LocalDateTime, laterDate: LocalDateTime): Long {
        return ChronoUnit.MINUTES.between(
            earlierDate.truncatedTo(ChronoUnit.MINUTES),
            laterDate.truncatedTo(ChronoUnit.MINUTES))
    }
}