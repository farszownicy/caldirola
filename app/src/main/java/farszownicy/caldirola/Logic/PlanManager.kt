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

    const val LOW_PRIORITY_SCORE = 0.0
    const val MEDIUM_PRIORITY_SCORE = 360.0
    const val HIGH_PRIORITY_SCORE = 720.0
    const val URGENT_PRIORITY_SCORE = 1440.0

    //TODO: nielegalne godziny (sen itp.) - zakazany przedzial czasu to moze byc jeszcze jedna klasa dziedziczaca
    //                                  po AgendaDrawableEntry i majaca tylko startTime i endTime i te przedzialy
    //                                  sa jeszcze jedna lista tak jak taski i eventy
    //                                   ALBO sa to krotki (dzien tyg i godz startu, gdzien tyg i godz konca)


    init{ }
    var mTaskSlices: ArrayList<TaskSlice> = ArrayList()
    var mAllInsertedEntries: ArrayList<AgendaDrawableEntry> = ArrayList()
    var mPlaces: ArrayList<Place> = ArrayList()
    var illegalIntervals: ArrayList<IllegalInterval> = ArrayList()

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
        return if(canEventBeEdited(newEvent, event)) {
            var rearrangementRequired = nET != event.endTime || nST != event.startTime
            event.Location = nLoc
            event.name = nName
            event.description = nDesc
            event.endTime = nET
            event.startTime = nST
            if (rearrangementRequired) {
                rearrangeTasks()
                updateAllEntries()
            }
            true
        } else false
    }

    @ExperimentalTime
    public fun updateTask(task: Task, nName: String, nDesc: String, nDDL : LocalDateTime, nLoc : List<Place>, nPriority: String, nDivisible: Boolean, nMinSlice: Int, nDuration:Duration):Boolean{
        val oldDivisible = task.divisible
        val oldMinSliceSize = task.minSliceSize
        val oldDeadline = task.deadline
        val oldDuration = task.duration

        task.divisible = nDivisible
        task.minSliceSize = nMinSlice
        task.deadline = nDDL
        task.duration = nDuration

        //val oldPrerequisites =task.prerequisites
        val prerequisitesChanged = true

        if((oldDivisible != nDivisible) || (oldMinSliceSize != nMinSlice) ||
            isBefore(nDDL, oldDeadline) || nDuration != oldDuration || prerequisitesChanged)
        {
//            removeTask(task)
//            addTask(task)
            rearrangeTasks()
            updateAllEntries()
        }

        return true
    }


    @ExperimentalTime
    private fun updateAllEntries() {
        mAllInsertedEntries = ArrayList()
        mAllInsertedEntries.addAll(mEvents)
        mAllInsertedEntries.addAll(mTaskSlices)
        mAllInsertedEntries.sortBy{it.startTime}
    }

    @ExperimentalTime
    private fun distributeTasks(argTasks: List<Task>, sorted: Boolean): Boolean {
        val tasks =
            if(!sorted)
                argTasks.sortedBy { calcImportance(it)}
            else
                argTasks
        var allTasksInserted = true
        val tasksWaitingForPrerequisitesInsertion = ArrayList<Task>()

        for(task in tasks) {
            //val notCompletedPreTasks = task.prerequisites.filter{preTask -> (tasks.contains(preTask) && !preTask.doable)}
            //val allPreTasksInserted = distributeTasks(notCompletedPreTasks, false)
            //var inserted: Boolean

            //check if any of the waiting tasks have all prerequisites inserted
            var insertableWaitingTask = tasksWaitingForPrerequisitesInsertion.firstOrNull{ it -> it.prerequisites.all { it.doable }}
            while(insertableWaitingTask != null){
                insertTaskThatCouldBePartiallyCompleted(insertableWaitingTask)
                tasksWaitingForPrerequisitesInsertion.remove(insertableWaitingTask)
                insertableWaitingTask = tasksWaitingForPrerequisitesInsertion.firstOrNull{ it -> it.prerequisites.all { it.doable }}
            }

            //check if current task have all prerequisites inserted -> if yes try to insert it, else add to waiting Tasks
            val allPreTasksInserted = task.prerequisites.all { it.doable }
            if(allPreTasksInserted) {
                insertTaskThatCouldBePartiallyCompleted(task)
                if (!task.doable)
                    allTasksInserted = false
            }
            else
                tasksWaitingForPrerequisitesInsertion.add(task)
        }

        //handle remaining waiting tasks
        var insertableWaitingTask = tasksWaitingForPrerequisitesInsertion.firstOrNull{ it -> it.prerequisites.all { it.doable }}
        while(insertableWaitingTask != null){
            insertTaskThatCouldBePartiallyCompleted(insertableWaitingTask)
            insertableWaitingTask = tasksWaitingForPrerequisitesInsertion.firstOrNull{ it -> it.prerequisites.all { it.doable }}
        }
        if(tasksWaitingForPrerequisitesInsertion.any())
            allTasksInserted = false

        return allTasksInserted
    }

    @ExperimentalTime
    private fun insertTaskThatCouldBePartiallyCompleted(task: Task) : Boolean {
        var inserted = false
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
        return inserted
    }

    @ExperimentalTime
    private fun calcImportance(task: Task) : Double {
        val minutesTillDeadline = differenceInMinutes(LocalDateTime.now(), task.deadline).toInt()
        val priorityVal = getPriorityImportance(task)
        val score = minutesTillDeadline - priorityVal + task.duration.inMinutes
        return score
    }

    private fun getPriorityImportance(task: Task): Double {
        val priorities = arrayOf("Low", "Medium", "High", "Urgent")

        return when(task.priority)
            {
                priorities[0] -> LOW_PRIORITY_SCORE
                priorities[1] -> MEDIUM_PRIORITY_SCORE
                priorities[2] -> HIGH_PRIORITY_SCORE
                priorities[3] -> URGENT_PRIORITY_SCORE
                else ->  LOW_PRIORITY_SCORE
            }
    }

    @ExperimentalTime
    public fun addTask(task: Task): Boolean{
        task.doable = false
        mTasks.add(task)
        rearrangeTasks()
        return task.doable
//        val inserted =
//            if (!task.divisible)
//                insertNonDivisibleTask(task)
//            else
//                insertDivisibleTask(task)
//        task.doable = inserted
//        mTasks.add(task)
//        mTasks.sortBy {it.deadline}
//        return inserted
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
        var currTime = findLastPrerequisiteEndTime(task)
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
//        val prerequisitesCompleted =
//            if(task.prerequisites.isNotEmpty())
//                task.prerequisites.all { t -> t.doable }
//            else
//                true
//        if(!prerequisitesCompleted) {
//            Log.d("LOG", "zadania ${task.name} nie da sie wcisnac do kalendarza - prerekwizyty nie sa ukonczone")
//            return false
//        }
        val minStartTime: LocalDateTime? =  findLastPrerequisiteEndTime(task)
        val startTime = findNextEmptySlotLasting(task.duration, task.deadline, minStartTime)
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

    private fun findLastPrerequisiteEndTime(task: Task): LocalDateTime {
        return if(task.prerequisites.isEmpty())
            LocalDateTime.now()
        else{
            val time = mTaskSlices.filter { ts -> task.prerequisites.contains(ts.parent)
            }.map { ts -> ts.endTime }.max()!!

            if(isBefore(time, LocalDateTime.now()))
                LocalDateTime.now()
            else
                time
        }
    }

    @ExperimentalTime
    private fun findNextEmptySlotLasting(minutes: Duration, deadline: LocalDateTime, startTime: LocalDateTime?): LocalDateTime? {
        var currTime = startTime!!
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
        val free=  !mAllInsertedEntries.any{isBeforeOrEqual(it.startTime,currTime) && isAfter(it.endTime,currTime)}
        val legal = illegalIntervals.any { it.dayOfWeek == currTime.dayOfWeek
                        && isTimeBetweenInclusive(currTime, it.startTime, it.endTime) }
        return free && legal
    }

    @ExperimentalTime
    private fun isTimeAvailableExclude(currTime: LocalDateTime, exclEvent: Event):Boolean{
        val exclEvents = mAllInsertedEntries.filter{e -> e != exclEvent}
        val free = !exclEvents.any{isBeforeOrEqual(it.startTime,currTime) && isAfter(it.endTime,currTime)}
        val legal = illegalIntervals.any { it.dayOfWeek == currTime.dayOfWeek
                && isTimeBetweenInclusive(currTime, it.startTime, it.endTime) }
        return free && legal
    }

    private fun isTimeBetweenInclusive(time: LocalDateTime, earlierDate: LocalDateTime, laterDate:LocalDateTime) : Boolean{
        return DateHelper.isBetweenInclusive(time,
            earlierDate.withYear(time.year).withMonth(time.monthValue).withDayOfMonth(time.dayOfYear),
            earlierDate.withYear(time.year).withMonth(time.monthValue).withDayOfMonth(time.dayOfYear))
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
    fun removeTask(task: Task) {
        val taskChildren: List<TaskSlice> = getSlicesOfTask(task)
        mTaskSlices.removeAll (taskChildren)
        mTasks.remove(task)
        mAllInsertedEntries.removeAll(taskChildren)
        for(tsk in mTasks)
            tsk.prerequisites = tsk.prerequisites.filter {  t-> t!= task }
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
        distributeTasks(tasksToDistribute.toList(), false)
    }

    @ExperimentalTime
    fun getAllPossiblePrerequisites(task:Task): List<Task> {
        //nie mozna do X dac jako prerekwizytu taska, ktorego X jest juz prerekwizytem (zrobi sie glupi cykl)
        return mTasks.filter { t -> !getAllSubTasksInHierarchy(t).contains(task)}
    }

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