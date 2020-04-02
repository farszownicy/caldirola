package farszownicy.caldirola.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.R
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.utils.CalendarUtils
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.readObjectsFromSharedPreferences
import farszownicy.caldirola.utils.writeObjectToSharedPreferences
import kotlinx.android.synthetic.main.activity_add_event.*
import java.text.SimpleDateFormat
import java.util.*


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
    private val calendar_utils = CalendarUtils()

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
        calendar_utils.setDatePicker(ae_start_date, this@AddEventActivity)
        calendar_utils.setTimePicker(ae_start_time,this@AddEventActivity)
        calendar_utils.setDatePicker(ae_end_date,this@AddEventActivity)
        calendar_utils.setTimePicker(ae_end_time,this@AddEventActivity)
        calendar_utils.setDefaultDate(ae_start_date, 0)
        calendar_utils.setDefaultDate(ae_end_date, 1)
        calendar_utils.setDefaultTime(ae_start_time)
        calendar_utils.setDefaultTime(ae_end_time)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun addEvent()
    {
        val name = ae_input_name.text.toString()
        val description = ae_input_description.text.toString()

        val cal_start = calendar_utils.getCalFromTV(ae_start_date, ae_start_time)
        val cal_end = calendar_utils.getCalFromTV(ae_end_date, ae_end_time)

        val selected_location = places

        if(name.isEmpty() || description.isEmpty())
            return
        val event_data = hashMapOf(
            NAME_KEY to name,
            DESCRIPTION_KEY to description,
            START_DATE_KEY to cal_start.time,
            END_DATE_KEY to cal_end.time,
            LOCATION_KEY to selected_location
        )
        db.collection("events").add(event_data).addOnSuccessListener {
            documentReference -> Log.d(TAG, "Event added with ID: ${documentReference.id}")
        }.addOnFailureListener{
            e -> Log.w(TAG, "Error adding event", e)}

        saveEventToInternalMemory(Event(name, description, startTime = cal_start, endTime = cal_end))
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
