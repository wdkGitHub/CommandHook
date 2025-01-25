package blog.dekun.wang.command.hook.utils

import blog.dekun.wang.command.hook.command.Command
import blog.dekun.wang.command.hook.constants.Constant
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class Utils {

    companion object {

        private const val RECENT_PROJECT_SELECTED_ITEM = "RECENT_PROJECT_SELECTED_ITEM"


        /**
         * ProjectsGroupItem
         * RecentProjectItem
         * @see com.intellij.openapi.wm.impl.welcomeScreen.projectActions.RecentProjectsWelcomeScreenActionBase
         */
        fun isRecentProjectItem(event: AnActionEvent): Boolean {
            val recentProjectSelectedItem = event.getData(DataKey.create<Any>(RECENT_PROJECT_SELECTED_ITEM))
            return recentProjectSelectedItem != null && recentProjectSelectedItem::class.java.simpleName == "RecentProjectItem"
        }

        fun getProjectPath(event: AnActionEvent): String? {
            val recentProjectSelectedItem = event.getData(DataKey.create<Any>(RECENT_PROJECT_SELECTED_ITEM)) ?: return null
            val projectPathField = recentProjectSelectedItem::class.java.getField("projectPath")
            return projectPathField[recentProjectSelectedItem].toString()
        }

        /**
         * 判断当前选中的文件是否是markdown文件
         */
        fun checkMarkdownFile(event: AnActionEvent): Boolean {
            val psiFile = event.getData(PlatformDataKeys.PSI_FILE) ?: return false
            return !psiFile.isDirectory && psiFile.name.endsWith(".md")
        }

//        fun getPsiFile(event: AnActionEvent): PsiFile? {
//            return event.getData(PlatformDataKeys.PSI_FILE)
//        }


        fun getVirtualFile(event: AnActionEvent): VirtualFile? {
            return event.getData(PlatformDataKeys.VIRTUAL_FILE)
        }

        /**
         * 获取git仓库根目录
         */
        fun getGitRepoRootPath(event: AnActionEvent): String? {
            val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return null
            if (virtualFile.fileSystem.protocol != "file") {
                println("Not a file system path: ${virtualFile.fileSystem.protocol} ${virtualFile.path}")
                return null
            }
            val path: String = if (virtualFile.isDirectory) {
                virtualFile.path
            } else {
                virtualFile.path.replace(virtualFile.name, "")
            }
            return Command.gitRevParseShowTopLevel(path)
        }


        /**
         * 判断是否是git仓库
         */
        fun isGitRepo(gitRepoRootPath: String?): Boolean {
            return gitRepoRootPath?.let { !it.contains("fatal") } ?: false
        }


        /**
         * 判断git仓库是否关联远程仓库
         */
        fun hasGitRemote(gitRepoRootPath: String?): Boolean {
            return gitRepoRootPath?.let {
                val result = Command.gitRemote(gitRepoRootPath)
                return result.trim().isNotEmpty() && !result.contains("fatal")
            } ?: false
        }


        fun showNotification(project: Project?, title: String, content: String, type: NotificationType) {
            NotificationGroupManager.getInstance().getNotificationGroup(Constant.NOTIFICATION_GROUP_ID)
                .createNotification(title, content, type).notify(project)
        }

        fun showNotificationAnActionId(project: Project?, title: String, content: String) {
            NotificationGroupManager.getInstance().getNotificationGroup(Constant.NOTIFICATION_GROUP_AN_ACTION_ID)
                .createNotification(title, content, NotificationType.INFORMATION)
                .notify(project)
        }
    }
}