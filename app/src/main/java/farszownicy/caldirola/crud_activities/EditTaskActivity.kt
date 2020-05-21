package farszownicy.caldirola.crud_activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.agendacalendar.CalendarManager
import farszownicy.caldirola.models.data_classes.Place
import farszownicy.caldirola.models.data_classes.Task
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.DateTimeUtils
import farszownicy.caldirola.utils.memory.saveTasksToMemory
import kotlinx.android.synthetic.main.activity_edit_task.*
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

class EditTaskActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    companion object
    {
        const val NAME_KEY = "name"
        const val TAG = "debug"
    }
    private val db = FirebaseFirestore.getInstance()
    private val locations = db.collection("locations")
    private val places = ArrayList<String>()
    private var taskPlaces = ArrayList<Place>()
    private val datetime_utils = DateTimeUtils()
    private var editedTask: Task? = null
    private var editedIndex:String? = null
    private var adapter:LocationAdapter? = null
    private val simpleCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
            return true
        }

        override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
            val position = p0.adapterPosition
            adapter!!.removeLocation(position)
        }
    }

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        et_edit_button.setOnClickListener{
            editTask()
        }
        val taskID = intent.getStringExtra("ID")
        editedTask = PlanManager.getTask(taskID)
        editedIndex = taskID
        taskPlaces = ArrayList(editedTask!!.places)
        et_add_location_btn.setOnClickListener{
            addLocation()
        }
        fillBoxes()
    }

    private fun addLocation(){
        val name = et_location_search.text.toString()
        et_location_search.setText("")
        if(name != "" && !adapter!!.getItems().contains(Place(name))) {
            val newPlace = Place(name)
            adapter!!.addItem(newPlace)
            taskPlaces = adapter!!.getItems()
        }
    }

    private fun setLocationsList()
    {
        recycler_view.adapter = LocationAdapter(taskPlaces)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        adapter = recycler_view.adapter as LocationAdapter
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)

        val arrAdapter = ArrayAdapter<String>(this@EditTaskActivity, android.R.layout.simple_list_item_1, places)
        et_location_search.setAdapter(arrAdapter)
    }

    private fun setSlices()
    {
        if(et_divisible.isChecked){
            et_input_slice_size_hr.isEnabled = true
            et_input_slice_size_min.isEnabled = true
        }
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
        et_divisible.isChecked = editedTask!!.divisible
        //setPreviousDuration()
        setWheelPickers()
        if(et_divisible.isChecked) setPreviousSlices()
        setSpinner()
        setDatePickers()
        setSlices()
        validateMinutes()
        setLocationsList()
    }

    @ExperimentalTime
    private fun setPreviousDuration()
    {
        val prevDur = editedTask!!.duration
        val hrs = prevDur.inHours.toInt()
        val mins = prevDur.inMinutes.toInt() / 60
        //et_input_time.setText("$hrs".padStart(2, '0') + ":" + "$mins".padStart(2, '0'))
        //et_input_time.setText("$hrs:$mins")
    }

    @ExperimentalTime
    private fun setWheelPickers()
    {
        val prevDur = editedTask!!.duration
        val hrs = prevDur.inHours.toInt()
        val mins = prevDur.inMinutes.toInt() % 60

        et_hour_picker.minValue=0
        et_hour_picker.maxValue=999
        et_hour_picker.value = hrs
        et_hour_picker.wrapSelectorWheel=true

        et_minute_picker.minValue=0
        et_minute_picker.maxValue=59
        et_minute_picker.value = mins
        et_minute_picker.wrapSelectorWheel=true
    }


    private fun setPreviousSlices()
    {
        val prevSlices = editedTask!!.minSliceSize
        val hrs = prevSlices / 60
        val mins = prevSlices % 60
        et_input_slice_size_hr.setText("$hrs".padStart(2, '0'))
        et_input_slice_size_min.setText("$mins")
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
                setDefSpinner()
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        val priorities = resources.getStringArray(R.array.Priorities)
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        et_priority.onItemSelectedListener = this@EditTaskActivity
        et_priority.adapter = priorityAdapter
    }

    private fun setDefSpinner()
    {
        if(!editedTask!!.places.isEmpty()) {
            val curr = places.indexOfFirst { e -> e.equals(editedTask!!.places.first().name) }
            //et_location.setSelection(curr)
        }
        val priorities = resources.getStringArray(R.array.Priorities)
        val curr_priority = priorities.indexOfFirst { pr -> pr.equals(editedTask!!.priority) }
        et_priority.setSelection(curr_priority)
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
        //val locations = listOf(Place(et_location.selectedItem as String))
        val locations = adapter!!.getItems()
        val priority = et_priority.toString()
        val minSlice = getSliceTimeInMinutes()
        val divisible = et_divisible.isChecked
        val duration =  et_hour_picker.value * 60 + et_minute_picker.value
        val taskUpdated = PlanManager.updateTask(editedTask!!, name, description, deadline, locations, priority, divisible, minSlice, duration.minutes)
        saveTasksToMemory(this)
        PlanManager.memoryUpToDate = true
        val taskIntent = Intent()
        taskIntent.putExtra(Constants.EDIT_TASK_KEY, true)
        setResult(Activity.RESULT_OK, taskIntent)
        PlanManager.memoryUpToDate = false
        CalendarManager.getInstance(applicationContext).loadEventsAndTasks()
        finish()
        //val taskIntent = Intent()
    }

}