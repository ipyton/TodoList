package com.example.myapplication.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
@Composable
fun PieChartScreen(future:MutableState<Int>, done:MutableState<Int>, missed:MutableState<Int>, today: MutableState<Int>) {
    val sum = future.value + done.value + missed.value

    val pieEntries = listOf(
        PieEntry(future.value.toFloat()/sum.toFloat()  * 100, "Future"),
        PieEntry(done.value.toFloat()/sum.toFloat() * 100, "Done"),
        PieEntry(missed.value.toFloat()/sum.toFloat() * 100, "Missed"),
        PieEntry(today.value.toFloat()/sum.toFloat() * 100, "Today"),
    )
    val pieDataSet = PieDataSet(pieEntries, "")
    pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
    val pieData = PieData(pieDataSet)
    pieDataSet.xValuePosition =
        PieDataSet.ValuePosition.INSIDE_SLICE;
    pieDataSet.yValuePosition =
        PieDataSet.ValuePosition.INSIDE_SLICE;
    //we created a class for adding "%" sign using
    pieDataSet.valueFormatter = PercentValueFormatter()
    pieDataSet.valueTextSize= 20f
    AndroidView(
        modifier = Modifier.size(300.dp),
        factory = { context ->
            PieChart(context).apply {
                data = pieData
                description.isEnabled = false
                centerText = "Statics about your progress"
                setDrawCenterText(true)
                setEntryLabelTextSize(12f)
                animateY(300)
            }
        }
    )
}
//we used this class for formatting value (adding % sign)
class PercentValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        //you can create your own formatting style below
        return "${value.toInt()}%"
    }
}