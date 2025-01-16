package blog.dekun.wang.extension.action.base

import blog.dekun.wang.extension.command.Command
import blog.dekun.wang.extension.constants.CommandType
import blog.dekun.wang.extension.constants.Constant
import blog.dekun.wang.extension.utils.Utils
import com.intellij.openapi.actionSystem.AnActionEvent

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
            Constant.WELCOME_SCREEN -> Utils.getProjectPath(event)?.let { Command.build().execute(it, commandType) }
            else -> action.invoke(commandType)
        }
    }


}

