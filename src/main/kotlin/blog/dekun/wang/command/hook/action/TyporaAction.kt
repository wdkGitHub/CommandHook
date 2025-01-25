package blog.dekun.wang.command.hook.action

import blog.dekun.wang.command.hook.command.Command
import blog.dekun.wang.command.hook.constants.CommandType
import blog.dekun.wang.command.hook.constants.Constant
import blog.dekun.wang.command.hook.utils.Utils
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class TyporaAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
    override fun update(event: AnActionEvent) {
        if (event.place == Constant.WELCOME_SCREEN) {
            event.presentation.text = "Reveal In Typora"
        } else {
            event.presentation.text = "Reveal In Typora"
        }
        event.presentation.isEnabledAndVisible = Utils.checkMarkdownFile(event)
    }

    override fun actionPerformed(event: AnActionEvent) {
        Utils.getVirtualFile(event)?.path?.let { Command.build().execute(it, CommandType.TYPORA_APP) }
    }
}
