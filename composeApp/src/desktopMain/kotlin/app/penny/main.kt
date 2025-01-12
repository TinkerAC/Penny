package app.penny

import App
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.penny.platform.ApplicationInitializer
import app.penny.platform.initialize
import app.penny.platform.printDeviceInfo
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import java.awt.BorderLayout
import java.awt.Button
import java.awt.Dialog
import java.awt.FlowLayout
import java.awt.Frame
import java.awt.Panel
import java.awt.TextArea

//JVM entry point
fun main() {
//    Thread.setDefaultUncaughtExceptionHandler { _, e ->
//        // 创建一个 AWT 对话框显示异常信息
//        Dialog(Frame(), "Error").apply {
//            layout = BorderLayout() // 使用 BorderLayout 更好地布局组件
//
//            // 创建一个多行文本区域用于显示完整的堆栈跟踪信息
//            val textArea = TextArea(e.stackTraceToString(), 10, 50, TextArea.SCROLLBARS_VERTICAL_ONLY).apply {
//                isEditable = false // 禁止编辑
//            }
//            add(textArea, BorderLayout.CENTER)
//
//            // 添加一个按钮用于关闭对话框
//            val buttonPanel = Panel(FlowLayout()).apply {
//                val button = Button("OK").apply {
//                    addActionListener { dispose() }
//                }
//                add(button)
//            }
//            add(buttonPanel, BorderLayout.SOUTH)
//
//            // 设置对话框大小并显示
//            setSize(500, 400)
//            isVisible = true
//        }
//    }





    application {


        val appInitializer = ApplicationInitializer(
            application = this
        )

        appInitializer.printDeviceInfo()

        appInitializer.initialize()

        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(SharedRes.strings.app_name),
            state = rememberWindowState(
                placement = WindowPlacement.Floating,
                width = 450.dp,
                height = 850.dp,
                position = WindowPosition.Aligned(Alignment.Center)
            )
        ) {
            App()
        }
    }
}