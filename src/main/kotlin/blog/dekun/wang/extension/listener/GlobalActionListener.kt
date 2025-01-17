package blog.dekun.wang.extension.listener

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
        val actionId = ActionManager.getInstance().getId(action)
        val actionName = action.templatePresentation.text
        println("${action.javaClass.name} Action beforeActionPerformed: ID = $actionId, Name = $actionName")
    }

    override fun afterActionPerformed(action: AnAction, event: AnActionEvent, result: AnActionResult) {
        val actionId = ActionManager.getInstance().getId(action)
        val actionName = action.templatePresentation.text
        println("${action.javaClass.name} Action afterActionPerformed: ID = $actionId, Name = $actionName")
    }

    override fun beforeEditorTyping(c: Char, dataContext: DataContext) {
        println("beforeEditorTyping")
    }

    override fun afterEditorTyping(c: Char, dataContext: DataContext) {
        println("afterEditorTyping")
    }

    override fun beforeShortcutTriggered(shortcut: Shortcut, actions: MutableList<AnAction>, dataContext: DataContext) {
        println("beforeShortcutTriggered")
    }
}