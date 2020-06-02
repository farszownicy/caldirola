package farszownicy.caldirola.crud_activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.Logic.PlanManager.mPlaces
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.Place
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.DateTimeUtils
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import farszownicy.caldirola.utils.memory.saveLocationsToMemory
import kotlinx.android.synthetic.main.activity_edit_event.*
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime

class EditEventActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object
    {
        const val NAME_KEY = "name"
        const val TAG = "debug"
    }

    private val db = FirebaseFirestore.getInstance()
    private val locations = db.collection("locations")
    private var places = mPlaces.map{ el -> el.name}.toMutableList()
    private val datetime_utils = DateTimeUtils()
    private var editedEvent:Event? = null
    private var editedIndex:String? = null
    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)
        ee_add_button.setOnClickListener {
            editEvent()
        }
        ee_remove_btn.setOnClickListener{
            deleteEvent()
        }
        ee_add_location_btn.setOnClickListener{
            addLocation()
        }
        val eventID = intent.getStringExtra("ID")
        editedEvent = PlanManager.getEvent(eventID)
        editedIndex = eventID
        fillBoxes()
    }

    private fun fillBoxes()
    {
        ee_input_name.setText(editedEvent!!.name)
        ee_input_description.setText(editedEvent!!.description)
        setAllDatePickers()
        setSpinner()
    }

    private fun setSpinner(){
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ee_location.onItemSelectedListener = this@EditEventActivity
        ee_location.adapter = adapter
        setDefSpinner()
    }

    private fun setDefSpinner()
    {
        if(editedEvent!!.Location != null) {
            System.out.println("WYBOR LOKALIZACJI)" + editedEvent!!.Location!!.name)
            val curr = places.indexOfFirst { e -> e.equals(editedEvent!!.Location!!.name) }
            ee_location.setSelection(curr)
        }
    }

    @ExperimentalTime
    private fun addLocation(){
        val name = ee_location_search.text.toString()
        ee_location_search.setText("")
        if(name != "" && !places.contains(name)) {
            val newPlace = Place(name)
            if(!mPlaces.contains(Place(name))) {
                mPlaces.add(newPlace)
                saveLocationsToMemory(this)
                places.add(newPlace.name)
                ee_location.setSelection(places.size - 1)
            }
        }
    }

    @ExperimentalTime
    private fun deleteEvent()
    {
        PlanManager.removeEvent(editedEvent!!)
        val eventIntent = Intent()
        eventIntent.putExtra(Constants.REMOVE_EVENT_KEY, true)
        setResult(Activity.RESULT_OK, eventIntent)
        saveEventsToMemory(this)
        finish()
    }

    private fun setAllDatePickers()
    {
        datetime_utils.setDate(ee_start_date, editedEvent!!.startTime)
        datetime_utils.setTime(ee_start_time, editedEvent!!.startTime)
        datetime_utils.setDate(ee_end_date, editedEvent!!.endTime)
        datetime_utils.setTime(ee_end_time, editedEvent!!.endTime)
        datetime_utils.setDatePicker(ee_start_date, this@EditEventActivity)
        datetime_utils.setTimePicker(ee_start_time,this@EditEventActivity)
        datetime_utils.setDatePicker(ee_end_date,this@EditEventActivity)
        datetime_utils.setTimePicker(ee_end_time,this@EditEventActivity)
    }

    @ExperimentalTime
    private fun editEvent(){
        val name = ee_input_name.text.toString()
        val description = ee_input_description.text.toString()

        val dt_start = datetime_utils.getDTFromTV(ee_start_date, ee_start_time)
        val dt_end = datetime_utils.getDTFromTV(ee_end_date, ee_end_time)

        val selected_location = Place(ee_location.selectedItem as String)
        val eventUpdated = PlanManager.updateEvent(editedEvent!!, name, description, dt_start, dt_end, selected_location)
        val eventIntent = Intent()

        if(eventUpdated)
        {
            eventIntent.putExtra(Constants.EDIT_EVENT_KEY, eventUpdated)
            setResult(Activity.RESULT_OK, eventIntent)
            saveEventsToMemory(this)
            finish()
        }
        else{
            Toast.makeText(this, "Eventu ${editedEvent!!.name} nie da sie wcisnac do kalendarza.", Toast.LENGTH_SHORT).show()
        }
    }

}