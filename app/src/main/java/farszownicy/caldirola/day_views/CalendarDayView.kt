package farszownicy.caldirola.day_views

import TaskSliceView
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import farszownicy.caldirola.R
import farszownicy.caldirola.data_classes.AgendaDrawableEntry
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.data_classes.Task
import farszownicy.caldirola.data_classes.TaskSlice
import kotlinx.android.synthetic.main.agenda_view.view.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.collections.ArrayList
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

//Widok agendy dnia
class CalendarDayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr){

    /**
     * SECTION CLASS VARIABLES
     * */

    //TODO: przerob AllInsertedEntries na SortedListe, ogarnij zeby jak w activity dodasz taska juz po onCreate to zeby on sie pojawił

    private var mHourHeight = 0
    private var mEventMarginSide = 4
    private var mHourTextWidth = 120
    private var mHourTextHeight = 120
    private var mSeparateHourHeight = 0
    private var mStartHour = 0
    private var mEndHour = 24

    var mHandler: AgendaHandler? = null

    @ExperimentalTime
    var mEvents: ArrayList<Event> = ArrayList()
    set(events){
        events.sortBy{it.startTime}
        field = events
        updateAllEntries()
        drawEvents()
        //drawEvents()
    }

    @ExperimentalTime
    var mTasks: ArrayList<Task> = ArrayList()
    set(tasks){
        tasks.sortBy{it.deadline}
        field = tasks
        distributeTasks()
        //updateAllEntries()
        //drawTasks()
        drawTasks()
    }

    var mTaskSlices: ArrayList<TaskSlice> = ArrayList()
    var mAllInsertedEntries: ArrayList<AgendaDrawableEntry> = ArrayList()

    init{
        LayoutInflater.from(context).inflate(R.layout.agenda_view, this, true)
        mHourHeight = resources.getDimensionPixelSize(R.dimen.day_height)
        mHandler = AgendaHandler(context)
        //extractTaskSlices()
        drawHourViews()
    }

    companion object {
        const val SECOND_MILLIS: Long = 1000
        const val MINUTE_MILLIS = SECOND_MILLIS * 60
    }

    /**
     * SECTION: DRAWING AND POSITIONING
     * */

    @ExperimentalTime
    private fun updateAllEntries() {
        mAllInsertedEntries = ArrayList()
        mAllInsertedEntries.addAll(mEvents)
        mAllInsertedEntries.addAll(mTaskSlices)
        mAllInsertedEntries.sortBy{it.startTime}
    }

    @ExperimentalTime
    fun refreshWholeView() {
        drawHourViews()
        event_container.removeAllViews()
        drawEvents()
        //distributeTasks()
        drawTasks()
    }

    fun drawHourViews() {
        if(dayview_container != null)
            dayview_container.removeAllViews()
        var hourView: HourView? = null
        for (i in mStartHour..mEndHour) {
            hourView = mHandler!!.getHourView(i)
            dayview_container.addView(hourView)
        }
        mHourTextWidth = hourView!!.hourTextWidth.toInt()
        mHourTextHeight = hourView.hourTextHeight.toInt()
        mSeparateHourHeight = hourView.separateHeight.toInt()
    }

    @ExperimentalTime
    private fun drawEvents() {
//        event_container.removeAllViews()
        for (event in mEvents) {
            val rect = getTimeBound(event)
            val eventView: EventView =
                mHandler!!.getEventView(event, rect, mHourTextHeight/2, mSeparateHourHeight)
            event_container.addView(eventView, eventView.layoutParams)
        }
    }

    @ExperimentalTime
    private fun drawTasks() {
        for(taskSlice in mTaskSlices) {
            val rect = getTimeBound(taskSlice)
            val taskSliceView: TaskSliceView =
                mHandler!!.getTaskSliceView(taskSlice, rect, mHourTextHeight/2 , mSeparateHourHeight)
            event_container.addView(taskSliceView, taskSliceView.layoutParams)
        }
    }

    private fun getTimeBound(entry: AgendaDrawableEntry): Rect {
        val rect = Rect()
        rect.top = getPositionOfTime(entry.startTime) + mSeparateHourHeight
        Log.d("DEBUG", "separator: $mSeparateHourHeight")
        Log.d("Debug", "mHourTextHeight: $mHourTextHeight")
        rect.bottom =
            getPositionOfTime(entry.endTime) - mSeparateHourHeight
        rect.left = mHourTextWidth + mEventMarginSide
        Log.d("Debug", "eventMargin: $mEventMarginSide")
        rect.right = -mEventMarginSide//width - mEventMarginSide
        Log.d("Debug", "width: $width")
        return rect
    }

