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
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.activities.MainActivity
import farszownicy.caldirola.data_classes.Place
import farszownicy.caldirola.data_classes.Task
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.DateTimeUtils
import kotlinx.android.synthetic.main.activity_edit_task.*
import java.lang.Exception
import java.sql.Time
import java.time.LocalTime
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.minutes
import androidx.appcompat.app.AppCompatActivity

class EditTaskActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
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
    private val locations = db.collection("locations")
    private val places = ArrayList<String>()
    private val calendarUtils = DateTimeUtils()
    private val datetime_utils = DateTimeUtils()
    private var editedTask: Task? = null
    private var editedIndex:String? = null

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        et_edit_button.setOnClickListener{
            editTask()
        }
        //val taskID = intent.getStringExtra("ID")
        val taskID = "aabbcc"
        editedTask = PlanManager.getTask(taskID)
        editedIndex = taskID
        fillBoxes()
    }

    private fun setSlices()
    {
        et_divisible.setOnCheckedChangeListener {
                _, isChecked ->
            et_input_slice_size_hr.isEnabled = isChecked
            et_input_slice_size_min.isEnabled = isChecked
        }
    }

    @ExperimentalTime
    private fun fillBoxes()
    {
        et_input_name.setText(editedTask!!.name)
        et_input_description.setText(editedTask!!.description)
        et_input_priority.setText(editedTask!!.priority)
        et_divisible.isChecked = editedTask!!.divisible
        setPreviousDuration()
        setPreviousSlices()
        setSpinner()
        setDatePickers()
        setSlices()
        validateMinutes()
    }

    @ExperimentalTime
    private fun setPreviousDuration()
    {
        val prevDur = editedTask!!.duration
        val hrs = prevDur.inHours.toInt()
        val mins = prevDur.inMinutes.toInt() / 60
        et_input_time.setText("$hrs:$mins")
    }

    private fun setPreviousSlices()
    {
        val prevSlices = editedTask!!.minSliceSize
        val hrs = prevSlices / 60
        val mins = prevSlices % 60
        et_input_slice_size_hr.setText("$hrs".padStart(2, '0'))
        et_input_slice_size_min.setText(mins)
    }

    private fun getSliceTimeInMinutes(): Int
    {
        val slice_hrs = et_input_slice_size_hr.text.toString()
        val slice_min = et_input_slice_size_min.text.toString()
        var mins = 0
        if(slice_min != "") mins += Integer.parseInt(slice_min)
        if(slice_hrs != "") mins += Constants.MINUTES_IN_HOUR * Integer.parseInt(slice_hrs)
        return mins
    }

    private fun validateMinutes()
    {
        et_input_slice_size_min.addTextChangedListener(object : TextWatcher {
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
                et_location.onItemSelectedListener = this@EditTaskActivity
                et_location.adapter = adapter
                setDefSpinner()
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun setDefSpinner()
    {
        println(places.size)
        if(!editedTask!!.places.isEmpty()) {
            val curr = places.indexOfFirst { e -> e.equals(editedTask!!.places.first().name) }
            et_location.setSelection(curr)
        }
    }

    private fun setDatePickers()
    {
        datetime_utils.setDate(et_deadline_date, editedTask!!.deadline)
        datetime_utils.setTime(et_deadline_time, editedTask!!.deadline)
        datetime_utils.setDatePicker(et_deadline_date, this@EditTaskActivity)
        datetime_utils.setTimePicker(et_deadline_time,this@EditTaskActivity)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}

    @ExperimentalTime
    private fun editTask()
    {
        val name = et_input_name.text.toString()
        val description = et_input_description.text.toString()
        val deadline = datetime_utils.getDTFromTV(et_deadline_date, et_deadline_time)
        val locations = listOf(Place(et_location.selectedItem as String))
        val priority = et_input_priority.text.toString().toInt()
        val minSlice = getSliceTimeInMinutes()
        val divisible = et_divisible.isChecked

        //TODO Ja bym zrobil tak, tez w samym dodawaniu, zeby task sie dodal nawet jesli sie go nie da aktualnie wcisnac, tak samo z edycja
        val taskUpdated = PlanManager.updateTask(editedTask!!, name, description, deadline, locations, priority, divisible, minSlice)
        //val taskIntent = Intent()
    }

}