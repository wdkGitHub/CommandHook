package blog.dekun.wang.extension.action.git

import blog.dekun.wang.extension.action.base.GitAnAction
import blog.dekun.wang.extension.constants.CommandType
import blog.dekun.wang.extension.constants.Constant
import com.intellij.openapi.actionSystem.AnActionEvent


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class SourcetreeAction : GitAnAction() {

    override fun update(event: AnActionEvent) {
        if (event.place == Constant.WELCOME_SCREEN) {
            event.presentation.text = "Reveal In SourceTree"
        } else {
            event.presentation.text = "Reveal In SourceTree"
        }
        setEnableVisible(event, false)
    }

    override fun actionPerformed(event: AnActionEvent) {
        executeGitCommand(event, CommandType.SOURCE_TREE_APP)
    }
}