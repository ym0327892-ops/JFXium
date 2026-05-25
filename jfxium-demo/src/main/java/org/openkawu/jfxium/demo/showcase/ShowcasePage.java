package org.openkawu.jfxium.demo.showcase;

import javafx.scene.Node;

/**
 * Showcase 单组件展示页接口。
 *
 * <p>每个 *Ant 组件对应一个 ShowcasePage 实现类，集中展示该组件的：</p>
 * <ul>
 *   <li>所有姿态（变体、尺寸、状态）</li>
 *   <li>API 用法（链式 Builder 各方法）</li>
 *   <li>典型场景（admin 中常用搭配）</li>
 *   <li>源码片段（折叠区，可复制粘贴）</li>
 * </ul>
 */
public interface ShowcasePage {

    /** 组件标识，用于路由。约定使用 *Ant 类的小写名（不含 Ant 后缀），如 "table"/"button"/"watermark"。 */
    String key();

    /** 组件展示名（左侧菜单显示）。 */
    String title();

    /** 分类（决定在左侧菜单的分组）。 */
    Category category();

    /** 页面根节点。 */
    Node getView();

    /** Ant Design 组件分类 + JFXium 业务模板。 */
    enum Category {
        GENERAL("通用"),
        LAYOUT("布局"),
        NAVIGATION("导航"),
        DATA_ENTRY("数据录入"),
        DATA_DISPLAY("数据展示"),
        FEEDBACK("反馈"),
        OTHER("其他"),
        TEMPLATE("业务模板");

        private final String label;
        Category(String label) { this.label = label; }
        public String getLabel() { return label; }
    }
}
