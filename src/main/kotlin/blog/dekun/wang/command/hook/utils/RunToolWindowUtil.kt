package blog.dekun.wang.command.hook.utils

import blog.dekun.wang.command.hook.data.ActionConfig
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
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.SystemInfo
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.File
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel

object RunToolWindowUtil {

    fun executeWithRealTimeOutput(project: Project, actionConfig: ActionConfig) {
        val regex = Regex("\\{\\{([a-zA-Z_]+)}}")
        var commandStr = actionConfig.commandStr.trim()

        if (commandStr.isBlank()) {
            Utils.showNotification(project, actionConfig.name, "commandStr is empty", NotificationType.ERROR)
            return
        }
        // 参数替换逻辑
        commandStr = regex.replace(commandStr) { matchResult ->
            actionConfig.commandParams[matchResult.groupValues[1]] ?: System.getenv(matchResult.groupValues[1]) ?: matchResult.value
        }
        // 执行命令逻辑
        val consoleView: ConsoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
        val command = listOf(System.getenv("SHELL") ?: "/bin/bash", "-c", commandStr)
        val processBuilder = ProcessBuilder(command)

        // macOS 环境PATH处理
        if (SystemInfo.isMac) {
            System.getenv("PATH")?.let { path ->
                val requiredPaths = listOf("/usr/local/bin", "/usr/local/sbin")
                val existingPaths = path.split(":").toSet()
                val missingPaths = requiredPaths.filter { it !in existingPaths }
                val newPath = if (missingPaths.isNotEmpty()) {
                    (missingPaths + existingPaths).joinToString(":")
                } else {
                    path
                }
                System.setProperty("PATH", newPath)
                processBuilder.environment()["PATH"] = newPath
            }
        }

        // 工作目录设置
        val directory = actionConfig.workingDirectory?.let { File(it) } ?: project.basePath?.let { File(it) } ?: File(System.getProperty("user.home"))

        consoleView.print("$directory \n", ConsoleViewContentType.LOG_INFO_OUTPUT)
        processBuilder.directory(directory)

        try {
            val process = processBuilder.start()
            val processHandler = OSProcessHandler(process, commandStr)

            // 停止按钮配置
            val stopButton = JButton().apply {
                preferredSize = Dimension(28, 28)
                icon = AllIcons.Actions.Suspend
                isBorderPainted = false
                addActionListener {
                    processHandler.destroyProcess()
                    icon = AllIcons.Debugger.Db_invalid_breakpoint
                }
            }

            // 进程监听器
            processHandler.addProcessListener(object : ProcessAdapter() {
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    when {
                        commandStr == event.text.trim() -> {
                            consoleView.print("Execute：${event.text}", ConsoleViewContentType.LOG_INFO_OUTPUT)
                            consoleView.print(" -".repeat(40) + "\n", ConsoleViewContentType.LOG_INFO_OUTPUT)
                        }

                        else -> consoleView.print(event.text, ConsoleViewContentType.LOG_DEBUG_OUTPUT)
                    }
                }

                override fun processTerminated(event: ProcessEvent) {
                    stopButton.isVisible = false
                }

                override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
                    if (willBeDestroyed) stopButton.isVisible = false
                }
            })

            val panel = JPanel(BorderLayout()).apply {
                add(consoleView.component, BorderLayout.CENTER)
                add(JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    add(stopButton)
                }, BorderLayout.WEST)
            }

            // 创建运行内容描述符
            val descriptor = RunContentDescriptor(null, processHandler, panel, actionConfig.name).apply {
                isAutoFocusContent = true
            }

            // 显示运行内容
            RunContentManager.getInstance(project).showRunContent(
                DefaultRunExecutor.getRunExecutorInstance(), descriptor
            )

            processHandler.startNotify()

        } catch (e: Exception) {
            consoleView.print("Failed to execute command: ${e.message}\n", ConsoleViewContentType.LOG_ERROR_OUTPUT)
        }
    }
}