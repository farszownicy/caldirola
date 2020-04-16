package farszownicy.caldirola.activities

import TaskSliceView
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.crud_activities.AddEventActivity
import farszownicy.caldirola.data_classes.Place
import farszownicy.caldirola.day_views.EventView
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.data_classes.Task
import farszownicy.caldirola.data_classes.TaskSlice
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.readObjectsFromSharedPreferences
import kotlinx.android.synthetic.main.activity_agenda.*
import java.time.LocalDateTime
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

class AgendaActivity : AppCompatActivity() {
    var events: ArrayList<Event> = ArrayList()
    var tasks: ArrayList<Task> = ArrayList()

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        agenda.setLimitTime(0, 24)
        (agenda.mHandler)!!.setOnEventClickListener(
            object : EventView.OnEventClickListener {
                override fun onEventClick(view: EventView?, data: Event?) {
                    Log.e("TAG", "onEventClick: ${data!!.name}, event start: ${data.startTime}, event end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
                    addTasks()
                }
            })
        agenda.mHandler!!.setOnTaskSliceClickListener(
            object : TaskSliceView.OnTaskClickListener{
                override fun onTaskClick(view: TaskSliceView?, data: TaskSlice?) {
                    Log.e("TAG","onTaskClick:${data!!.parent.name}, task start: ${data.startTime}, task end: ${data.endTime}, top:${view!!.top}, bottom:${view.bottom}")
                    //addEvents()
                }
            })
        addButton.setOnClickListener{
            val intent = Intent(this, AddEventActivity::class.java)
            startActivityForResult(intent, Constants.ADD_EVENT_CODE)
        }
        addEvents()
        //addTasks()
    }

    @ExperimentalTime
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                Constants.ADD_EVENT_CODE -> {
                    agenda.drawEvents()
                }
            }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @ExperimentalTime
    private fun addTasks() {
        val deadline1 = LocalDateTime.now().withHour(22).withMinute(0)
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
        tasks.add(task3)
        PlanManager.mTasks = tasks
        agenda.drawTasks()
    }

    @ExperimentalTime
    private fun addEvents() {
        val timeStart1 = LocalDateTime.now().withHour(2).withMinute(0)
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
        events.add(event3)//PlanManager.addEvent(event3)
        PlanManager.addEvent(event3)
        agenda.drawEvents()
    }

}
