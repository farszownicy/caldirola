package farszownicy.caldirola.crud_activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.R
import farszownicy.caldirola.activities.MainActivity
import farszownicy.caldirola.utils.DateTimeUtils
import kotlinx.android.synthetic.main.activity_add_task.*
import java.util.ArrayList

class AddTaskActivity : AppCompatActivity() {
    companion object
    {
        const val NAME_KEY = "name"
        const val DESCRIPTION_KEY = "description"
        const val DEADLINE_KEY = "deadline"
        const val LOCATION_KEY = "location"
        const val TASKS_KEY = "tasks"
        const val DIVISIBILITY_KEY = "divisibility"
        const val PRIORITY_KEY = "priority"
        const val TAG = "debug"
    }
    private val db = FirebaseFirestore.getInstance()
    private val userDoc = db.collection("tasks").document("D3KEydQ5IWiBzZtuQegV")
    private val locations = db.collection("locations")
    private val places = ArrayList<String>()
    private val calendarUtils = DateTimeUtils()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        at_add_button.setOnClickListener {
            addTask()
        }

        locations.get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    Log.d(AddEventActivity.TAG, "${document.id} => ${document.data}")
                    val name = document.getString(NAME_KEY)
                    places.add(name!!)
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

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
        calendarUtils.setDatePicker(at_deadline_date, this@AddTaskActivity)
        calendarUtils.setTimePicker(at_deadline_time, this@AddTaskActivity)
        calendarUtils.setDefaultDate(at_deadline_date, 0)
        calendarUtils.setDefaultTime(at_deadline_time)
    }

    //TODO: event->task
    @RequiresApi(Build.VERSION_CODES.O)
    fun addTask()
    {
        val name = at_input_name.text.toString()
        val description = at_input_description.text.toString()
        val deadline = calendarUtils.getDTFromTV(at_deadline_date, at_deadline_time)
        //val selected_location = places
        val divisible = at_divisible.isSelected
        val priority = at_input_priority.text

        if(name.isEmpty() || description.isEmpty())
            return
        val task_data = hashMapOf(
            NAME_KEY to name,
            DESCRIPTION_KEY to description,
            DEADLINE_KEY to deadline,
            //LOCATION_KEY to selected_location,
            DIVISIBILITY_KEY to divisible,
            PRIORITY_KEY to priority
        )
        db.collection("tasks").add(task_data).addOnSuccessListener {
                documentReference -> Log.d(TAG, "Task added with ID: ${documentReference.id}")
        }.addOnFailureListener{
                e -> Log.w(TAG, "Error adding task", e)}
    }


}