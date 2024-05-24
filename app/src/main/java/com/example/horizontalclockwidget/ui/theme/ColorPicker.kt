package com.example.horizontalclockwidget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ColorPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    var selectedColor: Int = -0x1000000 // Default to black

    init {
        setOnClickListener {
            // Show color picker dialog and set selectedColor
            // Example: selectedColor = Color.RED
        }
    }
}
