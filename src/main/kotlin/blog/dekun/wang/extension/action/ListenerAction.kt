package blog.dekun.wang.extension.action

import blog.dekun.wang.extension.action.base.BaseAnAction
import blog.dekun.wang.extension.services.UnregisterActionService
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
            event.presentation.text = "监控AnAction(已开启)"
        }
    }


    override fun actionPerformed(event: AnActionEvent) {
        UnregisterActionService.toggleNotify()
    }
}