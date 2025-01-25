package blog.dekun.wang.command.hook.action.base

import blog.dekun.wang.command.hook.command.Command
import blog.dekun.wang.command.hook.constants.CommandType
import blog.dekun.wang.command.hook.constants.Constant
import blog.dekun.wang.command.hook.utils.Utils
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

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
            "Vcs.Push.ContextMenu" -> event.getData(CommonDataKeys.PROJECT)?.basePath?.let { path -> Command.build().execute(path, commandType) }
            Constant.WELCOME_SCREEN -> Utils.getProjectPath(event)?.let { Command.build().execute(it, commandType) }
            else -> action.invoke(commandType)
        }
    }


}

