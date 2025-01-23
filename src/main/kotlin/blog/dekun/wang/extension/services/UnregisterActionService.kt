package blog.dekun.wang.extension.services

import blog.dekun.wang.extension.constants.Constant
import blog.dekun.wang.extension.data.UnregisterActionConfig
import blog.dekun.wang.extension.data.UnregisterActions
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */

@Service(Service.Level.APP)
@State(name = Constant.NOTIFICATION_GROUP_AN_ACTION_ID, storages = [(Storage(value = Constant.APP_CONFIG_XML_FILE_NAME))])
class UnregisterActionService : PersistentStateComponent<UnregisterActionConfig> {

    private var unregisterActionConfig: UnregisterActionConfig = UnregisterActionConfig()
    override fun getState(): UnregisterActionConfig = unregisterActionConfig

    override fun loadState(unregisterActionConfig: UnregisterActionConfig) {
        this.unregisterActionConfig = unregisterActionConfig
    }

    companion object {

        fun getInstance(): UnregisterActionService {
            return ApplicationManager.getApplication().getService(UnregisterActionService::class.java)
        }

        fun unregisterActions(): List<UnregisterActions> {
            return getInstance().state.actions
        }

        fun isEnableNotify(): Boolean {
            return getInstance().state.isEnableAnActionIdNotificationGroup
        }

        fun toggleNotify() {
            getInstance().state.isEnableAnActionIdNotificationGroup = !getInstance().state.isEnableAnActionIdNotificationGroup
        }


        fun unregister() {
            val actionManager = ActionManager.getInstance()
            getInstance().state.actions.filter { it.isUnregister && it.actionId.isNotBlank() }.forEach {
                actionManager.unregisterAction(it.actionId)
            }
        }

        fun unregisterActions(unregisterActions: List<UnregisterActions>) {
            getInstance().state.actions = unregisterActions
            val actionManager = ActionManager.getInstance()
            unregisterActions.filter { it.actionId.isNotBlank() }.forEach {
                if (it.isUnregister) {
                    actionManager.unregisterAction(it.actionId)
                }
            }
        }
    }


}