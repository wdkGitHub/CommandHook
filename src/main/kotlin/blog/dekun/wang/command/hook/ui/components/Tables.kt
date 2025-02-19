package blog.dekun.wang.command.hook.ui.components

import blog.dekun.wang.command.hook.data.TemplateConfig
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.table.JBTable
import javax.swing.*
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */

class Tables {

    companion object {

        private val COLUMN_NAMES = arrayOf("名字", "值", "仅此项目可见")

        fun commandTables(columns: Array<String> = COLUMN_NAMES, data: List<TemplateConfig>): Pair<JPanel, () -> List<TemplateConfig>> {
            // 使用可变列表存储当前配置，并创建深拷贝避免修改原始数据
            val configs = data.map { it.copy() }.toMutableList()
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
                configs.forEach { addRow(arrayOf<Any>(it.name, it.value, it.onlyProject)) }
            }

            val table = JBTable(model).apply {
                // 配置为多行选择模式
                selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                // 配置列宽自适应策略
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

            // 初始化单元格渲染器和编辑器（使用单例模式提升性能）
            val textFieldRenderer = ExpandableTextField()
            val checkBoxRenderer = JCheckBox().apply { horizontalAlignment = SwingConstants.LEFT;isOpaque = true }

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
                        checkBoxRenderer.apply { isSelected = value as? Boolean == true;border = BorderFactory.createEmptyBorder(0, 30, 0, 0) }
                    }
                }
            }

            // 监听表格数据变化并同步到configs集合
            model.addTableModelListener { e ->
                when (e.type) {
                    TableModelEvent.UPDATE -> {
                        val row = e.firstRow
                        configs[row] = TemplateConfig(
                            model.getValueAt(row, 0).toString(),
                            model.getValueAt(row, 1).toString(),
                            model.getValueAt(row, 2) as Boolean
                        )
                    }

                    TableModelEvent.INSERT -> {
                        val newRow = model.rowCount - 1
                        configs.add(
                            newRow, TemplateConfig(
                                model.getValueAt(newRow, 0).toString(),
                                model.getValueAt(newRow, 1).toString(),
                                model.getValueAt(newRow, 2) as Boolean
                            )
                        )
                    }

                    TableModelEvent.DELETE -> configs.removeAt(e.firstRow)
                }
            }
            // 创建带操作工具栏的面板
            val panel = ToolbarDecorator.createDecorator(table)
                .setAddAction {
                    model.addRow(arrayOf<Any>("", "", false))
                }
                .setRemoveAction {
                    // 获取所有选中的行
                    val selectedRows = table.selectedRows
                    if (selectedRows.isNotEmpty()) {
                        // 删除选中的行，逆序删除以避免索引问题
                        selectedRows.sortedDescending().forEach { row ->
                            model.removeRow(row)
                        }
                    }
                }.createPanel().apply {
                    border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
                }

            return Pair(panel) { configs.toList() }
        }

        fun commandTables(): Pair<JPanel, () -> List<TemplateConfig>> {
            return commandTables(data = listOf(TemplateConfig("命令(ls -1)", "s -1", true)))
        }

        fun paramTables(): Pair<JPanel, () -> List<TemplateConfig>> {
            return commandTables(COLUMN_NAMES, listOf(TemplateConfig("参数(p=1)", "p=1", true)))
        }
    }
}


