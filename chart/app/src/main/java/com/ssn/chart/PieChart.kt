package com.ssn.chart
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View



class PieChart : View {
    private var pieChartPainter: Paint? = null
    private var valuePainter: Paint? = null
    private var series: ArrayList<SeriesPie>? = null
    private var sum: Float = 0F
    private var bound: RectF = RectF(50F, 50F, 100F, 100F)
    private var lowerBound: RectF = RectF(50F, 50F, 100F, 100F)
    private var radius: Float = 0F
    private var centre: PointF? = null
    private var startPoint: PointF? = null
    private var SO = -1.0  // Trái sang bên tâm
    private var canvas: Canvas? = null

    constructor (context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        pieChartPainter = Paint(Paint.ANTI_ALIAS_FLAG)
        pieChartPainter!!.strokeWidth = 3F
        pieChartPainter!!.style = Paint.Style.FILL

        valuePainter = Paint(Paint.ANTI_ALIAS_FLAG)
        valuePainter!!.color = Color.RED
        valuePainter!!.textSize = 17F
        valuePainter!!.textAlign = Paint.Align.CENTER

        series = ArrayList()
        series!!.add(SeriesPie(Color.BLACK, 400F))
        series!!.add(SeriesPie(Color.BLUE, 400F))

        series!!.forEach {
            sum += it.value!!
        }

    }

    override fun onDraw(canvas: Canvas?) {
        this.canvas = canvas
        super.onDraw(canvas)
        var startAngle = 0F
        var sweepAngle = 0F
        initCircle()
        series!!.forEach {
            startAngle += sweepAngle
            sweepAngle = it.value!! / sum * 360f
            it.startAngle = startAngle
            it.sweepAngle = sweepAngle
            if (it.click) {
                pieChartPainter!!.color = Color.LTGRAY
            } else {
                pieChartPainter!!.color = it.color!!
            }
            canvas!!.drawArc(bound, startAngle, sweepAngle, true, pieChartPainter!!)
            //
            drawText(canvas, bound, it, startAngle, sweepAngle)
        }
        pieChartPainter!!.color = Color.WHITE
        canvas!!.drawOval(lowerBound, pieChartPainter!!)
    }

    private fun drawText(
        canvas: Canvas,
        txtCentre: RectF?,
        item: SeriesPie,
        startAngle: Float,
        sweepAngle: Float
    ) {
        val angle: Float = (startAngle + sweepAngle + startAngle) / 2
        val str: String = item.value.toString() + "%"
        val txtRect = Rect()
        valuePainter!!.getTextBounds(str, 0, str.length, txtRect)
        val cenX =
            (txtCentre!!.centerX() + (radius - txtRect.height() - txtRect.width()) * Math.cos(
                Math.toRadians(angle.toDouble())
            )).toFloat()
        val cexY = (txtCentre.centerY() + (radius - txtRect.height() - txtRect.width()) * Math.sin(
            Math.toRadians(angle.toDouble())
        )).toFloat()
        canvas.drawText(str, cenX, cexY, valuePainter!!)
    }


    private fun initCircle() {
        val padding = 90F
        val w = width
        val h = height
        bound = RectF(0F, 0F, w.toFloat(), w.toFloat())
        radius = bound.width() / 2;
        lowerBound = RectF(
            1F * padding,
            1F * padding,
            w.toFloat() - padding,
            w.toFloat() - padding
        )
        if (null == centre) {
            centre = PointF(bound.centerX(), bound.centerY())
        }
        if (null == startPoint) {
            startPoint = PointF(bound.width(), bound.height() / 2)
        }
        if (SO.toInt() == -1) {
            SO = getDistance(startPoint!!, centre!!)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event!!.action
        if (action == MotionEvent.ACTION_DOWN) {
            val A = PointF(event.x, event.y)
            val AO = getDistance(A, centre!!)
            val AS = getDistance(A, startPoint!!)
            var click: Int = 0
            var angle = Math.toDegrees(Math.acos((AO * AO + SO * SO - AS * AS) / (2 * AO * SO)))
            if (A.y > startPoint!!.y) {
                angle = 360 - angle;
            }
            for (i in 0 until series!!.size) {
                val pieChart: SeriesPie = series!![i]
                val endAngle: Float = pieChart.startAngle!! + pieChart.sweepAngle!!

                if (endAngle == 360.toFloat()) {
                    series!![i].click = angle <= pieChart.startAngle!! && angle <= endAngle
                    if (series!![i].click) {
                        click = i
                    }
                } else {
                    series!![i].click = false
                }
                if (endAngle == 180.toFloat()) {
                    val after360 = (endAngle - 360).toDouble()
                    if (angle >= pieChart.startAngle!! && angle <= 360) {
                        series!![i].click = true
                    }
                    if (angle in 0.0..after360) {
                        series!![i].click = true
                    }
                    if (series!![i].click) {
                        click = i
                    }
                }
            }
            for (i in 0 until series!!.size) {
                if (i != click) {
                    series!![i].click = false
                }
            }
            postInvalidate()
        }
        return super.onTouchEvent(event)
    }


    // Tinh khoang cach giua 2 diem trong oxy
    private fun getDistance(a: PointF, b: PointF): Double {
        return Math.sqrt(
            Math.pow((a.x - b.x).toDouble(), 2.0) + Math.pow(
                (a.y - b.y).toDouble(),
                2.0
            )
        )
    }
}