package farszownicy.caldirola.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class DateTimeUtils {
    fun getDTFromTV(tvDate: TextView, tvTime: TextView): LocalDateTime
    {
        val formatter = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT)
        //val _date = LocalDateTime.parse(tvDate.text.toString()))//SimpleDateFormat(Constants.DATE_FORMAT).parse(tvDate.text.toString())
        //val _time = SimpleDateFormat(Constants.TIME_FORMAT).parse(tvTime.text.toString())
        val dtString = tvDate.text.toString() + " " + tvTime.text.toString()
        val datetime = LocalDateTime.parse(dtString, formatter);
        return datetime
    }
    fun setDatePicker(tv: TextView, context: Context)
    {
        val cal = Calendar.getInstance()

        //USTAWIENIE DATY NIE-DEFAULTOWEJ - TO W EDYCJI TASKA/EVENTU
        if(tv.text.toString() != "date"){
            val splitted = tv.text.toString().split('.')
            cal.set(Calendar.YEAR, Integer.parseInt(splitted[2]))

            //INDEKSOWANIE MSC OD 0, TRZEBA ODJAC 1
            cal.set(Calendar.MONTH, Integer.parseInt(splitted[1])-1)
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splitted[0]))
        }
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
        val cal =  Calendar.getInstance()
        cal.add(Calendar.HOUR, offset)
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT, Locale.FRANCE)
        tv.text = sdf.format(cal.time)
    }

    fun setDate(tv :TextView, dt: LocalDateTime){
        tv.text = dt.format(DateTimeFormatter.ofPattern(Constants.DATE_FORMAT))
    }

    fun setTimePicker(tv: TextView, context: Context)
    {
        val cal = Calendar.getInstance()

        if(tv.text.toString() != "time"){
            val splitted = tv.text.toString().split(':')
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitted[0]))
            cal.set(Calendar.MINUTE, Integer.parseInt(splitted[1]))
        }

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

    fun setDefaultTime(tv :TextView, offset:Int)
    {
        val cal = Calendar.getInstance()
        cal.add(Calendar.HOUR, offset)
        val sdf = SimpleDateFormat(Constants.TIME_FORMAT, Locale.FRANCE)
        tv.text = sdf.format(cal.time)
    }

    fun setTime(tv:TextView, dt:LocalDateTime){
        tv.text = dt.format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT))
    }
}