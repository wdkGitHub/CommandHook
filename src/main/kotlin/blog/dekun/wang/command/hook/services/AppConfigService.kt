//package blog.dekun.wang.command.hook.services
//
//import blog.dekun.wang.command.hook.constants.Constant
//import blog.dekun.wang.command.hook.data.ConfigInfo
//import com.intellij.openapi.application.ApplicationManager
//import com.intellij.openapi.components.PersistentStateComponent
//import com.intellij.openapi.components.Service
//import com.intellij.openapi.components.State
//import com.intellij.openapi.components.Storage
//
//
///**
// *
// * @author WangDeKun
// * <p>
// * Email :  wangdekunemail@gmail.com
// *@see com.intellij.ide.projectView.impl.ProjectViewState
// */
//
//@Service(Service.Level.APP)
//@State(name = Constant.STATE_NAME, storages = [(Storage(value = Constant.APP_CONFIG_XML_FILE_NAME))])
//class AppConfigService : PersistentStateComponent<AppConfigService.State> {
//
//    // 定义状态类
//    data class State(var commands: MutableList<ConfigInfo> = mutableListOf())
//
//    private var state = State()
//
//    override fun getState(): State = state
//
//    override fun loadState(state: State) {
//        this.state = state
//    }
//
//
//    companion object {
//
//        fun getInstance(): AppConfigService {
//            return ApplicationManager.getApplication().getService(AppConfigService::class.java)
//        }
//
//    }
//}
