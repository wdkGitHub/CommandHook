package blog.dekun.wang.command.hook.utils


import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.execution.ui.RunContentManager
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel

object RunToolWindowUtil {


    fun executeWithRealTimeOutput(project: Project, tabName: String, commandLine: String, dirPath: String? = null) {

        val consoleView: ConsoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console

        val command = listOf(System.getenv("SHELL") ?: "/bin/bash", "-c", commandLine)
        val processBuilder = ProcessBuilder(command)
        val directory = when {
            dirPath != null -> File(dirPath)
            project.basePath != null -> File(project.basePath!!)
            else -> File(System.getProperty("user.home"))
        }
        consoleView.print("$directory \n", ConsoleViewContentType.LOG_INFO_OUTPUT)
        processBuilder.directory(directory)
        try {
            val process = processBuilder.start()

            val processHandler = OSProcessHandler(process, commandLine)
            val stopButton = JButton().apply {
                preferredSize = Dimension(28, 28)
                icon = AllIcons.Actions.Suspend
                isBorderPainted = false
            }
            processHandler.addProcessListener(object : ProcessAdapter() {
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    consoleView.print(event.text, ConsoleViewContentType.LOG_DEBUG_OUTPUT)
                }

                override fun processTerminated(event: ProcessEvent) {
                    stopButton.isVisible = false
                }

                override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
                    if (willBeDestroyed) {
                        stopButton.isVisible = false
                    }
                }
            })
            val buttonPanel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
            }

            stopButton.addActionListener {
                processHandler.destroyProcess()
                stopButton.icon = AllIcons.Debugger.Db_invalid_breakpoint
            }
            buttonPanel.add(stopButton)
            // 创建包含控制台和停止按钮的面板
            val panel = JPanel(BorderLayout())
            panel.add(consoleView.component, BorderLayout.CENTER)
            panel.add(buttonPanel, BorderLayout.WEST)

            // 将面板添加到 RunContentDescriptor 中
            val descriptor = RunContentDescriptor(null, processHandler, panel, tabName).apply {
                isAutoFocusContent = true
            }

            val executor = DefaultRunExecutor.getRunExecutorInstance()
            RunContentManager.getInstance(project).showRunContent(executor, descriptor)

            processHandler.startNotify()

        } catch (e: Exception) {
            consoleView.print("Failed to execute command: ${e.message}\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
        }
    }


}
