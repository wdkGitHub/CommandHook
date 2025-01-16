package blog.dekun.wang.extension.ui

import javax.swing.JCheckBox
import javax.swing.JTextField

data class RightDetail(
    val nameField: JTextField = JTextField().apply {
        isEnabled = false
    },
    val commandField: JTextField = JTextField(),
    val isApp: JCheckBox = JCheckBox("是应用"),
    val isTargetFile: JCheckBox = JCheckBox("是目标文件"),
    val isTargetDir: JCheckBox = JCheckBox("是目标目录"),
    val isRightClick: JCheckBox = JCheckBox("添加右键菜单").apply {
        isEnabled = false
        isVisible = false
    },
    val isEnable: JCheckBox = JCheckBox("启用")
) {

    fun addChangeListeners(
        onNameChanged: (String) -> Unit = { value ->
            println(value)
        },
        onCommandChanged: (String) -> Unit = { value ->
            println(value)
        },
        onAppChanged: (Boolean) -> Unit = { value ->
            println(value)
        },
        onTargetFileChanged: (Boolean) -> Unit = { value ->
            println(value)
        },
        onTargetDirChanged: (Boolean) -> Unit = { value ->
            println(value)
        },
        onRightClickChanged: (Boolean) -> Unit = { value ->
            println(value)
        },
        onEnableChanged: (Boolean) -> Unit = { value ->
            println(value)
        }
    ) {

        nameField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) {
                onNameChanged(nameField.text)
            }

            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) {
                onNameChanged(nameField.text)
            }

            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) {
                onNameChanged(nameField.text)
            }
        })

        commandField.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) {
                onCommandChanged(commandField.text)
            }

            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) {
                onCommandChanged(commandField.text)
            }

            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) {
                onCommandChanged(commandField.text)
            }
        })

        isApp.addItemListener {
            onAppChanged(isApp.isSelected)
        }
        isTargetFile.addItemListener {
            onTargetFileChanged(isTargetFile.isSelected)
        }

        isTargetDir.addItemListener {
            onTargetDirChanged(isTargetDir.isSelected)
        }

        isRightClick.addItemListener {
            onRightClickChanged(isRightClick.isSelected)
        }

        isEnable.addItemListener {
            onEnableChanged(isEnable.isSelected)
        }
    }
}
