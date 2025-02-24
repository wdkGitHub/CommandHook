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

        val panel = ToolbarDecorator.createDecorator(table).setAddAction {
            model.addRow(arrayOf<Any>("", "", true))
        }.setRemoveAction {
            table.selectedRows.sortedDescending().forEach { row ->
                model.removeRow(row)
            }
        }.createPanel().apply {
            border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        }
        return panel
    }

    fun commandTables(commandData: MutableList<TemplateConfig>, onDataChanged: (() -> Unit)? = null): JPanel {
        return commandTables(arrayOf("命令模板名称", "命令模板值", "仅此项目可见"), data = commandData, onDataChanged = onDataChanged)
    }

    fun paramTables(paramData: MutableList<TemplateConfig>, onDataChanged: (() -> Unit)? = null): JPanel {
        return commandTables(arrayOf("参数模板名称", "参数(key=value)", "仅此项目可见"), paramData, onDataChanged)
    }
}
}