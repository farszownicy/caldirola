package farszownicy.caldirola.crud_activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.Logic.PlanManager.mPlaces
import farszownicy.caldirola.R
import farszownicy.caldirola.activities.AddUserFragment
import farszownicy.caldirola.agendacalendar.CalendarManager
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.Place
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.DateTimeUtils
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import farszownicy.caldirola.utils.memory.saveLocationsToMemory
import farszownicy.caldirola.utils.memory.saveTasksToMemory
import kotlinx.android.synthetic.main.activity_add_event.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime


class AddEventActivity : AppCompatActivity()
    ,AdapterView.OnItemSelectedListener
{
    companion object
    {
        const val NAME_KEY = "name"
        const val DESCRIPTION_KEY = "description"
        const val START_DATE_KEY = "start_date"
        const val END_DATE_KEY = "end_date"
        const val LOCATION_KEY = "location"
        const val TAG = "debug"
    }

    private val db = FirebaseFirestore.getInstance()
    private val userDoc = db.collection("events").document("sKNWMetaOLJXTIkXeU0W")
    private val locations = db.collection("locations")
    private var places = mPlaces.map{ el -> el.name}.toMutableList()
    private val datetime_utils = DateTimeUtils()

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)
        if(places.isEmpty()) places.add("Dom")

        ae_add_button.setOnClickListener {
            addEvent(false)
        }

        ae_add_location_btn.setOnClickListener{
            addLocation()
        }

        setSpinner()
        setAllDatePickers()

        userDoc.addSnapshotListener(this
        ) { snapshot, e ->
            if (e != null)
            {
                Log.w(AddUserFragment.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists())
            {
                Log.d(AddUserFragment.TAG, "Current data: ${snapshot.data}")
            }
            else
            {
                Log.d(AddUserFragment.TAG, "Current data: null")
            }
        }
    }

    @ExperimentalTime
    private fun addLocation(){
        val name = ae_location_search.text.toString()
        ae_location_search.setText("")
        if(name != "" && !places.contains(name)) {
            val newPlace = Place(name)
            if(!mPlaces.contains(Place(name))) {
                mPlaces.add(newPlace)
                saveLocationsToMemory(this)
                places.add(newPlace.name)
                ae_location.setSelection(places.size - 1)
            }
        }
    }

    private fun setSpinner(){
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ae_location.onItemSelectedListener = this@AddEventActivity
        ae_location.adapter = adapter
    }

    private fun setAllDatePickers()
    {
        datetime_utils.setDatePicker(ae_start_date, this@AddEventActivity)
        datetime_utils.setTimePicker(ae_start_time,this@AddEventActivity)
        datetime_utils.setDatePicker(ae_end_date,this@AddEventActivity)
        datetime_utils.setTimePicker(ae_end_time,this@AddEventActivity)
        datetime_utils.setDefaultDate(ae_start_date, 0)
        datetime_utils.setDefaultDate(ae_end_date, 1)
        datetime_utils.setDefaultTime(ae_start_time, 0)
        datetime_utils.setDefaultTime(ae_end_time, 1)
    }


    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    fun addEvent(force:Boolean)
    {
        val name = ae_input_name.text.toString()
        val description = ae_input_description.text.toString()

        val dt_start = datetime_utils.getDTFromTV(ae_start_date, ae_start_time)
        val dt_end = datetime_utils.getDTFromTV(ae_end_date, ae_end_time)

        val selected_location = Place(ae_location.selectedItem as String)

        if(name.isEmpty() || description.isEmpty() ||  PlanManager.areEqual(dt_start, dt_end))
            return
        val event_data = hashMapOf(
            NAME_KEY to name,
            DESCRIPTION_KEY to description,
            START_DATE_KEY to dt_start,
            END_DATE_KEY to dt_end,
            LOCATION_KEY to selected_location
        )
        val event = Event(UUID.randomUUID().toString(),name, description, dt_start, dt_end, selected_location)
        val eventIntent = Intent()

        val eventAdded = PlanManager.addEvent(event, force)
        if(eventAdded == null)
            conflictDialog()
        saveTasksToMemory(this)
        saveEventsToMemory(this)
        PlanManager.memoryUpToDate = true
        if(eventAdded == true) {
            db.collection("events").add(event_data).addOnSuccessListener { documentReference ->
                Log.d(TAG, "Event added with ID: ${documentReference.id}")
            }.addOnFailureListener { e -> Log.w(TAG, "Error adding event", e) }
            eventIntent.putExtra(Constants.ADD_EVENT_KEY, eventAdded)
            setResult(Activity.RESULT_OK, eventIntent)
            PlanManager.memoryUpToDate = false
            CalendarManager.getInstance().loadEventsAndTasks()
            finish()
        }
        else{
            Toast.makeText(this, "Eventu ${event.name} nie da sie wcisnac do kalendarza.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //finishActivity(Activity.RESULT_CANCELED)
    }

    @ExperimentalTime
    fun conflictDialog(){
        AlertDialog.Builder(this@AddEventActivity).setTitle("There is a task scheduled at this time. Add anyway and reschedule?")
            .setPositiveButton("YES"){_, _ -> addEvent(true)}
            .setNegativeButton("NO"){_, _ -> }
            .create().show()
    }

    //SPINNER METHODS
    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
    }
}
