//package blog.dekun.wang.command.hook.ui
//
//import blog.dekun.wang.command.hook.action.CustomCommandAction
//import blog.dekun.wang.command.hook.data.ConfigInfo
//import blog.dekun.wang.command.hook.services.ServiceUtils
//import blog.dekun.wang.command.hook.utils.Utils
//import com.intellij.icons.AllIcons
//import com.intellij.notification.NotificationType
//import com.intellij.openapi.options.Configurable
//import com.intellij.openapi.project.Project
//import com.intellij.ui.ToolbarDecorator
//import com.intellij.ui.components.JBList
//import java.awt.BorderLayout
//import java.awt.CardLayout
//import javax.swing.*
//
//class ConfigConfigurable(val project: Project) : Configurable {
//
//    private val mainPanel = JPanel(BorderLayout())
//
//    private val leftItemList = DefaultListModel<ConfigInfo>()
//
//    private val leftList = JBList(leftItemList).apply {
//        selectionMode = ListSelectionModel.SINGLE_SELECTION
//        cellRenderer = ListCellRenderer { list, value, index, isSelected, cellHasFocus ->
//            JLabel(value.name).apply {
//                isOpaque = true
//                value.isApp?.let { icon = if (it) AllIcons.Ide.Rating else null }
//                border = BorderFactory.createEmptyBorder(0, 5, 1, 0)
//                background = if (isSelected) list.selectionBackground else list.background
//                foreground = if (isSelected) list.selectionForeground else list.foreground
//            }
//        }
//    }
//
//
//    private fun loadPersistedData() {
//        leftItemList.clear()
//        ServiceUtils.getConfigInfoList(project).forEach { leftItemList.addElement(it) }
//    }
//
//    override fun createComponent(): JComponent {
//        loadPersistedData()
//
//        val rightPanel = JPanel(CardLayout(10, 10)).apply {
//            add(JPanel(BorderLayout()).apply { add(JLabel("请选择或添加命令", JLabel.CENTER), BorderLayout.CENTER) }, "placeholder")
//        }
//
//        fun updateDetailPanel(selectedIndex: Int) {
//            val cardLayout = rightPanel.layout as CardLayout
//            if (selectedIndex == -1) {
//                cardLayout.show(rightPanel, "placeholder")
//            } else {
//                val configInfo = leftItemList[selectedIndex]
//                val rightDetailPanel = JPanel(BorderLayout()).apply {
//                    add(RightDetail.rightDetail {
//                        nameField.text = configInfo.name
//                        commandField.text = configInfo.commandStr
//                        commandExecutionDirectory.text = configInfo.executionDir ?: ""
//                        isTargetFile.isSelected = configInfo.isTargetFile ?: false
//                        isTargetDir.isSelected = configInfo.isTargetFolder ?: false
//                        isRightClick.isSelected = configInfo.isRightClick ?: false
//                        isEnable.isSelected = configInfo.isEnable ?: true
//                        isApp.isSelected = configInfo.isApp ?: false
//                        addChangeListeners(
//                            onNameChanged = { value -> configInfo.name = value },
//                            onCommandChanged = { value -> configInfo.commandStr = value },
//                            onCommandExecutionDirectory = { value -> configInfo.executionDir = value },
//                            onTargetFileChanged = { value -> configInfo.isTargetFile = value },
//                            onTargetDirChanged = { value -> configInfo.isTargetFolder = value },
//                            onRightClickChanged = { value -> configInfo.isRightClick = value },
//                            onEnableChanged = { value -> configInfo.isEnable = value },
//                            onAppChanged = { value -> configInfo.isApp = value })
//                        configInfo.index = selectedIndex
//                    }.jPanel(), BorderLayout.NORTH)
//                }
//
//                // Replace existing panel with new one
//                cardLayout.show(rightPanel, "detail")
//                rightPanel.add(rightDetailPanel, "detail")
//            }
//        }
//
//        val leftListOperationsPanel = ToolbarDecorator.createDecorator(leftList).setAddAction {
//            //添加对话框
//            val newName = JOptionPane.showInputDialog(mainPanel, "请输入名称：", "添加命令", JOptionPane.PLAIN_MESSAGE)
//            if (!newName.isNullOrBlank()) {
//                // 判断是否重复
//                val isDuplicate = (0 until leftItemList.size).any { leftItemList[it].name == newName }
//                if (isDuplicate) {
//                    JOptionPane.showMessageDialog(mainPanel, "名称已存在，请输入唯一的名称！", "错误", JOptionPane.ERROR_MESSAGE)
//                } else {
//                    val newConfig = ConfigInfo(newName, leftItemList.size)
//                    leftItemList.addElement(newConfig)
//                    val newIndex = leftItemList.size - 1
//                    leftList.setSelectedIndex(newIndex)
//                    updateDetailPanel(newIndex)
//                }
//            }
//        }.setRemoveAction {
//            val selectedIndex = leftList.selectedIndex
//            if (selectedIndex != -1) {
//                leftItemList.remove(selectedIndex)
//                // 更新剩余命令的index值
//                for (i in selectedIndex until leftItemList.size) {
//                    leftItemList[i].index = i
//                }
//                updateDetailPanel(leftList.selectedIndex)
//            }
//        }.setEditAction {
//            val selectedIndex = leftList.selectedIndex
//            if (selectedIndex != -1) {
//                val currentName = leftItemList[selectedIndex].name
//                val newName = JOptionPane.showInputDialog(mainPanel, "编辑名称：", currentName)
//                if (!newName.isNullOrBlank() && newName != currentName) {
//                    val isDuplicate = (0 until leftItemList.size).any { it != selectedIndex && leftItemList[it].name == newName }
//                    if (isDuplicate) {
//                        JOptionPane.showMessageDialog(mainPanel, "名称已存在，请输入唯一的名称！", "错误", JOptionPane.ERROR_MESSAGE)
//                    } else {
//                        val currentConfig = leftItemList[selectedIndex]
//                        currentConfig.name = newName
//                        currentConfig.index = selectedIndex
//                        leftList.repaint()
//                        updateDetailPanel(selectedIndex)
//                    }
//                }
//            }
//        }.createPanel()
//
//        leftList.addListSelectionListener { updateDetailPanel(leftList.selectedIndex) }
//
//        mainPanel.preferredSize = java.awt.Dimension(800, 300)
//        leftListOperationsPanel.preferredSize = java.awt.Dimension(150, 300)
//        rightPanel.preferredSize = java.awt.Dimension(650, 300)
//
//        mainPanel.add(leftListOperationsPanel, BorderLayout.WEST)
//        mainPanel.add(rightPanel, BorderLayout.CENTER)
//        return mainPanel
//    }
//
//    override fun isModified(): Boolean {
//        val persistedData = ServiceUtils.getConfigInfoList(project)
//        val currentData = (0 until leftItemList.size).map { leftItemList[it] }
//        return persistedData != currentData
//    }
//
//
//    override fun apply() {
//        val currentData = (0 until leftItemList.size).map { leftItemList[it] }
//        currentData.forEachIndexed { index, item -> item.index = index }
//        val persistedData = ServiceUtils.getConfigInfoList(project)
//        val changes = currentData.size != persistedData.size || currentData.indices.any { i -> !currentData[i].equalsValue(persistedData[i]) }
//        if (changes) {
//            // 更新持久化数据
//            val currentDataSort = currentData.toMutableList().apply { sortBy { it.index } }
//            CustomCommandAction.modifyAction(currentDataSort, persistedData)
//            ServiceUtils.saveConfigInfoList(project, currentDataSort)
//        }
//        leftItemList.clear()
//        Utils.showNotification(project, "配置保存成功", "配置保存成功", NotificationType.INFORMATION)
//    }
//
//    override fun getDisplayName(): String {
//        return "扩展命令配置"
//    }
//}