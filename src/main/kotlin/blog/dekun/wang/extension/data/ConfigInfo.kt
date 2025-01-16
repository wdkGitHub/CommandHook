package blog.dekun.wang.extension.data

/**
 * @author WangDeKun
 *
 *
 * Email :  wangdekunemail@gmail.com
 */
data class ConfigInfo(
    /**
     * 是否是应用
     */
    var isApp: Boolean? = null,

    /**
     * 名字
     */
    var name: String = "",

    /**
     * 命令
     *
     * $target
     */
    var commandStr: String? = null,

    /**
     * 指定命令执行目录
     */
    var executionDir: String? = null,

    /**
     * 命令目标是文件
     */
    var isTargetFile: Boolean? = null,

    /**
     * 命令目标是文件夹
     */
    var isTargetFolder: Boolean? = null,

    /**
     * 右键
     */
    var isRightClick: Boolean? = null,

    /**
     * 是否可用
     */
    var isEnable: Boolean? = null,

    var index: Int = -1

) {

    constructor(name: String, index: Int, commandStr: String? = null, executionDir: String? = null) : this() {
        this.isApp = false
        this.isTargetFile = false
        this.isTargetFolder = false
        this.isRightClick = false
        this.isEnable = true
        this.name = name
        this.index = index
        this.commandStr = commandStr
        this.executionDir = executionDir
    }

    fun equalsValue(other: ConfigInfo?): Boolean {
        return other?.let {
            return this.name == it.name
                    && this.index == it.index
                    && this.isApp == it.isApp
                    && this.isTargetFile == it.isTargetFile
                    && this.isTargetFolder == it.isTargetFolder
                    && this.isRightClick == it.isRightClick
                    && this.isEnable == it.isEnable
                    && this.commandStr == it.commandStr
                    && this.executionDir == it.executionDir
        } ?: false
    }

}