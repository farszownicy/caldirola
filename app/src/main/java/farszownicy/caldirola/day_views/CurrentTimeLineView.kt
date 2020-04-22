package farszownicy.caldirola.day_views

import android.content.Context
import android.text.Layout
import android.text.StaticLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import farszownicy.caldirola.R
import farszownicy.caldirola.utils.Constants.TIME_FORMAT
import kotlinx.android.synthetic.main.current_time_line.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.max

class CurrentTimeLineView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.current_time_line, this, true)
    }

    fun setText(text: String) {
         curr_hour_tv.text = text
    }

    val timeLineWidth: Float
        get() {
            val param: LinearLayout.LayoutParams =
                curr_hour_tv.layoutParams as LinearLayout.LayoutParams
            val measureTextWidth: Float = curr_hour_tv.paint.measureText("12:00")
            return (max(measureTextWidth, param.width.toFloat())
                    + param.marginEnd
                    + param.marginStart)
        }

    val timeLineHeight: Float
        get() = StaticLayout(
            "12:00", curr_hour_tv.paint, timeLineWidth.toInt(),
            Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true
        ).height.toFloat()

    val lineHeight: Float
        get() {
            return time_line.layoutParams.height.toFloat()
        }

    fun updatePosition(position: Float, time: LocalDateTime){
        val simpleDateFormat = DateTimeFormatter.ofPattern(TIME_FORMAT)
        y = position
        setText(simpleDateFormat.format(time))
    }
}