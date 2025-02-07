package blog.dekun.wang.command.hook.action.base

import blog.dekun.wang.command.hook.command.Command
import blog.dekun.wang.command.hook.constants.CommandType
import blog.dekun.wang.command.hook.constants.Constant
import blog.dekun.wang.command.hook.utils.Utils
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
            println(event.place)
            val gitRepoRootPath = if (event.place == Constant.WELCOME_SCREEN) {
                Utils.getProjectPath(event)
            } else if (event.place == "Vcs.Push.ContextMenu" || event.place == "Vcs.Log.ContextMenu") {
                getRepositoryDir(event)
            } else {
                Utils.getGitRepoRootPath(event)
            }
            Utils.isGitRepo(gitRepoRootPath) && (!checkRemote || Utils.hasGitRemote(gitRepoRootPath))
        }
    }


    fun executeGitCommand(event: AnActionEvent, commandType: CommandType) {
        execute(event, commandType) {
            if (event.place == "Vcs.Push.ContextMenu" || event.place == "Vcs.Log.ContextMenu") {
                getRepositoryDir(event)?.let { path -> Command.build().execute(path, commandType) }
            } else {
                Utils.getGitRepoRootPath(event)?.let { Command.build().execute(it, commandType) }
            }
        }
    }
}
