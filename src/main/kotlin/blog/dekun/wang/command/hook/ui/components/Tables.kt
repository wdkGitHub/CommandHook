package blog.dekun.wang.command.hook.ui.components

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.table.JBTable
import java.awt.Component
import javax.swing.*
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableCellRenderer

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */

class Tables {

    companion object {

        fun commandTables(): JPanel {
            val columns = arrayOf("Name", "Command", "Is Only Project")
            val data = arrayOf(
                arrayOf("Command 1", "command1", false),
                arrayOf("Command 2", "command2", true)
            )
            val model = DefaultTableModel(data, columns)

            val table = JBTable(model)
            val columnModel = table.getColumnModel()
            SwingUtilities.invokeLater {
                // 获取正确的表格宽度
                val totalWidth = table.width
                val nameColumnWidth = (totalWidth * 0.2).toInt()
                val commandColumnWidth = (totalWidth * 0.7).toInt()
                val projectColumnWidth = (totalWidth * 0.1).toInt()

                // 设置列宽

                columnModel.getColumn(0).preferredWidth = nameColumnWidth
                columnModel.getColumn(1).preferredWidth = commandColumnWidth
                columnModel.getColumn(2).preferredWidth = projectColumnWidth
            }

            table.setAutoCreateRowSorter(true)
            val checkBox = JCheckBox()
            columnModel.getColumn(1).cellEditor = DefaultCellEditor(ExpandableTextField())
            columnModel.getColumn(1).cellRenderer = ExpandableTextFieldCellRenderer()
            columnModel.getColumn(2).cellEditor = DefaultCellEditor(checkBox)
            columnModel.getColumn(2).cellRenderer = CheckBoxCellRenderer()

            return ToolbarDecorator.createDecorator(table)
                .setAddAction {
                    val newRow = arrayOf("New Command", "new_command", false)
                    model.addRow(newRow)
                }
                .setRemoveAction {
                    val selectedRow = table.selectedRow
                    if (selectedRow >= 0) {
                        model.removeRow(selectedRow)
                    }
                }
                .createPanel()
        }

        fun paramTables(): JPanel {
            val columns = arrayOf("Name", "params")
            val data = arrayOf(
                arrayOf("Command 1", "command1"),
                arrayOf("Command 2", "command2")
            )
            val model = DefaultTableModel(data, columns)

            val table = JBTable(model)
            val columnModel = table.getColumnModel()
            SwingUtilities.invokeLater {
                // 获取正确的表格宽度
                val totalWidth = table.width
                val nameColumnWidth = (totalWidth * 0.2).toInt()
                val commandColumnWidth = (totalWidth * 0.8).toInt()
                columnModel.getColumn(0).preferredWidth = nameColumnWidth
                columnModel.getColumn(1).preferredWidth = commandColumnWidth
            }

            table.setAutoCreateRowSorter(true)
            columnModel.getColumn(1).cellEditor = DefaultCellEditor(ExpandableTextField())
            columnModel.getColumn(1).cellRenderer = ExpandableTextFieldCellRenderer()

            return ToolbarDecorator.createDecorator(table)
                .setAddAction {
                    val newRow = arrayOf("New Command", "new_command")
                    model.addRow(newRow)
                }
                .setRemoveAction {
                    val selectedRow = table.selectedRow
                    if (selectedRow >= 0) {
                        model.removeRow(selectedRow)
                    }
                }
                .createPanel()
        }
    }

    class CheckBoxCellRenderer : TableCellRenderer {

        private val checkBox: JCheckBox = JCheckBox()
        override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
            checkBox.isSelected = value as? Boolean ?: false
            return checkBox
        }
    }

    class ExpandableTextFieldCellRenderer : TableCellRenderer {

        private val expandableTextField: ExpandableTextField = ExpandableTextField()
        override fun getTableCellRendererComponent(table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component? {
            expandableTextField.text = value?.toString() ?: ""
            return expandableTextField
        }
    }
}


