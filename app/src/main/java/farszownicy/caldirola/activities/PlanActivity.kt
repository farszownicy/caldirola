package farszownicy.caldirola.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.tibolte.agendacalendarview.AgendaCalendarView
import com.github.tibolte.agendacalendarview.CalendarManager
import com.github.tibolte.agendacalendarview.CalendarPickerController
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent
import com.github.tibolte.agendacalendarview.models.CalendarEvent
import com.github.tibolte.agendacalendarview.models.DayItem
import com.github.tibolte.agendacalendarview.models.WeekItem
import farszownicy.caldirola.R
import kotlinx.android.synthetic.main.test.*
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
        val startTime1 = Calendar.getInstance()
        val endTime1 = Calendar.getInstance()
        endTime1.add(Calendar.MONTH, 1)
        startTime1[Calendar.HOUR_OF_DAY] = 14
        startTime1[Calendar.MINUTE] = 0
        endTime1[Calendar.HOUR_OF_DAY] = 15
        endTime1[Calendar.MINUTE] = 0
        val event1 = BaseCalendarEvent(
            "Ulep pierogi", "A wonderful journey!", "POLSKA!",
            ContextCompat.getColor(this, R.color.blue_selected), startTime1, endTime1, true
        )
        eventList.add(event1)
        val startTime2 = Calendar.getInstance()
        startTime2.add(Calendar.DAY_OF_YEAR, 1)
        val endTime2 = Calendar.getInstance()
        endTime2.add(Calendar.DAY_OF_YEAR, 3)
        startTime2[Calendar.HOUR_OF_DAY]= 14
        startTime2[Calendar.MINUTE] = 0
        endTime2[Calendar.HOUR_OF_DAY] = 15
        endTime2[Calendar.MINUTE] = 0
        val event2 = BaseCalendarEvent(
            "Epstein didn't kill himself", "A beautiful small town", "Wrocław",
            ContextCompat.getColor(this, R.color.yellow), startTime2, endTime2, true
        )
        eventList.add(event2)
        val startTime3 = Calendar.getInstance()
        val endTime3 = Calendar.getInstance()
        startTime3[Calendar.HOUR_OF_DAY] = 14
        startTime3[Calendar.MINUTE] = 0
        endTime3[Calendar.HOUR_OF_DAY] = 15
        endTime3[Calendar.MINUTE] = 0
        val event3 = DrawableCalendarEvent(
            "przygotuj farsz",
            "",
            "New York",
            ContextCompat.getColor(this, R.color.blue_selected),
            startTime3,
            endTime3,
            false,
            R.drawable.ic_launcher_background//ic_dialog_info
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
