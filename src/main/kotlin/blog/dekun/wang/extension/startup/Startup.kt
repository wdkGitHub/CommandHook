package blog.dekun.wang.extension.startup

import blog.dekun.wang.extension.services.ServiceUtils
import blog.dekun.wang.extension.services.UnregisterActionService
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
        ServiceUtils.setOneClickOpenFile(project)
        ServiceUtils.initCommandAction(project)
        UnregisterActionService.unregister()

    }
}