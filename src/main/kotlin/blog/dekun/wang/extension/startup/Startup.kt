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

//        val actionManager = ActionManager.getInstance()
//        val actionIdList = actionManager.getActionIdList("")
//        actionIdList.forEach { id ->
//            actionManager.getAction(id)?.let { action ->
//                if (action is DefaultActionGroup) {
//                    action.getChildren(null).forEach {
//                        if (it.javaClass.name.contains("ai.codegeex.plugin.actions")) {
////                                        println("actionId = ${it.javaClass.name}")
//                            //ai.codegeex.plugin.actions.ProjectMapTitleAction
//                            if (it.javaClass.name.contains("ai.codegeex.plugin.actions.ProjectMapTitleAction")) {
//                                action.remove(it)
//                            }
//                        }
//                    }
//                }
//            }
//        }