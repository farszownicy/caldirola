package farszownicy.caldirola.activities;

import TaskSliceView
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.agendacalendar.CalendarManager
import farszownicy.caldirola.crud_activities.AddEventActivity
import farszownicy.caldirola.crud_activities.AddTaskActivity
import farszownicy.caldirola.crud_activities.EditEventActivity
import farszownicy.caldirola.crud_activities.EditTaskActivity
import farszownicy.caldirola.day_views.CalendarDayView
import farszownicy.caldirola.day_views.CurrentTimeLineView
import farszownicy.caldirola.day_views.EventView
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.TaskSlice
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.Constants.EDIT_EVENT_CODE
import farszownicy.caldirola.utils.Constants.EDIT_TASK_CODE
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import farszownicy.caldirola.utils.memory.saveTasksToMemory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs
import kotlin.time.ExperimentalTime


class AgendaFragment : Fragment() {
    private lateinit var minuteUpdateReceiver: BroadcastReceiver
    lateinit var currTime: LocalDateTime
    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    private var x1 : Float = 0F
    private var x2 : Float = 0F
    var y1 : Float = 0F
    var y2 : Float = 0F

    companion object{
        const val LOG_TAG = "DEBUG"
        const val DAY_KEY = "DAY"
        const val MONTH_KEY = "MONTH"
        const val YEAR_KEY = "YEAR"
        const val MIN_SWIPE_DISTANCE = 80
        const val VERTICAL_SWIPE_THRESHOLD = 70
    }

    private lateinit var root: View
    private lateinit var agenda: CalendarDayView

