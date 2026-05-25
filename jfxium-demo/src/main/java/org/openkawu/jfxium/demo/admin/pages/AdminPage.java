package org.openkawu.jfxium.demo.admin.pages;

import javafx.scene.Node;

/**
 * Admin 页面接口。
 *
 * <p>所有 admin 页面都实现此接口，由 Router 统一管理。
 * 设计原则（Karpathy 准则 #2 极简至上）：只暴露三个最必要的钩子，
 * 后续真有需要再扩展。</p>
 */
public interface AdminPage {

    /** 页面唯一 key，用于路由匹配（如 "user.list"、"dashboard"）。 */
    String key();

    /** 页面标题（用于浏览器/窗口标题、面包屑等）。 */
    String title();

    /** 返回页面根节点。Router 把它塞进 content 区。 */
    Node getView();
}
