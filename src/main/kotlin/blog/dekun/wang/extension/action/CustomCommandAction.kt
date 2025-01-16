package blog.dekun.wang.extension.action

import blog.dekun.wang.extension.action.base.BaseAnAction
import blog.dekun.wang.extension.command.Command
import blog.dekun.wang.extension.constants.Constant
import blog.dekun.wang.extension.data.ConfigInfo
import blog.dekun.wang.extension.utils.Utils
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

        private fun getActionId(config: ConfigInfo): String {
            return getActionId(config.name, config.isRightClick ?: false)
        }

        private fun getActionId(name: String, isRightClick: Boolean): String {
            return if (isRightClick) {
                "${Constant.ACTION_GROUP_ID_RIGHT_CLICK}.${name}"
            } else {
                "${Constant.ACTION_GROUP_ID}.${name}"
            }
        }

        private fun addAction(config: ConfigInfo) {
            val actionManager = ActionManager.getInstance()
            if (config.isEnable != true) return
            // 右键逻辑处理
            if (config.isRightClick == true) {
                println("右键")
            }
            if (actionManager.getAction(getActionId(config)) != null) {
                return
            }
            val configInfoMainToolbarRight = config.takeIf { it.isRightClick != true } ?: config.copy().apply { isRightClick = false }
            val action = CustomCommandAction(configInfoMainToolbarRight)
            actionManager.registerAction(getActionId(configInfoMainToolbarRight), action)
            val commandExtensions = actionManager.getAction(Constant.ACTION_GROUP_ID)
            if (commandExtensions is DefaultActionGroup) {
                commandExtensions.add(action)
            }
        }

        private fun removeAction(config: ConfigInfo) {
            val actionManager = ActionManager.getInstance()
            fun remove(actionId: String, actionGroupId: String) {
                val action = actionManager.getAction(actionId) ?: return
                (actionManager.getAction(actionGroupId) as? DefaultActionGroup)?.remove(action)
                actionManager.unregisterAction(actionId)
            }
            remove(getActionId(config.name, true), Constant.ACTION_GROUP_ID_RIGHT_CLICK)
            remove(getActionId(config.name, false), Constant.ACTION_GROUP_ID)
        }

    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(event: AnActionEvent) {
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
        val commandStr = configInfo.commandStr?.trim()
        if (commandStr.isNullOrEmpty()) {
            Utils.showNotification(event.project, configInfo.name, "commandStr is empty", NotificationType.ERROR)
            return
        }
        if (configInfo.isTargetFile == true || configInfo.isTargetFolder == true) {
            val virtualFile = Utils.getVirtualFile(event)
            if (virtualFile != null) {
                val path = configInfo.executionDir?.takeIf { it.isNotBlank() } ?: if (virtualFile.isDirectory) virtualFile.path.substringBeforeLast("/")
                else virtualFile.path
                Command.execute(commandStr, path)
            } else {
                Command.execute(commandStr)
            }
        } else {
            Command.execute(commandStr)
        }
    }


}

