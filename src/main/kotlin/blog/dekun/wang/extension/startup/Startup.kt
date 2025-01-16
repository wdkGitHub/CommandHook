package blog.dekun.wang.extension.startup

import blog.dekun.wang.extension.action.CustomCommandAction
import blog.dekun.wang.extension.services.WorkspaceConfigService
import blog.dekun.wang.extension.utils.Utils
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class Startup : ProjectActivity {

    override suspend fun execute(project: Project) {
        Utils.setOneClickOpenFile(project)
        CustomCommandAction.modifyAction(add = WorkspaceConfigService.getInstance(project).state.commands)
    }
}