package blog.dekun.wang.command.hook.ui.components

import blog.dekun.wang.command.hook.data.ActionConfig
import blog.dekun.wang.command.hook.data.ActionPosition
import blog.dekun.wang.command.hook.data.TemplateConfig
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.OnOffButton
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import java.awt.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.plaf.basic.BasicComboBoxEditor
import javax.swing.table.DefaultTableModel

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *https://plugins.jetbrains.com/docs/intellij/empty-state.html#master-detail-layout
 */

class ActionConfigDetail {

    companion object {

        var init = false

        val actionConfigDetail = ActionConfigDetail()

        fun component(): JComponent {
            if (!init) {
                actionConfigDetail.createUIComponents()
                actionConfigDetail.setupEventListeners()
                init = true
            }
            return actionConfigDetail.rootPanel
        }

        fun updateData(newCurrentConfig: ActionConfig, paramTemplateModel: DefaultComboBoxModel<TemplateConfig>, commandTemplateModel: DefaultComboBoxModel<TemplateConfig>) {
            actionConfigDetail.updateData(newCurrentConfig, paramTemplateModel, commandTemplateModel)
        }
    }

    private lateinit var currentConfig: ActionConfig

    private var paramTemplate: DefaultComboBoxModel<TemplateConfig> = DefaultComboBoxModel<TemplateConfig>()
    private var commandTemplate: DefaultComboBoxModel<TemplateConfig> = DefaultComboBoxModel<TemplateConfig>()

    private lateinit var rootPanel: JPanel
    private lateinit var onlyProjectCheckBox: JBCheckBox
    private lateinit var enableToggle: OnOffButton
    private lateinit var workingDirectory: TextFieldWithBrowseButton

    //    private lateinit var paramsCombo: ComboBox<TemplateConfig>
    private lateinit var commandCombo: ComboBox<TemplateConfig>
    private lateinit var defaultRadio: JBRadioButton
    private lateinit var centralToolbarRadio: JBRadioButton
    private lateinit var rightClickRadio: JBRadioButton


    private fun printlnConfig() {
        println("currentConfig: $currentConfig")
    }

    private fun setupEventListeners() {
        onlyProjectCheckBox.addItemListener {
            currentConfig.onlyProject = onlyProjectCheckBox.isSelected
            printlnConfig()
        }

        enableToggle.addActionListener {
            currentConfig.enable = enableToggle.isSelected
            printlnConfig()
        }

        workingDirectory.addActionListener {
            val chooser = FileChooserDescriptor(false, true, false, false, false, false)
            val selectedDir = FileChooser.chooseFile(chooser, null, null)
            selectedDir?.let {
                workingDirectory.text = it.path
            }
        }
        workingDirectory.textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) {
                updateTargetDir()
            }

            override fun removeUpdate(e: DocumentEvent?) {
                updateTargetDir()
            }

            override fun changedUpdate(e: DocumentEvent?) {
                updateTargetDir()
            }

