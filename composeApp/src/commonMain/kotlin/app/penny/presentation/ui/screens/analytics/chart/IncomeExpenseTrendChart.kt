package app.penny.presentation.ui.screens.analytics.chart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import app.penny.domain.enum.TransactionType
import app.penny.domain.model.TransactionModel
import com.aay.compose.baseComponents.model.LegendPosition
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun IncomeExpenseTrendChart(transactions: List<TransactionModel>) {
    // 过滤收入和支出
    val incomeData = transactions.filter { it.transactionType == TransactionType.INCOME }
    val expenseData = transactions.filter { it.transactionType == TransactionType.EXPENSE }

    // 按日期汇总金额
    val incomeByDate = incomeData.groupBy {
        it.transactionDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }.mapValues { entry ->
        entry.value.fold(BigDecimal.ZERO) { acc, transaction ->
            acc + transaction.amount
        }
    }

    val expenseByDate = expenseData.groupBy {
        it.transactionDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }.mapValues { entry ->
        entry.value.fold(BigDecimal.ZERO) { acc, transaction ->
            acc + transaction.amount
        }
    }

    // 获取所有日期并排序
    val dates = (incomeByDate.keys + expenseByDate.keys).distinct().sorted()

    // 准备收入和支出数据
    val incomeValues = dates.map { incomeByDate[it] ?: 0.0 }
    val expenseValues = dates.map { expenseByDate[it] ?: 0.0 }

    // 创建 LineParameters 列表
    val incomeLine = LineParameters(
        data = incomeValues.map { it.toString().toDouble() },
        lineColor = Color(0xFF4CAF50), // 绿色
        label = "收入",
        lineType = LineType.DEFAULT_LINE,
        lineShadow = true
    )

    val expenseLine = LineParameters(
        data = expenseValues.map { it.toString().toDouble() },
        lineColor = Color(0xFFF44336), // 红色
        label = "支出",
        lineType = LineType.DEFAULT_LINE,
        lineShadow = true
    )

    // 创建 X 轴数据
    val xAxisData = dates.map { it.toString() }

    // 绘制图表
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        LineChart(
            linesParameters = listOf(incomeLine, expenseLine),
            xAxisData = xAxisData,
            legendPosition = LegendPosition.TOP,
            isGrid = true,
            gridColor = Color.LightGray,
            yAxisStyle = TextStyle(color = Color.Black),
            xAxisStyle = TextStyle(color = Color.Black),
            descriptionStyle = TextStyle(color = Color.Black),
            showGridWithSpacer = true
        )
    }
}

