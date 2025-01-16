package blog.dekun.wang.extension.utils


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class DynamicAnAction {

    companion object {
    }
}

/*
package blog.dekun.wang.extension.test.CommandExtensions;


import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 */


public class ConfigX extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setText("配置");
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // 获取项目上下文
        Project project = anActionEvent.getProject();

        if (project != null) {
            ActionManager actionManager = ActionManager.getInstance();

            // 定义新的 AnAction
            AnAction newAction = new AnAction("New Dynamic Action") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    System.out.println("新动作被点击！");
                }
            };

            // 为这个动作设置一个唯一的 ID
            String actionId = "DynamicAction" + System.currentTimeMillis();

            // 注册新动作
            actionManager.registerAction(actionId, newAction);

            // 获取目标菜单或工具栏，将新动作添加到其中
            DefaultActionGroup group = (DefaultActionGroup) actionManager.getAction("CommandExtensions");
            if (group != null) {
                group.add(newAction);
            }

            // 刷新工具栏或菜单
            // refreshActionToolbar("MyToolbar");
        }
    }

    /**
     * 刷新工具栏的方法，确保工具栏动作得到更新
     */
    private void refreshActionToolbar(@NotNull String toolbarId) {
        ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar toolbar = actionManager.createActionToolbar(toolbarId, (ActionGroup) actionManager.getAction(toolbarId), false);
        // 重新布局和渲染工具栏
        toolbar.getComponent().revalidate();
        toolbar.getComponent().repaint();
    }

}



 */