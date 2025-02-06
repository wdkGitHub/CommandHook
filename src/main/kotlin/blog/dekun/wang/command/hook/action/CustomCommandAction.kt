package blog.dekun.wang.command.hook.action

import blog.dekun.wang.command.hook.action.base.BaseAnAction
import blog.dekun.wang.command.hook.constants.Constant
import blog.dekun.wang.command.hook.data.ConfigInfo
import blog.dekun.wang.command.hook.utils.RunToolWindowUtil
import blog.dekun.wang.command.hook.utils.Utils
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.vfs.isFile

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class CustomCommandAction(private val configInfo: ConfigInfo) : BaseAnAction() {

    companion object {

        fun modifyAction(add: List<ConfigInfo>? = null, remove: List<ConfigInfo>? = null) {
            remove?.let {
                remove.forEach {
                    removeAction(it)
                }
            }
            add?.let {
                add.forEach { addAction(it) }
            }
        }

        private fun getActionId(config: ConfigInfo): Array<String> {
            val baseActionId = getActionId(config.name, false)
            return if (config.isRightClick == true) {
                arrayOf(baseActionId, getActionId(config.name, true))
            } else {
                arrayOf(baseActionId)
            }
        }

        private fun getActionId(name: String, isRightClick: Boolean): String {
            val groupId =
                if (isRightClick) Constant.ACTION_GROUP_ID_RIGHT_CLICK else Constant.ACTION_GROUP_ID
            return "$groupId.$name"
        }

        private fun addAction(config: ConfigInfo) {
            val actionManager = ActionManager.getInstance()
            if (config.isEnable != true) return
            if (config.isRightClick == true) {
                // 右键逻辑处理
                println("右键")
            } else {
                val actionId = getActionId(config.name, false)
                if (actionManager.getAction(actionId) != null) {
                    return
                }
                val configInfoMainToolbarRight = config.takeIf { it.isRightClick != true } ?: config.copy().apply { isRightClick = false }
                val action = CustomCommandAction(configInfoMainToolbarRight)
                actionManager.registerAction(actionId, action)
                val commandExtensions = actionManager.getAction(Constant.ACTION_GROUP_ID)
                if (commandExtensions is DefaultActionGroup) {
                    commandExtensions.add(action)
                }
            }
        }

        private fun removeAction(config: ConfigInfo) {
            val actionManager = ActionManager.getInstance()
            fun remove(actionId: String, actionGroupId: String) {
                val action = actionManager.getAction(actionId) ?: return
                (actionManager.getAction(actionGroupId) as? DefaultActionGroup)?.remove(action)
                actionManager.unregisterAction(actionId)
            }
            getActionId(config).forEach { actionId ->
                when {
                    actionId.startsWith(Constant.ACTION_GROUP_ID) -> remove(
                        actionId,
                        Constant.ACTION_GROUP_ID
                    )

                    actionId.startsWith(Constant.ACTION_GROUP_ID_RIGHT_CLICK) -> remove(
                        actionId,
                        Constant.ACTION_GROUP_ID_RIGHT_CLICK
                    )
                }
            }
        }

    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(event: AnActionEvent) {
        if (event.project == null) {
            event.presentation.isEnabledAndVisible = false
            return
        }
        val name = configInfo.name.trim()
        event.presentation.text = name
        if (name.isBlank()) {
            event.presentation.isEnabledAndVisible = false
            return
        }
        val virtualFile = Utils.getVirtualFile(event)
        val isEnabled = configInfo.isEnable == true
        event.presentation.isEnabledAndVisible = when {
            virtualFile == null || configInfo.isRightClick != true -> isEnabled
            configInfo.isTargetFile == true && configInfo.isTargetFolder == true -> isEnabled
            virtualFile.isDirectory && configInfo.isTargetFolder == true -> isEnabled
            virtualFile.isFile && configInfo.isTargetFile == true -> isEnabled
            else -> false
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
//        val regex = Regex("@@(\\S+)")
        var commandStr = configInfo.commandStr?.trim()
        if (commandStr.isNullOrEmpty()) {
            Utils.showNotification(project, configInfo.name, "commandStr is empty", NotificationType.ERROR)
            return
        }
//        commandStr = regex.replace(commandStr) { matchResult ->
//            // 获取 @ 后的内容，例如 PATH
//            val variableName = matchResult.groupValues[1]
//            val value = System.getenv(variableName) ?: "NotFoundEnvVariable"
//            // 替换为环境变量值
//            value
//        }
        if (configInfo.isTargetFile == true || configInfo.isTargetFolder == true) {
            val virtualFile = Utils.getVirtualFile(event)
            if (virtualFile != null) {
                val path = configInfo.executionDir?.takeIf { it.isNotBlank() } ?: if (virtualFile.isDirectory) virtualFile.path.substringBeforeLast("/")
                else virtualFile.path
                RunToolWindowUtil.executeWithRealTimeOutput(project, configInfo.name, commandStr.split(" "), path)
            } else {
                RunToolWindowUtil.executeWithRealTimeOutput(project, configInfo.name, commandStr.split(" "))
            }
        } else {
            RunToolWindowUtil.executeWithRealTimeOutput(project, configInfo.name, commandStr.split(" "))
        }
    }


}

