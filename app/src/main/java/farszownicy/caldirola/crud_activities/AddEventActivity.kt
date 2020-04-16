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
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.activities.MainActivity
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.DateTimeUtils
import farszownicy.caldirola.utils.readObjectsFromSharedPreferences
import farszownicy.caldirola.utils.writeObjectToSharedPreferences
import kotlinx.android.synthetic.main.activity_add_event.*
import java.util.*
import kotlin.time.ExperimentalTime


class AddEventActivity : AppCompatActivity(),
    AdapterView.OnItemSelectedListener {
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
    private val places = ArrayList<String>()
    private val datetime_utils = DateTimeUtils()

    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        ae_add_button.setOnClickListener {
            addEvent()
        }

        locations.get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val name = document.getString(NAME_KEY)
                    places.add(name!!)
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        //TODO NAPRAWIC SPINNER
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, places)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ae_location.onItemSelectedListener = this@AddEventActivity
        ae_location.adapter = adapter

        setAllDatePickers()

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

    private fun setAllDatePickers()
    {
        datetime_utils.setDatePicker(ae_start_date, this@AddEventActivity)
        datetime_utils.setTimePicker(ae_start_time,this@AddEventActivity)
        datetime_utils.setDatePicker(ae_end_date,this@AddEventActivity)
        datetime_utils.setTimePicker(ae_end_time,this@AddEventActivity)
        datetime_utils.setDefaultDate(ae_start_date, 0)
        datetime_utils.setDefaultDate(ae_end_date, 1)
        datetime_utils.setDefaultTime(ae_start_time)
        datetime_utils.setDefaultTime(ae_end_time)
    }


    @ExperimentalTime
    @RequiresApi(Build.VERSION_CODES.O)
    fun addEvent()
    {
        val name = ae_input_name.text.toString()
        val description = ae_input_description.text.toString()

        val dt_start = datetime_utils.getDTFromTV(ae_start_date, ae_start_time)
        val dt_end = datetime_utils.getDTFromTV(ae_end_date, ae_end_time)

        val selected_location = places

        if(name.isEmpty() || description.isEmpty() ||  PlanManager.areEqual(dt_start, dt_end))
            return
        val event_data = hashMapOf(
            NAME_KEY to name,
            DESCRIPTION_KEY to description,
            START_DATE_KEY to dt_start,
            END_DATE_KEY to dt_end,
            LOCATION_KEY to selected_location
        )
        val event = Event(UUID.randomUUID().toString(),name, description, startTime = dt_start, endTime = dt_end)
        val eventIntent = Intent()

        val eventAdded = PlanManager.addEvent(event)
        if(eventAdded) {
            db.collection("events").add(event_data).addOnSuccessListener { documentReference ->
                Log.d(TAG, "Event added with ID: ${documentReference.id}")
            }.addOnFailureListener { e -> Log.w(TAG, "Error adding event", e) }
            saveEventToInternalMemory(event)
            eventIntent.putExtra(Constants.ADD_EVENT_KEY, eventAdded)
            setResult(Activity.RESULT_OK, eventIntent)
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

    private fun saveEventToInternalMemory(event: Event){
        var eventList = readObjectsFromSharedPreferences<ArrayList<Event>>(
            applicationContext,
            Constants.SHARED_PREF_CALENDAR_FILE_NAME,
            Constants.SHARED_PREF_EVENTS_LIST_KEY
        )
        if(eventList == null) {
            eventList = ArrayList()
        }
        eventList.add(event)

        writeObjectToSharedPreferences(applicationContext,
            Constants.SHARED_PREF_CALENDAR_FILE_NAME,
            Constants.SHARED_PREF_EVENTS_LIST_KEY,
            eventList
        )
    }

    //SPINNER METHODS
    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Toast.makeText(applicationContext, "Test", Toast.LENGTH_SHORT).show()
        val t = p0!!.getItemAtPosition(p2).toString()
    }
}
