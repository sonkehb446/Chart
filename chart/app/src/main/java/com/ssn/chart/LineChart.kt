package com.ssn.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.random.Random

class LineChart : View {
    private var axisPainter: Paint? = null
    private var circlePainter: Paint? = null
    private var guidePainter: Paint? = null
    private var linePainter: Paint? = null
    private var yLabelPainter: Paint? = null
    private var padding: Float = 20F
    private var radius: Float = 10F
    private var traction: Float = 10F //Lực kéo
    private var yLabelWidth: Float = 10F //chiều rộng label text
    private var xLine: Float = 10F
    private var yLine: Float = 10F
    private var maxValue: Float = 10F
    private var minValue: Float = 10F
    private var series: ArrayList<Float>? = null
    private var path: Path? = null
    private var spaceHeight: Float? = null


    constructor (context: Context) : super(context) {
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

    private fun init(context: Context?, attrs: AttributeSet?, defStyle: Int) {
        series = ArrayList()
        path = Path()
        val ran = Random
        minValue = Float.MAX_VALUE //
        for (i in 0..10) {
            val num = ran.nextInt(100) + 10
            series!!.add(num.toFloat())
            if (maxValue < num) maxValue = num.toFloat()
            if (num < minValue) minValue = num.toFloat()
        }
        minValue = (Math.floor(minValue.toDouble()) - 10F).toFloat()
        maxValue = (Math.ceil(maxValue.toDouble()) + 10F).toFloat()

        axisPainter = Paint(Paint.ANTI_ALIAS_FLAG)
        axisPainter!!.strokeWidth = 3F
        axisPainter!!.color = Color.BLACK

        circlePainter = Paint(Paint.ANTI_ALIAS_FLAG)
        circlePainter!!.strokeWidth = 3F
        circlePainter!!.color = Color.RED

        guidePainter = Paint(Paint.ANTI_ALIAS_FLAG)
        guidePainter!!.strokeWidth = 3F
        guidePainter!!.color = Color.LTGRAY

        yLabelPainter = Paint(Paint.ANTI_ALIAS_FLAG)
        yLabelPainter!!.textSize = 30F
        yLabelPainter!!.textAlign = Paint.Align.RIGHT
        yLabelPainter!!.color = Color.BLUE

        linePainter = Paint(Paint.ANTI_ALIAS_FLAG)
        linePainter!!.style = Paint.Style.STROKE
        linePainter!!.color = Color.BLACK
        linePainter!!.strokeWidth = 10F
        linePainter!!.isDither = true
    }

    override fun onDraw(canvas: Canvas?) {
        val widthView = width
        val heightView = height
        val gTopLeft = padding + yLabelWidth
        val gRight = widthView - padding
        val gBottom = heightView - padding
        val gHeight = heightView - 2 * padding
        val drawHeight = maxValue - minValue
        val space = (widthView - 2 * padding) / series!!.size
        spaceHeight = gHeight / drawHeight // Point
        xLine = yLabelWidth + space
        yLine = (series!![0] - minValue) * spaceHeight!!
        if (canvas == null) return
        drawAxis(canvas, gTopLeft, gBottom, gRight)
        drawGuide(canvas, gRight, gHeight, gTopLeft)
        drawPoint(canvas, space, gHeight, drawHeight)
        super.onDraw(canvas)
    }


    private fun drawAxis(canvas: Canvas, gTopLeft: Float, gBottom: Float, gRight: Float) {
        canvas.drawLine(gTopLeft, padding, gTopLeft, gBottom, axisPainter!!)
        canvas.drawLine(gTopLeft, gBottom, gRight, gBottom, axisPainter!!)
    }

    private fun drawPoint(canvas: Canvas, space: Float, gHeight: Float, drawHeight: Float) {
        path!!.reset()
        path!!.moveTo(xLine, yLine)
        canvas.drawCircle(xLine, yLine, radius, circlePainter!!)
        for (i in 0 until series!!.size) {
            xLine += space-10
            yLine = ((series!![i] - minValue) * spaceHeight!!)
            Log.d("Check", "X= $xLine , y = $yLine , value = ${series!![i]}")
            canvas.drawCircle(
                xLine,
                yLine,
                radius,
                circlePainter!!
            )
            path!!.lineTo(xLine, yLine)
        }
        canvas.drawPath(path!!, linePainter!!)
    }

    private fun drawGuide(canvas: Canvas, gRight: Float, gHeight: Float, gTopLeft: Float) {
        val space = gHeight / series!!.size
        // Tinh toan label theo max min cua list
        var currentLabel = maxValue
        val stepLabel = (maxValue - minValue) / series!!.size
        val bounds = Rect()

        var y: Float
        for (i in 0..series!!.size) {
            y = space * i + padding
            val label = String.format("%d", currentLabel.toLong())
            val width = yLabelPainter!!.measureText(label)
            yLabelPainter!!.getTextBounds(label, 0, label.length, bounds)
            if (yLabelWidth < width) {
                yLabelWidth = width
                path!!.reset()
                postInvalidate()
            }
            canvas.drawText(label, gTopLeft - 5f, y + bounds.height() / 2, yLabelPainter!!)
            currentLabel -= stepLabel
            //
            canvas.drawLine(gTopLeft, y, gRight, y, guidePainter!!)
        }
    }
}