    @ExperimentalTime
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_agenda, container, false)
        agenda = root.findViewById(R.id.agenda)

        if (arguments != null) {
            day = requireArguments().getInt(DAY_KEY)
            month = requireArguments().getInt(MONTH_KEY)
            year = requireArguments().getInt(YEAR_KEY)
        } else {
            day = LocalDateTime.now().dayOfMonth
            month = LocalDateTime.now().monthValue
            year = LocalDateTime.now().year
        }

        setListeners()
        agenda.setDay(day, month, year)
        agenda.setLimitTime(0, 24)
        updateDisplayedDay()

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    @ExperimentalTime
    private fun setListeners() {
        (agenda.mHandler)!!.setOnEventClickListener(
            object : EventView.OnEventClickListener {
                override fun onEventClick(view: EventView?, data: Event?) {
//                    Toast.makeText(requireContext(),
//                        "onEventClick: ${data!!.name}, event start: ${data.startTime}, event end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}",
//                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), EditEventActivity::class.java)
                    intent.putExtra("ID", data!!.id)
                    startActivityForResult(intent, Constants.EDIT_EVENT_CODE)
                }
            })
//        (agenda.mHandler)!!.setOnEventLongClickListener(
//            object : EventView.OnEventLongClickListener {
//                override fun onEventClick(view: EventView?, data: Event?) {
//                    Log.e("TAG", "onEventLongClick: ${data!!.name}, event start: ${data.startTime}, event end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
//                    PlanManager.removeEvent(data)
//                    agenda.updateEvents()
//                    PlanManager.memoryUpToDate = false
//                    agenda.refreshEntries()
//                    CalendarManager.getInstance().loadEventsAndTasks()
//                }
//            })

        agenda.mHandler!!.setOnTaskSliceClickListener(
            object : TaskSliceView.OnTaskClickListener{
                override fun onTaskClick(view: TaskSliceView?, data: TaskSlice?) {
//                    Toast.makeText(requireContext(),
//                        "onTaskClick:${data!!.parent.name}, divisible: ${data.parent.divisible}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}",
//                        Toast.LENGTH_SHORT).show()
//                    Log.e("TAG","onTaskClick:${data.parent.name}, divisible: ${data.parent.divisible}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view.top}, bottom:${view.bottom}")

                    //Go to edit task activity, then refresh
                    val intent = Intent(requireContext(), EditTaskActivity::class.java)
                    intent.putExtra("ID", data!!.parent.id)
                    startActivityForResult(intent, Constants.EDIT_TASK_CODE)
                    agenda.updateTasks()
                    agenda.refreshEntries()
                    PlanManager.memoryUpToDate = false
                    CalendarManager.getInstance().loadEventsAndTasks()
                }
            })

//        agenda.mHandler!!.setOnTaskSliceLongClickListener(
//            object : TaskSliceView.OnTaskLongClickListener{
//                override fun onTaskClick(view: TaskSliceView?, data: TaskSlice?) {
//                    Log.e("TAG","onTaskClick:${data!!.parent.name}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
//                    //PlanManager.mTaskSlices.remove(data)
//                    PlanManager.removeTask(data.parent)
//                    agenda.updateTasks()
//                    agenda.refreshEntries()
//                    PlanManager.memoryUpToDate = false
//                    CalendarManager.getInstance().loadEventsAndTasks()
//                }
//            })

        root.findViewById<Button>(R.id.next_day_btn).setOnClickListener{
            agenda.moveDay(1)
            updateDisplayedDay()
        }
        root.findViewById<Button>(R.id.prev_day_btn).setOnClickListener{
            agenda.moveDay(-1)
            updateDisplayedDay()
        }

        root.findViewById<FloatingActionButton>(R.id.addButton).setOnClickListener {
            addDialog()
        }

        root.findViewById<ScrollView>(R.id.agenda_scroll_view).setOnTouchListener { v, event ->
            when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x
                    y1 = event.y
                }
                MotionEvent.ACTION_UP -> {
                    x2 = event.x
                    y2 = event.y
                    val deltaX: Float = x2 - x1
                    val deltaY:Float = abs(y2- y1)
                    if (deltaX > MIN_SWIPE_DISTANCE && deltaY < VERTICAL_SWIPE_THRESHOLD) {
                        agenda.moveDay(-1)
                        updateDisplayedDay()
                    }
                    else if(deltaX < - MIN_SWIPE_DISTANCE && deltaY < VERTICAL_SWIPE_THRESHOLD){
                        agenda.moveDay(1)
                        updateDisplayedDay()
                    }
                }
            }
            false
        }
    }

    private fun animateDaySwitch() {
        root.findViewById<View>(R.id.agenda).apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(400).setListener(null)
        }
        root.findViewById<View>(R.id.day_of_month_tv).apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(150).setListener(null)
        }
        root.findViewById<View>(R.id.day_of_week_tv).apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(150).setListener(null)
        }

    }

    @ExperimentalTime
    private fun updateDisplayedDay() {
        val formatDayOfMonth =
            DateTimeFormatter.ofPattern(Constants.DAY_OF_MONTH_FORMAT, Locale.UK)
        val formatDayOfWeek =
            DateTimeFormatter.ofPattern(Constants.DAY_OF_WEEK_FORMAT, Locale.UK)

        root.findViewById<TextView>(R.id.day_of_month_tv).text = agenda.mDay.format(formatDayOfMonth)
        root.findViewById<TextView>(R.id.day_of_week_tv).text = agenda.mDay.format(formatDayOfWeek)
        animateDaySwitch()
        agenda.refreshEntries()
    }

    @ExperimentalTime
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.ADD_EVENT_CODE -> {
                    agenda.refreshWholeView()
                }
                Constants.ADD_TASK_CODE -> {
                    agenda.refreshWholeView()
                }
                Constants.EDIT_EVENT_CODE ->{
                    agenda.refreshWholeView()
                }
                Constants.EDIT_TASK_CODE ->{
                    agenda.refreshWholeView()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


     @ExperimentalTime
     fun addDialog(){
        AlertDialog.Builder(requireContext()).setTitle("CHOOSE ENTRY TYPE")
            .setPositiveButton("EVENT"){_, _ -> startAEActivity()}
            .setNegativeButton("TASK"){_, _ -> startATActivity()}
            .create().show()
    }

    private fun startAEActivity(){
        val intent = Intent(requireContext(), AddEventActivity::class.java)
        startActivityForResult(intent, Constants.ADD_EVENT_CODE)
    }
    //TODO: ZMIENIC NA ADD EVENT, ADD TASK
    @ExperimentalTime
    fun startATActivity() {
        val intent = Intent(requireContext(), AddTaskActivity::class.java)
        startActivityForResult(intent, Constants.ADD_TASK_CODE)
    }

    @ExperimentalTime
    override fun onStop() {
        if(!PlanManager.memoryUpToDate){
            saveEventsToMemory(requireContext())
            saveTasksToMemory(requireContext())
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
        (requireActivity() as AppCompatActivity).unregisterReceiver(minuteUpdateReceiver)
    }

    private fun startCurrMinuteUpdater(){
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_TIME_TICK)
        minuteUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?){
                currTime = currTime.plusMinutes(1)
                updateTime()
            }
        }
        (requireActivity() as AppCompatActivity).registerReceiver(minuteUpdateReceiver, filter)
        updateTime()
    }

    private fun updateTime() {
        val linePosition = agenda.getPositionOfTime(currTime)
        root.findViewById<CalendarDayView>(R.id.agenda)
            .findViewById<CurrentTimeLineView>(R.id.curr_time_line)
            .updatePosition(linePosition, currTime)
    }
}
