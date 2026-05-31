package org.openkawu.jfxium.jfxiumUiExample.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;

/**
 * UI 示例项目用的假数据集中点。
 *
 * <p>所有展示页要用的"演示用数据"都写在这里——避免 page 里硬编码业务字段，
 * 同时方便后续替换成真实 service 调用。</p>
 *
 * <p>命名约定：返回值类型直接放 {@link ObservableList}，让 TableAnt / ListAnt
 * 等组件 setData 时不用再二次包装。</p>
 */
public final class FakeData {

    private FakeData() {}

    /** 用户表数据。用 record 避免写一堆 getter / equals / hashCode。 */
    public record User(int id, String name, String email, String role,
                       String department, boolean active, LocalDate createdAt) {}

    /** 订单表数据——用于 Table / Pagination 等示例。 */
    public record Order(String orderNo, String customer, double amount,
                        String status, LocalDate date) {}

    /** 树形数据——用于 Tree / TreeSelect / Cascader 示例。 */
    public record Region(String code, String name, List<Region> children) {}

    // ============================================================
    // 用户列表（10 条）
    // ============================================================

    public static ObservableList<User> users() {
        return FXCollections.observableArrayList(
                new User(1,  "张三", "zhangsan@example.com", "管理员", "技术部",   true,  LocalDate.of(2024, 3, 12)),
                new User(2,  "李四", "lisi@example.com",     "用户",   "产品部",   true,  LocalDate.of(2024, 4,  8)),
                new User(3,  "王五", "wangwu@example.com",   "用户",   "运营部",   false, LocalDate.of(2024, 5, 21)),
                new User(4,  "赵六", "zhaoliu@example.com",  "管理员", "技术部",   true,  LocalDate.of(2024, 6,  3)),
                new User(5,  "孙七", "sunqi@example.com",    "访客",   "市场部",   true,  LocalDate.of(2024, 6, 15)),
                new User(6,  "周八", "zhouba@example.com",   "用户",   "财务部",   true,  LocalDate.of(2024, 7,  9)),
                new User(7,  "吴九", "wujiu@example.com",    "用户",   "人力资源", false, LocalDate.of(2024, 8,  1)),
                new User(8,  "郑十", "zhengshi@example.com", "管理员", "运营部",   true,  LocalDate.of(2024, 9, 17)),
                new User(9,  "钱一", "qianyi@example.com",   "访客",   "技术部",   true,  LocalDate.of(2024,10, 22)),
                new User(10, "冯二", "fenger@example.com",   "用户",   "产品部",   true,  LocalDate.of(2024,11,  5))
        );
    }

    // ============================================================
    // 订单列表（12 条）
    // ============================================================

    public static ObservableList<Order> orders() {
        return FXCollections.observableArrayList(
                new Order("20240501001", "张三", 1280.00, "已支付", LocalDate.of(2024, 5,  1)),
                new Order("20240502002", "李四",  399.50, "待支付", LocalDate.of(2024, 5,  2)),
                new Order("20240503003", "王五", 2580.00, "已发货", LocalDate.of(2024, 5,  3)),
                new Order("20240504004", "赵六",   89.90, "已完成", LocalDate.of(2024, 5,  4)),
                new Order("20240505005", "孙七", 6499.00, "已取消", LocalDate.of(2024, 5,  5)),
                new Order("20240506006", "周八", 1099.00, "已支付", LocalDate.of(2024, 5,  6)),
                new Order("20240507007", "吴九",  259.00, "已完成", LocalDate.of(2024, 5,  7)),
                new Order("20240508008", "郑十", 3399.99, "退款中", LocalDate.of(2024, 5,  8)),
                new Order("20240509009", "钱一",  799.00, "已发货", LocalDate.of(2024, 5,  9)),
                new Order("20240510010", "冯二",  159.00, "待支付", LocalDate.of(2024, 5, 10)),
                new Order("20240511011", "张三", 4999.00, "已支付", LocalDate.of(2024, 5, 11)),
                new Order("20240512012", "李四",   29.90, "已完成", LocalDate.of(2024, 5, 12))
        );
    }

    // ============================================================
    // 区域树（用于 Tree / TreeSelect / Cascader）
    // ============================================================

    public static List<Region> regions() {
        return List.of(
                new Region("ZJ", "浙江省", List.of(
                        new Region("ZJ-HZ", "杭州市", List.of(
                                new Region("ZJ-HZ-XH", "西湖区", List.of()),
                                new Region("ZJ-HZ-YH", "余杭区", List.of()),
                                new Region("ZJ-HZ-BJ", "滨江区", List.of())
                        )),
                        new Region("ZJ-NB", "宁波市", List.of(
                                new Region("ZJ-NB-HS", "海曙区", List.of()),
                                new Region("ZJ-NB-JB", "江北区", List.of())
                        ))
                )),
                new Region("JS", "江苏省", List.of(
                        new Region("JS-NJ", "南京市", List.of(
                                new Region("JS-NJ-XW", "玄武区", List.of()),
                                new Region("JS-NJ-GL", "鼓楼区", List.of())
                        )),
                        new Region("JS-SZ", "苏州市", List.of(
                                new Region("JS-SZ-GS", "姑苏区", List.of()),
                                new Region("JS-SZ-WJ", "吴江区", List.of())
                        ))
                )),
                new Region("BJ", "北京市", List.of(
                        new Region("BJ-DC", "东城区", List.of()),
                        new Region("BJ-XC", "西城区", List.of()),
                        new Region("BJ-CY", "朝阳区", List.of()),
                        new Region("BJ-HD", "海淀区", List.of())
                ))
        );
    }

    // ============================================================
    // 简单字符串列表（用于 ComboBox / AutoComplete / Tag 等）
    // ============================================================

    public static List<String> roles() {
        return List.of("管理员", "用户", "访客", "审核员");
    }

    public static List<String> departments() {
        return List.of("技术部", "产品部", "运营部", "市场部", "财务部", "人力资源", "设计部");
    }

    public static List<String> tags() {
        return List.of("Java", "JavaFX", "Spring", "MySQL", "Redis", "Kubernetes", "Docker", "Git");
    }

    public static List<String> cities() {
        return List.of("北京", "上海", "广州", "深圳", "杭州", "成都", "武汉", "南京", "西安", "苏州");
    }
}
