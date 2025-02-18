package blog.dekun.wang.command.hook.ui.components

import blog.dekun.wang.command.hook.data.ActionConfig
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.event.ListDataEvent
import javax.swing.event.ListDataListener
import javax.swing.event.ListSelectionEvent

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *https://plugins.jetbrains.com/docs/intellij/empty-state.html#master-detail-layout
 */

class ActionConfigList(val listModel: DefaultListModel<ActionConfig>, val detailPanel: (config: ActionConfig?) -> JPanel) {


    private lateinit var masterList: JBList<DefaultListModel<ActionConfig>>


    fun masterDetailPanel() = JPanel(BorderLayout()).apply {
        val splitPanel = JPanel(null).apply {
            preferredSize = Dimension(800, 600)
            layout = null
            val masterPanel = masterPanel()
            masterPanel.setBounds(0, 0, 200, 600)
            add(masterPanel)
            val invoke = detailPanel.invoke(null)
            invoke.setBounds(200, 0, 600, 600)
            add(invoke)
        }
        add(splitPanel, BorderLayout.CENTER)
    }

    fun masterPanel() = ToolbarDecorator.createDecorator(masterList).apply {
        addExtraAction(object : AnAction("Copy") {
            override fun getActionUpdateThread() = ActionUpdateThread.BGT

            override fun update(e: AnActionEvent) {
                e.presentation.isEnabled = masterList.selectedIndex != -1
            }

            override fun actionPerformed(e: AnActionEvent) {
                // 获取选中的项
                val selectedIndex = masterList.selectedIndex
                if (selectedIndex == -1) return
                // 获取数据模型
                val model = masterList.model as DefaultListModel<ActionConfig>
                val value = model.getElementAt(selectedIndex)
                val newName = JOptionPane.showInputDialog(null, "请输入名称：", "添加命令", JOptionPane.PLAIN_MESSAGE)
                model.add(selectedIndex + 1, value.copy(name = newName)) // 根据需求调整复制逻辑
                masterList.selectedIndex = selectedIndex + 1 // 选中新项
            }


        })
        setAddAction { JOptionPane.showInputDialog(null, "请输入名称：", "添加命令", JOptionPane.PLAIN_MESSAGE) }
    }.createPanel()

    fun init() {
        listModel.addListDataListener(object : ListDataListener {
            override fun intervalRemoved(e: ListDataEvent) {
                if (listModel.isEmpty()) {
                    masterList.selectedIndex = -1 // 强制清除选中状态
                    detailPanel.apply {
                        detailPanel.invoke(null)
                    }
                }
            }

            override fun contentsChanged(e: ListDataEvent) {}
            override fun intervalAdded(e: ListDataEvent) {}
        })
        // 监听鼠标点击，拦截空白处点击
        masterList.apply {
            // 初始化最后选中的索引
            var lastSelectedIndex = -1
            border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
            cellRenderer = object : DefaultListCellRenderer() {
                override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus).apply {
                        (this as JLabel).text = (value as? ActionConfig)?.name ?: ""
                    }
                }
            }
            addMouseListener(object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    if (listModel.size == 0) {
                        masterList.selectedIndex = -1
                        lastSelectedIndex = -1
                        return
                    }
                    // 获取鼠标点击的 Y 坐标
                    val y = e.y
                    // 检查是否点击了有效项
                    val index = masterList.locationToIndex(e.point)
                    val itemHeight = masterList.getCellBounds(0, 0).height // 获取单个项的高度
                    // 如果点击的位置超出了列表项的区域
                    if (index == -1 || y < 0 || y >= itemHeight * listModel.size) {
                        if (lastSelectedIndex == -1) {
                            // 第一次点击空白处，默认选中第一项
                            masterList.selectedIndex = 0
                            lastSelectedIndex = 0
                        } else {
                            // 否则，回退到上次选中的项
                            masterList.selectedIndex = lastSelectedIndex
                        }
                    }
                }
            })
            addListSelectionListener { e: ListSelectionEvent ->
                if (!e.valueIsAdjusting) { // 关键过滤条件
                    if (selectedIndex != -1) {
                        lastSelectedIndex = selectedIndex
                    }
                    lastSelectedIndex = if (model.size > 0) selectedIndex else -1
                    // 强制清空条件判断
                    if (model.size == 0 || selectedValue == null) {
                        detailPanel.apply {
                            removeAll()
                            add(JLabel("没有可显示的内容", JLabel.CENTER))
                            revalidate()
                            repaint()
                        }
                    } else {
                        detailPanel.apply {
//                        CommandActionConfigUI.updateData(
//                            selectedValue,
//                            DefaultComboBoxModel(paramTemplate.toTypedArray()),
//                            DefaultComboBoxModel(commandTemplate.toTypedArray())
//                        )
                        }
                    }
                }
            }
        }
    }


}
