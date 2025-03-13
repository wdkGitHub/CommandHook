package blog.dekun.wang.command.hook.command

import blog.dekun.wang.command.hook.constants.CommandType
import com.intellij.openapi.util.SystemInfo
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

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

        fun execute(commands: List<String>, dirPath: String? = null): String {
            val commandStr = commands.joinToString(" ")
            println("执行的命令：$commandStr $dirPath")
            val processBuilder = ProcessBuilder(listOf(System.getenv("SHELL") ?: "/bin/bash", "-c", commandStr))
            dirPath?.takeIf { File(it).isDirectory }?.let { processBuilder.directory(File(it)) }
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
                    resultCache.put(cacheKey, "true")  // 缓存结果
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

    // 线程安全的缓存
    private val cache = ConcurrentHashMap<String, String>()

    // 维护插入顺序
    private val keys = ConcurrentLinkedQueue<String>()

    fun get(key: String): String? {
        return cache[key]
    }

    /**
     * 将数据添加到缓存中，如果缓存已满，删除最早添加的项
     */
    fun put(key: String, value: String) {
        synchronized(this) {
            if (cache.size >= maxSize) {
                val oldestKey = keys.poll()  // 移除最早的键
                if (oldestKey != null) {
                    cache.remove(oldestKey)
                }
            }
            cache[key] = value
            keys.add(key)  // 记录新插入的键
        }
    }

    /**
     * 清除缓存
     */
    fun clear() {
        synchronized(this) {
            cache.clear()
            keys.clear()
        }
    }
}

