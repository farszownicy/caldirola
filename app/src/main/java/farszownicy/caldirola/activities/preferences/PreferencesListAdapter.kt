package farszownicy.caldirola.activities.preferences

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.DateTimeUtils
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime


class PreferencesListAdapter(val context: Context) : RecyclerView.Adapter<PreferencesListAdapter.ViewHolder>() {
    val timeUtils : DateTimeUtils = DateTimeUtils()

    override fun getItemCount(): Int = PlanManager.illegalIntervals.size;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.preference_time_card, parent, false)
        return ViewHolder(itemView)
    }

    @ExperimentalTime
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = PlanManager.illegalIntervals[position]

        holder.deleteButton.setOnClickListener {
            PlanManager.illegalIntervals.remove(currentItem)
            notifyItemRemoved(position)
        }

        holder.titleText.setText(currentItem.title)
        holder.titleText.doOnTextChanged { text, _, _, _ ->
            currentItem.title = text.toString()
        }

        val weekdayChoices = context.resources.getStringArray(R.array.WeekdayChoices)

        holder.daySpinner.adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            weekdayChoices
        )

        holder.daySpinner.setSelection(currentItem.dayOfWeek?.value ?: 0)

        holder.daySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View,
                arg2: Int, id: Long
            ) {
                var dayOfWeek:DayOfWeek? = null
                if(id.toInt() != 0) dayOfWeek = DayOfWeek.of(id.toInt())
                currentItem.dayOfWeek = dayOfWeek
                Log.d("Pref", "Chosen day ID:$id")
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        timeUtils.setTime(holder.startTimeText, currentItem.startTime)
        holder.startTimeText.doOnTextChanged { text, _, _, _ ->
            currentItem.startTime = LocalDateTime.parse("21.03.2017 " + text.toString(),  DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT))
        }
        timeUtils.setTime(holder.endTimeText, currentItem.endTime)
        holder.endTimeText.doOnTextChanged { text, _, _, _ ->
            currentItem.endTime = LocalDateTime.parse("21.03.2017 " + text.toString(),  DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT))
        }

        timeUtils.setTimePicker(holder.startTimeText, context)
        timeUtils.setTimePicker(holder.endTimeText, context)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: EditText = itemView.findViewById(R.id.pref_card_title)
        val daySpinner: Spinner = itemView.findViewById(R.id.pref_card_day_spinner)
        val startTimeText: TextView = itemView.findViewById(R.id.pref_card_start_time)
        val endTimeText: TextView = itemView.findViewById(R.id.pref_card_end_time)
        val deleteButton: ImageButton = itemView.findViewById(R.id.pref_card_del_button)
    }
}