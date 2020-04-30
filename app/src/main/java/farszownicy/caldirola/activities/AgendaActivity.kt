package farszownicy.caldirola.activities;

import TaskSliceView
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.crud_activities.AddEventActivity
import farszownicy.caldirola.crud_activities.AddTaskActivity
import farszownicy.caldirola.day_views.EventView
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.data_classes.TaskSlice
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.Constants.MINUTES_IN_DAY
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import farszownicy.caldirola.utils.memory.saveTasksToMemory
import kotlinx.android.synthetic.main.activity_agenda.*
import kotlinx.android.synthetic.main.activity_agenda.view.*
import java.time.LocalDateTime
import kotlin.time.ExperimentalTime

class AgendaActivity : AppCompatActivity() {
    var memoryUpToDate = true
    lateinit var minuteUpdateReceiver: BroadcastReceiver
    lateinit var currTime:LocalDateTime

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        agenda.setLimitTime(0, 24)
        (agenda.mHandler)!!.setOnEventClickListener(
            object : EventView.OnEventClickListener {
                override fun onEventClick(view: EventView?, data: Event?) {
                    Toast.makeText(this@AgendaActivity,
                        "onEventClick: ${data!!.name}, event start: ${data.startTime}, event end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}",
                        Toast.LENGTH_SHORT).show()
                    Log.e("TAG", "onEventClick: ${data!!.name}, event start: ${data.startTime}, event end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
                }
            })
        (agenda.mHandler)!!.setOnEventLongClickListener(
            object : EventView.OnEventLongClickListener {
                override fun onEventClick(view: EventView?, data: Event?) {
                    Log.e("TAG", "onEventLongClick: ${data!!.name}, event start: ${data.startTime}, event end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
                    PlanManager.mEvents.remove(data)
                    PlanManager.mAllInsertedEntries.remove(data)
                    memoryUpToDate = false
                    agenda.refreshEntries()
                }
            })

        agenda.mHandler!!.setOnTaskSliceClickListener(
            object : TaskSliceView.OnTaskClickListener{
                override fun onTaskClick(view: TaskSliceView?, data: TaskSlice?) {
                    Toast.makeText(this@AgendaActivity,
                        "onTaskClick:${data!!.parent.name}, divisible: ${data.parent.divisible}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}",
                        Toast.LENGTH_SHORT).show()
                    Log.e("TAG","onTaskClick:${data!!.parent.name}, divisible: ${data.parent.divisible}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
                    memoryUpToDate = false
                }
            })

        agenda.mHandler!!.setOnTaskSliceLongClickListener(
            object : TaskSliceView.OnTaskLongClickListener{
                override fun onTaskClick(view: TaskSliceView?, data: TaskSlice?) {
                    Log.e("TAG","onTaskClick:${data!!.parent.name}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
                    PlanManager.mTaskSlices.remove(data)
                    PlanManager.mAllInsertedEntries.remove(data)
                    agenda.refreshEntries()
                }
            })
        addButton.setOnClickListener {
            AddDialog()
        }
        addEvents()
        addTasks()
    }

    @ExperimentalTime
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constants.ADD_EVENT_CODE -> {
                    agenda.drawEvents()
                }
                Constants.ADD_TASK_CODE -> {
                    agenda.drawTasks()
                }
            }
            memoryUpToDate = false
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @ExperimentalTime
    private fun addTasks() {
/*        val deadline1 = LocalDateTime.now().withHour(22).withMinute(0)
        val task1 = Task("id2", "ulep pierogi", "graj w minikraft",
            deadline1, 340.minutes, 0,divisible=true)
        tasks.add(task1)

        val deadline4 = LocalDateTime.now().withHour(22).withMinute(0)
        val task4 = Task("id4", "podzielne zadanie", "graj w minikraft",
            deadline4, 400.minutes, 0,divisible=true)
        tasks.add(task4)
        val deadline5 = LocalDateTime.now().withHour(22).withMinute(30)
        val task5 = Task("id4", "podzielne zadanie 2", "graj w minikraft",
            deadline5, 200.minutes, 0,divisible=true)
        //tasks.add(task5)


        val deadline2 = LocalDateTime.now().withHour(15).withMinute(0)
        val task2 = Task("id1", "wałkuj ciasto", "opis",
            deadline2, 120.minutes, 0,divisible=false)
        tasks.add(task2)

        val deadline3 = LocalDateTime.now().withHour(23).withMinute(0)
        val task3 = Task("id3", "graj w minecraft", "opis",
            deadline3, 60.minutes, 0,divisible=false)
        tasks.add(task3)*/
        agenda.drawTasks()
    }

    @ExperimentalTime
    private fun addEvents() {
/*        val timeStart1 = LocalDateTime.now().withHour(2).withMinute(0)
        val timeEnd1 = timeStart1.withHour(3).withMinute(30)
        val event1 = Event("id1","Hurtownie Danych", "laby", timeStart1, timeEnd1, Place("Uczelnia"))
        PlanManager.addEvent(event1)

        val timeStart2 = LocalDateTime.now().withHour(18).withMinute(0)
        val timeEnd2 = LocalDateTime.now().withHour(20).withMinute(0)
        val event2 = Event("id2","Zlot fanów farszu", "cos tam", timeStart2, timeEnd2, Place("stołówka SKS"))
        PlanManager.addEvent(event2)

        val timeStart3 = LocalDateTime.now().withHour(14).withMinute(0)
        val timeEnd3 = LocalDateTime.now().withHour(15).withMinute(15)
        val event3 = Event("id3","Wyjazd do Iraku", "aaa",  timeStart3, timeEnd3, Place("Irak"))

        PlanManager.addEvent(event3)*/
        agenda.drawEvents()
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

    @ExperimentalTime
    fun StartATActivity() {
        val intent = Intent(this, AddTaskActivity::class.java)
        startActivityForResult(intent, Constants.ADD_TASK_CODE)
    }

    @ExperimentalTime
    override fun onStop() {
        if(!memoryUpToDate){
            saveEventsToMemory(this)
            saveTasksToMemory(this)
            memoryUpToDate = true
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
