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
import com.github.mikephil.charting.utils.ColorTemplate.rgb
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.AgendaDrawableEntry
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.TaskSlice
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime


class StartupFragment : Fragment() {

    @ExperimentalTime
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        loadTasksFromMemory(requireContext())
//        loadEventsFromMemory(requireContext())

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
            val endTime =
            if(entry.startTime.dayOfMonth != entry.endTime.dayOfMonth)
                entry.startTime.withHour(23).withMinute(59)
            else
                entry.endTime
            val entryTime = entry.startTime.until(endTime, MINUTES)
            if(slackTime > 0L) {
                visitors.add(PieEntry(slackTime.toFloat(), ""))
                colors.add(Color.rgb(240, 240, 240))
            }
            if(entryTime > 0L) {
                if(entry is Event) {
                    visitors.add(
                        PieEntry(entryTime.toFloat(), abbreviateMiddle(entry.name, "...",
                            (entryTime.toFloat()/7).toInt()
                        )))
//                            entry.name.substring(
//                                0, entry.name.length.coerceAtMost(16))))
                    colors.add(rgb("#3498db"))
                }
                else if(entry is TaskSlice) {
                    visitors.add(
                            PieEntry(entryTime.toFloat(), abbreviateMiddle(entry.parent.name, "...", 16)))
                            //entry.parent.name.substring(0, entry.parent.name.length.coerceAtMost(16))))
                    when(entry.parent.priority.toLowerCase()){
                        "low" -> colors.add(rgb("#2ecc71"))
                        "medium" ->colors.add(rgb("#f1c40f"))
                        "high" -> colors.add(rgb("#fc9003"))
                        "urgent" -> colors.add(rgb("#e74c3c"))
                        }
                }
                //colors.add(ColorTemplate.MATERIAL_COLORS[i % ColorTemplate.MATERIAL_COLORS.size])
            }
            visitors.add(PieEntry(3f, ""))
            colors.add(rgb("#000000"))
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
            "In progress: " +
            if(currentEntry is TaskSlice) {
                abbreviateMiddle((currentEntry as TaskSlice).parent.name, "...", 20)
            } else {
                abbreviateMiddle((currentEntry as Event).name, "...", 20)
            }
        } else {
            val nextEntry =
                entries.filter{e -> e.startTime >= LocalDateTime.now()}.minBy {e -> LocalDateTime.now().until(e.startTime, MINUTES)  }
            if(nextEntry != null) {
                "In ${LocalDateTime.now().until(nextEntry.startTime, MINUTES)} minutes: " +
                if (nextEntry is TaskSlice) {
                    abbreviateMiddle(nextEntry.parent.name, "...", 20)
                } else {
                    abbreviateMiddle((nextEntry as Event).name, "...", 20)
                }
            }
            else
                ""
        }

        val pieDataSet = PieDataSet(visitors, "")
        pieDataSet.setColors(
            convertIntegers(colors), 255
        )

        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 0f
        pieDataSet.formLineWidth = 20f

        val pieData = PieData(pieDataSet)

        pieChart.setDrawRoundedSlices(true)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.holeRadius = 60f
        pieChart.setHoleColor(invisibleColor)
        pieChart.setDrawEntryLabels(true);
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.legend.isEnabled = false
        pieChart.isEnabled = true
        pieChart.invalidate()
        pieChart.setTouchEnabled(true)
        var busyMinutes = 0L
        for(e in entries){
            val endTime =
                if(e.startTime.dayOfMonth != e.endTime.dayOfMonth)
                    e.startTime.withHour(23).withMinute(59)
                else
                    e.endTime
            busyMinutes += PlanManager.differenceInMinutes(e.startTime, endTime)
        }
        pieChart.centerText = "${busyMinutes/60}h ${busyMinutes%60}min" +
                "\n ${LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.UK)}"
        pieChart.setCenterTextSize(26f)
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

    fun abbreviateMiddle(
        input: String?,
        middle: String,
        targetLength: Int
    ): String? {
        if (input == null || input.length <= targetLength) {
            return input
        }
        val inputLength = input.length
        val halfTargetLength = (targetLength - middle.length) / 2
        val startLastSpace = input.substring(0, halfTargetLength)//.lastIndexOf(" ")
        val endFirstSpace = input.substring(input.length - halfTargetLength, input.length)
        //return if (startLastSpace != -1 || endFirstSpace != -1) {
//            (input.substring(0, startLastSpace)
//                    + middle
//                    + input.substring(endFirstSpace + 1, inputLength))
        return startLastSpace + middle + endFirstSpace
    }

}
