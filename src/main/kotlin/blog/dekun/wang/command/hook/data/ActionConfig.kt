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
    var commandParams: List<String>? = null,
    var commandStr: String,
    var position: ActionPosition = ActionPosition.DEFAULT,
) {

    constructor(name: String, commandStr: String) : this(name, true, true, null, null, commandStr, ActionPosition.DEFAULT)
    constructor(name: String, commandStr: String, position: ActionPosition) : this(name, true, true, null, null, commandStr, position)
}

data class TemplateConfig(val name: String = "", val value: String = "", val onlyProject: Boolean = true)

