package com.ssn.chart

class Ch(val X: Int, val Y: Int) {
}

data class Series(var label: String, var value: Float)


data class SeriesPie(
    var color: Int? = null,
    var value: Float? = null,
    var click:Boolean = false,
    var startAngle: Float? = null,
    var sweepAngle: Float? = null
)