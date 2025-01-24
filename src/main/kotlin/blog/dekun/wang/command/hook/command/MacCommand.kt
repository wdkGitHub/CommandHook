package blog.dekun.wang.command.hook.command

import blog.dekun.wang.command.hook.command.Command.Companion.isInstall
import blog.dekun.wang.command.hook.constants.CommandType
import blog.dekun.wang.command.hook.constants.KMDItemCFBundleIdentifier
import com.intellij.openapi.diagnostic.Logger


/**
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


class MacCommand : Command {

    private val logger = Logger.getInstance(Command::class.java)
    override fun execute(dirPath: String, commandType: CommandType) {
        when (commandType) {
            CommandType.TERMINAL_APP -> openTerminal(dirPath)
            CommandType.FORK_APP -> fork(dirPath)
            CommandType.SOURCE_TREE_APP -> sourceTree(dirPath)
            CommandType.GIT_OPEN_COMMAND -> gitOpen(dirPath)
            CommandType.TYPORA_APP -> typora(dirPath)
            else -> {
                listOf("echo \$SHELL")
            }
        }
    }


    private fun typora(dirOrFile: String) {
        logger.info(dirOrFile)
        execute(listOf("open", "-b", KMDItemCFBundleIdentifier.TYPORA.bundleIdentifier, dirOrFile))
    }

    private fun gitOpen(dirPath: String) {
        execute(listOf(System.getProperty("user.home") + "/.oh-my-zsh/custom/plugins/git-open/git-open"), dirPath)
    }

    private fun openTerminal(dirPath: String) {
        val bundleIdentifier = if (isInstall(KMDItemCFBundleIdentifier.ITERM2.bundleIdentifier)) {
            KMDItemCFBundleIdentifier.ITERM2.bundleIdentifier
        } else {
            KMDItemCFBundleIdentifier.TERMINAL.bundleIdentifier
        }
        execute(listOf("open", "-b", bundleIdentifier, dirPath))
    }

    private fun fork(dirPath: String) {
        execute(listOf("/usr/local/bin/fork", "open", "."), dirPath)
    }

    private fun sourceTree(dirPath: String) {
        execute(listOf("/usr/local/bin/stree", "."), dirPath)
    }

}