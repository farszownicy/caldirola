package farszownicy.caldirola.activities

import TaskSliceView
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import farszownicy.caldirola.R
import farszownicy.caldirola.data_classes.Place
import farszownicy.caldirola.day_views.EventView
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.data_classes.Task
import farszownicy.caldirola.data_classes.TaskSlice
import kotlinx.android.synthetic.main.activity_agenda.*
import java.util.*
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
                    Log.e("TAG", "onEventClick:" + data!!.name)
                    //addTasks()
                }
            })
        agenda.mHandler!!.setOnTaskSliceClickListener(
            object : TaskSliceView.OnTaskClickListener{
                override fun onTaskClick(view: TaskSliceView?, data: TaskSlice?) {
                    Log.e("TAG","onTaskClick:" + data!!.parent.name)
                }
            })
        addEvents()
        addTasks()
    }

    @ExperimentalTime
    private fun addTasks() {

        val deadline1 = Calendar.getInstance()
        deadline1.set(Calendar.HOUR_OF_DAY, 22) //[Calendar.HOUR_OF_DAY] = 23
        deadline1.set(Calendar.MINUTE, 0)
        val task1 = Task("id2", "graj w minikraft", "graj w minikraft",
            deadline1, 800.minutes, 0,divisible=true)
        tasks.add(task1)

        val deadline2 = Calendar.getInstance()
        deadline2.set(Calendar.HOUR_OF_DAY, 15) //[Calendar.HOUR_OF_DAY] = 23
        deadline2.set(Calendar.MINUTE, 0)
        val task2 = Task("id1", "wałkuj ciasto", "opis",
            deadline2, 120.minutes, 0,divisible=false)
        tasks.add(task2)

        val deadline3 = Calendar.getInstance()
        deadline3.set(Calendar.HOUR_OF_DAY, 23) //[Calendar.HOUR_OF_DAY] = 23
        deadline3.set(Calendar.MINUTE, 0)
        val task3 = Task("id3", "ulep pierogi", "opis",
            deadline3, 60.minutes, 0,divisible=false)
        tasks.add(task3)
        agenda.mTasks = tasks
    }

    private fun addEvents() {
        val timeStart1 = Calendar.getInstance()
        timeStart1[Calendar.HOUR_OF_DAY] = 11
        timeStart1[Calendar.MINUTE] = 0
        val timeEnd1 = timeStart1.clone() as Calendar
        timeEnd1[Calendar.HOUR_OF_DAY] = 13
        timeEnd1[Calendar.MINUTE] = 30
        val event1 = Event("id1","Hurtownie Danych", "laby", timeStart1, timeEnd1, Place("Uczelnia"))
        events.add(event1)

        val timeStart2 = Calendar.getInstance()
        timeStart2[Calendar.HOUR_OF_DAY] = 18
        timeStart2[Calendar.MINUTE] = 0
        val timeEnd2 = Calendar.getInstance()
        timeEnd2[Calendar.HOUR_OF_DAY] = 20
        timeEnd2[Calendar.MINUTE] = 0
        val event2 = Event("id2","Zlot fanów farszu", "cos tam", timeStart2, timeEnd2, Place("stołówka SKS"))
        events.add(event2)

        val timeStart3 = Calendar.getInstance()
        timeStart3[Calendar.HOUR_OF_DAY] = 14
        timeStart3[Calendar.MINUTE] = 0
        val timeEnd3 = Calendar.getInstance()
        timeEnd3[Calendar.HOUR_OF_DAY] = 15
        timeEnd3[Calendar.MINUTE] = 15
        val event3 = Event("id3","Wyjazd do Iraku", "aaa",  timeStart3, timeEnd3, Place("Irak"))
        events.add(event3)
        agenda.mEvents = events
    }

}
