package blog.dekun.wang.extension.constants


/**
 * 使用 open -b com.apple.Terminal.
 * @param bundleIdentifier 使用 mdls -name kMDItemCFBundleIdentifier /System/Applications/Utilities/Terminal.app 获取
 *
 * @author WangDeKun
 * <p>
 * Email :  wangdekunemail@gmail.com
 *
 */


enum class KMDItemCFBundleIdentifier(val bundleIdentifier: String) {

    TYPORA("abnerworks.Typora"),
    TERMINAL("com.apple.Terminal"),
    ITERM2("com.googlecode.iterm2"),
    ;
}