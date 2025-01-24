package blog.dekun.wang.command.hook.utils


import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.execution.ui.RunContentManager
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Key
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel

object RunToolWindowUtil {


    fun executeWithRealTimeOutput(tabName: String, commands: List<String>, dirPath: String? = null) {
        val resolvedProject = ProjectManager.getInstance().openProjects.firstOrNull() ?: throw IllegalStateException("No open project found!")

        val consoleView: ConsoleView = TextConsoleBuilderFactory.getInstance().createBuilder(resolvedProject).console

        val processBuilder = ProcessBuilder(commands)
        val directory = when {
            dirPath != null -> File(dirPath)
            resolvedProject.basePath != null -> File(resolvedProject.basePath!!)
            else -> File(System.getProperty("user.home"))
        }
        processBuilder.directory(directory)
        try {
            val process = processBuilder.start()

            val processHandler = OSProcessHandler(process, commands.joinToString(" "))
            val stopButton = JButton().apply {
                preferredSize = Dimension(28, 28)
                icon = AllIcons.Actions.Suspend
                isBorderPainted = false
            }
            processHandler.addProcessListener(object : ProcessAdapter() {
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    consoleView.print(event.text, com.intellij.execution.ui.ConsoleViewContentType.SYSTEM_OUTPUT)
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
            RunContentManager.getInstance(resolvedProject).showRunContent(executor, descriptor)

            processHandler.startNotify()

        } catch (e: Exception) {
            consoleView.print("Failed to execute command: ${e.message}\n", com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT)
        }
    }


}
