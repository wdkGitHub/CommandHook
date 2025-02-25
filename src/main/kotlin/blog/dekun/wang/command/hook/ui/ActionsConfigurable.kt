package blog.dekun.wang.command.hook.ui

import blog.dekun.wang.command.hook.action.CustomCommandAction
import blog.dekun.wang.command.hook.data.ActionConfig
import blog.dekun.wang.command.hook.data.TemplateConfig
import blog.dekun.wang.command.hook.services.ActionConfigService
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


class ActionsConfigurable(val project: Project) : Configurable {

    private val defaultListModel = DefaultListModel<ActionConfig>()

    private var paramTemplate: MutableList<TemplateConfig> = mutableListOf()

    private var commandTemplate: MutableList<TemplateConfig> = mutableListOf()
    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
        return "Actions配置"
    }

    override fun createComponent(): JComponent {
        ActionConfigService.getParamTemplates(project).forEach { paramTemplate.add(it.copy()) }
        ActionConfigService.getCommandTemplates(project).forEach { commandTemplate.add(it.copy()) }
        for (actionConfig in ActionConfigService.getConfigs(project)) {
            defaultListModel.addElement(actionConfig.copy())
        }
        return JBTabbedPane().apply {
            preferredSize = Dimension(900, 600)
            addTab("Actions", JPanel(BorderLayout()).apply {
                add(ActionConfigMasterDetailComponent(defaultListModel, paramTemplate, commandTemplate).getComponent(), BorderLayout.CENTER)
            })
            addTab("命令模板", Tables.commandTables(commandTemplate) {
                ActionConfigDetail.refreshComboBoxModel(null, commandTemplate)
            })
            addTab("参数模板", Tables.paramTables(paramTemplate) {
                ActionConfigDetail.refreshComboBoxModel(paramTemplate, null)
            })
        }

    }

    override fun isModified(): Boolean {
        ActionConfigDetail.refreshParamTable()
        val actionConfigList: MutableList<ActionConfig> = mutableListOf()
        for (i in 0 until defaultListModel.size) {
            actionConfigList.add(defaultListModel.get(i))
        }
        return isDataModified(ActionConfigService.getConfigs(project), actionConfigList) ||
                isDataModified(ActionConfigService.getParamTemplates(project), paramTemplate) ||
                isDataModified(ActionConfigService.getCommandTemplates(project), commandTemplate)
    }

    private fun <T> isDataModified(currentData: List<T>, newData: List<T>): Boolean {
        if (currentData.size != newData.size) return true
        return currentData.zip(newData).any { it.first != it.second }
    }

    override fun apply() {
        val configs = ActionConfigService.getConfigs(project)
        val actionConfigList = (0 until defaultListModel.size).map { defaultListModel.get(it) }
        val addActionConfigs = actionConfigList.filterNot { configs.contains(it) }
        val removeActionConfigs = configs.filterNot { actionConfigList.contains(it) }
        CustomCommandAction.modifyAction(addActionConfigs, removeActionConfigs)
        ActionConfigService.saveConfigs(project, actionConfigList)
        ActionConfigService.saveParamTemplates(project, paramTemplate)
        ActionConfigService.saveCommandTemplates(project, commandTemplate)
        clear()
    }

    private fun clear() {
        defaultListModel.clear()
        paramTemplate.clear()
        commandTemplate.clear()
    }

}




