package farszownicy.caldirola.activities.preferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.IllegalInterval
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.memory.savePreferencesToMemory
import kotlinx.android.synthetic.main.activity_preferences.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PreferencesActivity : AppCompatActivity() {

    private val preferencesAdapter = PreferencesListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        pref_recycler_view.adapter = preferencesAdapter
        pref_recycler_view.layoutManager = LinearLayoutManager(this)

        pref_add_button.setOnClickListener {
            val now = LocalDateTime.now()
            PlanManager.illegalIntervals.add(IllegalInterval(null, now, now, "Untitled"))
            preferencesAdapter.notifyItemInserted(PlanManager.illegalIntervals.size-1)
        }

        setupInputs()
    }

    private fun setupInputs(){
        pref_min_time_input.setText(PlanManager.minutesBetweenTasks.toString())
        pref_min_time_input.doOnTextChanged { text, _, _, _ ->
            if(text.isNullOrEmpty()) PlanManager.minutesBetweenTasks = 0
            else PlanManager.minutesBetweenTasks = text.toString().toInt()}
        pref_task_amount_input.setText(PlanManager.maxTasksPerDay.toString())
        pref_task_amount_input.doOnTextChanged { text, _, _, _  ->
            if(text.isNullOrEmpty()) PlanManager.maxTasksPerDay = 0
            else PlanManager.maxTasksPerDay = text.toString().toInt()
        }
    }

    override fun onStop() {
        super.onStop()
        savePreferencesToMemory(this)
    }
}
