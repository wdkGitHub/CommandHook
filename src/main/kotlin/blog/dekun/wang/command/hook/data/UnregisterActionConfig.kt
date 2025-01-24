package blog.dekun.wang.command.hook.data

/**
 * @author WangDeKun
 *
 *
 * Email :  wangdekunemail@gmail.com
 */
data class UnregisterActionConfig(
    var isEnableAnActionIdNotificationGroup: Boolean = true,
    var actions: List<UnregisterActions> = listOf()

)

data class UnregisterActions(
    var actionId: String = "",
    var description: String? = null,
    var isUnregister: Boolean = false
)

