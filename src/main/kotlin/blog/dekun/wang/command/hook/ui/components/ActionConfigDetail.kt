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
    private var paramTableData: Array<Array<String>> = arrayOf()
    private val tableModel = DefaultTableModel(paramTableData, arrayOf("Name", "Value"))
    private lateinit var rootPanel: JPanel
    private lateinit var onlyProjectCheckBox: JBCheckBox
    private lateinit var enableToggle: OnOffButton
    private lateinit var workingDirectory: TextFieldWithBrowseButton
    private lateinit var paramTable: JBTable
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
        copyComboBoxModel(paramTemplateModel, paramTemplate, null)
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
        currentConfig.commandParams.let {
            tableModel.rowCount = 0
            it.forEach { (key, value) ->
                tableModel.addRow(arrayOf(key, value))
            }
        }
    }

    private fun customizeJComboBox(items: ComboBoxModel<TemplateConfig>, selectItemLambda: (selectedValue: String) -> Unit) = ComboBox(items).apply {

        isEditable = true

        // 渲染器：下拉框的显示内容为 TemplateConfig 的 name
        renderer = ListCellRenderer { list, value, _, isSelected, _ ->
            val name = value?.let { it.name + " : " + value.value.replace("\n".toRegex(), " ") } ?: ""
            JLabel(if (name.length > rootPanel.width / 10) name.substring(0, rootPanel.width / 10) + "..." else name).apply {
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
        currentConfig = ActionConfig.empty()
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
        paramTable = JBTable(tableModel).apply {
            rowHeight = 30
            val totalWidth = preferredSize.width
            columnModel.getColumn(0).preferredWidth = (totalWidth * 0.1).toInt()
            columnModel.getColumn(1).preferredWidth = (totalWidth * 0.9).toInt()
        }
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
                    horizontalAlignment = SwingConstants.RIGHT
                }, BorderLayout.WEST)
                add(commandCombo, BorderLayout.CENTER)
            }, gbc)
            add(JPanel(BorderLayout()).apply {
                val panel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0)).apply {
                    add(JLabel("命令参数").apply {
                        preferredSize = Dimension(60, preferredSize.height)
                        border = BorderFactory.createEmptyBorder(0, 0, 0, 3)
                        horizontalAlignment = SwingConstants.RIGHT
                    })
                }
                add(panel, BorderLayout.WEST)
                add(paramTableJPanel(tableModel), BorderLayout.CENTER)
            }, gbc)

        }, BorderLayout.NORTH)
    }

    private fun paramTableJPanel(tableModel: DefaultTableModel) = ToolbarDecorator.createDecorator(paramTable).apply {
        var index: Int = 0
        setAddAction {
            currentConfig.commandParams.put("id${index}", "desc")
            tableModel.addRow(arrayOf<Any?>("id${index++}", "desc"))
        }
        setRemoveAction {
            paramTable.selectedRows.takeIf { it.isNotEmpty() }?.let { selectedRows ->
                val lastSelectedRow = selectedRows.last()
                for (i in selectedRows.size - 1 downTo 0) {
                    val rowIndex = selectedRows[i]
                    currentConfig.commandParams.remove(tableModel.getValueAt(rowIndex, 0))
                    tableModel.removeRow(rowIndex)
                }
                if (tableModel.rowCount > 0) {
                    val rowIndexToSelect = if (lastSelectedRow > tableModel.rowCount - 1) tableModel.rowCount - 1 else lastSelectedRow
                    paramTable.setRowSelectionInterval(rowIndexToSelect, rowIndexToSelect)
                }
            }
        }
        addExtraAction(object : AnAction("模板") {
            override fun getActionUpdateThread() = ActionUpdateThread.BGT
            override fun actionPerformed(e: AnActionEvent) {
                var commandParams: List<String>? = null
                val paramsCombo = customizeJComboBox(paramTemplate) {
                    commandParams = it.split(" ")
                }
                val result = JOptionPane.showConfirmDialog(null, paramsCombo, "选择命令", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)

                if (result == JOptionPane.OK_OPTION) {
                    commandParams?.let { currentConfig.commandParams.putAll(it.associate { it.split("=").first() to it.split("=").last() }) }
                    commandParams?.forEach { tableModel.addRow(arrayOf(it.split("=").first(), it.split("=").last())) }
                }
            }

            override fun update(e: AnActionEvent) {
                e.presentation.icon = AllIcons.Actions.AddList
            }
        })
    }.createPanel().apply {
        preferredSize = Dimension(preferredSize.width, 300)
    }
}
