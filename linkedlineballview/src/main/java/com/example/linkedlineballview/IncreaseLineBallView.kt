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
        drawCircle(0f, 0f, (r / (j + 1)) * sfj.divideScale(0, 2), paint)
        if (j != circles - 1) {
            drawLine(0f, 0f, 0f, gap * sfj.divideScale(1, 2), paint)
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

class IncreaseLineBallView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class ILBNode(var i : Int, val state : State = State()) {

        private var next : ILBNode? = null
        private var prev : ILBNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = ILBNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawILBNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : ILBNode {
            var curr : ILBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class IncreaseLineBall(var i : Int) {

        private var curr : ILBNode = ILBNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : IncreaseLineBallView) {

        private val animator : Animator = Animator(view)
        private val ilb : IncreaseLineBall = IncreaseLineBall(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            ilb.draw(canvas, paint)
            animator.animate {
                ilb.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            ilb.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : IncreaseLineBallView {
            val view : IncreaseLineBallView = IncreaseLineBallView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
