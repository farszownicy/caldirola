package farszownicy.caldirola.utils

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
/*
    To może się kiedyś przydać ale jest do przerobienia!
*/

class MultiSpinner : AppCompatSpinner, OnMultiChoiceClickListener,
    DialogInterface.OnCancelListener {
    private var items: List<String>? = null
    private var selected: BooleanArray = BooleanArray(0)
    private var defaultText: String? = null
    private var listener: MultiSpinnerListener? = null

    constructor(context: Context?) : super(context) {}
    constructor(arg0: Context?, arg1: AttributeSet?) : super(arg0, arg1) {}
    constructor(arg0: Context?, arg1: AttributeSet?, arg2: Int) : super(arg0, arg1, arg2) {}

    override fun onClick(
        dialog: DialogInterface,
        which: Int,
        isChecked: Boolean
    ) {
        if (isChecked) selected[which] = true else selected[which] = false
    }

    override fun onCancel(dialog: DialogInterface?) { // refresh text on spinner
        val spinnerBuffer = StringBuffer()
        var someUnselected = false
        for (i in items!!.indices) {
            if (selected[i]) {
                spinnerBuffer.append(items!![i])
                spinnerBuffer.append(", ")
            } else {
                someUnselected = true
            }
        }
        var spinnerText: String?
        if (someUnselected) {
            spinnerText = spinnerBuffer.toString()
            if (spinnerText.length > 2) spinnerText =
                spinnerText.substring(0, spinnerText.length - 2)
        } else {
            spinnerText = defaultText
        }
        val adapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item, arrayOf(spinnerText)
        )
        setAdapter(adapter)
        listener!!.onItemsSelected(selected)
    }

    override fun performClick(): Boolean {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMultiChoiceItems(
            items!!.toTypedArray<CharSequence>(), selected, this
        )
        builder.setPositiveButton(
            R.string.ok,
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        builder.setOnCancelListener(this)
        builder.show()
        return true
    }

    fun setItems(
        items: List<String>, allText: String?,
        listener: MultiSpinnerListener?
    ) {
        this.items = items
        defaultText = allText
        this.listener = listener
        selected = BooleanArray(items.size)
        for (i in selected.indices) selected[i] = true
        val adapter = ArrayAdapter(
            context,
            R.layout.simple_spinner_item, arrayOf(allText)
        )
        setAdapter(adapter)
    }

    interface MultiSpinnerListener {
        fun onItemsSelected(selected: BooleanArray?): ArrayList<String>
    }
}