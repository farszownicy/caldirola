package farszownicy.caldirola.activities

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.AgendaDrawableEntry
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.TaskSlice
import farszownicy.caldirola.utils.memory.loadEventsFromMemory
import farszownicy.caldirola.utils.memory.loadTasksFromMemory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit.MINUTES
import kotlin.time.ExperimentalTime


class StartupFragment : Fragment() {

    @ExperimentalTime
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loadTasksFromMemory(requireContext())
        loadEventsFromMemory(requireContext())

        return inflater.inflate(R.layout.startup_fragment, container, false)
    }

    @ExperimentalTime
    override fun onResume() {
        super.onResume()

        val pieChart: PieChart = requireView().findViewById(R.id.pieChart)

        val visitors: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()

        val invisibleColor = Color.rgb(250, 250, 250) // lol

        val now = LocalDateTime.now()
        val entries = getSortedEntries()
        var currentTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT)
        var currentEntry: AgendaDrawableEntry? = null
        entries.forEachIndexed { i, entry ->
            val slackTime = currentTime.until(entry.startTime, MINUTES)
            val entryTime = entry.startTime.until(entry.endTime, MINUTES)
            if(slackTime > 0L) {
                visitors.add(PieEntry(slackTime.toFloat(), ""))
                colors.add(Color.rgb(240, 240, 240))
            }
            if(entryTime > 0L) {
                visitors.add(PieEntry(entryTime.toFloat(), ""))
                colors.add(ColorTemplate.MATERIAL_COLORS[i])
            }
            if(now.isAfter(entry.startTime) && now.isBefore(entry.endTime)) {
                currentEntry = entry
            }
            currentTime = entry.endTime
        }
        val nextMidnight = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIDNIGHT)
        val finalSlackTime = currentTime.until(nextMidnight, MINUTES)
        if(finalSlackTime > 0L) {
            visitors.add(PieEntry(finalSlackTime.toFloat(), ""))
            colors.add(Color.rgb(240, 240, 240))
        }

        val textView: TextView = requireView().findViewById(R.id.textView5)
        textView.text = if(currentEntry != null) {
            if(currentEntry is TaskSlice) {
                (currentEntry as TaskSlice).parent.name
            } else {
                (currentEntry as Event).name
            }
        } else {
            ""
        }

        val pieDataSet = PieDataSet(visitors, "")
        pieDataSet.setColors(
            convertIntegers(colors), 255
        )

        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 0f

        val pieData = PieData(pieDataSet)

        pieChart.setDrawRoundedSlices(true)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.holeRadius = 75f
        pieChart.setHoleColor(invisibleColor)

        pieChart.legend.isEnabled = false
        pieChart.isEnabled = true
        pieChart.invalidate()
        pieChart.setTouchEnabled(true)
    }

    @ExperimentalTime
    private fun getSortedEntries(): List<AgendaDrawableEntry> {
        val events = PlanManager.getEventsByDate(LocalDateTime.now())
        val tasks = PlanManager.getTaskSlicesByDate(LocalDateTime.now())
        val entries: List<AgendaDrawableEntry> = events + tasks
        return entries.sortedBy { x -> x.startTime }
    }

    private fun convertIntegers(integers: List<Int>): IntArray? {
        val ret = IntArray(integers.size)
        for (i in ret.indices) {
            ret[i] = integers[i]
        }
        return ret
    }
}
