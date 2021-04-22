package com.example.linkedlineballview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity
import android.content.Context

val colors : Array<Int> = arrayOf(
    "#f44336",
    "#2196F3",
    "#004D40",
    "#BF360C",
    "#00C853"
).map {
    Color.parseColor(it)
}.toTypedArray()
val circles : Int = 4
val lines : Int = 3
val scGap : Float = 0.02f / (circles + lines)
val strokeFactor : Float = 90f
val rFactor : Float = 5.6f
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

