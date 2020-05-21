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
import farszownicy.caldirola.R
import farszownicy.caldirola.activities.MainActivity
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.Place
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.DateTimeUtils
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import kotlinx.android.synthetic.main.activity_edit_event.*
import java.util.*
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
    private val places = ArrayList<String>()
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
        locations.get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val name = document.getString(NAME_KEY)
                    places.add(name!!)
                    println("PLACE" + name!!)
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                ee_location.onItemSelectedListener = this@EditEventActivity
                ee_location.adapter = adapter
                setDefSpinner()
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun setDefSpinner()
    {
        if(editedEvent!!.Location != null) {
            System.out.println("WYBOR LOKALIZACJI)" + editedEvent!!.Location!!.name)
            val curr = places.indexOfFirst { e -> e.equals(editedEvent!!.Location!!.name) }
            ee_location.setSelection(curr)
        }
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