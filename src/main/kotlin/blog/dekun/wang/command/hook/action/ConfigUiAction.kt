package blog.dekun.wang.command.hook.action

import blog.dekun.wang.command.hook.action.base.BaseAnAction
import blog.dekun.wang.command.hook.ui.ConfigConfigurable
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
        event.presentation.text = "命令配置"
    }

    override fun actionPerformed(event: AnActionEvent) {
        val configConfigurable = ConfigConfigurable()
        configConfigurable.project = event.project
        ShowSettingsUtil.getInstance().editConfigurable(event.project, configConfigurable)
    }
}