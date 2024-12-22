package app.penny.presentation.ui.components.aayChart.utils

import app.penny.presentation.ui.aay.aayChart.barChart.model.BarParameters
import app.penny.presentation.ui.components.aayChart.lineChart.model.LineParameters

internal fun checkIfDataValid(
    xAxisData: List<String>,
    linesParameters: List<LineParameters> = emptyList(),
    barParameters: List<BarParameters> = emptyList()
) {
    if (linesParameters.isEmpty()) {
        val data = barParameters.map { it.data }
        data.forEach {
            if (it.size != xAxisData.size) {
                throw Exception("The data size of bar must be equal to the x-axis data size.")
            }
            checkIfDataIsNegative(it)
        }
    } else {
        val data = linesParameters.map { it.data }
        data.forEach {
            if (it.size != xAxisData.size) {
                throw Exception("The data size of line must be equal to the x-axis data size.")
            }
            checkIfDataIsNegative(it)
        }
    }
}

internal fun checkIfDataIsNegative(data: List<Double>) {
    data.forEach {
        if (it < 0.0) {
            throw Exception("The data can't contains negative values.")
        }
    }
}