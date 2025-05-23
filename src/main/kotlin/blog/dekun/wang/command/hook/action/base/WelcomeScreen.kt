package blog.dekun.wang.command.hook.action.base

import blog.dekun.wang.command.hook.command.Command
import blog.dekun.wang.command.hook.constants.CommandType
import blog.dekun.wang.command.hook.constants.Constant
import blog.dekun.wang.command.hook.utils.Utils
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.vcs.VcsDataKeys
import git4idea.repo.GitRepositoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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
        } else if (event.place == "Vcs.Log.ContextMenu" || event.place == "Vcs.Push.ContextMenu") {
            getRepositoryDir(event)?.let {
                event.presentation.isEnabledAndVisible = Command.isSupport() && visible.invoke()
            } ?: run {
                event.presentation.isEnabledAndVisible = false
            }
        } else {
            event.presentation.isEnabledAndVisible = Command.isSupport() && visible.invoke()
        }
    }

    fun execute(event: AnActionEvent, commandType: CommandType, action: (CommandType) -> Unit) {
        when (event.place) {
            "Vcs.Log.ContextMenu", "Vcs.Push.ContextMenu" -> {
                ApplicationManager.getApplication().executeOnPooledThread {
                    getRepositoryDir(event)?.let { path ->
                        Command.build().execute(path, commandType)
                    }
                }
            }

            Constant.WELCOME_SCREEN -> {
                ApplicationManager.getApplication().executeOnPooledThread {
                    Utils.getProjectPath(event)?.let {
                        Command.build().execute(it, commandType)
                    }
                }
            }

            else -> action.invoke(commandType)
        }
    }

    fun getRepositoryDir(event: AnActionEvent): String? {
        val project = event.project ?: return null
        val gitRepositoryManager = GitRepositoryManager.getInstance(project)

        val data = event.getData(VcsDataKeys.CHANGES_SELECTION)
        val firstFile = data?.list?.firstOrNull()?.afterRevision?.file ?: return null
        return runBlocking { // 确保在非 UI 线程（EDT）获取仓库路径
            withContext(Dispatchers.IO) {
                ReadAction.compute<String?, RuntimeException> {
                    gitRepositoryManager.getRepositoryForFile(firstFile)?.root?.path
                }
            }
        }
    }

    fun getRepositoryDirByVirtualFile(event: AnActionEvent): String? {
        val project = event.project ?: return null
        val gitRepositoryManager = GitRepositoryManager.getInstance(project)
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)
        return runBlocking {
            withContext(Dispatchers.IO) {
                ReadAction.compute<String?, RuntimeException> {
                    gitRepositoryManager.getRepositoryForFile(virtualFile)?.root?.path
                }
            }
        }
    }

}


