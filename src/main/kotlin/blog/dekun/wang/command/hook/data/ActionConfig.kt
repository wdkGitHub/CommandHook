package blog.dekun.wang.command.hook.data


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */

enum class ActionPosition {

    DEFAULT, CENTRAL_TOOLBAR, RIGHT_CLICK
}

data class ActionConfig(
    var name: String,
    var enable: Boolean = true, var onlyProject: Boolean = true,
    var workingDirectory: String? = null,
    var commandParams: MutableMap<String, String> = mutableMapOf(),
    var commandStr: String,
    var position: ActionPosition = ActionPosition.DEFAULT,
) {

    companion object {

        fun default(name: String, commandStr: String): ActionConfig {
            return ActionConfig(name, commandStr)
        }

        fun empty(): ActionConfig {
            return ActionConfig("", false, true, null, mutableMapOf(), "", ActionPosition.DEFAULT)
        }
    }

    constructor(name: String, commandStr: String) : this(name, true, true, null, mutableMapOf(), commandStr, ActionPosition.DEFAULT)
    constructor(name: String, commandStr: String, position: ActionPosition) : this(name, true, true, null, mutableMapOf(), commandStr, position)
}

data class TemplateConfig(val name: String = "", val value: String = "", val onlyProject: Boolean = true)

