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

fun Canvas.drawIncreaseLineBall(scale : Float, w : Float, h : Float, paint : Paint) {

    val r : Float = Math.min(w, h) / rFactor
    val gap : Float = (h - 2 * r) / lines
    val sf : Float = scale.sinify()
    save()
    translate(w / 2, r)
    for (j in 0..(circles - 1)) {
        val sfj : Float = sf.divideScale(j, circles)
        save()
        translate(0f, gap * j)
        drawCircle(0f, 0f, (r / (j + 1)) * sfj, paint)
        if (j != circles - 1) {
            drawLine(0f, 0f, 0f, gap * sfj, paint)
        }
        restore()
    }
    restore()
}

fun Canvas.drawILBNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawIncreaseLineBall(scale, w, h, paint)
}


