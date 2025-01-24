package blog.dekun.wang.command.hook.test

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import javax.swing.*
import javax.swing.table.DefaultTableModel

class ConfigToolWindow {

    private val mainPanel: JPanel = JPanel()
    private val tableModel: DefaultTableModel
    private val table: JTable

    init {
        // 定义表格的列名
        val columnNames = arrayOf("Command", "Name")
        // 定义表格模型
        tableModel = DefaultTableModel(columnNames, 0)
        table = JTable(tableModel)
        mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)

        // 添加标题行
        val titleLabel = JLabel("Command Configuration")
        titleLabel.font = titleLabel.font.deriveFont(16f) // 设置字体大小
        mainPanel.add(titleLabel)

        // 向主面板添加表格
        mainPanel.add(JScrollPane(table))

        // 添加“添加行”按钮
        val addButton = JButton("Add Row")
        addButton.addActionListener { addRow() }
        mainPanel.add(addButton)

        // 添加“保存”按钮
        val saveButton = JButton("Save")
        mainPanel.add(saveButton)
    }

    private fun addRow() {
        // 为表格添加一行空数据
        tableModel.addRow(arrayOf("", ""))
    }

    fun getContent(): Content {
        val toolWindowPanel = SimpleToolWindowPanel(true, true).apply { setContent(mainPanel) }
        val contentFactory = ContentFactory.getInstance()
        return contentFactory.createContent(toolWindowPanel, null, false).apply { isCloseable = false }
    }
}


//class ConfigToolWindow {
//
//    private val mainPanel: JPanel = JPanel()
//
//    init {
//        mainPanel.layout = javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS)
//        mainPanel.add(JLabel("Welcome to My Tool Window!"))
//        val button = JButton("Click Me")
//        button.addActionListener { _: ActionEvent? ->
//            val service = ApplicationManager.getApplication().getService(AppConfigService::class.java)
//            service.state
//            JOptionPane.showMessageDialog(mainPanel, "Button clicked! ${service.test}")
//        }
//        mainPanel.add(button)
//    }
//
//    fun getContent(): Content {
//        val toolWindowPanel = SimpleToolWindowPanel(true, true).apply { setContent(mainPanel) }
//        val contentFactory = ContentFactory.getInstance()
//        return contentFactory.createContent(toolWindowPanel, null, false).apply { isCloseable = false }
//    }
//}


// 名字  命令
