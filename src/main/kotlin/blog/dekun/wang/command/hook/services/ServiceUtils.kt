package blog.dekun.wang.command.hook.services

import blog.dekun.wang.command.hook.action.CustomCommandAction
import blog.dekun.wang.command.hook.constants.Constant
import com.intellij.ide.projectView.impl.ProjectViewState
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.updateSettings.impl.UpdateSettings


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class ServiceUtils {

    companion object {

        fun initCommandAction(project: Project?) {
            CustomCommandAction.modifyAction(configsToAdd = ActionConfigService.getConfigs(project))
        }

        /**
         *  行为: 一键打开文件
         *  行为: 一键打开目录
         *  @see com.intellij.ide.projectView.impl.ProjectViewState
         */
        fun setOneClickOpenFile(project: Project) {
            val projectViewStateService = project.getService(ProjectViewState::class.java)
            val state = projectViewStateService.state
            if (!state.autoscrollToSource || !state.openDirectoriesWithSingleClick) {
                projectViewStateService.autoscrollToSource = true
                projectViewStateService.openDirectoriesWithSingleClick = true
                projectViewStateService.state
            }
        }
        fun setUpdateRepositoryAddress() {
            ApplicationManager.getApplication().getService(UpdateSettings::class.java).state.pluginHosts
                .takeIf { !it.contains(Constant.PLUGIN_HOSTS) }?.add(Constant.PLUGIN_HOSTS)
        }
    }
}