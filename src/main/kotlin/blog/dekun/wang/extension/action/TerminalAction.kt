package blog.dekun.wang.extension.action

import blog.dekun.wang.extension.action.base.BaseAnAction
import blog.dekun.wang.extension.action.base.WelcomeScreen
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


class TerminalAction : BaseAnAction(), WelcomeScreen {

    override fun update(event: AnActionEvent) {
        enableVisible(event) { true }
        if (event.place == Constant.WELCOME_SCREEN) {
            event.presentation.text = "Reveal In Terminal"
        } else {
            event.presentation.text = "Reveal In Terminal"
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        execute(event, CommandType.TERMINAL_APP) {
            Utils.getVirtualFile(event)?.let { virtualFile ->
                val path = virtualFile.takeIf { it.isDirectory }?.path
                    ?: virtualFile.parent?.path
                    ?: event.project?.basePath
                path?.let {
                    Command.build().execute(it, CommandType.TERMINAL_APP)
                }
            }
        }
    }

}


