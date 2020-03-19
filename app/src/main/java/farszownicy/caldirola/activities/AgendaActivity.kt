package farszownicy.caldirola.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import farszownicy.caldirola.R
import farszownicy.caldirola.data_classes.Place
import farszownicy.caldirola.day_views.EventView
import farszownicy.caldirola.data_classes.Event
import kotlinx.android.synthetic.main.activity_agenda.*
import java.util.*
import kotlin.collections.ArrayList

class AgendaActivity : AppCompatActivity() {
    var events: ArrayList<Event> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agenda)
        agenda.setLimitTime(0, 24)
        (agenda.mHandler)!!.setOnEventClickListener(
            object : EventView.OnEventClickListener {
                override fun onEventClick(view: EventView?, data: Event?) {
                    Log.e("TAG", "onEventClick:" + data!!.name)
                }

                override fun onEventViewClick(
                    view: View?,
                    eventView: EventView?,
                    data: Event?
                ) {
                    Log.e("TAG", "onEventViewClick:" + data!!.name)
                    agenda.mEvents = events
                }
            })
        addEvents()
    }

    private fun addEvents() {
        val timeStart1 = Calendar.getInstance()
        timeStart1[Calendar.HOUR_OF_DAY] = 11
        timeStart1[Calendar.MINUTE] = 0
        val timeEnd1 = timeStart1.clone() as Calendar
        timeEnd1[Calendar.HOUR_OF_DAY] = 15
        timeEnd1[Calendar.MINUTE] = 30
        val event1 = Event("abc", timeStart1, timeEnd1, Place("Dom"))
        events.add(event1)
        val timeStart2 = Calendar.getInstance()
        timeStart2[Calendar.HOUR_OF_DAY] = 18
        timeStart2[Calendar.MINUTE] = 0
        val timeEnd2 = Calendar.getInstance()
        timeEnd2[Calendar.HOUR_OF_DAY] = 20
        timeEnd2[Calendar.MINUTE] = 30
        val event2 = Event("abc", timeStart2, timeEnd2, Place("Uczelnia"))
        events.add(event2)
        agenda.mEvents = events
    }

}