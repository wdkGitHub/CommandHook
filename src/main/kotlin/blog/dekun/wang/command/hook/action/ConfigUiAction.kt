package blog.dekun.wang.command.hook.action

import blog.dekun.wang.command.hook.action.base.BaseAnAction
import blog.dekun.wang.command.hook.ui.ActionsConfigurable
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class ConfigUiAction : BaseAnAction() {

    override fun update(event: AnActionEvent) {
        event.project ?: { event.presentation.isEnabledAndVisible = false }
        event.presentation.text = "Actions配置"
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        ShowSettingsUtil.getInstance().editConfigurable(project, ActionsConfigurable(project))
    }
}