package blog.dekun.wang.command.hook.listener

import blog.dekun.wang.command.hook.services.UnregisterActionService
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.AnActionListener


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class GlobalActionListener : AnActionListener {

    override fun beforeActionPerformed(action: AnAction, event: AnActionEvent) {
        if (UnregisterActionService.isEnableNotify() && !action.javaClass.name.contains("dekun.wang")) {
            val actionId = ActionManager.getInstance().getId(action)
            val actionName = action.templatePresentation.text
            val message = """
            ID = $actionId <br>
            Name = $actionName <br>
            Class = ${action.javaClass.name}
            """.trimIndent()
            blog.dekun.wang.command.hook.utils.Utils.showNotificationAnActionId("AnActionIdNotificationGroup", message)
        }
    }

    override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
    }

    override fun beforeEditorTyping(c: Char, dataContext: DataContext) {
    }

    override fun afterEditorTyping(c: Char, dataContext: DataContext) {
    }

    override fun beforeShortcutTriggered(shortcut: Shortcut, actions: MutableList<AnAction>, dataContext: DataContext) {
    }
}