package blog.dekun.wang.command.hook.ui.components

import com.intellij.openapi.ui.MasterDetailsComponent
import com.intellij.openapi.util.NlsContexts

/**
 *  @see com.jetbrains.plugins.webDeployment.ui.config.DeploymentServersEditor
 *  @see com.jetbrains.plugins.webDeployment.actions.ConfigureDeploymentAction
 */
class TestMasterDetailsComponent : MasterDetailsComponent() {


    init {

        super.initTree()
        super.initUi()
    }


    override fun getDisplayName(): @NlsContexts.ConfigurableName String? {
        return "ActionConfigListUI"
    }


}



