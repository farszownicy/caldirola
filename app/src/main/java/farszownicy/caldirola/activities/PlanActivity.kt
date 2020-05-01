package farszownicy.caldirola.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.agendacalendar.AgendaCalendarView
import com.example.agendacalendar.CalendarPickerController
import com.example.agendacalendar.models.BaseCalendarEvent
import com.example.agendacalendar.models.CalendarEvent
import com.example.agendacalendar.models.DayItem
//import com.github.tibolte.agendacalendarview.AgendaCalendarView
//import com.github.tibolte.agendacalendarview.CalendarManager
//import com.github.tibolte.agendacalendarview.CalendarPickerController
//import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent
//import com.github.tibolte.agendacalendarview.models.CalendarEvent
//import com.github.tibolte.agendacalendarview.models.DayItem




import farszownicy.caldirola.R
import kotlinx.android.synthetic.main.test.*
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class PlanActivity : AppCompatActivity(), CalendarPickerController {
    lateinit var mAgendaCalendarView: AgendaCalendarView

    companion object{
        const val LOG_TAG = "DEBUG"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        setSupportActionBar(activity_toolbar)
        mAgendaCalendarView = findViewById(R.id.agenda_calendar_view)
        val minDate: Calendar = Calendar.getInstance()
        val maxDate: Calendar = Calendar.getInstance()

        minDate.add(Calendar.MONTH, -2)
        minDate.set(Calendar.DAY_OF_MONTH, 1)
        maxDate.add(Calendar.YEAR, 1)

        val eventList: List<CalendarEvent> = ArrayList()
        mockList(eventList as MutableList<CalendarEvent>)
        //val calendarManager = CalendarManager.getInstance(applicationContext)
        //calendarManager.buildCal(minDate, maxDate, Locale.getDefault())
        //Log.d(LOG_TAG, eventList.toString())
        //calendarManager.loadEvents(eventList)
        ////////

        ////////
        //val readyEvents = calendarManager.events
        //val readyDays: List<DayItem> = calendarManager.days
        //val readyWeeks: List<WeekItem> = calendarManager.weeks
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this)
        mAgendaCalendarView.addEventRenderer(DrawableEventRenderer())
    }

    private fun mockList(eventList: MutableList<CalendarEvent>) {
        val timeStart1 = LocalDateTime.now().
                                withHour(14).withMinute(0)
        val timeEnd1 = LocalDateTime.now().plusMonths(1).withHour(15).withMinute(0)
//        endTime1.add(Calendar.MONTH, 1)
//        startTime1[Calendar.HOUR_OF_DAY] = 14
//        startTime1[Calendar.MINUTE] = 0
//        endTime1[Calendar.HOUR_OF_DAY] = 15
//        endTime1[Calendar.MINUTE] = 0
        val event1 = BaseCalendarEvent(
            "Ulep pierogi", "A wonderful journey!", "POLSKA!",
            ContextCompat.getColor(this, R.color.blue_selected), timeStart1, timeEnd1, true
        )
        eventList.add(event1)

        val startTime2 = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0)
        //Calendar.getInstance()
        //startTime2.add(Calendar.DAY_OF_YEAR, 1)
        val endTime2 = LocalDateTime.now().plusDays(3).withHour(15).withMinute(0)
        //Calendar.getInstance()
        //endTime2.add(Calendar.DAY_OF_YEAR, 3)
        //startTime2[Calendar.HOUR_OF_DAY]= 14
        //startTime2[Calendar.MINUTE] = 0
        //endTime2[Calendar.HOUR_OF_DAY] = 15
        //endTime2[Calendar.MINUTE] = 0
        val event2 = BaseCalendarEvent(
            "Epstein didn't kill himself", "A beautiful small town", "Wrocław",
            ContextCompat.getColor(this, R.color.yellow), startTime2, endTime2, true
        )
        eventList.add(event2)

        val startTime3 = LocalDateTime.now().withHour(14).withMinute(0)//Calendar.getInstance()
        val endTime3 = LocalDateTime.now().withHour(15).withMinute(0)//Calendar.getInstance()
//        startTime3[Calendar.HOUR_OF_DAY] = 14
//        startTime3[Calendar.MINUTE] = 0
//        endTime3[Calendar.HOUR_OF_DAY] = 15
//        endTime3[Calendar.MINUTE] = 0
        val event3 = BaseCalendarEvent(
            "przygotuj farsz",
            "",
            "New York",
            ContextCompat.getColor(this, R.color.blue_selected),
            startTime3,
            endTime3,
            false
        )
        eventList.add(event3)
    }

    override fun onDaySelected(dayItem: DayItem?) {
        Log.d(LOG_TAG, String.format("Selected day: %s", dayItem))
    }

    override fun onScrollToDate(calendar: Calendar?) {
        if (supportActionBar != null) {
            supportActionBar!!.title =
                calendar!!.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        }
    }

    override fun onEventSelected(event: CalendarEvent?) {
        Log.d(LOG_TAG, String.format("Selected event: %s", event))
    }
}