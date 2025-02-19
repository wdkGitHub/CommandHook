package blog.dekun.wang.command.hook.ui

import blog.dekun.wang.command.hook.ui.components.ActionConfigListUI
import blog.dekun.wang.command.hook.ui.components.Tables
import blog.dekun.wang.command.hook.ui.components.TestMasterDetailsComponent
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.components.JBTabbedPane
import java.awt.BorderLayout
import java.awt.Dimension
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

    override fun getDisplayName(): @NlsContexts.ConfigurableName String? {
        return "Test"
    }

    override fun createComponent(): JComponent? {
        val tabbedPane = JBTabbedPane()
        tabbedPane.preferredSize = Dimension(800, 600)
        tabbedPane.addTab("Actions", JPanel(BorderLayout()).apply {
            border = null
            add(ActionConfigListUI().getComponent(), BorderLayout.CENTER)
        })
        tabbedPane.addTab("命令模板", Tables.commandTables().first)
        tabbedPane.addTab("参数模板", Tables.paramTables().first)
        tabbedPane.addTab("TEST", TestMasterDetailsComponent().createComponent())
        return tabbedPane
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun apply() {
    }


}




