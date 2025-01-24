package blog.dekun.wang.command.hook.action.base

import blog.dekun.wang.command.hook.command.Command
import blog.dekun.wang.command.hook.constants.CommandType
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
        if (event.place == blog.dekun.wang.command.hook.constants.Constant.WELCOME_SCREEN) {
            event.presentation.isEnabled = blog.dekun.wang.command.hook.utils.Utils.isRecentProjectItem(event)
            event.presentation.isVisible = Command.isSupport() && visible.invoke()
        } else {
            event.presentation.isEnabledAndVisible = Command.isSupport() && visible.invoke()
        }
    }

    fun execute(event: AnActionEvent, commandType: CommandType, action: (CommandType) -> Unit) {
        when (event.place) {
            blog.dekun.wang.command.hook.constants.Constant.WELCOME_SCREEN -> blog.dekun.wang.command.hook.utils.Utils.getProjectPath(event)
                ?.let { Command.build().execute(it, commandType) }
            else -> action.invoke(commandType)
        }
    }


}

