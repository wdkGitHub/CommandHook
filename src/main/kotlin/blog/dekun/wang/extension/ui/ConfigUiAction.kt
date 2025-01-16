package blog.dekun.wang.extension.ui

import blog.dekun.wang.extension.action.base.BaseAnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class ConfigUiAction : BaseAnAction() {

    override fun update(event: AnActionEvent) {
        event.presentation.text = "配置"
    }

    override fun actionPerformed(event: AnActionEvent) {

        val configConfigurable = ConfigConfigurable()
        configConfigurable.project = event.project
        // JOptionPane.showMessageDialog(frame, "这是一个自定义的弹窗内容！");
        ShowSettingsUtil.getInstance().editConfigurable(event.project, configConfigurable) {
            println("Display Name: ${configConfigurable.displayName}") // 动态获取 displayName
            println("回调")
        }
    }
}