package blog.dekun.wang.command.hook.action.base

import blog.dekun.wang.command.hook.command.Command
import blog.dekun.wang.command.hook.constants.CommandType
import blog.dekun.wang.command.hook.constants.Constant
import blog.dekun.wang.command.hook.utils.Utils
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vcs.VcsDataKeys
import git4idea.repo.GitRepositoryManager

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


interface WelcomeScreen {


    fun enableVisible(event: AnActionEvent, visible: () -> Boolean) {
        if (event.place == Constant.WELCOME_SCREEN) {
            event.presentation.isEnabled = Utils.isRecentProjectItem(event)
            event.presentation.isVisible = Command.isSupport() && visible.invoke()
        } else {
            event.presentation.isEnabledAndVisible = Command.isSupport() && visible.invoke()
        }
    }

    fun execute(event: AnActionEvent, commandType: CommandType, action: (CommandType) -> Unit) {
        when (event.place) {
            "Vcs.Log.ContextMenu", "Vcs.Push.ContextMenu" -> getRepositoryDir(event)?.let { path -> Command.build().execute(path, commandType) }
            Constant.WELCOME_SCREEN -> Utils.getProjectPath(event)?.let { Command.build().execute(it, commandType) }
            else -> action.invoke(commandType)
        }
    }

    fun getRepositoryDir(event: AnActionEvent): String? {
        val project = event.project ?: return null
        val gitRepositoryManager = GitRepositoryManager.getInstance(project)

        val data = event.getData(VcsDataKeys.CHANGES_SELECTION)
        val firstFile = data?.list?.firstOrNull()?.afterRevision?.file ?: return null

        val gitRepository = gitRepositoryManager.getRepositoryForFile(firstFile)
        return gitRepository?.root?.path
    }

}

