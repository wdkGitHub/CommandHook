package icons

import com.intellij.openapi.util.IconLoader.getIcon
import javax.swing.Icon

/**
 * @author WangDeKun
 * @see [SDK](https://www.jetbrains.org/intellij/sdk/docs/reference_guide/work_with_icons_and_images.html)
 * <br></br>
 */
object Icons {

    @JvmField
    val TERMINAL_ICON: Icon = getIcon("/icons/terminal.png", Icons::class.java)

    @JvmField
    val TYPORA: Icon = getIcon("/icons/typora.svg", Icons::class.java)

    @JvmField
    val FORK: Icon = getIcon("/icons/fork.svg", Icons::class.java)

    @JvmField
    val SOURCETREE: Icon = getIcon("/icons/sourcetree.svg", Icons::class.java)
    
}
