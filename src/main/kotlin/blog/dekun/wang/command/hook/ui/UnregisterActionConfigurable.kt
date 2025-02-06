package blog.dekun.wang.command.hook.ui

import blog.dekun.wang.command.hook.data.UnregisterActions
import blog.dekun.wang.command.hook.services.UnregisterActionService
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import java.awt.BorderLayout
import java.io.Serial
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class UnregisterActionConfigurable(val project: Project) : Configurable {

    private val tableModel = object : DefaultTableModel(arrayOf("AnActionId", "功能描述", "注销"), 0) {
        @Serial
        private val serialVersionUID: Long = 1945279952936920373L

        override fun getColumnClass(columnIndex: Int): Class<*> {
            return if (columnIndex == 2) Boolean::class.java else String::class.java
        }
    }

    private val originalData = mutableListOf<UnregisterActions>()

    override fun createComponent(): JComponent {
        originalData.clear()
        val unregisterActions = UnregisterActionService.unregisterActions()
        unregisterActions.forEach {
            tableModel.addRow(arrayOf<Any?>(it.actionId, it.description, it.isUnregister))
        }
        originalData.addAll(unregisterActions)

        // 创建表格
        val table = JBTable(tableModel).apply {
            autoCreateRowSorter = true // 启用排序功能
            setDefaultRenderer(Boolean::class.java, { table, value, isSelected, hasFocus, row, column ->
                val checkBox = JCheckBox().apply {
                    text = "取消需要重启IDE生效"
                }
                checkBox.isSelected = value as? Boolean ?: false
                checkBox.isOpaque = true
                checkBox.background = if (isSelected) table.selectionBackground else table.background
                checkBox
            })
            // 添加鼠标监听器以处理复选框点击事件
            addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseClicked(e: java.awt.event.MouseEvent) {
                    val column = columnAtPoint(e.point)
                    val row = rowAtPoint(e.point)
                    if (column == 2) { // 检查是否点击了第三列
                        val isChecked = getValueAt(row, column) as? Boolean ?: false
                        setValueAt(!isChecked, row, column) // 切换复选框状态
                    }
                }
            })
            // 添加鼠标移动监听器以处理悬停提示
            addMouseMotionListener(object : java.awt.event.MouseMotionAdapter() {
                override fun mouseMoved(e: java.awt.event.MouseEvent) {
                    val column = columnAtPoint(e.point)
                    if (column == 2) { // 如果鼠标悬停在第三列
                        toolTipText = "勾选后将注销该 AnAction,取消需要重启IDEA才生效" // 显示提示
                    } else {
                        toolTipText = null // 移除提示
                    }
                }
            })
        }

        // 使用 ToolbarDecorator 包装表格
        val decorator = ToolbarDecorator.createDecorator(table).apply {
            setAddAction {
                tableModel.addRow(arrayOf<Any?>("id", "desc", true)) // 添加空行
            }
            setRemoveAction {
                val selectedRows = table.selectedRows

                for (i in selectedRows.indices.reversed()) {
                    tableModel.removeRow(selectedRows[i]) // 删除选中行
                }
            }
            setEditAction(null) // 此处可根据需要实现编辑功能
        }

        // 返回带有工具栏的面板
        return JPanel(BorderLayout()).apply {
            preferredSize = java.awt.Dimension(800, 300)
            add(decorator.createPanel(), BorderLayout.CENTER)
        }
    }

    override fun isModified(): Boolean {
        for (i in 0 until tableModel.rowCount) {
            val originalRow = originalData.getOrNull(i) ?: return true
            val currentRow = Array(3) { tableModel.getValueAt(i, it) }
            if (!originalRow.equals(currentRow)) {
                return true
            }
        }
        return originalData.size != tableModel.rowCount
    }

    override fun apply() {
        val unregisterActions = mutableListOf<UnregisterActions>()
        for (i in 0 until tableModel.rowCount) {
            val row = Array(3) { tableModel.getValueAt(i, it) }
            unregisterActions.add(UnregisterActions(row[0] as String, row[1] as String?, row[2] as Boolean))
        }
        UnregisterActionService.unregisterActions(unregisterActions)
        originalData.clear()
    }

    override fun getDisplayName(): String {
        return "注销AnAction配置"
    }
}
