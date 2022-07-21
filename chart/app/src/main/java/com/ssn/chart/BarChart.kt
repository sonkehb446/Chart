package com.ssn.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View

class BarChart : View {
    private var barPainter: Paint? = null
    private var axisPainter: Paint? = null
    private var levelPainter: Paint? = null
    private var guidePainter: Paint? = null
    private var padding: Float? = null
    private var xLabel: Float? = 0F
    private var yLabel: Float? = 0F
    private var series: ArrayList<Series>? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context?, attrs: AttributeSet?, defStyle: Int?) {
        padding = 20f
        barPainter = Paint()
        barPainter!!.style = Paint.Style.FILL
        barPainter!!.color = Color.LTGRAY

        axisPainter = Paint()
        axisPainter!!.style = Paint.Style.STROKE
        axisPainter!!.color = Color.BLACK
        axisPainter!!.strokeWidth = 5f

        guidePainter = Paint()
        guidePainter!!.style = Paint.Style.STROKE
        guidePainter!!.color = Color.RED
        axisPainter!!.strokeWidth = 3f

        levelPainter = Paint()
        levelPainter!!.textSize = 36F

        series = ArrayList()
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        val height = height
        val width = width

        val girdBottom = height - padding!! - yLabel!! // chieu cao
        val girdRight = width - padding!! // chieu rong
        drawGuides(canvas, girdBottom, girdRight)
        val girdTopLeft = padding!! + yLabel!! + 10f//
        drawAxis(canvas, girdTopLeft, girdBottom, girdRight)
        drawBars(canvas, girdTopLeft, girdBottom, girdRight, height)
        super.onDraw(canvas)
    }

    private fun drawBars(
        canvas: Canvas?,
        girdTopLeft: Float?,
        girdBottom: Float,
        girdRight: Float,
        canvasHeight: Int,
    ) {
        val space = 10f
        val totalSpace = series!!.size * 10f
        val width = (girdRight - girdTopLeft!! - totalSpace) / series!!.size
        var left = girdTopLeft!! + space
        var right = width + left
        val height = canvasHeight - 2 * padding!!
        series!!.forEach {
            val top = height + padding!! * (1f - it.value)
            canvas!!.drawRect(left, top, right, girdBottom, barPainter!!)
            canvas!!.save()
//            canvas.drawText(it.label, left, girdBottom + xLabel!!, levelPainter!!)
            canvas.rotate(270F)
            canvas.drawText(it.label, 1 - height, left + (width + 2 * space) / 2, levelPainter!!)
            canvas!!.restore()
            left = right + space
            right = left + width
        }
    }

    private fun drawAxis(
        canvas: Canvas?,
        girdTopLeft: Float?,
        girdBottom: Float,
        girdRight: Float
    ) {
        canvas!!.drawLine(girdTopLeft!!, girdBottom, girdTopLeft, padding!!, axisPainter!!)
        canvas!!.drawLine(girdTopLeft!!, girdBottom!!, girdRight, girdBottom, axisPainter!!)
    }

    private fun drawGuides(canvas: Canvas?, girdBottom: Float, girdRight: Float) {
        val spacing = (girdBottom - padding!!) / 10f
        var y: Float
        for (i in 0 until 10) {
            val bounds = Rect()
            val level = (100 - i * 10).toString()
            val width = levelPainter!!.measureText(level)
            if (yLabel!! < width) yLabel = width
            levelPainter!!.getTextBounds(level, 0, level.length, bounds)
            y = padding!! + i * spacing
            canvas!!.drawLine(padding!! + yLabel!! + 10f, y, girdRight, y, guidePainter!!)
            canvas!!.drawText(level, 0F, y + bounds.height() / 2, levelPainter!!)
        }
    }

    public fun setData(series: ArrayList<Series>) {
        val bounds = Rect()
        this.series = series
        val label = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            series.stream().max(Comparator.comparingInt { x -> x.label.length }).get().label

        } else {
            TODO("VERSION.SDK_INT < N")
        }
        levelPainter!!.getTextBounds(label, 0, label.length, bounds)
        yLabel = bounds.height().toFloat()
    }
}

