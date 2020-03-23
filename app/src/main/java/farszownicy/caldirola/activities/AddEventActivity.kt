package farszownicy.caldirola.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.R
import kotlinx.android.synthetic.main.activity_add_event.*
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity()
{
    companion object
    {
        const val NAME_KEY = "name"
        const val DESCRIPTION_KEY = "description"
        const val START_DATE_KEY = "start_date"
        const val END_DATE_KEY = "end_date"
        const val LOCATION_KEY = "location"
        const val TAG = "debug"
        const val DATE_FORMAT = "dd.MM.yyyy"
        const val TIME_FORMAT = "HH:mm:ss"
    }

    private val db = FirebaseFirestore.getInstance()
    private val userDoc = db.collection("events").document("sKNWMetaOLJXTIkXeU0W")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        ae_add_button.setOnClickListener {
            addEvent()
        }

        setDatePicker(ae_start_date)
        setTimePicker(ae_start_time)
        setDatePicker(ae_end_date)
        setTimePicker(ae_end_time)

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

    fun setDatePicker(tv: TextView)
    {
        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = DATE_FORMAT
            val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
            tv.text = sdf.format(cal.time)
        }

        tv.setOnClickListener{
            DatePickerDialog(this@AddEventActivity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))//.setButton(DialogInterface.BUTTON_POSITIVE)
                .show()
            //Emulowanie kliknięcia na wybór czasu po wybraniu daty TODO?
            //ae_start_time.performClick()
        }
    }

    fun setTimePicker(tv: TextView)
    {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            val myFormat = TIME_FORMAT
            val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
            tv.text = sdf.format(cal.time)
        }
        tv.setOnClickListener{
            TimePickerDialog(this@AddEventActivity, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addEvent()
    {
        val name = ae_input_name.text.toString()
        val description = ae_input_description.text.toString()

        val cal_start = getCalFromTV(ae_start_date, ae_start_time)
        val cal_end = getCalFromTV(ae_end_date, ae_end_time)

        if(name.isEmpty() || description.isEmpty())
            return
        val event_data = hashMapOf(
            NAME_KEY to name,
            DESCRIPTION_KEY to description,
            START_DATE_KEY to cal_start.time,
            END_DATE_KEY to cal_end.time
        )
        db.collection("events").add(event_data).addOnSuccessListener {
            documentReference -> Log.d(TAG, "Event added with ID: ${documentReference.id}")
        }.addOnFailureListener{
            e -> Log.w(TAG, "Error adding event", e)}
    }

    fun getCalFromTV(tvDate:TextView, tvTime:TextView): Calendar
    {
        val _date = SimpleDateFormat(DATE_FORMAT).parse(tvDate.text.toString())
        val _time = SimpleDateFormat(TIME_FORMAT).parse(tvTime.text.toString())
        val cal = Calendar.getInstance()
        cal.time = _date
        cal.add(Calendar.HOUR, _time.hours)
        cal.add(Calendar.MINUTE, _time.minutes)
        return cal
    }
}
