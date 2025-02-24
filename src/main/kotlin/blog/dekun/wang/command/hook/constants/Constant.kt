package blog.dekun.wang.command.hook.constants


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


object Constant {

    const val ACTION_GROUP_ID = "blog.dekun.wang.action.group"

    //<editor-fold desc="内置组ID">
    const val WELCOME_SCREEN = "WelcomeScreen"

    const val MAIN_TOOLBAR = "MainToolbar"

    const val MAIN_TOOLBAR_CENTER = "MainToolbarCenter"
    //</editor-fold>


    //<editor-fold desc="持久化">
    const val STATE_NAME = ACTION_GROUP_ID

    const val STATE_NAME_TEMPLATE_PARAM = "$ACTION_GROUP_ID.params"

    const val STATE_NAME_TEMPLATE_COMMAND = "$ACTION_GROUP_ID.commands"

    const val WORKSPACE_CONFIG_XML_FILE_NAME = "wdk.workspace.xml"

    const val APP_CONFIG_XML_FILE_NAME = "wdk.app.xml"
    //</editor-fold>

    const val NOTIFICATION_GROUP_ID = ACTION_GROUP_ID

    const val NOTIFICATION_GROUP_AN_ACTION_ID = "AnActionIdNotificationGroup"

    const val PLUGIN_HOSTS = "https://raw.githubusercontent.com/wdkGitHub/CommandHook/refs/heads/master/updatePlugins.xml"
}