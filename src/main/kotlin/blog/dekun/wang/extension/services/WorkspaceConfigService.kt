package blog.dekun.wang.extension.services

import blog.dekun.wang.extension.constants.Constant
import blog.dekun.wang.extension.data.ConfigInfo
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *@see com.intellij.ide.projectView.impl.ProjectViewState
 */

@Service(Service.Level.PROJECT)
@State(name = Constant.STATE_NAME, storages = [(Storage(value = Constant.WORKSPACE_CONFIG_XML_FILE_NAME))])
class WorkspaceConfigService : PersistentStateComponent<WorkspaceConfigService.State> {

    data class State(var commands: MutableList<ConfigInfo> = mutableListOf())

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }


    // 显式通知框架状态已更改
//    ProjectManager.getInstance().save()

    companion object {

        fun getInstance(project: Project): WorkspaceConfigService {
            return project.getService(WorkspaceConfigService::class.java)
        }
    }
}


/*

    // @Override
    // public void actionPerformed(AnActionEvent e) {
    //     // WorkspaceConfigService instance = WorkspaceConfigService.Companion.getInstance(Objects.requireNonNull(e.getProject()));
    //     //
    //     // // 读取当前状态
    //     // WorkspaceConfigService.State state = instance.getState();
    //     // state.getCommands().forEach(System.out::println);
    //     //
    //     // // 修改状态
    //     // List<Config> newCommands = new ArrayList<>(state.getCommands());
    //     // newCommands.add(new Config("Build", "gradle build"));
    //     // instance.loadState(new WorkspaceConfigService.State(newCommands));
    //     //
    //     // // 验证保存后的状态
    //     // instance.getState().getCommands().forEach(System.out::println);
    //
    //     AppConfigService instance = AppConfigService.Companion.getInstance();
    //
    //     // 获取当前命令列表
    //     List<Config> commands = instance.getState().getCommands();
    //     commands.forEach(System.out::println);
    //
    //     // 添加新命令
    //     commands.add(new Config("Clean", "gradle clean"));
    //     commands.forEach(System.out::println);
    //     // instance.loadState(new AppConfigService.State(commands));
    //     Config config = new Config("Clean", "gradle clean");
    //     instance.addCommand(config);
    //     instance.getState().getCommands().forEach(System.out::println);
    //
    // }
*/