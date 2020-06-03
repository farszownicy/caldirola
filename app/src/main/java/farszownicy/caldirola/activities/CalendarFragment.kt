package farszownicy.caldirola.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.activities.AgendaFragment.Companion.DAY_KEY
import farszownicy.caldirola.activities.AgendaFragment.Companion.MONTH_KEY
import farszownicy.caldirola.activities.AgendaFragment.Companion.YEAR_KEY
import farszownicy.caldirola.agendacalendar.AgendaCalendarView
import farszownicy.caldirola.agendacalendar.CalendarManager
import farszownicy.caldirola.agendacalendar.CalendarPickerController
import farszownicy.caldirola.crud_activities.AddEventActivity
import farszownicy.caldirola.crud_activities.AddTaskActivity
import farszownicy.caldirola.models.BaseCalendarEntry
import farszownicy.caldirola.models.DayItem
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.Place
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import farszownicy.caldirola.utils.memory.saveTasksToMemory
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.time.LocalDateTime
import java.util.*
import kotlin.time.ExperimentalTime


class CalendarFragment : Fragment(), CalendarPickerController {
    lateinit var mAgendaCalendarView: AgendaCalendarView
    var initialized = false;

    companion object {
        const val LOG_TAG = "DEBUG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @ExperimentalTime
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_calendar, container, false)

        (requireActivity() as AppCompatActivity).setSupportActionBar(activity_toolbar)
        if(!initialized) {
            mAgendaCalendarView = root.findViewById(R.id.agenda_calendar_view)
            val minDate: Calendar = Calendar.getInstance()
            val maxDate: Calendar = Calendar.getInstance()

            minDate.add(Calendar.MONTH, -1)
            minDate.set(Calendar.DAY_OF_MONTH, 1)
            maxDate.add(Calendar.MONTH, 6)
            mAgendaCalendarView.init(minDate, maxDate, Locale.UK, this)
            root.findViewById<FloatingActionButton>(R.id.calAddButton).setOnClickListener {
                AddDialog()
            }
            root.findViewById<FloatingActionButton>(R.id.rearrangeButton).setOnClickListener { v: View? ->
                PlanManager.rearrangeTasks()
                saveTasksToMemory(requireContext())
                CalendarManager.getInstance().loadEventsAndTasks()
            }
            initialized = true;
        }
//        mAgendaCalendarView.addEventRenderer(DefaultEventRenderer())

        return root
    }

    private fun mockList(eventList: MutableList<Event>) {
        val timeStart1 = LocalDateTime.now().withHour(2).withMinute(0)
        val timeEnd1 = timeStart1.withHour(3).withMinute(30)
        val event1 =
            Event("id1", "Hurtownie Danych", "laby", timeStart1, timeEnd1, Place("Uczelnia"))
        eventList.add(event1)

        val timeStart2 = LocalDateTime.now().withHour(18).withMinute(0)
        val timeEnd2 = LocalDateTime.now().withHour(20).withMinute(0)
        val event2 = Event(
            "id2",
            "Zlot fanów farszu",
            "cos tam",
            timeStart2,
            timeEnd2,
            Place("stołówka SKS")
        )
        eventList.add(event2)

        val timeStart3 = LocalDateTime.now().withHour(14).withMinute(0)
        val timeEnd3 = LocalDateTime.now().withDayOfMonth(5).withHour(15).withMinute(15)
        val event3 = Event("id3", "Wyjazd do Iraku", "aaa", timeStart3, timeEnd3, Place("Irak"))

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
        if (!PlanManager.memoryUpToDate) {
            saveEventsToMemory(requireContext())
            saveTasksToMemory(requireContext())
            PlanManager.memoryUpToDate = true
        }
        super.onStop()
    }

    override fun onScrollToDate(calendar: Calendar?) {
        if ((requireActivity() as AppCompatActivity).supportActionBar != null) {
            (requireActivity() as AppCompatActivity).supportActionBar!!.title =
                calendar!!.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
        }
    }

    override fun onEventSelected(entry: BaseCalendarEntry) {
        Log.d(LOG_TAG, String.format("Selected event: %s", entry))
        val bundle = Bundle()
        bundle.putInt(DAY_KEY, entry.instanceDay.get(Calendar.DAY_OF_MONTH))
        bundle.putInt(MONTH_KEY, entry.instanceDay.get(Calendar.MONTH) + 1)
        bundle.putInt(YEAR_KEY, entry.instanceDay.get(Calendar.YEAR))
        requireView().findNavController().navigate(R.id.nav_agenda, bundle)
    }

    override fun onResume() {
        super.onResume()
    }

    @ExperimentalTime
    fun AddDialog() {
        AlertDialog.Builder(requireContext()).setTitle("CHOOSE ENTRY TYPE")
            .setPositiveButton("EVENT") { dialog, id -> StartAEActivity() }
            .setNegativeButton("TASK") { dialog, id -> StartATActivity() }
            .create().show()
    }

    fun StartAEActivity() {
        val intent = Intent(requireContext(), AddEventActivity::class.java)
        startActivityForResult(intent, Constants.ADD_EVENT_CODE)
    }

    @ExperimentalTime
    fun StartATActivity() {
        val intent = Intent(requireContext(), AddTaskActivity::class.java)
        startActivityForResult(intent, Constants.ADD_TASK_CODE)
    }

}