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
    var name: String = "",
    var enable: Boolean = true,
    var onlyProject: Boolean = true,
    var workingDirectory: String? = null,
    var commandParams: MutableMap<String, String> = mutableMapOf(),
    var commandStr: String = "",
    var position: ActionPosition = ActionPosition.DEFAULT,
    var index: Int = 0
) {

    fun hasDynamicParameters(): Map<String, List<String>> {
        val result = mutableMapOf<String, List<String>>()
        for ((key, value) in commandParams) {
            if (value.contains(",")) {
                result[key] = value.split(",")
            }
        }
        return result
    }
    fun copy() = ActionConfig(
        name = name,
        enable = enable,
        onlyProject = onlyProject,
        workingDirectory = workingDirectory,
        commandParams = commandParams.toMutableMap(),
        commandStr = commandStr,
        position = position,
        index = index
    )
}

data class TemplateConfig(
    var name: String = "",
    var value: String = "",
    var onlyProject: Boolean = true,
    var index: Int = 0
)