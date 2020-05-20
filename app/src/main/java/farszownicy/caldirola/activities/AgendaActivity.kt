package farszownicy.caldirola.activities;

import TaskSliceView
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.agendacalendar.CalendarManager
import farszownicy.caldirola.crud_activities.AddEventActivity
import farszownicy.caldirola.crud_activities.AddTaskActivity
import farszownicy.caldirola.crud_activities.EditEventActivity
import farszownicy.caldirola.crud_activities.EditTaskActivity
import farszownicy.caldirola.day_views.EventView
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.TaskSlice
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.Constants.EDIT_EVENT_CODE
import farszownicy.caldirola.utils.Constants.EDIT_TASK_CODE
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import farszownicy.caldirola.utils.memory.saveTasksToMemory
import kotlinx.android.synthetic.main.activity_agenda.*
import kotlinx.android.synthetic.main.activity_agenda.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs
import kotlin.time.ExperimentalTime


class AgendaActivity : AppCompatActivity() {
    lateinit var minuteUpdateReceiver: BroadcastReceiver
    lateinit var currTime:LocalDateTime
    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    var x1 : Float = 0F
    var x2 : Float = 0F

    companion object{
        const val LOG_TAG = "DEBUG"
        const val DAY_KEY = "DAY"
        const val MONTH_KEY = "MONTH"
        const val YEAR_KEY = "YEAR"
        const val MIN_SWIPE_DISTANCE = 100
    }

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        val b = intent.extras
        if (b != null) {
            day = b.getInt(DAY_KEY)
            month = b.getInt(MONTH_KEY)
            year = b.getInt(YEAR_KEY)
        }

