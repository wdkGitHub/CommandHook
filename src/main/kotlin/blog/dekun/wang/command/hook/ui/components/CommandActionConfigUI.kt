package blog.dekun.wang.command.hook.ui.components

import blog.dekun.wang.command.hook.data.ActionConfig
import blog.dekun.wang.command.hook.data.ActionPosition
import blog.dekun.wang.command.hook.data.TemplateConfig
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.OnOffButton
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.util.ui.JBUI
import java.awt.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.plaf.basic.BasicComboBoxEditor

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *https://plugins.jetbrains.com/docs/intellij/empty-state.html#master-detail-layout
 */

class CommandActionConfigUI {

    companion object {

        var init = false

        val commandActionConfigUI = CommandActionConfigUI()

        fun component(): JComponent {
            if (!init) {
                commandActionConfigUI.createUIComponents()
                commandActionConfigUI.setupEventListeners()
                init = true
            }
            return commandActionConfigUI.rootPanel
        }

        fun updateData(newCurrentConfig: ActionConfig, paramTemplateModel: DefaultComboBoxModel<TemplateConfig>, commandTemplateModel: DefaultComboBoxModel<TemplateConfig>) {
            commandActionConfigUI.updateData(newCurrentConfig, paramTemplateModel, commandTemplateModel)
        }
    }

    private lateinit var currentConfig: ActionConfig

    private var paramTemplate: DefaultComboBoxModel<TemplateConfig> = DefaultComboBoxModel<TemplateConfig>()
    private var commandTemplate: DefaultComboBoxModel<TemplateConfig> = DefaultComboBoxModel<TemplateConfig>()

    private lateinit var rootPanel: JPanel
    private lateinit var onlyProjectCheckBox: JBCheckBox
    private lateinit var enableToggle: OnOffButton
    private lateinit var workingDirectory: TextFieldWithBrowseButton
    private lateinit var paramsCombo: ComboBox<TemplateConfig>
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
        // 更新 ComboBox 选择项
        params?.let { paramsCombo.selectedItem = TemplateConfig(it, params, true) }
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
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        }
        onlyProjectCheckBox = JBCheckBox("项目").apply {
            toolTipText = "是否仅此项目可见"
        }
        enableToggle = OnOffButton()
        workingDirectory = TextFieldWithBrowseButton()
        paramsCombo = customizeJComboBox(paramTemplate) {
            currentConfig.commandParams = it.split(" ")
        }
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
            add(JPanel(BorderLayout()).apply {
                add(JLabel("工作目录:"), BorderLayout.WEST)
                add(workingDirectory, BorderLayout.CENTER)
            }, gbc)
            add(JPanel(BorderLayout()).apply {
                add(JLabel("命令参数:"), BorderLayout.WEST)
                add(paramsCombo, BorderLayout.CENTER)
            }, gbc)
            add(JPanel(BorderLayout()).apply {
                add(JLabel("命令:"), BorderLayout.WEST)
                add(commandCombo, BorderLayout.CENTER)
            }, gbc)
            add(JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                alignmentX = Component.LEFT_ALIGNMENT
                add(JLabel("位置:"))
                add(defaultRadio)
                add(centralToolbarRadio)
                add(rightClickRadio)
                ButtonGroup().apply {
                    add(defaultRadio)
                    add(centralToolbarRadio)
                    add(rightClickRadio)
                }
            }, gbc)
        }, BorderLayout.NORTH)
    }


}
