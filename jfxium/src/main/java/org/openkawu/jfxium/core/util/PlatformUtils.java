package org.openkawu.jfxium.core.util;

/**
 * 操作系统判断工具（M19.31 引入）。
 *
 * <p>JavaFX 应用经常需要根据操作系统调整细节（快捷键 Cmd vs Ctrl、字体回退、
 * 窗口装饰、native 文件对话框等）。本工具集封装 {@code System.getProperty("os.name")}
 * 的字符串判断，避免每个调用方各自拼接 lowerCase / contains。</p>
 *
 * <h2>用法</h2>
 * <pre>{@code
 * if (PlatformUtils.isMac()) {
 *     accelerator = new KeyCodeCombination(KeyCode.S, KeyCombination.META_DOWN);
 * } else {
 *     accelerator = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
 * }
 * }</pre>
 *
 * <h2>设计契约</h2>
 * <ul>
 *   <li><b>启动期一次解析</b>：{@code os.name} 在 JVM 生命周期内不变，
 *       只读一次缓存到 static final 字段</li>
 *   <li><b>大小写无关</b>：内部统一用 lowerCase 比对，避免不同 JVM 实现差异</li>
 *   <li><b>{@link #isUnix()} 含 Mac</b>：macOS 是 Unix-like 系统，也认为是 Unix
 *       （这点跟 AtlantaFX 行为一致）</li>
 * </ul>
 *
 * <p>设计参考 <a href="https://github.com/mkpaz/atlantafx">AtlantaFX</a>
 * 的 {@code base/util/PlatformUtils.java}，让两个项目互相阅读时无翻译成本。</p>
 */
public final class PlatformUtils {

    /** 启动时一次性解析 os.name，缓存到 static final 避免重复读取系统属性。 */
    private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase();

    private PlatformUtils() {
        // 工具类禁实例化
    }

    /** 当前是否运行在 Windows 上。 */
    public static boolean isWindows() {
        return OS_NAME.contains("windows");
    }

    /** 当前是否运行在 macOS 上（os.name 通常为 "Mac OS X"）。 */
    public static boolean isMac() {
        return OS_NAME.contains("mac");
    }

    /** 当前是否运行在 Linux 上。 */
    public static boolean isLinux() {
        return OS_NAME.contains("linux");
    }

    /**
     * 当前是否运行在 Unix-like 系统上（含 macOS / Linux / Solaris / FreeBSD）。
     *
     * <p>注意：macOS 也会返回 {@code true}，因为它是 Darwin / BSD 内核的 Unix。
     * 想精确区分 Mac vs 其他 Unix 时，先调 {@link #isMac()}。</p>
     */
    public static boolean isUnix() {
        return isMac() || isLinux()
                || OS_NAME.contains("nix")    // AIX / IRIX / HP-UX
                || OS_NAME.contains("nux")    // Linux 异写
                || OS_NAME.contains("sunos"); // Solaris
    }
}
