package farszownicy.caldirola.utils

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.serialization.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Serializer(forClass = Calendar::class)
object CalendarSerializer: KSerializer<Calendar> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("CalendarSerializer", PrimitiveKind.STRING)

    @SuppressLint("SimpleDateFormat")
    private val df: DateFormat = SimpleDateFormat("HH:mm dd.mm.yyyy")

    override fun serialize(encoder: Encoder, value: Calendar) {
        encoder.encodeString(df.format(value.time))
    }

    override fun deserialize(decoder: Decoder): Calendar {
        val calendar = Calendar.getInstance()
        val date = df.parse(decoder.decodeString())
        if(date == null) {
            Log.e("TAG", "Deserialization of calendar failed")
        }
        else {
            calendar.time = date
        }
        return calendar
    }
}