package blog.dekun.wang.command.hook.ui.components

import blog.dekun.wang.command.hook.data.ActionConfig
import blog.dekun.wang.command.hook.data.TemplateConfig
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.JBColor
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.util.PlatformIcons
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*


class ActionConfigMasterDetailComponent(project: Project, private val listModel: DefaultListModel<ActionConfig>, private var paramTemplate: MutableList<TemplateConfig>, private var commandTemplate: MutableList<TemplateConfig>) {

    private val masterList = JBList(listModel)

    private val detailsComponent = ActionConfigDetail.component(project)

    private val detailPanel = JPanel(BorderLayout()).apply {
        add(createEmptyStatePanel(), BorderLayout.CENTER)
    }

    private val splitPane = Splitter(false, 0.23f).apply {
        firstComponent = ToolbarDecorator.createDecorator(masterList).createPanel()
        secondComponent = detailPanel
        dividerWidth = 3
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
                override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus).apply {
                        (this as JLabel).apply {
                            text = (value as? ActionConfig)?.name ?: ""
                            foreground = if ((value as? ActionConfig)?.enable == true) JBColor.CYAN else JBColor.GRAY
                            icon = if ((value as? ActionConfig)?.onlyProject == true) AllIcons.Empty else AllIcons.Actions.Share
                        }
                    }
                }
            }
            emptyText.text = "Empty"
            selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION
        }

        val toolbarDecorator = ToolbarDecorator.createDecorator(masterList).apply {
            setAddAction {
                val newName = JOptionPane.showInputDialog(null, "名称", "添加Action", JOptionPane.PLAIN_MESSAGE)
                if (newName.isNullOrBlank()) {
                    return@setAddAction
                }
                // 验证是否有重复的 Action 名称
                val isDuplicate = listModel.elements().toList().any { it?.name == newName }
                if (isDuplicate) {
                    JOptionPane.showMessageDialog(null, "重复的名称，请重新输入.", "", JOptionPane.PLAIN_MESSAGE)
                    return@setAddAction
                }
                val newConfig = ActionConfig(name = newName, commandStr = "")
                listModel.addElement(newConfig)
                masterList.selectedIndex = listModel.size - 1
            }.setEditAction {
                selectedConfig?.let {
                    val newName = JOptionPane.showInputDialog(null, "请输入名称：", "修改名称", JOptionPane.PLAIN_MESSAGE, null, null, it.name) as? String ?: ""
                    if (newName.isNotBlank()) {
                        it.name = newName
                        masterList.repaint()
                    }

                }
            }.setRemoveAction {
                masterList.selectedIndex.takeIf { it != -1 }?.also {
                    listModel.remove(it)
                    masterList.selectedIndex = 0.coerceAtLeast(it - 1)
                }
            }
            addExtraAction(object : AnAction("Copy") {
                init {
                    // 定义快捷键
                    val keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.META_DOWN_MASK)
                    val shortcutSet = CustomShortcutSet(keyStroke)
                    registerCustomShortcutSet(shortcutSet, null)
                }

                override fun getActionUpdateThread() = ActionUpdateThread.BGT

                override fun actionPerformed(e: AnActionEvent) {
                    selectedConfig?.let {
                        val newConfig: ActionConfig = it.copy()
                        newConfig.index = masterList.selectedIndex + 1
                        newConfig.name += " (copy)"
                        listModel.add(masterList.selectedIndex + 1, newConfig)
                        masterList.selectedIndex += 1
                    }
                }

                override fun update(e: AnActionEvent) {
                    e.presentation.icon = PlatformIcons.COPY_ICON
                    e.presentation.isEnabled = selectedConfig != null
                }
            })
        }
        splitPane.firstComponent = toolbarDecorator.createPanel()
    }


    private fun setupListSelectionListener() {
        masterList.apply {

            var lastSelectedIndex = -1
            addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    if (listModel.isEmpty) {
                        masterList.selectedIndex = -1
                        lastSelectedIndex = -1
                        return
                    }
                    val y = e.y
                    val index = masterList.locationToIndex(e.point)
                    val itemHeight = masterList.getCellBounds(0, 0).height
                    /// 如果点击的位置超出了列表项的区域，增加一个偏移量，以允许用户点击列表项的底部区域。
                    val isClickOutside = index == -1 || y < 0 || y > itemHeight * (listModel.size + 1)
                    if (isClickOutside) {
                        masterList.selectedIndex = if (lastSelectedIndex == -1) 0 else lastSelectedIndex
                        lastSelectedIndex = masterList.selectedIndex
                    }
                }
            })

            addListSelectionListener { e ->
                if (!e.valueIsAdjusting) {
                    lastSelectedIndex = if (selectedIndex != -1 && model.size > 0) selectedIndex else -1
                    detailPanel.removeAll()
                    selectedConfig?.let {
                        ActionConfigDetail.updateData(it, paramTemplate, commandTemplate)
                        detailPanel.add(detailsComponent, BorderLayout.CENTER)
                    } ?: run {
                        detailPanel.add(createEmptyStatePanel(), BorderLayout.CENTER)
                    }
                    detailPanel.revalidate()
                    detailPanel.repaint()
                }
            }
        }
    }

    private fun createEmptyStatePanel(): JComponent {
        return JPanel(BorderLayout()).apply {
            add(JLabel("Empty", SwingConstants.CENTER), BorderLayout.CENTER)
        }
    }

}
