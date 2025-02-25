package blog.dekun.wang.command.hook.action

import blog.dekun.wang.command.hook.action.base.BaseAnAction
import blog.dekun.wang.command.hook.constants.Constant
import blog.dekun.wang.command.hook.data.ActionConfig
import blog.dekun.wang.command.hook.data.ActionPosition
import blog.dekun.wang.command.hook.services.ActionConfigService
import blog.dekun.wang.command.hook.utils.RunToolWindowUtil
import com.intellij.icons.AllIcons
import com.intellij.ide.ChangeProjectIconPalette
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.registry.Registry
import com.intellij.util.ui.AvatarIcon

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 * @see  com.intellij.openapi.wm.impl.welcomeScreen.projectActions.ChangeProjectIconAction ICON
 */


class CustomCommandAction(private val actionConfig: ActionConfig) : BaseAnAction() {

    companion object {

        fun modifyAction(configsToAdd: List<ActionConfig>? = null, configsToRemove: List<ActionConfig>? = null) {
            val actionManager = ActionManager.getInstance()
            configsToRemove?.forEach {
//                println("删除：${it.name}  ${it.position}")
                removeAction(it, actionManager)
            }
            configsToAdd?.forEach {
//                println("添加：${it.name}  ${it.position}")
                addAction(it, actionManager)
            }
        }

        private fun generateActionId(actionConfig: ActionConfig): String {
            return actionConfig.name.hashCode().toString()
        }

        private fun getActionGroup(position: ActionPosition, actionManager: ActionManager): DefaultActionGroup? {
            val actionGroupId = when (position) {
                ActionPosition.DEFAULT -> Constant.ACTION_GROUP_ID
                ActionPosition.CENTRAL_TOOLBAR -> Constant.MAIN_TOOLBAR_CENTER
                ActionPosition.RIGHT_CLICK -> return null
            }
            return (actionManager.getAction(actionGroupId) as? DefaultActionGroup).also {
                if (it == null) println("Action group not found for position: $position")
            }
        }

        private fun addAction(actionConfig: ActionConfig, actionManager: ActionManager) {
            if (!actionConfig.enable) return
            val actionId = generateActionId(actionConfig)
            if (actionManager.getAction(actionId) != null) {
                return
            }
            val group = getActionGroup(actionConfig.position, actionManager) ?: return
            CustomCommandAction(actionConfig).apply {
                actionManager.registerAction(actionId, this)
                group.add(this)
            }
        }

        private fun removeAction(actionConfig: ActionConfig, actionManager: ActionManager) {
            val actionId = generateActionId(actionConfig)
            val action = actionManager.getAction(actionId) ?: return
//            ActionPosition.entries.forEach {
//                getActionGroup(it, actionManager)?.remove(action)
//            }
            getActionGroup(actionConfig.position, actionManager)?.remove(action)
            actionManager.unregisterAction(actionId)
        }

    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
    private fun isConfigValid(project: Project?): Boolean {
        return ActionConfigService.getConfigs(project).any { it.name == actionConfig.name }
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = when {
            event.project == null -> false
            actionConfig.name.isBlank() -> false
            !isConfigValid(event.project) -> false
            else -> actionConfig.enable
        }
        event.presentation.text = actionConfig.name.trim()
        if (actionConfig.position == ActionPosition.DEFAULT) {
            event.presentation.icon = if (actionConfig.onlyProject) AllIcons.Actions.Execute else AllIcons.Actions.Resume
        } else {
            event.presentation.icon = AvatarIcon(
                targetSize = Registry.intValue("ide.project.icon.size", 20),
                arcRatio = 0.4,
                gradientSeed = actionConfig.hashCode().toString(),
                avatarName = actionConfig.name,
                palette = ChangeProjectIconPalette(actionConfig.index)
            ).withIconPreScaled(false)
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let {
            RunToolWindowUtil.executeWithRealTimeOutput(it, actionConfig)
        }
    }


}

