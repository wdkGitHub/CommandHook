package blog.dekun.wang.extension.action.base

import blog.dekun.wang.extension.command.Command
import blog.dekun.wang.extension.constants.CommandType
import blog.dekun.wang.extension.constants.Constant
import blog.dekun.wang.extension.utils.Utils
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
            val gitRepoRootPath = if (event.place == Constant.WELCOME_SCREEN) {
                Utils.getProjectPath(event)
            } else {
                Utils.getGitRepoRootPath(event)
            }
            Utils.isGitRepo(gitRepoRootPath) && (!checkRemote || Utils.hasGitRemote(gitRepoRootPath))
        }
    }

    fun executeGitCommand(event: AnActionEvent, commandType: CommandType) {
        execute(event, commandType) {
            Utils.getGitRepoRootPath(event)?.let { Command.build().execute(it, commandType) }
        }
    }
}