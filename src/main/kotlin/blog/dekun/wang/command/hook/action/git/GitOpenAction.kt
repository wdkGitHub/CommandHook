package blog.dekun.wang.command.hook.action.git

import blog.dekun.wang.command.hook.action.base.GitAnAction
import blog.dekun.wang.command.hook.constants.CommandType
import com.intellij.openapi.actionSystem.AnActionEvent


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class GitOpenAction : GitAnAction() {

    override fun update(event: AnActionEvent) {
        if (event.place == blog.dekun.wang.command.hook.constants.Constant.WELCOME_SCREEN) {
            event.presentation.text = "Git-Open"
        } else {
            event.presentation.text = "Git-Open"
        }
        setEnableVisible(event, true)
    }

    override fun actionPerformed(event: AnActionEvent) {
        executeGitCommand(event, CommandType.GIT_OPEN_COMMAND)
    }
}