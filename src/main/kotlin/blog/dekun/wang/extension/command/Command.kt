package blog.dekun.wang.extension.command

import blog.dekun.wang.extension.constants.CommandType
import com.intellij.openapi.util.SystemInfo
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


interface Command {


    fun execute(dirPath: String, commandType: CommandType) {
        throw NotImplementedError("Not implemented")
    }


    companion object {

        fun isSupport(): Boolean {
            return when {
                SystemInfo.isMac -> true
                else -> false
            }
        }

        fun build(): Command {
            return if (SystemInfo.isMac) {
                MacCommand()
            } else {
                val osNameAndVersion = SystemInfo.getOsNameAndVersion()
                throw UnsupportedOperationException("$osNameAndVersion is not supported")
            }
        }


        fun execute(commands: String, dirPath: String? = null): String {
            return execute(commands.split(" "), dirPath)
        }

        fun execute(commands: List<String>, dirPath: String? = null): String {
            val processBuilder = ProcessBuilder(commands)
            dirPath?.let { processBuilder.directory(File(it)) }
            val joinToString = commands.joinToString(" ")
            println("执行的命令：$joinToString")
            processBuilder.redirectErrorStream(true)
            val process = processBuilder.start()
            val output = StringBuilder()
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    output.appendLine(line)
                }
            }
            process.waitFor()
            return output.toString().trim()
        }

        fun gitRemote(gitRepoRootPath: String): String {
            if (SystemInfo.isMac) {
                return execute(listOf(System.getenv("SHELL"), "-c", "git remote -v"), gitRepoRootPath)
            }
            return ""
        }

        fun gitRevParseShowTopLevel(path: String): String {
            if (SystemInfo.isMac) {
                return execute(listOf(System.getenv("SHELL"), "-c", "git rev-parse --show-toplevel"), path)
            }
            return ""
        }

        fun isInstall(mMDItemCFBundleIdentifier: String): Boolean {
            if (SystemInfo.isMac) {
                val result = execute(listOf(System.getenv("SHELL"), "-c", "mdfind \"kMDItemCFBundleIdentifier == $mMDItemCFBundleIdentifier\""))
                if (result.isNotEmpty()) {
                    return true
                }
            }
            return false
        }

    }

    /**
     * 备注：listOf(System.getenv("SHELL"), "-c", "open -b com.googlecode.iterm2 .")
     */
    fun execute(commands: List<String>): String {
        return execute(commands, null)
    }

    fun execute(commands: List<String>, dirPath: String?): String {
        return Command.execute(commands, dirPath)
    }


}
