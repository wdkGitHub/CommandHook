package blog.dekun.wang.command.hook.listener

import blog.dekun.wang.command.hook.services.UnregisterActionService
import blog.dekun.wang.command.hook.utils.Utils
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
            if ("EditorCopy" == actionId) {
                return
            }
            val actionName = action.templatePresentation.text
            val groupId = getGroupId(actionName)
            val message = """
            ID = $actionId <br>
            Name = $actionName <br>
            GroupId = $groupId <br>
            Class = ${action.javaClass.name}
            """.trimIndent()
            Utils.showNotificationAnActionId(event.project, "AnActionIdNotificationGroup", message)
        }
    }

    fun getGroupId(actionText: String?): List<String> {
        val mutableListOf = mutableListOf<String>()
        actionText?.let {
            ActionManager.getInstance().getActionIdList("").forEach { id ->
                ActionManager.getInstance().getAction(id)?.let { action ->
                    if (action is DefaultActionGroup) {
                        action.getChildren(null).forEach {
                            it.templatePresentation.text?.let { text ->
                                if (text == actionText) {
                                    mutableListOf.add(id)
                                }
                            }
                        }
                    }
                }
            }
        }
        return mutableListOf
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