        agenda.setDay(day, month, year)
        agenda.setLimitTime(0, 24)
        updateDisplayedDay()
        setListeners()
    }

    @ExperimentalTime
    private fun setListeners() {
        (agenda.mHandler)!!.setOnEventClickListener(
            object : EventView.OnEventClickListener {
                override fun onEventClick(view: EventView?, data: Event?) {
                    Toast.makeText(this@AgendaActivity,
                        "onEventClick: ${data!!.name}, event start: ${data.startTime}, event end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}",
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AgendaActivity, EditEventActivity::class.java)
                    intent.putExtra("ID", data.id)
                    startActivityForResult(intent, Constants.EDIT_EVENT_CODE)
                }
            })
        (agenda.mHandler)!!.setOnEventLongClickListener(
            object : EventView.OnEventLongClickListener {
                override fun onEventClick(view: EventView?, data: Event?) {
                    Log.e("TAG", "onEventLongClick: ${data!!.name}, event start: ${data.startTime}, event end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
                    PlanManager.removeEvent(data)
                    agenda.updateEvents()
                    PlanManager.memoryUpToDate = false
                    agenda.refreshEntries()
                    CalendarManager.getInstance().loadEventsAndTasks()
                }
            })

        agenda.mHandler!!.setOnTaskSliceClickListener(
            object : TaskSliceView.OnTaskClickListener{
                override fun onTaskClick(view: TaskSliceView?, data: TaskSlice?) {
                    Toast.makeText(this@AgendaActivity,
                        "onTaskClick:${data!!.parent.name}, divisible: ${data.parent.divisible}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}",
                        Toast.LENGTH_SHORT).show()
                    Log.e("TAG","onTaskClick:${data!!.parent.name}, divisible: ${data.parent.divisible}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")

                    //Go to edit task activity, then refresh
                    val intent = Intent(this@AgendaActivity, EditTaskActivity::class.java)
                    intent.putExtra("ID", data.parent.id)
                    startActivityForResult(intent, Constants.EDIT_TASK_CODE)
                    agenda.updateTasks()
                    agenda.refreshEntries()
                    PlanManager.memoryUpToDate = false
                    CalendarManager.getInstance().loadEventsAndTasks()
                }
            })

        agenda.mHandler!!.setOnTaskSliceLongClickListener(
            object : TaskSliceView.OnTaskLongClickListener{
                override fun onTaskClick(view: TaskSliceView?, data: TaskSlice?) {
                    Log.e("TAG","onTaskClick:${data!!.parent.name}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
                    //PlanManager.mTaskSlices.remove(data)
                    PlanManager.removeTask(data.parent);
                    agenda.updateTasks()
                    agenda.refreshEntries()
                    PlanManager.memoryUpToDate = false
                    CalendarManager.getInstance().loadEventsAndTasks()
                }
            })

        next_day_btn.setOnClickListener{
            agenda.moveDay(1)
            updateDisplayedDay()
        }
        prev_day_btn.setOnClickListener{
            agenda.moveDay(-1)
            updateDisplayedDay()
        }

        addButton.setOnClickListener {
            AddDialog()
        }

        agenda_scroll_view.setOnTouchListener { v, event ->
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> x1 = event.x
                MotionEvent.ACTION_UP -> {
                    x2 = event.x
                    val deltaX: Float = x2 - x1
                    if (deltaX > MIN_SWIPE_DISTANCE) {
                        agenda.moveDay(-1)
                        updateDisplayedDay()
                    }
                    else if(deltaX < - MIN_SWIPE_DISTANCE){
                        agenda.moveDay(1)
                        updateDisplayedDay()
                    }
                }
            }
            false
        }
    }

    @ExperimentalTime
    private fun updateDisplayedDay() {
        val formatDayOfMonth =
            DateTimeFormatter.ofPattern(Constants.DAY_OF_MONTH_FORMAT, Locale.UK);
        val formatDayOfWeek =
            DateTimeFormatter.ofPattern(Constants.DAY_OF_WEEK_FORMAT, Locale.UK)

        day_of_month_tv.text = agenda.mDay.format(formatDayOfMonth)
        day_of_week_tv.text = agenda.mDay.format(formatDayOfWeek)
        agenda.refreshEntries()
    }

    @ExperimentalTime
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.ADD_EVENT_CODE -> {
                    agenda.updateEvents()
                    agenda.drawEvents()
                }
                Constants.ADD_TASK_CODE -> {
                    agenda.updateTasks()
                    agenda.drawTasks()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


     @ExperimentalTime
     fun AddDialog(){
        AlertDialog.Builder(this@AgendaActivity).setTitle("CHOOSE ENTRY TYPE")
            .setPositiveButton("EVENT"){dialog, id -> StartAEActivity()}
            .setNegativeButton("TASK"){dialog, id -> StartATActivity()}
            .create().show()
    }

    fun StartAEActivity(){
        val intent = Intent(this, AddEventActivity::class.java)
        startActivityForResult(intent, Constants.ADD_EVENT_CODE)
    }
    //TODO: ZMIENIC NA ADD EVENT, ADD TASK
    @ExperimentalTime
    fun StartATActivity() {
        val intent = Intent(this, AddTaskActivity::class.java)
        startActivityForResult(intent, Constants.ADD_TASK_CODE)
    }

    @ExperimentalTime
    override fun onStop() {
        if(!PlanManager.memoryUpToDate){
            saveEventsToMemory(this)
            saveTasksToMemory(this)
            PlanManager.memoryUpToDate = true
        }
        super.onStop()
    }

    override fun onResume(){
        super.onResume()
        currTime = LocalDateTime.now()
        startCurrMinuteUpdater()
    }
    override fun onPause(){
        super.onPause()
        unregisterReceiver(minuteUpdateReceiver)
    }

    fun startCurrMinuteUpdater(){
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_TIME_TICK)
        minuteUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?){
                currTime = currTime.plusMinutes(1)
                updateTime()
            }
        }
        registerReceiver(minuteUpdateReceiver, filter)
        updateTime()
    }

    private fun updateTime() {
        val linePosition = agenda.getPositionOfTime(currTime)
        agenda.curr_time_line.updatePosition(linePosition, currTime)
    }
}
