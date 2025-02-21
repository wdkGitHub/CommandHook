package blog.dekun.wang.command.hook.ui

import blog.dekun.wang.command.hook.data.ActionConfig
import blog.dekun.wang.command.hook.data.ActionPosition
import blog.dekun.wang.command.hook.data.TemplateConfig
import blog.dekun.wang.command.hook.ui.components.ActionConfigDetail
import blog.dekun.wang.command.hook.ui.components.ActionConfigMasterDetailComponent
import blog.dekun.wang.command.hook.ui.components.Tables
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.components.JBTabbedPane
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.DefaultListModel
import javax.swing.JComponent
import javax.swing.JPanel


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class TestConfigurable(val project: Project) : Configurable {

    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
        return "Test"
    }

    private var listModel = DefaultListModel<ActionConfig>().apply {
        addElement(ActionConfig("动作1", false, true, "/wdk1", mutableMapOf("ppppp" to "1"), "sl", ActionPosition.DEFAULT))
        addElement(ActionConfig("动作2", true, false, "/wdk2", mutableMapOf("ppppp" to "2"), "git", ActionPosition.CENTRAL_TOOLBAR))
        addElement(ActionConfig("动作3", false, true, "/wdk3", mutableMapOf("ppppp" to "3"), "pwd", ActionPosition.RIGHT_CLICK))
    }
    private var paramTemplate: MutableList<TemplateConfig> = mutableListOf(
        TemplateConfig("参数(p=1)", "p=1", true), TemplateConfig("参数(p=2)", "p=2", true),
//        TemplateConfig("参数(p=3)", "p=3", true),
//        TemplateConfig("参数(p=4)", "p=4", true),
        TemplateConfig("参数(p=5)", "p=5 p=4", true)
    )

    private var commandTemplate: MutableList<TemplateConfig> = mutableListOf(
        TemplateConfig(
            "命令(where)",
            "cd\n" + "0.shell/\n" + "&&\n" + "./deployFile.sh\n" + "\"deployFile\"\n" + "\"cenyang@192.168.0.203\"\n" + "\"Ceny@ng666\"\n" + "\"middle-platform\"\n" + "\"auth-manager\"",
            true
        )
    )

    override fun createComponent(): JComponent {
        val tabbedPane = JBTabbedPane()
        tabbedPane.preferredSize = Dimension(800, 600)
        tabbedPane.addTab("Actions", JPanel(BorderLayout()).apply {
            add(ActionConfigMasterDetailComponent(listModel, paramTemplate, commandTemplate).getComponent(), BorderLayout.CENTER)
        })
        tabbedPane.addTab("命令模板", Tables.commandTables(commandTemplate) {
            ActionConfigDetail.refreshComboBoxModel(null, commandTemplate)
        })
        tabbedPane.addTab("参数模板", Tables.paramTables(paramTemplate) {
            ActionConfigDetail.refreshComboBoxModel(paramTemplate, null)
        })
        return tabbedPane

    }

    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {
    }


}




