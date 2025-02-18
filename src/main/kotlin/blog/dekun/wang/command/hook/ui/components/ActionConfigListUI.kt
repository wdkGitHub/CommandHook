package blog.dekun.wang.command.hook.ui.components

import blog.dekun.wang.command.hook.data.ActionConfig
import blog.dekun.wang.command.hook.data.ActionPosition
import blog.dekun.wang.command.hook.data.TemplateConfig
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.util.PlatformIcons
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.*


class ActionConfigListUI {

    val listModel = DefaultListModel<ActionConfig>().apply {
        addElement(ActionConfig("动作1", false, true, "/wdk1", listOf("ppppp=1"), "sl", ActionPosition.DEFAULT))
        addElement(ActionConfig("动作2", true, false, "/wdk2", listOf("ppppp=2"), "git", ActionPosition.CENTRAL_TOOLBAR))
        addElement(ActionConfig("动作3", false, true, "/wdk3", listOf("ppppp=3"), "pwd", ActionPosition.RIGHT_CLICK))
    }
    val paramTemplate = listOf(TemplateConfig("参数(p=1)", "p=1", true))

    val commandTemplate = listOf(TemplateConfig("命令(where)", "where", true))

    private val masterList = JBList(listModel)

    private val detailPanel = JPanel(BorderLayout()).apply {
        add(createEmptyStatePanel(), BorderLayout.CENTER)
    }
    private val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT).apply {
        isContinuousLayout = true
        dividerSize = 1
        resizeWeight = 0.1
        rightComponent = detailPanel
        border = null
    }


    private val selectedConfig: ActionConfig?
        get() = masterList.selectedValue

    fun getComponent(): JComponent = splitPane

    init {
        createMasterPanel()
        setupListSelectionListener()
    }

    private fun createMasterPanel() {
        masterList.apply {
            cellRenderer = object : DefaultListCellRenderer() {
                override fun getListCellRendererComponent(
                    list: JList<*>?,
                    value: Any?,
                    index: Int,
                    isSelected: Boolean,
                    cellHasFocus: Boolean
                ): Component {
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus).apply {
                        (this as JLabel).apply {
                            text = (value as? ActionConfig)?.name ?: ""
                            icon = if ((value as? ActionConfig)?.enable == true)
                                AllIcons.Actions.Execute else AllIcons.Actions.Suspend
                        }
                    }
                }
            }
            emptyText.text = "无命令"
            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        }

        val toolbarDecorator = ToolbarDecorator.createDecorator(masterList).apply {
            setAddAction {
                val newName = JOptionPane.showInputDialog("New Action Name:")
                newName?.takeIf { it.isNotBlank() }?.let {
                    val newConfig = ActionConfig(it, "")
                    listModel.addElement(newConfig)
                    masterList.selectedIndex = masterList.selectedIndex + 1
                }
            }.setEditAction {
                selectedConfig?.let {
                    val newName = JOptionPane.showInputDialog(null, "请输入名称：", "添加命令", JOptionPane.PLAIN_MESSAGE, null, null, it.name) as String
                    if (newName.isNotBlank()) {
                        it.name = newName
                        masterList.repaint()
                    }

                }
            }
            addExtraAction(object : AnAction("Copy") {
                override fun getActionUpdateThread() = ActionUpdateThread.BGT

                override fun actionPerformed(e: AnActionEvent) {
                    selectedConfig?.let {
                        val newConfig = it.copy(name = "${it.name} Copy")
                        listModel.addElement(newConfig)
                        masterList.selectedIndex = masterList.selectedIndex + 1
                    }
                }

                override fun update(e: AnActionEvent) {
                    e.presentation.icon = PlatformIcons.COPY_ICON;
                    e.presentation.isEnabled = selectedConfig != null
                }
            })
        }
        splitPane.leftComponent = toolbarDecorator.createPanel().apply {
            border = null

        }
    }


    private fun setupListSelectionListener() {
        masterList.addListSelectionListener { e ->
            if (!e.valueIsAdjusting) {
                detailPanel.removeAll()
                selectedConfig?.let {
                    val configUI = CommandActionConfigUI.component().apply {
                        CommandActionConfigUI.updateData(
                            it,
                            DefaultComboBoxModel(paramTemplate.toTypedArray()),
                            DefaultComboBoxModel(commandTemplate.toTypedArray())
                        )
                    }
                    detailPanel.add(configUI, BorderLayout.CENTER)
                } ?: run {
                    detailPanel.add(createEmptyStatePanel(), BorderLayout.CENTER)
                }
                detailPanel.revalidate()
                detailPanel.repaint()
            }
        }
    }

    private fun createEmptyStatePanel(): JComponent {
        return JPanel(BorderLayout()).apply {
            add(JLabel("请选择", SwingConstants.CENTER), BorderLayout.CENTER)
        }
    }

}
