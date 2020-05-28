package farszownicy.caldirola.activities.preferences

import android.app.TimePickerDialog
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
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import kotlin.time.ExperimentalTime


class PreferencesListAdapter(val context: Context) : RecyclerView.Adapter<PreferencesListAdapter.ViewHolder>() {
    val timeUtils : DateTimeUtils = DateTimeUtils()

    override fun getItemCount(): Int = PlanManager.mPreferences.size;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.preference_time_card, parent, false)
        return ViewHolder(itemView)
    }

    @ExperimentalTime
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = PlanManager.mPreferences[position]

        val weekdayChoices = context.resources.getStringArray(R.array.WeekdayChoices)

        holder.daySpinner.adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            weekdayChoices
        )

        holder.daySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?, arg1: View,
                arg2: Int, id: Long
            ) {
                currentItem.weekDay = id.toInt()
                Log.d("Pref", "Chosen day ID:$id")
            }
            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        holder.startTimeText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentItem.startTime = LocalTime.parse(s)
            }
        })
        holder.endTimeText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentItem.endTime = LocalTime.parse(s)
            }
        })
        timeUtils.setTimePicker(holder.startTimeText, context)
        timeUtils.setTimePicker(holder.endTimeText, context)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val daySpinner: Spinner = itemView.findViewById(R.id.pref_day_spinner)
        val startTimeText: TextView = itemView.findViewById(R.id.pref_start_time)
        val endTimeText: TextView = itemView.findViewById(R.id.pref_end_time)
    }
}