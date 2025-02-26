package blog.dekun.wang.command.hook.ui.components

import blog.dekun.wang.command.hook.data.TemplateConfig
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.table.JBTable
import javax.swing.*
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

class Tables { companion object {

    private val COLUMN_NAMES = arrayOf("名字", "值", "仅此项目可见")

    private fun commandTables(columns: Array<String> = COLUMN_NAMES, data: MutableList<TemplateConfig>, onDataChanged: (() -> Unit)? = null): JPanel {
        // 创建表格模型并初始化数据
        val model = object : DefaultTableModel(columns, 0) {
            private val serialVersionUID: Long = -1648333981082710225L

            override fun getColumnClass(columnIndex: Int): Class<*> {
                return when (columnIndex) {
                    2 -> Boolean::class.java
                    else -> String::class.java
                }
            }
        }.apply {
            data.forEach { addRow(arrayOf<Any>(it.name, it.value, it.onlyProject)) }
            addTableModelListener { e ->
                when (e.type) {
                    TableModelEvent.UPDATE, TableModelEvent.INSERT, TableModelEvent.DELETE -> {
                        onDataChanged?.invoke() // 数据变更时触发回调
                    }
                }
            }
        }

        val table = JBTable(model).apply {
            selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS

            columnModel.apply {
                SwingUtilities.invokeLater {
                    val widths = listOf(0.2, 0.7, 0.1).map { (width * it).toInt() }
                    widths.forEachIndexed { index, width ->
                        getColumn(index).preferredWidth = width
                    }
                }
            }
        }

        val textFieldRenderer = ExpandableTextField()
        val checkBoxRenderer = JCheckBox().apply { horizontalAlignment = SwingConstants.LEFT; isOpaque = true }

        with(table.columnModel) {
            getColumn(1).apply {
                cellEditor = DefaultCellEditor(ExpandableTextField())
                cellRenderer = TableCellRenderer { _, value, _, _, _, _ ->
                    textFieldRenderer.apply { text = value?.toString() ?: "" }
                }
            }
            getColumn(2).apply {
                cellEditor = DefaultCellEditor(JCheckBox())
                cellRenderer = TableCellRenderer { _, value, _, _, _, _ ->
                    checkBoxRenderer.apply { isSelected = value as? Boolean == true; border = BorderFactory.createEmptyBorder(0, 30, 0, 0) }
                }
            }
        }

        model.addTableModelListener { e ->
            when (e.type) {
                TableModelEvent.UPDATE -> {
                    val row = e.firstRow
                    // 直接修改原始数据列表中的元素
                    data[row] = TemplateConfig(
                        model.getValueAt(row, 0).toString(), model.getValueAt(row, 1).toString(), model.getValueAt(row, 2) as Boolean
                    )
                }

                TableModelEvent.INSERT -> {
                    val newRow = model.rowCount - 1
                    // 向原始数据列表中添加新元素
                    data.add(
                        newRow, TemplateConfig(
                            model.getValueAt(newRow, 0).toString(), model.getValueAt(newRow, 1).toString(), model.getValueAt(newRow, 2) as Boolean
                        )
                    )
                }

                TableModelEvent.DELETE -> data.removeAt(e.firstRow) // 直接删除原始数据列表中的元素
            }
        }

        return ToolbarDecorator.createDecorator(table)
            .setAddAction {
                model.addRow(arrayOf<Any>("", "", true))
            }.setRemoveAction {
                // 获取选中的行
                val selectedRows = table.selectedRows.sortedDescending()
                // 如果选中了行，进行删除
                if (selectedRows.isNotEmpty()) {
                    // 记录最后选中的行索引（删除前的选中行）
                    val lastSelectedRow = selectedRows.first()
                    // 删除选中的行
                    selectedRows.forEach { row ->
                        model.removeRow(row)
                    }
                    val newSelectionIndex = when {
                        model.rowCount == 0 -> -1
                        lastSelectedRow >= model.rowCount -> model.rowCount - 1
                        else -> lastSelectedRow
                    }

                    if (newSelectionIndex >= 0) {
                        table.selectionModel.setSelectionInterval(newSelectionIndex, newSelectionIndex)
                    }
                }
            }.setMoveUpAction {
                moveRow(table, model, -1)
            }.setMoveDownAction {
                moveRow(table, model, 1)
            }.createPanel().apply {
                border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
            }
    }

    private fun moveRow(table: JBTable, model: DefaultTableModel, offset: Int) {
        val selectedRows = table.selectedRows
        if (selectedRows.isNotEmpty()) {
            val row = if (offset < 0) selectedRows.first() else selectedRows.last()
            if ((row + offset >= 0) && (row + offset < model.rowCount)) {
                val tempRow = Array<Any>(model.columnCount) { model.getValueAt(row, it) }
                val targetRow = Array<Any>(model.columnCount) { model.getValueAt(row + offset, it) }

                for (column in 0 until model.columnCount) {
                    model.setValueAt(tempRow[column], row + offset, column)
                    model.setValueAt(targetRow[column], row, column)
                }

                table.selectionModel.setSelectionInterval(row + offset, row + offset)
            }
        }
    }

    fun commandTables(commandData: MutableList<TemplateConfig>, onDataChanged: (() -> Unit)? = null): JPanel {
        return commandTables(arrayOf("命令模板名称", "命令模板值", "仅此项目可见"), data = commandData, onDataChanged = onDataChanged)
    }

    fun paramTables(paramData: MutableList<TemplateConfig>, onDataChanged: (() -> Unit)? = null): JPanel {
        return commandTables(arrayOf("参数模板名称", "参数(key=value)", "仅此项目可见"), paramData, onDataChanged)
    }
}
}