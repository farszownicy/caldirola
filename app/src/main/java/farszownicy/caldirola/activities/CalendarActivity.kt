package farszownicy.caldirola.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.activities.AgendaActivity.Companion.DAY_KEY
import farszownicy.caldirola.activities.AgendaActivity.Companion.MONTH_KEY
import farszownicy.caldirola.activities.AgendaActivity.Companion.YEAR_KEY
import farszownicy.caldirola.agendacalendar.AgendaCalendarView
import farszownicy.caldirola.agendacalendar.CalendarPickerController
import farszownicy.caldirola.models.BaseCalendarEntry
import farszownicy.caldirola.models.DayItem
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.Place
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import farszownicy.caldirola.utils.memory.saveTasksToMemory
import kotlinx.android.synthetic.main.calendar_view.*
import java.time.LocalDateTime
import java.util.*
import kotlin.time.ExperimentalTime


class CalendarActivity : AppCompatActivity(), CalendarPickerController {
    lateinit var mAgendaCalendarView: AgendaCalendarView

    companion object{
        const val LOG_TAG = "DEBUG"
    }
    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_view)
        setSupportActionBar(activity_toolbar)
        mAgendaCalendarView = findViewById(R.id.agenda_calendar_view)
        val minDate: Calendar = Calendar.getInstance()
        val maxDate: Calendar = Calendar.getInstance()

        minDate.add(Calendar.MONTH, -2)
        minDate.set(Calendar.DAY_OF_MONTH, 1)
        maxDate.add(Calendar.YEAR, 1)

//        val eventList: List<Event> = ArrayList()
        //mockList(eventList as MutableList<Event>)
        //val calendarManager = CalendarManager.getInstance(applicationContext)
        //calendarManager.buildCal(minDate, maxDate, Locale.getDefault())
        //Log.d(LOG_TAG, eventList.toString())
        //calendarManager.loadEvents(eventList)
        ////////

        ////////
        //val readyEvents = calendarManager.events
        //val readyDays: List<DayItem> = calendarManager.days
        //val readyWeeks: List<WeekItem> = calendarManager.weeks
//        mAgendaCalendarView.init(events, slices, minDate, maxDate, Locale.UK, this)
        mAgendaCalendarView.init(minDate, maxDate, Locale.UK, this)
//        mAgendaCalendarView.addEventRenderer(DefaultEventRenderer())
    }

    private fun mockList(eventList: MutableList<Event>) {
        val timeStart1 = LocalDateTime.now().withHour(2).withMinute(0)
        val timeEnd1 = timeStart1.withHour(3).withMinute(30)
        val event1 = Event("id1","Hurtownie Danych", "laby", timeStart1, timeEnd1, Place("Uczelnia"))
        eventList.add(event1)

        val timeStart2 = LocalDateTime.now().withHour(18).withMinute(0)
        val timeEnd2 = LocalDateTime.now().withHour(20).withMinute(0)
        val event2 = Event("id2","Zlot fanów farszu", "cos tam", timeStart2, timeEnd2, Place("stołówka SKS"))
        eventList.add(event2)

        val timeStart3 = LocalDateTime.now().withHour(14).withMinute(0)
        val timeEnd3 = LocalDateTime.now().withDayOfMonth(5).withHour(15).withMinute(15)
        val event3 = Event("id3","Wyjazd do Iraku", "aaa",  timeStart3, timeEnd3, Place("Irak"))

        eventList.add(event3)
//        val timeStart1 = LocalDateTime.now().
//                                withHour(14).withMinute(0)
//        val timeEnd1 = LocalDateTime.now().plusMonths(1).withHour(15).withMinute(0)
//        val event1 = BaseCalendarEvent(
//            "Ulep pierogi", "A wonderful journey!", "POLSKA!", timeStart1, timeEnd1, true
//        )
//        eventList.add(event1)
//
//        val startTime2 = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0)
//        //Calendar.getInstance()
//        //startTime2.add(Calendar.DAY_OF_YEAR, 1)
//        val endTime2 = LocalDateTime.now().plusDays(3).withHour(11).withMinute(0)
//        val event2 = BaseCalendarEvent(
//            "Epstein didn't kill himself", "A beautiful small town", "", startTime2, endTime2, true
//        )
//        eventList.add(event2)
//
//        val startTime3 = LocalDateTime.now().withHour(9).withMinute(0)//Calendar.getInstance()
//        val endTime3 = LocalDateTime.now().withHour(10).withMinute(0)//Calendar.getInstance()
////        startTime3[Calendar.HOUR_OF_DAY] = 14
////        startTime3[Calendar.MINUTE] = 0
////        endTime3[Calendar.HOUR_OF_DAY] = 15
////        endTime3[Calendar.MINUTE] = 0
//        val event3 = BaseCalendarEvent(
//            "przygotuj farsz",
//            "",
//            "New York",
//            startTime3,
//            endTime3,
//            false
//        )
//        eventList.add(event3)
    }

    override fun onDaySelected(dayItem: DayItem?) {
        Log.d(LOG_TAG, String.format("Selected day: %s", dayItem))
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

    override fun onScrollToDate(calendar: Calendar?) {
        if (supportActionBar != null) {
            supportActionBar!!.title =
                calendar!!.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
        }
    }

    override fun onEventSelected(entry: BaseCalendarEntry) {
        Log.d(LOG_TAG, String.format("Selected event: %s", entry))
        val intent = Intent(this, AgendaActivity::class.java)
        val bundle = Bundle()
        bundle.putInt(DAY_KEY, entry.instanceDay.get(Calendar.DAY_OF_MONTH))
        bundle.putInt(MONTH_KEY, entry.instanceDay.get(Calendar.MONTH)+ 1)
        bundle.putInt(YEAR_KEY, entry.instanceDay.get(Calendar.YEAR))

        intent.putExtras(bundle)

        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

    }
}