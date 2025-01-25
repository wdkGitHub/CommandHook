package blog.dekun.wang.command.hook.constants


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


object Constant {

    private const val SIGN = "wdk"

    const val ACTION_GROUP_ID = "blog.dekun.wang.action.group"

    const val ACTION_GROUP_ID_RIGHT_CLICK = "${SIGN}.right.click"

    const val WELCOME_SCREEN = "WelcomeScreen"

    const val MAIN_TOOLBAR = "MainToolbar"


    //<editor-fold desc="持久化">
    const val STATE_NAME = ACTION_GROUP_ID

    const val WORKSPACE_CONFIG_XML_FILE_NAME = "wdk.workspace.xml"

    const val APP_CONFIG_XML_FILE_NAME = "wdk.app.xml"
    //</editor-fold>


    const val NOTIFICATION_GROUP_ID = ACTION_GROUP_ID

    const val NOTIFICATION_GROUP_AN_ACTION_ID = "AnActionIdNotificationGroup"


    const val PLUGIN_HOSTS = "https://raw.githubusercontent.com/wdkGitHub/CommandHook/refs/heads/master/updatePlugins.xml"
}