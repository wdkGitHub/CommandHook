package blog.dekun.wang.command.hook.action

import blog.dekun.wang.command.hook.action.base.BaseAnAction
import blog.dekun.wang.command.hook.services.UnregisterActionService
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class ListenerAction : BaseAnAction() {

    override fun update(event: AnActionEvent) {
        event.presentation.text = "监控AnAction"
        if (UnregisterActionService.isEnableNotify()) {
            event.presentation.icon = AllIcons.General.InspectionsOKEmpty
        } else {
            event.presentation.icon = AllIcons.Actions.Close
        }
    }


    override fun actionPerformed(event: AnActionEvent) {
        UnregisterActionService.toggleNotify()
    }
}