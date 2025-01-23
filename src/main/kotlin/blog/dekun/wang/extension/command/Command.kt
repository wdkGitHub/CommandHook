package blog.dekun.wang.extension.command

import blog.dekun.wang.extension.constants.CommandType
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.SystemInfo
import java.io.BufferedReader
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

        fun executeDefaultDir(commands: List<String>, dirPath: String? = null): String {
            return execute(commands, dirPath ?: ProjectManager.getInstance().openProjects.firstOrNull()?.basePath ?: System.getProperty("user.home"))
        }

        fun execute(commands: List<String>, dirPath: String? = null): String {
            val commandStr = commands.joinToString(" ")
            println("执行的命令：$commandStr $dirPath")
            val processBuilder = ProcessBuilder(commands)
            dirPath?.let { processBuilder.directory(java.io.File(it)) }
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

        // 创建一个全局缓存对象
        private val resultCache = ResultCache()

        // 更新 gitRemote 函数，增加缓存
        fun gitRemote(gitRepoRootPath: String): String {
            // 检查缓存中是否已有结果
            val cacheKey = "gitRemote:$gitRepoRootPath"
            resultCache.get(cacheKey)?.let {
                return it  // 如果缓存中有结果，直接返回
            }

            // 执行 git 命令并缓存结果
            if (SystemInfo.isMac) {
                val result = execute(listOf("git", "remote", "-v"), gitRepoRootPath)
                if (result.isNotEmpty()) {
                    resultCache.put(cacheKey, result)  // 缓存结果
                }
                return result
            }
            return ""
        }

        // 更新 gitRevParseShowTopLevel 函数，增加缓存
        fun gitRevParseShowTopLevel(path: String): String {
            // 检查缓存中是否已有结果
            val cacheKey = "gitRevParseShowTopLevel:$path"
            resultCache.get(cacheKey)?.let {
                return it  // 如果缓存中有结果，直接返回
            }

            // 执行 git 命令并缓存结果
            if (SystemInfo.isMac) {
                val result = execute(listOf("git", "rev-parse", "--show-toplevel"), path)
                if (result.isNotEmpty()) {
                    resultCache.put(cacheKey, result)  // 缓存结果
                }
                return result
            }
            return ""
        }

        // 更新 isInstall 函数，增加缓存
        fun isInstall(mMDItemCFBundleIdentifier: String): Boolean {
            val cacheKey = "isInstall:$mMDItemCFBundleIdentifier"
            resultCache.get(cacheKey)?.let {
                return it.toBoolean()  // 如果缓存中有结果，直接返回
            }

            // 执行 mdfind 命令并缓存结果
            if (SystemInfo.isMac) {
                val result = execute(listOf("mdfind", "\"kMDItemCFBundleIdentifier == $mMDItemCFBundleIdentifier\""))
                if (result.isNotEmpty()) {
                    resultCache.put(cacheKey, result)  // 缓存结果
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

class ResultCache(private val maxSize: Int = 100) {

    // 使用 LinkedHashMap 来保持插入顺序
    private val cache = LinkedHashMap<String, String>(maxSize, 0.75f, true)

    /**
     * 获取缓存中的值，如果没有则返回 null
     */
    fun get(key: String): String? {
        return cache[key]
    }

    /**
     * 将数据添加到缓存中，如果缓存已满，删除最早添加的项
     */
    fun put(key: String, value: String) {
        if (cache.size >= maxSize) {
            // 删除最早添加的项
            val firstKey = cache.entries.iterator().next().key
            cache.remove(firstKey)
        }
        cache[key] = value
    }

    /**
     * 清除缓存
     */
    fun clear() {
        cache.clear()
    }
}

