package farszownicy.caldirola.Logic

import android.util.Log

import farszownicy.caldirola.utils.DateHelper
import farszownicy.caldirola.models.data_classes.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

object PlanManager {

    init{ }
    var mTaskSlices: ArrayList<TaskSlice> = ArrayList()
    var mAllInsertedEntries: ArrayList<AgendaDrawableEntry> = ArrayList()
    var mPlaces: ArrayList<Place> = ArrayList()
    var memoryUpToDate = true
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
            //distributeTasks(tasks)
        }

    @ExperimentalTime
    public fun getEvent(id: String): Event?{
        var returnEvent: Event? = null
        mEvents.forEach{event ->
            if(event.id.equals(id)) returnEvent = event
        }
        return returnEvent
    }

    @ExperimentalTime
    public fun getTask(id: String): Task?{
        var returnTask: Task? = null
        mTasks.forEach{task ->
            if(task.id.equals(id)) returnTask = task
        }
        return returnTask
    }

    @ExperimentalTime
    public fun updateEvent(event: Event, nName: String, nDesc: String, nST : LocalDateTime, nET : LocalDateTime, nLoc : Place?):Boolean{

        var newEvent: Event = Event()
        newEvent.name = nName
        newEvent.description = nDesc
        newEvent.startTime = nST
        newEvent.endTime = nET
        newEvent.Location = nLoc
        if(canEventBeEdited(newEvent, event))
        {
            event.Location = nLoc
            event.name = nName
            event.description = nDesc
            event.endTime = nET
            event.startTime = nST
            updateAllEntries()
            return true
        } else return false
    }

    @ExperimentalTime
    public fun updateTask(task: Task, nName: String, nDesc: String, nDDL : LocalDateTime, nLoc : List<Place>, nPriority: String, nDivisible: Boolean, nMinSlice: Int, nDuration:Duration):Boolean{
        val oldDivisible = task.divisible
        val oldMinSliceSlize = task.minSliceSize
        val oldDeadline = task.deadline

        task.divisible = nDivisible
        task.minSliceSize = nMinSlice
        task.deadline = nDDL

        if((oldDivisible != nDivisible) || (oldMinSliceSlize != nMinSlice) || isBefore(nDDL, oldDeadline))
        {
            removeTask(task)
            addTask(task)
        }

        //val oldPrerequisites =task.prerequisites
        val prerequisitesChanged = false
        if (prerequisitesChanged) {
            handlePrerequisites(task)
        }
        updateAllEntries()
        return true
    }

    private fun handlePrerequisites(task: Task) {
        //topTasksInHierarchy(task)
        val notFinishedPrereqSlices = mTaskSlices.filter { ts -> ts.startTime > LocalDateTime.now()
                && task.prerequisites.contains(ts.parent)}

        val futureTasks = getFutureSlices().map{ts -> ts.parent};
        val notFinishedPrerequisites = task.prerequisites.filter { pt -> futureTasks.contains(pt)}
        //addTaskWithPrerequisites()
    }


    @ExperimentalTime
    private fun updateAllEntries() {
        mAllInsertedEntries = ArrayList()
        mAllInsertedEntries.addAll(mEvents)
        mAllInsertedEntries.addAll(mTaskSlices)
        mAllInsertedEntries.sortBy{it.startTime}
    }

    @ExperimentalTime
    private fun distributeTasks(argTasks: List<Task>): Boolean {
        val tasks = argTasks.sortedBy { it.deadline }
        //Log.d("rearrange", "${tasks.size}")
        var allTasksInserted = true
        for(task in tasks) {
            val notCompletedPreTasks = task.prerequisites.filter{preTask -> (tasks.contains(preTask) && !preTask.doable)}
            val allPreTasksInserted = distributeTasks(notCompletedPreTasks)

            var inserted: Boolean
            if(allPreTasksInserted) {
                if (!task.doable) {
                    if (!task.divisible)
                        inserted = insertNonDivisibleTask(task)
                    else {
                        val originalDuration = task.duration
                        val completedMinutes =
                            getSlicesOfTask(task).map { ts ->
                                differenceInMinutes(
                                    ts.startTime,
                                    ts.endTime
                                )
                            }.sum().minutes
                        task.duration = originalDuration.minus(completedMinutes)
                        inserted = insertDivisibleTask(task)

                        task.duration = originalDuration
                    }
                    task.doable = inserted
                }
            }
            if(!task.doable)
                allTasksInserted = false
        }
        return allTasksInserted
    }

    @ExperimentalTime
    public fun addTask(task: Task): Boolean{
        val inserted =
            if (!task.divisible)
                insertNonDivisibleTask(task)
            else
                insertDivisibleTask(task)
        task.doable = inserted
        mTasks.add(task)
        mTasks.sortBy {it.deadline}
        return inserted
    }

    @ExperimentalTime
    public fun addEvent(event: Event): Boolean{
        if (canEventBeInserted(event)) {
            mEvents.add(event)
            mEvents.sortBy{it.startTime}
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
    private fun canEventBeEdited(upEvent : Event, oldEvent:Event): Boolean{
        var currTime = upEvent.startTime
        var numOfAvailableMinutes = 0
        val eventDuration = differenceInMinutes(upEvent.startTime, upEvent.endTime)
        while(isTimeAvailableExclude(currTime, oldEvent) && numOfAvailableMinutes < eventDuration) {
            currTime = currTime.plusMinutes(1)
            numOfAvailableMinutes += 1
        }

        return numOfAvailableMinutes >= eventDuration
    }

    @ExperimentalTime
    private fun insertDivisibleTask(task: Task): Boolean {
        var currTime = LocalDateTime.now()//.withHour(9).withMinute(0)
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

            if(sliceDuration >= task.minSliceSize) {
                val slotEndTime = slotStartTime.plusMinutes(sliceDuration.toLong())
                val slice = TaskSlice(task, slotStartTime, slotEndTime)
                insertedTaskSlices.add(slice)
                totalInsertedDuration += sliceDuration
            }
        }
        var inserted = true
        if(totalInsertedDuration < task.duration.inMinutes) {
            //abortTask(task)
            Log.d("LOG", "Nie udalo sie wcisnac calego taska ${task.name}.")
            inserted = false
            //Toast.makeText(this.context, "Podzielnego zadania ${task.name} nie da sie wcisnac do kalendarza.", Toast.LENGTH_SHORT).show()
        }
        else{
            mTaskSlices.addAll(insertedTaskSlices)
            mAllInsertedEntries.addAll(insertedTaskSlices)
            mAllInsertedEntries.sortBy{it.startTime}
        }
        return inserted
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
        var currTime = LocalDateTime.now()
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

    @ExperimentalTime
    private fun isTimeAvailableExclude(currTime: LocalDateTime, exclEvent: Event):Boolean{
        val exclEvents = mEvents.filter{e -> e != exclEvent}
        return !exclEvents.any{isBeforeOrEqual(it.startTime,currTime) && isAfter(it.endTime,currTime)}
    }

    fun isBeforeOrEqual(earlierDate: LocalDateTime, laterDate: LocalDateTime): Boolean {
        return differenceInMinutes(earlierDate, laterDate) >= 0
    }

    fun isBefore(earlierDate: LocalDateTime, laterDate: LocalDateTime): Boolean {
        return differenceInMinutes(earlierDate, laterDate) > 0
    }

    fun isAfter(firstDate: LocalDateTime, secDate: LocalDateTime): Boolean {
        return differenceInMinutes(firstDate, secDate) < 0
    }

    fun areEqual(firstDate: LocalDateTime, secDate: LocalDateTime): Boolean {
        return differenceInMinutes(firstDate, secDate) == 0L
    }

    fun isAfterOrEqual(earlierDate: LocalDateTime, laterDate: LocalDateTime): Boolean {
        return differenceInMinutes(earlierDate, laterDate) <= 0
    }

    fun differenceInMinutes(earlierDate: LocalDateTime, laterDate: LocalDateTime): Long {
        return ChronoUnit.MINUTES.between(
            earlierDate.truncatedTo(ChronoUnit.MINUTES),
            laterDate.truncatedTo(ChronoUnit.MINUTES))
    }

    @ExperimentalTime
    fun getEventsByDate(date : LocalDateTime): List<Event>{
        Log.d("size", mTasks.size.toString())
        Log.d("size", mEvents.size.toString())
        return mEvents.filter { e -> DateHelper.isBetweenInclusive(date, e.startTime, e.endTime) }
    }

    @ExperimentalTime
    fun getTaskSlicesByDate(date : LocalDateTime): List<TaskSlice>{
        return mTaskSlices.filter { t -> DateHelper.isBetweenInclusive(date, t.startTime, t.endTime) }
    }

    fun getSlicesOfTask(task: Task): List<TaskSlice> {
        return mTaskSlices.filter { t -> t.parent == task}
    }


    @ExperimentalTime
    fun removeTask(parent: Task) {
        val taskChildren: List<TaskSlice> = getSlicesOfTask(parent)
        mTaskSlices.removeAll (taskChildren)
        mTasks.remove(parent)
        mAllInsertedEntries.removeAll(taskChildren)
    }

    @ExperimentalTime
    fun removeEvent(event:Event){
        mEvents.remove(event)
        mAllInsertedEntries.remove(event)
    }

    @ExperimentalTime
    fun getFutureAndCurrentEvents(): List<Event> {
        return mEvents.filter { e -> e.endTime > LocalDateTime.now() }
    }

    @ExperimentalTime
    fun getFutureAndCurrentTasks(): List<Task> {
        return mTasks.filter { t -> t.deadline > LocalDateTime.now() }
    }

    private fun getFutureSlices(): List<TaskSlice> {
        return mTaskSlices.filter { ts -> ts.startTime > LocalDateTime.now() }
    }

    @ExperimentalTime
    fun rearrangeTasks() {
        //val futureTasks = getFutureTasks()
        val futureSlices = getFutureSlices()

        mAllInsertedEntries.removeAll(futureSlices)
        mTaskSlices.removeAll(futureSlices)
        val tasksToDistribute = futureSlices.map { ts-> ts.parent }.union(mTasks.filter{t -> !t.doable})
        tasksToDistribute.forEach{t -> t.doable = false}
        distributeTasks(tasksToDistribute.toList())
    }

    @ExperimentalTime
    fun getAllPossiblePrerequisites(task:Task): List<Task> {
        //nie mozna do X dac jako prerekwizytu taska, ktorego X jest juz prerekwizytem (zrobi sie glupi cykl)
        return mTasks.filter { t -> !getAllSubTasksInHierarchy(t).contains(task)}
    }

    //TODO: dodaj liste nadTaskow jako pole w Tasku
    private fun getAllSubTasksInHierarchy(task: Task) : List<Task>{
        val subTasks = ArrayList<Task>()
        if(task.prerequisites.count() == 0){
            subTasks.add(task)
        }
        else {
            for(pt in task.prerequisites)
                subTasks.addAll(getAllSubTasksInHierarchy(pt))
        }
        return subTasks
    }

}