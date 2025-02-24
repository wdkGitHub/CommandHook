//package blog.dekun.wang.command.hook.ui
//
//import com.intellij.ui.components.fields.ExpandableTextField
//import com.intellij.util.ui.FormBuilder
//import javax.swing.JCheckBox
//import javax.swing.JPanel
//import javax.swing.JTextField
//
//
//data class RightDetail(
//    val isApp: JCheckBox = JCheckBox("全局配置"),
//    val nameField: JTextField = JTextField().apply { isEnabled = false },
//    val commandField: ExpandableTextField = ExpandableTextField().apply {
//
//    },
//    val isSetDirectory: JCheckBox = JCheckBox("指定目录").apply {
//        isEnabled = false
//        isVisible = false
//    },
//    val commandExecutionDirectory: JTextField = JTextField().apply {
//        isEnabled = false
//        isVisible = false
//        text = ""
//    },
//
//    val isRightClick: JCheckBox = JCheckBox("绑定右键菜单").apply {
//        isEnabled = false
//        isVisible = false
//    },
//    val isTargetFile: JCheckBox = JCheckBox("是目标文件").apply {
//        isEnabled = false
//        isVisible = false
//    },
//    val isTargetDir: JCheckBox = JCheckBox("是目标目录").apply {
//        isEnabled = false
//        isVisible = false
//    },
//    val isEnable: JCheckBox = JCheckBox("启用").apply {
//        isSelected = true
//    }
//) {
//
//    companion object {
//
//        fun rightDetail(configure: RightDetail.() -> Unit): RightDetail {
//            return RightDetail().apply(configure).apply {
//                updateRightClickVisibility()
//            }
//        }
//    }
//
//    private fun updateSetDirectoryVisibility() {
//
//    }
//
//    private fun updateRightClickVisibility() {
//
//    }
//
//
//    fun addChangeListeners(
//        onNameChanged: (String) -> Unit = {},
//        onCommandChanged: (String) -> Unit = {},
//        onCommandExecutionDirectory: (String) -> Unit = {},
//        onAppChanged: (Boolean) -> Unit = {},
//        onTargetFileChanged: (Boolean) -> Unit = {},
//        onTargetDirChanged: (Boolean) -> Unit = {},
//        onRightClickChanged: (Boolean) -> Unit = {},
//        onEnableChanged: (Boolean) -> Unit = {}
//    ) {
//        fun JTextField.addTextChangeListener(onChange: (String) -> Unit) {
//            document.addDocumentListener(object : javax.swing.event.DocumentListener {
//                override fun insertUpdate(e: javax.swing.event.DocumentEvent?) = onChange(text)
//                override fun removeUpdate(e: javax.swing.event.DocumentEvent?) = onChange(text)
//                override fun changedUpdate(e: javax.swing.event.DocumentEvent?) = onChange(text)
//            })
//        }
//
//        fun JCheckBox.addStateChangeListener(onChange: (Boolean) -> Unit) {
//            addItemListener { onChange(isSelected) }
//        }
//
//        nameField.addTextChangeListener(onNameChanged)
//        commandField.addTextChangeListener(onCommandChanged)
//
//        commandExecutionDirectory.addTextChangeListener(onCommandExecutionDirectory)
//        isApp.addStateChangeListener(onAppChanged)
//        isTargetFile.addStateChangeListener(onTargetFileChanged)
//        isTargetDir.addStateChangeListener(onTargetDirChanged)
//        isSetDirectory.addStateChangeListener {
//            updateSetDirectoryVisibility()
//        }
//        isRightClick.addStateChangeListener {
//            updateRightClickVisibility()
//            onRightClickChanged(it)
//        }
//        isEnable.addStateChangeListener(onEnableChanged)
//
//
//    }
//
//    fun jPanel(): JPanel {
//
//        return FormBuilder.createFormBuilder()
//            .addComponent(isApp)
//            .addLabeledComponent("   名称:", nameField)
//            .addLabeledComponent("   命令:", commandField)
//            .addComponent(isSetDirectory)
//            .addLabeledComponent("", commandExecutionDirectory)
//            .addComponent(isRightClick)
//            .addComponent(isTargetFile)
//            .addComponent(isTargetDir)
//            .addComponent(isEnable)
//            .panel
//    }
//
//}
