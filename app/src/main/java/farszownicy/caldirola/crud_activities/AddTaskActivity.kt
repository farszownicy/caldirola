package farszownicy.caldirola.crud_activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.activities.MainActivity
import farszownicy.caldirola.agendacalendar.CalendarManager
import farszownicy.caldirola.models.data_classes.Place
import farszownicy.caldirola.models.data_classes.Task
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.DateTimeUtils
import kotlinx.android.synthetic.main.activity_add_task.*
import java.lang.Exception
import java.sql.Time
import java.time.LocalTime
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

class AddTaskActivity : AppCompatActivity(),
    AdapterView.OnItemSelectedListener{
    companion object
    {
        const val NAME_KEY = "name"
        const val DESCRIPTION_KEY = "description"
        const val DEADLINE_KEY = "deadline"
        const val LOCATION_KEY = "location"
        const val TASKS_KEY = "tasks"
        const val DIVISIBILITY_KEY = "divisibility"
        const val PRIORITY_KEY = "priority"
        const val DURATION_KEY = "duration"
        const val SLICE_KEY = "slice"
        const val TAG = "debug"
    }
    private val db = FirebaseFirestore.getInstance()
    private val userDoc = db.collection("tasks").document("D3KEydQ5IWiBzZtuQegV")
    private val locations = db.collection("locations")
    private val places = ArrayList<String>()
    private val calendarUtils = DateTimeUtils()

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        at_add_button.setOnClickListener {
            addTask()
        }

        setSpinner()
        setAllDatePickers()
        setSlices()
        validateMinutes()

        userDoc.addSnapshotListener(this
        ) { snapshot, e ->
            if (e != null)
            {
                Log.w(MainActivity.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists())
            {
                Log.d(MainActivity.TAG, "Current data: ${snapshot.data}")
            }
            else
            {
                Log.d(MainActivity.TAG, "Current data: null")
            }
        }
    }

    private fun setSlices()
    {
        at_divisible.setOnCheckedChangeListener {
                _, isChecked ->
            at_input_slice_size_hr.isEnabled = isChecked
            at_input_slice_size_min.isEnabled = isChecked
        }
    }

    private fun validateMinutes()
    {
        at_input_slice_size_min.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {if(p0.toString() != "") if (Integer.parseInt(p0.toString()) > 59) p0!!.clear()}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun setSpinner() {
        locations.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val name = document.getString(NAME_KEY)
                    places.add(name!!)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                at_location.onItemSelectedListener = this@AddTaskActivity
                at_location.adapter = adapter
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
    private fun setAllDatePickers()
    {
        calendarUtils.setDatePicker(at_deadline_date, this@AddTaskActivity)
        calendarUtils.setTimePicker(at_deadline_time, this@AddTaskActivity)
        calendarUtils.setDefaultDate(at_deadline_date, 0)
        calendarUtils.setDefaultTime(at_deadline_time)
    }

    private fun getSliceTimeInMinutes(): Int
    {
        val slice_hrs = at_input_slice_size_hr.text.toString()
        val slice_min = at_input_slice_size_min.text.toString()
        var mins = 0
        if(slice_min != "") mins += Integer.parseInt(slice_min)
        if(slice_hrs != "") mins += Constants.MINUTES_IN_HOUR * Integer.parseInt(slice_hrs)
        return mins
    }

    //TODO: event->task
    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    fun addTask()
    {
        val name = at_input_name.text.toString()
        val description = at_input_description.text.toString()
        val deadline = calendarUtils.getDTFromTV(at_deadline_date, at_deadline_time)
        val selected_location = Place(at_location.selectedItem as String)
        val divisible = at_divisible.isChecked
        var priority = at_input_priority.text.toString()
        val minSlice = getSliceTimeInMinutes()
        val duration: Int
        val time: LocalTime
        try {
            time = LocalTime.parse(at_input_time.text.toString())
        }
        catch(e: Exception){
            Toast.makeText(this, "Podano czas trwania w niepoprawnym formacie. Wymagany format: hh:mm", Toast.LENGTH_SHORT).show()
            return
        }
        duration =  time.hour * 60 + time.minute

        if(name.isEmpty() || description.isEmpty() || duration<= 0)
            return
        val task_data = hashMapOf(
            NAME_KEY to name,
            DESCRIPTION_KEY to description,
            DEADLINE_KEY to deadline,
            LOCATION_KEY to selected_location,
            DIVISIBILITY_KEY to divisible,
            PRIORITY_KEY to priority,
            SLICE_KEY to minSlice,
            DURATION_KEY to duration
        )
        if(priority == "")
            priority = "-1"
        val task = Task(UUID.randomUUID().toString(), name, description,
            deadline, duration.minutes, priority= priority.toInt(),divisible=divisible,
            minSliceSize =  minSlice, places = listOf(selected_location)
        )
        val taskIntent = Intent()

        val taskAdded = PlanManager.addTask(task)
        if(taskAdded) {
            db.collection("tasks").add(task_data).addOnSuccessListener { documentReference ->
                Log.d(TAG, "Task added with ID: ${documentReference.id}")
            }.addOnFailureListener { e -> Log.w(TAG, "Error adding task", e) }
            taskIntent.putExtra(Constants.ADD_TASK_KEY, taskAdded)
            setResult(Activity.RESULT_OK, taskIntent)
            PlanManager.memoryUpToDate = false
            CalendarManager.getInstance(applicationContext).loadEventsAndTasks()
            finish()
        }
        else{
            Toast.makeText(this, "Taska ${task.name} nie da sie wcisnac do kalendarza.", Toast.LENGTH_SHORT).show()
        }
    }

    //SPINNER METHODS
    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }
}