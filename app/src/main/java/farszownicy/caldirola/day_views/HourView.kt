package farszownicy.caldirola.day_views

import android.content.Context
import android.text.Layout
import android.text.StaticLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import farszownicy.caldirola.R
import kotlinx.android.synthetic.main.hour_grid.view.*
import kotlin.math.max

//Widok godziny (jednego wiersza w agendzie)
class HourView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.hour_grid, this, true)
    }

    fun setText(text: String) {
        hour_tv.text = text
    }

    val hourTextWidth: Float
        get() {
            val param: LinearLayout.LayoutParams =
                hour_tv.layoutParams as LinearLayout.LayoutParams
            val measureTextWidth: Float = hour_tv.paint.measureText("12:00")
            return (max(measureTextWidth, param.width.toFloat())
                    + param.marginEnd
                    + param.marginStart)
        }

    val hourTextHeight: Float
        get() = StaticLayout(
            "12:00", hour_tv.paint, hourTextWidth.toInt(),
            Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true
        ).height.toFloat()

    val separateHeight: Float
        get() {
            return hour_separator.layoutParams.height.toFloat()
        }

    fun setHourSeparatorAsInvisible() {
        hour_separator.visibility = View.INVISIBLE
    }

    fun setHourSeparatorAsVisible() {
        hour_separator.visibility = View.VISIBLE
    }
}