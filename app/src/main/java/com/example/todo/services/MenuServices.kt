package com.example.todo.services

import android.view.View
import android.widget.LinearLayout

fun openFabMenu(verticaLayout: LinearLayout): Boolean {
    verticaLayout.visibility = View.VISIBLE
    verticaLayout.animate().alpha(1f).duration = 300
    return true
}

fun closeFabMenu(verticaLayout: LinearLayout): Boolean {
    verticaLayout.animate().alpha(0f).setDuration(300).withEndAction {
        verticaLayout.visibility = View.GONE
    }
    return false
}