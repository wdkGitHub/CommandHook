package blog.dekun.wang.command.hook.action.base

import blog.dekun.wang.command.hook.command.Command
import blog.dekun.wang.command.hook.constants.CommandType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


abstract class GitAnAction : WelcomeScreen, BaseAnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
    fun setEnableVisible(event: AnActionEvent, checkRemote: Boolean = false) {
        super.enableVisible(event) {
            val gitRepoRootPath = if (event.place == blog.dekun.wang.command.hook.constants.Constant.WELCOME_SCREEN) {
                blog.dekun.wang.command.hook.utils.Utils.getProjectPath(event)
            } else {
                blog.dekun.wang.command.hook.utils.Utils.getGitRepoRootPath(event)
            }
            blog.dekun.wang.command.hook.utils.Utils.isGitRepo(gitRepoRootPath) && (!checkRemote || blog.dekun.wang.command.hook.utils.Utils.hasGitRemote(gitRepoRootPath))
        }
    }

    fun executeGitCommand(event: AnActionEvent, commandType: CommandType) {
        execute(event, commandType) {
            blog.dekun.wang.command.hook.utils.Utils.getGitRepoRootPath(event)?.let { Command.build().execute(it, commandType) }
        }
    }
}