package farszownicy.caldirola.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import farszownicy.caldirola.activities.AddEventActivity
import java.text.SimpleDateFormat
import java.util.*

class CalendarUtils {
    fun getCalFromTV(tvDate: TextView, tvTime: TextView): Calendar
    {
        val _date = SimpleDateFormat(Constants.DATE_FORMAT).parse(tvDate.text.toString())
        val _time = SimpleDateFormat(Constants.TIME_FORMAT).parse(tvTime.text.toString())
        val cal = Calendar.getInstance()
        cal.time = _date
        cal.add(Calendar.HOUR, _time.hours)
        cal.add(Calendar.MINUTE, _time.minutes)
        return cal
    }
    fun setDatePicker(tv: TextView, context: Context)
    {
        val cal = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = Constants.DATE_FORMAT
            val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
            tv.text = sdf.format(cal.time)
        }

        tv.setOnClickListener{
            DatePickerDialog(context, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
                .show()
        }
    }

    fun setDefaultDate(tv :TextView, offset:Int)
    {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR_OF_DAY, offset)
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT, Locale.FRANCE)
        tv.text = sdf.format(cal.time)
    }

    fun setTimePicker(tv: TextView, context: Context)
    {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            val myFormat = Constants.TIME_FORMAT
            val sdf = SimpleDateFormat(myFormat, Locale.FRANCE)
            tv.text = sdf.format(cal.time)
        }
        tv.setOnClickListener{
            TimePickerDialog(context, timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true).show()
        }
    }

    fun setDefaultTime(tv :TextView)
    {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat(Constants.TIME_FORMAT, Locale.FRANCE)
        tv.text = sdf.format(cal.time)
    }
}