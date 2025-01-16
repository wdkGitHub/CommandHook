package blog.dekun.wang.extension.ui

import blog.dekun.wang.extension.action.CustomCommandAction
import blog.dekun.wang.extension.data.ConfigInfo
import blog.dekun.wang.extension.services.WorkspaceConfigService
import blog.dekun.wang.extension.utils.Utils
import com.intellij.notification.NotificationType
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.util.ui.FormBuilder
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.*

class ConfigConfigurable : Configurable {

    private val mainPanel = JPanel(BorderLayout())

    private val leftItemList = DefaultListModel<ConfigInfo>()

    private val leftList = JBList(leftItemList).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = ListCellRenderer { list, value, index, isSelected, cellHasFocus ->
            JLabel(value.name).apply {
                isOpaque = true
                background = if (isSelected) list.selectionBackground else list.background
                foreground = if (isSelected) list.selectionForeground else list.foreground
            }
        }
    }

    private var _project: Project? = ProjectManager.getInstance().openProjects.firstOrNull()

    var project: Project?
        get() = _project
        set(value) {
            _project = value
            loadPersistedData()
        }


    private fun loadPersistedData() {
        val project = project
        if (project != null) {
            val service = WorkspaceConfigService.getInstance(project)
            leftItemList.clear()
            service.state.commands.forEach { leftItemList.addElement(it.copy()) }
        }
    }

    override fun createComponent(): JComponent {
        loadPersistedData()


        val updateRightClickVisibility: (rightDetail: RightDetail) -> Unit = { rightDetail ->
            rightDetail.isRightClick.isEnabled = rightDetail.isTargetFile.isSelected || rightDetail.isTargetDir.isSelected
            rightDetail.isRightClick.isVisible = rightDetail.isTargetFile.isSelected || rightDetail.isTargetDir.isSelected
        }

        val rightPanel = JPanel(CardLayout(10, 10)).apply {
            add(JPanel(BorderLayout()).apply { add(JLabel("请选择或添加命令", JLabel.CENTER), BorderLayout.CENTER) }, "placeholder")
        }

        fun updateDetailPanel(selectedIndex: Int) {
            val cardLayout = rightPanel.layout as CardLayout
            if (selectedIndex == -1) {
                cardLayout.show(rightPanel, "placeholder")
            } else {
                val configInfo = leftItemList[selectedIndex]
                val rightDetail = RightDetail().apply {
                    nameField.text = configInfo.name
                    commandField.text = configInfo.commandStr
                    isTargetFile.isSelected = configInfo.isTargetFile ?: false
                    isTargetDir.isSelected = configInfo.isTargetFolder ?: false
                    isRightClick.isSelected = configInfo.isRightClick ?: false
                    isEnable.isSelected = configInfo.isEnable ?: true
                    isApp.isSelected = configInfo.isApp ?: false
                    addChangeListeners(onNameChanged = { value ->
                        configInfo.name = value
                    }, onCommandChanged = { value ->
                        configInfo.commandStr = value
                    }, onTargetFileChanged = { value ->
                        configInfo.isTargetFile = value
                        updateRightClickVisibility(this)
                    }, onTargetDirChanged = { value ->
                        configInfo.isTargetFolder = value
                        updateRightClickVisibility(this)
                    }, onRightClickChanged = { value ->
                        configInfo.isRightClick = value
                    }, onEnableChanged = { value ->
                        configInfo.isEnable = value
                    }, onAppChanged = { value ->
                        configInfo.isApp = value

                    })
                    configInfo.index = selectedIndex
                    updateRightClickVisibility(this)
                }

                val detailPanel = FormBuilder.createFormBuilder().addComponent(rightDetail.isApp).addLabeledComponent("名称:", rightDetail.nameField)
                    .addLabeledComponent("命令:", rightDetail.commandField).addComponent(rightDetail.isTargetFile).addComponent(rightDetail.isTargetDir)
                    .addComponent(rightDetail.isRightClick).addComponent(rightDetail.isEnable).panel

                val rightDetailPanel = JPanel(BorderLayout()).apply {
                    add(detailPanel, BorderLayout.NORTH)
                }

                // Replace existing panel with new one
                cardLayout.show(rightPanel, "detail")
                rightPanel.add(rightDetailPanel, "detail")
            }
        }

        val leftListOperationsPanel = ToolbarDecorator.createDecorator(leftList).setAddAction {
            //添加对话框
            val newName = JOptionPane.showInputDialog(mainPanel, "请输入名称：", "添加命令", JOptionPane.PLAIN_MESSAGE)
            if (!newName.isNullOrBlank()) {
                // 判断是否重复
                val isDuplicate = (0 until leftItemList.size).any { leftItemList[it].name == newName }
                if (isDuplicate) {
                    JOptionPane.showMessageDialog(mainPanel, "名称已存在，请输入唯一的名称！", "错误", JOptionPane.ERROR_MESSAGE)
                } else {
                    val newConfig = ConfigInfo(newName, leftItemList.size)
                    leftItemList.addElement(newConfig)
                    val newIndex = leftItemList.size - 1
                    leftList.setSelectedIndex(newIndex)
                    updateDetailPanel(newIndex)
                }
            }
        }.setRemoveAction {
            val selectedIndex = leftList.selectedIndex
            if (selectedIndex != -1) {
                leftItemList.remove(selectedIndex)
                // 更新剩余命令的index值
                for (i in selectedIndex until leftItemList.size) {
                    leftItemList[i].index = i
                }
                updateDetailPanel(leftList.selectedIndex)
            }
        }.setEditAction {
            val selectedIndex = leftList.selectedIndex
            if (selectedIndex != -1) {
                val currentName = leftItemList[selectedIndex].name
                val newName = JOptionPane.showInputDialog(mainPanel, "编辑名称：", currentName)
                if (!newName.isNullOrBlank() && newName != currentName) {
                    val isDuplicate = (0 until leftItemList.size).any { it != selectedIndex && leftItemList[it].name == newName }
                    if (isDuplicate) {
                        JOptionPane.showMessageDialog(mainPanel, "名称已存在，请输入唯一的名称！", "错误", JOptionPane.ERROR_MESSAGE)
                    } else {
                        val currentConfig = leftItemList[selectedIndex]
                        currentConfig.name = newName
                        currentConfig.index = selectedIndex
                        leftList.repaint()
                        updateDetailPanel(selectedIndex)
                    }
                }
            }
        }.createPanel()

        leftList.addListSelectionListener { updateDetailPanel(leftList.selectedIndex) }

        mainPanel.preferredSize = java.awt.Dimension(800, 300)
        leftListOperationsPanel.preferredSize = java.awt.Dimension(250, 300)
        rightPanel.preferredSize = java.awt.Dimension(550, 300)

        mainPanel.add(leftListOperationsPanel, BorderLayout.WEST)
        mainPanel.add(rightPanel, BorderLayout.CENTER)
        return mainPanel
    }

    override fun isModified(): Boolean {
        val project = project ?: return false
        val service = WorkspaceConfigService.getInstance(project)
        val persistedData = service.state.commands
        val currentData = (0 until leftItemList.size).map { leftItemList[it] }
        return persistedData != currentData
    }


    override fun apply() {
        val project = project ?: return
        val service = WorkspaceConfigService.getInstance(project)
        val currentData = (0 until leftItemList.size).map { leftItemList[it] }
        currentData.forEachIndexed { index, item -> item.index = index }
        val persistedData = service.state.commands
        val changes = currentData.size != persistedData.size || currentData.indices.any { i -> !currentData[i].equalsValue(persistedData[i]) }
        println(changes)
        if (changes) {
            // 更新持久化数据
            val currentDataSort = currentData.toMutableList().apply { sortBy { it.index } }
            currentDataSort.forEach {
                println(it)
            }
            persistedData.forEach {
                println(it)
            }
            CustomCommandAction.modifyAction(currentDataSort, persistedData)
            service.state.commands = currentDataSort
        }
        leftItemList.clear()
        Utils.showNotification(project, "配置保存成功", "配置保存成功", NotificationType.INFORMATION)
    }

    override fun getDisplayName(): String {
        return "扩展命令配置"
    }
}