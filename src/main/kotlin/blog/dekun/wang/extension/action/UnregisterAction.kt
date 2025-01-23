package blog.dekun.wang.extension.action

import blog.dekun.wang.extension.action.base.BaseAnAction
import blog.dekun.wang.extension.ui.UnregisterActionConfigurable
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class UnregisterAction : BaseAnAction() {

    override fun update(event: AnActionEvent) {
        event.presentation.text = "注销AnAction"
    }

    override fun actionPerformed(event: AnActionEvent) {
        ShowSettingsUtil.getInstance().editConfigurable(event.project, UnregisterActionConfigurable(event.project))
    }
}