    private fun getPositionOfTime(dateTime: LocalDateTime): Int {
        val hour = dateTime.hour - mStartHour
        val minute = dateTime.minute//calendar[Calendar.MINUTE]
        return hour * mHourHeight + minute * mHourHeight / 60
    }

    @ExperimentalTime
    fun setLimitTime(startHour: Int, endHour: Int) {
        require(startHour < endHour) { "start hour must precede end hour" }
        mStartHour = startHour
        mEndHour = endHour
        refreshWholeView()
    }

    /**
     * SECTION: TASK CALCULATIONS
     * */

    @ExperimentalTime
    private fun distributeTasks() {
        for(task in mTasks) {
            if (!task.divisible) {
                insertNonDivisibleTask(task)
            }
            else
                insertDivisibleTask(task)
        }
    }

    @ExperimentalTime
    private fun insertDivisibleTask(task: Task) {
        var currTime = LocalDateTime.now().withHour(0).withMinute(0)
        var totalInsertedDuration = 0
        var sliceDuration: Int

        while(isBefore(currTime, task.deadline) && totalInsertedDuration < task.duration.inMinutes) {

            for(insertedEntry in mAllInsertedEntries){
                if(isBeforeOrEqual(insertedEntry.startTime, currTime) && isAfter(insertedEntry.endTime, currTime)) //jesli jakis event nachodzi na aktualny czas
                    currTime = insertedEntry.endTime //to przesun sie na czas konca tego eventu
            }

            val slotStartTime = currTime
            sliceDuration = 0

            while(isTimeAvailable(currTime) && totalInsertedDuration + sliceDuration < task.duration.inMinutes
                && isBefore(currTime, task.deadline)) {
                currTime = currTime.plusMinutes(1)
                sliceDuration += 1
            }

            val slotEndTime = slotStartTime.plusMinutes(sliceDuration.toLong())

            val slice = TaskSlice(task, slotStartTime, slotEndTime)
            mTaskSlices.add(slice)
            mAllInsertedEntries.add(slice)
            mAllInsertedEntries.sortBy{it.startTime}
            totalInsertedDuration += sliceDuration
        }
        if(totalInsertedDuration < task.duration.inMinutes) {
            //abortTask(task)
            Log.d("LOG", "Nie udalo sie wcisnac calego taska ${task.name}.")
            Toast.makeText(this.context, "Podzielnego zadania ${task.name} nie da sie wcisnac do kalendarza.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abortTask(task: Task) {
        throw NotImplementedError()
        //mTaskSlices = mTaskSlices.filter { it.parent != task } as ArrayList<TaskSlice>
    }

    @ExperimentalTime
    private fun insertNonDivisibleTask(task: Task) {
        val startTime = findNextEmptySlotLasting(task.duration, task.deadline)
        if(startTime != null) {
            val endTime = startTime.plusMinutes(task.duration.inMinutes.toLong())
            val slice = TaskSlice(task, startTime, endTime)
            mTaskSlices.add(slice)
            mAllInsertedEntries.add(slice)
            mAllInsertedEntries.sortBy { it.startTime }
        }
        else
            Log.d("LOG", "zadania ${task.name} nie da sie wcisnac do kalendarza")
        Toast.makeText(this.context, "Niepodzielnego zadania ${task.name} nie da sie wcisnac do kalendarza.", Toast.LENGTH_SHORT).show()
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

    fun isBeforeOrEqual(earlierDate: LocalDateTime, laterDate: LocalDateTime): Boolean {
        return differenceInMinutes(earlierDate, laterDate) >= 0
    }

    fun isBefore(earlierDate: LocalDateTime, laterDate: LocalDateTime): Boolean {
        return differenceInMinutes(earlierDate, laterDate) > 0
    }

    fun isAfter(firstDate: LocalDateTime, secDate: LocalDateTime): Boolean {
        return differenceInMinutes(firstDate, secDate) < 0
    }

    private fun areEqual(firstDate: LocalDateTime, secDate: LocalDateTime): Boolean {
        return differenceInMinutes(firstDate, secDate) == 0L
    }

    private fun differenceInMinutes(earlierDate: LocalDateTime, laterDate: LocalDateTime): Long {
        return ChronoUnit.MINUTES.between(
            earlierDate.truncatedTo(ChronoUnit.MINUTES),
            laterDate.truncatedTo(ChronoUnit.MINUTES))
    }

//    private fun extractTaskSlices() {
//        for(task in mTasks)
//            for(timeSlice in task.timeSlices) {
//                val taskSlice = TaskSlice(task, timeSlice.first, timeSlice.second)
//                mTaskSlices.add(taskSlice)
//            }
//    }
}