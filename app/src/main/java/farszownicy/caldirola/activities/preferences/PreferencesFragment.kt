package farszownicy.caldirola.activities.preferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.IllegalInterval
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.memory.savePreferencesToMemory
import kotlinx.android.synthetic.main.activity_preferences.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PreferencesFragment : Fragment() {

    //private val preferencesAdapter = PreferencesListAdapter(this)
    private lateinit var root: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //super.onCreate(savedInstanceState)
        root = inflater.inflate(R.layout.activity_preferences, container, false)
        val prefRV: RecyclerView = root.findViewById(R.id.pref_recycler_view)
        val preferencesAdapter = PreferencesListAdapter(requireContext())
        prefRV.adapter = preferencesAdapter
        prefRV.layoutManager = LinearLayoutManager(requireContext())

        val prefAddBtn: Button = root.findViewById(R.id.pref_add_button)
        prefAddBtn.setOnClickListener {
            val now = LocalDateTime.now()
            PlanManager.illegalIntervals.add(IllegalInterval(null, now, now, "Untitled"))
            preferencesAdapter.notifyItemInserted(PlanManager.illegalIntervals.size-1)
        }

        setupInputs()
        return root
    }

    private fun setupInputs(){
        val prefMinTimeInput:EditText = root.findViewById(R.id.pref_min_time_input)
        prefMinTimeInput.setText(PlanManager.minutesBetweenTasks.toString())
        prefMinTimeInput.doOnTextChanged { text, _, _, _ ->
            if(text.isNullOrEmpty()) PlanManager.minutesBetweenTasks = 0
            else PlanManager.minutesBetweenTasks = text.toString().toInt()}

        val prefTaskAmountInput:EditText = root.findViewById(R.id.pref_task_amount_input)
        prefTaskAmountInput.setText(PlanManager.maxTaskHoursPerDay.toString())
        prefTaskAmountInput.doOnTextChanged { text, _, _, _  ->
            if(text.isNullOrEmpty()) PlanManager.maxTaskHoursPerDay = 24
            else PlanManager.maxTaskHoursPerDay = text.toString().toInt()
        }
    }

    override fun onStop() {
        super.onStop()
        savePreferencesToMemory(requireContext())
    }
}