            private fun updateTargetDir() {
                currentConfig.workingDirectory = workingDirectory.text
                printlnConfig()
            }
        })

        defaultRadio.addActionListener {
            if (defaultRadio.isSelected) currentConfig.position = ActionPosition.DEFAULT
            printlnConfig()
        }

        centralToolbarRadio.addActionListener {
            if (centralToolbarRadio.isSelected) currentConfig.position = ActionPosition.CENTRAL_TOOLBAR
            printlnConfig()
        }

        rightClickRadio.addActionListener {
            if (rightClickRadio.isSelected) currentConfig.position = ActionPosition.RIGHT_CLICK
            printlnConfig()
        }
    }

    private fun copyComboBoxModel(originalModel: DefaultComboBoxModel<TemplateConfig>, targetModel: DefaultComboBoxModel<TemplateConfig>, selectItem: String?) {
        targetModel.removeAllElements()
        selectItem?.let { targetModel.addElement(TemplateConfig(name = it, value = selectItem, onlyProject = true)) }
        for (i in 0 until originalModel.size) {
            targetModel.addElement(originalModel.getElementAt(i).copy())
        }
    }

    private fun updateData(newCurrentConfig: ActionConfig, paramTemplateModel: DefaultComboBoxModel<TemplateConfig>, commandTemplateModel: DefaultComboBoxModel<TemplateConfig>) {
        currentConfig = newCurrentConfig
        // 合并 commandStr 和 commandTemplateModel 到 commandCombo 数据源
        copyComboBoxModel(commandTemplateModel, commandTemplate, currentConfig.commandStr)
        // 合并 commandParams 和 paramTemplateModel 到 paramsCombo 数据源
        val params = currentConfig.commandParams?.joinToString(" ")
        copyComboBoxModel(paramTemplateModel, paramTemplate, params)

        //========================
        onlyProjectCheckBox.isSelected = currentConfig.onlyProject
        enableToggle.isSelected = currentConfig.enable
        workingDirectory.text = currentConfig.workingDirectory.orEmpty()
        commandCombo.selectedItem = TemplateConfig(name = currentConfig.commandStr, value = currentConfig.commandStr, onlyProject = true)
        when (currentConfig.position) {
            ActionPosition.DEFAULT -> defaultRadio.isSelected = true
            ActionPosition.CENTRAL_TOOLBAR -> centralToolbarRadio.isSelected = true
            ActionPosition.RIGHT_CLICK -> rightClickRadio.isSelected = true
        }

    }

    private fun customizeJComboBox(items: ComboBoxModel<TemplateConfig>, selectItemLambda: (selectedValue: String) -> Unit) = ComboBox(items).apply {

        isEditable = true

        // 渲染器：下拉框的显示内容为 TemplateConfig 的 name
        renderer = ListCellRenderer { list, value, _, isSelected, _ ->
            JLabel(value?.name ?: "").apply {
                isOpaque = true
                background = if (isSelected) list?.selectionBackground else background
                foreground = if (isSelected) list?.selectionForeground else foreground
            }
        }

        // 编辑器：显示 TemplateConfig 的 value 或 String
        editor = object : BasicComboBoxEditor() {
            override fun getItem(): Any {
                return (editorComponent as JTextField).text
            }

            override fun setItem(item: Any?) {
                if (item is TemplateConfig) {
                    (editorComponent as JTextField).text = item.value // 显示 value 属性
                }
                if (item is String) {
                    (editorComponent as JTextField).text = item
                }
            }

            override fun createEditorComponent(): JTextField {
                return ExpandableTextField().apply {
                    border = null
                    // 添加 DocumentListener 监听文本框的变化
                    document.addDocumentListener(object : DocumentListener {
                        override fun insertUpdate(e: DocumentEvent?) {
                            onTextChanged()
                        }

                        override fun removeUpdate(e: DocumentEvent?) {
                            onTextChanged()
                        }

                        override fun changedUpdate(e: DocumentEvent?) {
                            onTextChanged()
                        }

                        private fun onTextChanged() {
                            selectItemLambda.invoke(text)
                            printlnConfig()
                        }
                    })
                }
            }
        }
    }


    private fun createUIComponents() {
        currentConfig = ActionConfig("", false, false, null, null, "", ActionPosition.DEFAULT)
        rootPanel = JPanel().apply {
            layout = BorderLayout()
            border = BorderFactory.createEmptyBorder(3, 3, 3, 3)
        }
        onlyProjectCheckBox = JBCheckBox("仅此项目").apply {
            toolTipText = "是否仅此项目可见"
            horizontalTextPosition = SwingConstants.LEFT
            border = BorderFactory.createEmptyBorder(0, 8, 0, 0)
            horizontalAlignment = SwingConstants.RIGHT
        }
        enableToggle = OnOffButton()
        workingDirectory = TextFieldWithBrowseButton()
        commandCombo = customizeJComboBox(commandTemplate) {
            currentConfig.commandStr = it
        }
        defaultRadio = JBRadioButton("默认")
        centralToolbarRadio = JBRadioButton("中心工具栏")
        rightClickRadio = JBRadioButton("右键菜单")
        // layout
        rootPanel.add(JPanel(GridBagLayout()).apply {
            val gbc = GridBagConstraints().apply {
                gridx = 0
                gridy = GridBagConstraints.RELATIVE
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
                anchor = GridBagConstraints.WEST
                insets = JBUI.insets(5)
            }
            add(JPanel(BorderLayout()).apply {
                add(onlyProjectCheckBox, BorderLayout.WEST)
                add(enableToggle, BorderLayout.EAST)
            }, gbc)
            add(JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                alignmentX = Component.LEFT_ALIGNMENT
                add(JLabel("位置").apply {
                    preferredSize = Dimension(60, preferredSize.height)
                    border = BorderFactory.createEmptyBorder(0, 0, 0, 3)
                    // 设置文本右对齐
                    horizontalAlignment = SwingConstants.RIGHT
                })
                add(defaultRadio)
                add(centralToolbarRadio)
                add(rightClickRadio)
                ButtonGroup().apply {
                    add(defaultRadio)
                    add(centralToolbarRadio)
                    add(rightClickRadio)
                }
            }, gbc)
            add(JPanel(BorderLayout()).apply {
                add(JLabel("工作目录").apply {
                    preferredSize = Dimension(60, preferredSize.height)
                    border = BorderFactory.createEmptyBorder(0, 0, 0, 3)
                    // 设置文本右对齐
                    horizontalAlignment = SwingConstants.RIGHT
                }, BorderLayout.WEST)
                add(workingDirectory, BorderLayout.CENTER)
            }, gbc)
            add(JPanel(BorderLayout()).apply {
                add(JLabel("命令").apply {
                    preferredSize = Dimension(60, preferredSize.height)
                    border = BorderFactory.createEmptyBorder(0, 0, 0, 3)
                    // 设置文本右对齐
                    horizontalAlignment = SwingConstants.RIGHT
                }, BorderLayout.WEST)
                add(commandCombo, BorderLayout.CENTER)
            }, gbc)
            add(JPanel(BorderLayout()).apply {
                // 创建一个用于包装 JLabel 和 Table 的 JPanel
                val panel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
                    // 设置 JLabel 上对齐
                    add(JLabel("命令参数").apply {
                        preferredSize = Dimension(60, preferredSize.height)
                        border = BorderFactory.createEmptyBorder(0, 0, 0, 3)
                        // 设置文本右对齐
                        horizontalAlignment = SwingConstants.RIGHT
                    })
                }
                // 将这个 panel 添加到 WEST 区域
                add(panel, BorderLayout.WEST)

                // 将 Table 放到 CENTER 区域
                add(createComponentTable(), BorderLayout.CENTER)
            }, gbc)

        }, BorderLayout.NORTH)
    }


    fun createComponentTable(): JComponent {
//        val tableModel = DefaultTableModel(arrayOf("Name", "Value"), 0)
//        // 创建表格
//        val table = JBTable(tableModel).apply {
//            rowHeight = 30
//        }
        val columnNames = kotlin.arrayOf<kotlin.String>("Name", "Value")
        val data = kotlin.arrayOf<kotlin.Array<kotlin.Any>>(
//            kotlin.arrayOf<kotlin.Any>("Data 1", "Data 2", "Data 3"),
//            kotlin.arrayOf<kotlin.Any>("Data 4", "Data 5", "Data 6"),
//            kotlin.arrayOf<kotlin.Any>("Data 7", "Data 8", "Data 9")
        )
        val tableModel = DefaultTableModel(data, columnNames)


        // 创建 JBTable 实例
        val table: JBTable = JBTable(tableModel).apply {
            rowHeight = 30
            val totalWidth = preferredSize.width
            columnModel.getColumn(0).preferredWidth = (totalWidth * 0.1).toInt()
            columnModel.getColumn(1).preferredWidth = (totalWidth * 0.9).toInt()
        }
        // 使用 ToolbarDecorator 包装表格
        val decorator = ToolbarDecorator.createDecorator(table).apply {
            var index: Int = 0
            setAddAction {
                tableModel.addRow(arrayOf<Any?>("id${index++}", "desc")) // 添加空行
            }
            setRemoveAction {
                val selectedRows = table.selectedRows
                if (selectedRows.isNotEmpty()) {
                    val rowIndexToSelect: Int
                    // 获取删除的最后一行的索引
                    val lastSelectedRow = selectedRows.last()
                    // 删除选中行
                    for (i in selectedRows.indices.reversed()) {
                        tableModel.removeRow(selectedRows[i])
                    }
                    // 确定新的选中行（选择删除行前的上一行，或者选择删除后的第一行）
                    if (lastSelectedRow > 0) {
                        rowIndexToSelect = lastSelectedRow - 1 // 选择上一行
                    } else {
                        rowIndexToSelect = 0 // 如果删除的是第一行，选择第一行
                    }
                    // 更新选择的行
                    if (tableModel.rowCount != 0) {
                        table.setRowSelectionInterval(rowIndexToSelect, rowIndexToSelect)
                    }
                }
            }

            addExtraAction(object : AnAction("模板添加") {
                override fun getActionUpdateThread() = ActionUpdateThread.BGT

                override fun actionPerformed(e: AnActionEvent) {
                    var commandParams: List<String>? = null
                    val paramsCombo = customizeJComboBox(paramTemplate) {
                        commandParams = it.split(" ")
                    }
                    val result = JOptionPane.showConfirmDialog(null, paramsCombo, "选择命令", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)

                    if (result == JOptionPane.OK_OPTION) {
                        // 你可以根据需要获取 ComboBox 的选中项
                        commandParams?.forEach { tableModel.addRow(arrayOf(it.split("=")[0], it.split("=")[1])) }
                    }
                }

                override fun update(e: AnActionEvent) {
                    e.presentation.icon = AllIcons.Actions.AddList
                }
            })
        }
        return decorator.createPanel().apply {
            preferredSize = Dimension(preferredSize.width, 300)
        }
    }

}
