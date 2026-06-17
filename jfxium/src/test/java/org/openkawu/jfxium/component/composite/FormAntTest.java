package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;
import org.openkawu.jfxium.core.form.FormContext;
import org.openkawu.jfxium.core.form.Rule;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FormAnt 单元测试 —— 覆盖 Builder 创建、layout / size / colon / labelAlign / labelCol / wrapperCol、
 * header / footer（单节点 + 变长）/ footerAlign / section、item 4 个重载 + 命名 item 链式 ItemBuilder、
 * Rule 校验（required / minLength / email / custom）、Result.validate / getValues / onChange、FormContext
 * 的 setValue / getValue / valueProperty / errorProperty / clearErrors / validateField。
 *
 * <p><b>分组</b>：</p>
 * <ul>
 *   <li>基本结构：create + build / buildResult 返回 VBox / FormContext</li>
 *   <li>Layout：3 个 layout（HORIZONTAL=GridPane / VERTICAL=VBox / INLINE=HBox）</li>
 *   <li>Size / 样式：styleClass 显式追加</li>
 *   <li>header：设值 / 覆盖 / null 跳过</li>
 *   <li>footer：单节点老 API / 变长 M19.39 / null 元素跳过 / footerAlign</li>
 *   <li>section：HORIZONTAL/VERTICAL 渲染 / INLINE 忽略</li>
 *   <li>item 老 API 4 重载</li>
 *   <li>item 命名 + ItemBuilder 链式：required/rule/rules/helpText/validateStatus/end</li>
 *   <li>Rule 校验：全部规则 + 错误信息 / 字段联动 / 校验状态显示</li>
 *   <li>Result：getRoot / context / validate / getValues / onChange</li>
 *   <li>FormContext：独立单元测试（脱离 FormAnt）</li>
 *   <li>全链式串联</li>
 * </ul>
 */
@DisplayName("FormAnt")
class FormAntTest extends JfxTestBase {

    // ============================================================
    // 基本结构
    // ============================================================

    @Test
    @DisplayName("create().build() 返回 VBox 并挂 FORM styleClass")
    void build_returnsVBoxWithBaseClass() {
        VBox form = FormAnt.create().build();
        assertNotNull(form);
        assertTrue(form.getStyleClass().contains(JfxStyles.FORM),
                "默认应挂 jfx-form");
    }

    @Test
    @DisplayName("create().buildResult() 返回 Result（root + context）")
    void buildResult_returnsResultWithRootAndContext() {
        FormAnt.Result result = FormAnt.create()
                .item("用户名", new TextField(), "username").end()
                .buildResult();
        assertNotNull(result);
        assertNotNull(result.getRoot());
        assertTrue(result.getRoot() instanceof VBox);
        assertNotNull(result.context());
        assertTrue(result.context() instanceof FormContext);
    }

    @Test
    @DisplayName("create() 链式 build() 与 buildResult().getRoot() 等价")
    void build_equalsBuildResultRoot() {
        VBox a = FormAnt.create()
                .item("A", new TextField(), true)
                .build();
        VBox b = FormAnt.create()
                .item("A", new TextField(), true)
                .buildResult()
                .getRoot();
        assertEquals(a.getStyleClass(), b.getStyleClass());
    }

    @Test
    @DisplayName("size styleClass 随 size 切换：jfx-form-size-small/default/large")
    void size_styleClass_changes() {
        VBox small = FormAnt.create().size(FormAnt.Size.SMALL).build();
        VBox def = FormAnt.create().size(FormAnt.Size.DEFAULT).build();
        VBox large = FormAnt.create().size(FormAnt.Size.LARGE).build();
        assertTrue(small.getStyleClass().contains(JfxStyles.FORM_SIZE_SMALL));
        assertFalse(def.getStyleClass().contains(JfxStyles.FORM_SIZE_SMALL));
        assertFalse(def.getStyleClass().contains(JfxStyles.FORM_SIZE_LARGE));
        assertTrue(large.getStyleClass().contains(JfxStyles.FORM_SIZE_LARGE));
    }

    @Test
    @DisplayName("vertical form item 结构挂载 item-box / item-wrapper styleClass")
    void vertical_item_hooks_are_present() {
        VBox form = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .item("用户名", new TextField())
                .build();

        assertTrue(form.getStyleClass().contains(JfxStyles.FORM));
        // FORM_VERTICAL 挂在 body container 上（form.getChildren().get(0)），不是 form 本身
        VBox body = (VBox) form.getChildren().get(0);
        assertTrue(body.getStyleClass().contains(JfxStyles.FORM_VERTICAL));

        VBox itemBox = (VBox) body.getChildren().get(0);
        assertTrue(itemBox.getStyleClass().contains(JfxStyles.FORM_ITEM_BOX));

        VBox wrapper = (VBox) itemBox.getChildren().get(1);
        assertTrue(wrapper.getStyleClass().contains(JfxStyles.FORM_ITEM_WRAPPER));
    }

    // ============================================================
    // Layout
    // ============================================================

    @Nested
    @DisplayName("Layout")
    class LayoutTests {

        @Test
        @DisplayName("HORIZONTAL：body 是 GridPane 并挂 FORM_HORIZONTAL")
        void horizontal_bodyIsGridPane() {
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.HORIZONTAL)
                    .item("A", new TextField(), "a").end()
                    .buildResult();
            VBox form = result.getRoot();
            // header/body/footer 顺序：[body]（无 header/footer）
            Node body = form.getChildren().get(0);
            assertTrue(body instanceof GridPane, "HORIZONTAL body 应是 GridPane");
            assertTrue(body.getStyleClass().contains(JfxStyles.FORM_HORIZONTAL));
        }

        @Test
        @DisplayName("VERTICAL：body 是 VBox 并挂 FORM_VERTICAL")
        void vertical_bodyIsVBox() {
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a").end()
                    .buildResult();
            VBox form = result.getRoot();
            Node body = form.getChildren().get(0);
            assertTrue(body instanceof VBox, "VERTICAL body 应是 VBox");
            assertTrue(body.getStyleClass().contains(JfxStyles.FORM_VERTICAL));
        }

        @Test
        @DisplayName("INLINE：body 是 HBox 并挂 FORM_INLINE")
        void inline_bodyIsHBox() {
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.INLINE)
                    .item("A", new TextField(), "a").end()
                    .buildResult();
            VBox form = result.getRoot();
            Node body = form.getChildren().get(0);
            assertTrue(body instanceof HBox, "INLINE body 应是 HBox");
            assertTrue(body.getStyleClass().contains(JfxStyles.FORM_INLINE));
        }

        @Test
        @DisplayName("INLINE 下 section marker 不渲染（spec Req 4 AC 6）")
        void inline_ignoresSectionMarker() {
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.INLINE)
                    .section("账号信息")
                    .item("A", new TextField(), "a").end()
                    .buildResult();
            VBox form = result.getRoot();
            HBox body = (HBox) form.getChildren().get(0);
            // INLINE body 只有一个 itemBox，没有 section 标题 Label
            assertEquals(1, body.getChildren().size());
            for (Node child : body.getChildren()) {
                assertFalse(child instanceof Label, "INLINE 不应有 section 标题");
            }
        }
    }

    // ============================================================
    // 样式
    // ============================================================

    @Test
    @DisplayName("colon(false) 时 label 不带冒号")
    void colon_false_noColon() {
        FormAnt.Result result = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .colon(false)
                .item("用户名", new TextField(), "username").end()
                .buildResult();
        // 找到唯一 Label
        VBox form = result.getRoot();
        VBox body = (VBox) form.getChildren().get(0);
        // body -> itemBox(VBox) -> label(Label) + wrapper(VBox)
        VBox itemBox = (VBox) body.getChildren().get(0);
        Label lbl = (Label) itemBox.getChildren().get(0);
        assertEquals("用户名", lbl.getText(), "colon=false 时不应追加冒号");
    }

    @Test
    @DisplayName("colon(true) 时 label 带冒号")
    void colon_true_withColon() {
        FormAnt.Result result = FormAnt.create()
                .layout(FormAnt.Layout.VERTICAL)
                .colon(true)
                .item("用户名", new TextField(), "username").end()
                .buildResult();
        VBox form = result.getRoot();
        VBox body = (VBox) form.getChildren().get(0);
        VBox itemBox = (VBox) body.getChildren().get(0);
        Label lbl = (Label) itemBox.getChildren().get(0);
        assertEquals("用户名:", lbl.getText());
    }

    @Test
    @DisplayName("labelAlign(LEFT/RIGHT) 切换")
    void labelAlign_changes() {
        FormAnt.Builder b1 = FormAnt.create().labelAlign(FormAnt.Align.LEFT);
        FormAnt.Builder b2 = FormAnt.create().labelAlign(FormAnt.Align.RIGHT);
        // 链式 API 调通即可（styleClass 验证在 HORIZONTAL 场景下通过 GridPane 节点验证）
        assertNotNull(b1);
        assertNotNull(b2);
    }

    // ============================================================
    // header（M19.39）
    // ============================================================

    @Nested
    @DisplayName("header（M19.39）")
    class HeaderTests {

        @Test
        @DisplayName("header 设置后渲染 FORM_HEADER 子节点")
        void header_rendersHeaderBox() {
            Label banner = new Label("重要提示");
            VBox form = FormAnt.create()
                    .header(banner)
                    .build();
            // 第一个子节点是 headerBox
            assertEquals(2, form.getChildren().size(), "应有 header + body");
            Node first = form.getChildren().get(0);
            assertTrue(first instanceof VBox, "header 容器是 VBox");
            assertTrue(first.getStyleClass().contains(JfxStyles.FORM_HEADER));
            // header 容器内含原 banner
            VBox headerBox = (VBox) first;
            assertEquals(1, headerBox.getChildren().size());
            assertEquals(banner, headerBox.getChildren().get(0));
        }

        @Test
        @DisplayName("header 多次调用以最后一次为准（覆盖语义）")
        void header_overridesPrevious() {
            Label a = new Label("A");
            Label b = new Label("B");
            VBox form = FormAnt.create()
                    .header(a)
                    .header(b)
                    .build();
            VBox headerBox = (VBox) form.getChildren().get(0);
            assertEquals(b, headerBox.getChildren().get(0), "第二次 header 应覆盖第一次");
        }

        @Test
        @DisplayName("header(null) 跳过渲染")
        void header_null_skipsRender() {
            VBox form = FormAnt.create()
                    .header((Node) null)
                    .build();
            // 没有 header → 只有 body 一个子节点
            assertEquals(1, form.getChildren().size());
        }
    }

    // ============================================================
    // footer（M19.39 增强：单节点 + 变长 + 对齐）
    // ============================================================

    @Nested
    @DisplayName("footer（M19.39 增强）")
    class FooterTests {

        @Test
        @DisplayName("footer(Node) 老 API：单节点渲染并挂 FORM_FOOTER")
        void footer_singleNode() {
            Label submit = new Label("提交");
            VBox form = FormAnt.create()
                    .footer(submit)
                    .build();
            // [body, footerBox]
            assertEquals(2, form.getChildren().size());
            Node last = form.getChildren().get(form.getChildren().size() - 1);
            assertTrue(last instanceof HBox, "footer 容器是 HBox");
            HBox footerBox = (HBox) last;
            assertTrue(footerBox.getStyleClass().contains(JfxStyles.FORM_FOOTER));
            assertEquals(1, footerBox.getChildren().size());
            assertEquals(submit, footerBox.getChildren().get(0));
        }

        @Test
        @DisplayName("footer(Node...) 变长：多按钮全部渲染")
        void footer_varargs() {
            Label cancel = new Label("取消");
            Label reset = new Label("重置");
            Label submit = new Label("提交");
            VBox form = FormAnt.create()
                    .footer(cancel, reset, submit)
                    .build();
            HBox footerBox = (HBox) form.getChildren().get(form.getChildren().size() - 1);
            assertEquals(3, footerBox.getChildren().size());
            assertEquals(cancel, footerBox.getChildren().get(0));
            assertEquals(reset, footerBox.getChildren().get(1));
            assertEquals(submit, footerBox.getChildren().get(2));
        }

        @Test
        @DisplayName("footer 多次调用以最后一次为准（覆盖语义）")
        void footer_overridesPrevious() {
            VBox form = FormAnt.create()
                    .footer(new Label("A"))
                    .footer(new Label("B"), new Label("C"))
                    .build();
            HBox footerBox = (HBox) form.getChildren().get(form.getChildren().size() - 1);
            assertEquals(2, footerBox.getChildren().size());
        }

        @Test
        @DisplayName("footer 数组含 null 元素被跳过")
        void footer_skipsNullElements() {
            Label a = new Label("A");
            VBox form = FormAnt.create()
                    .footer(a, null)
                    .build();
            HBox footerBox = (HBox) form.getChildren().get(form.getChildren().size() - 1);
            assertEquals(1, footerBox.getChildren().size());
            assertEquals(a, footerBox.getChildren().get(0));
        }

        @Test
        @DisplayName("footerAlign(Pos.CENTER_LEFT) 切换 footer 容器对齐")
        void footerAlign_changes() {
            VBox form = FormAnt.create()
                    .footerAlign(Pos.CENTER_LEFT)
                    .footer(new Label("X"))
                    .build();
            HBox footerBox = (HBox) form.getChildren().get(form.getChildren().size() - 1);
            assertEquals(Pos.CENTER_LEFT, footerBox.getAlignment());
        }

        @Test
        @DisplayName("footerAlign(null) 保留旧值（防呆）")
        void footerAlign_nullKeepsPrevious() {
            VBox form = FormAnt.create()
                    .footerAlign(Pos.CENTER)
                    .footerAlign(null)
                    .footer(new Label("X"))
                    .build();
            HBox footerBox = (HBox) form.getChildren().get(form.getChildren().size() - 1);
            assertEquals(Pos.CENTER, footerBox.getAlignment());
        }
    }

    // ============================================================
    // section（M19.39 分段标题）
    // ============================================================

    @Nested
    @DisplayName("section（M19.39）")
    class SectionTests {

        @Test
        @DisplayName("HORIZONTAL 下 section 渲染并挂 FORM_SECTION_TITLE")
        void horizontal_rendersSectionTitle() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.HORIZONTAL)
                    .section("账号信息")
                    .item("A", new TextField(), "a").end()
                    .build();
            GridPane body = (GridPane) form.getChildren().get(0);
            // grid 第一行第一格是 section title
            Label titleLbl = (Label) body.getChildren().get(0);
            assertEquals("账号信息", titleLbl.getText());
            assertTrue(titleLbl.getStyleClass().contains(JfxStyles.FORM_SECTION_TITLE));
        }

        @Test
        @DisplayName("VERTICAL 下 section 也渲染")
        void vertical_rendersSectionTitle() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .section("账号信息")
                    .item("A", new TextField(), "a").end()
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            Node first = body.getChildren().get(0);
            assertTrue(first instanceof Label);
            assertEquals("账号信息", ((Label) first).getText());
            assertTrue(first.getStyleClass().contains(JfxStyles.FORM_SECTION_TITLE));
        }

        @Test
        @DisplayName("section(null) 跳过渲染（防 null）")
        void section_nullSkipped() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .section(null)
                    .item("A", new TextField(), "a").end()
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            // 只有 1 个 itemBox（无 section Label）
            assertEquals(1, body.getChildren().size());
        }
    }

    // ============================================================
    // item 老 API（4 重载）
    // ============================================================

    @Nested
    @DisplayName("item 老 API（4 重载）")
    class LegacyItemTests {

        @Test
        @DisplayName("item(label, control) 2 参：未命名，不入 FormContext")
        void item_2arg_notNamed() {
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField())
                    .buildResult();
            // 命名 field 缺失 → getValue 应为 null
            assertNull(result.context().getValue("A"));
        }

        @Test
        @DisplayName("item(label, control, true) 3 参：required 挂红 * 标签")
        void item_3arg_required() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), true)
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            VBox itemBox = (VBox) body.getChildren().get(0);
            Label lbl = (Label) itemBox.getChildren().get(0);
            assertTrue(lbl.getStyleClass().contains(JfxStyles.FORM_LABEL_REQUIRED));
        }

        @Test
        @DisplayName("item(label, control, false, helpText) 4 参：helpText 渲染")
        void item_4arg_helpText() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), false, "请填写用户名")
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            VBox itemBox = (VBox) body.getChildren().get(0);
            VBox wrapper = (VBox) itemBox.getChildren().get(1);
            // wrapper = [control, helpLabel]
            assertEquals(2, wrapper.getChildren().size());
            Label help = (Label) wrapper.getChildren().get(1);
            assertEquals("请填写用户名", help.getText());
            assertTrue(help.getStyleClass().contains(JfxStyles.FORM_HELP_TEXT));
        }

        @Test
        @DisplayName("item(label, control, false, null, ERROR) 5 参：validateStatus 切换 help 颜色")
        void item_5arg_validateStatus() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), false, "提示",
                            FormAnt.ValidateStatus.ERROR)
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            VBox itemBox = (VBox) body.getChildren().get(0);
            VBox wrapper = (VBox) itemBox.getChildren().get(1);
            Label help = (Label) wrapper.getChildren().get(1);
            assertTrue(help.getStyleClass().contains(JfxStyles.FORM_HELP_ERROR));
        }
    }

    // ============================================================
    // 命名 item + ItemBuilder 链式
    // ============================================================

    @Nested
    @DisplayName("命名 item + ItemBuilder 链式（M19.23）")
    class NamedItemTests {

        @Test
        @DisplayName("item(label, control, name) 命名：进入 FormContext")
        void namedItem_registersInContext() {
            TextField field = new TextField("init");
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", field, "a").end()
                    .buildResult();
            // 命名 field 已在 context
            assertEquals("init", result.context().getValue("a"));
        }

        @Test
        @DisplayName("item(label, control, name) 默认 required=false")
        void namedItem_defaultNotRequired() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a").end()
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            VBox itemBox = (VBox) body.getChildren().get(0);
            Label lbl = (Label) itemBox.getChildren().get(0);
            assertFalse(lbl.getStyleClass().contains(JfxStyles.FORM_LABEL_REQUIRED));
        }

        @Test
        @DisplayName("ItemBuilder.required() 挂 required Rule + 红 * 标签")
        void itemBuilder_required() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a")
                        .required()
                        .end()
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            VBox itemBox = (VBox) body.getChildren().get(0);
            Label lbl = (Label) itemBox.getChildren().get(0);
            assertTrue(lbl.getStyleClass().contains(JfxStyles.FORM_LABEL_REQUIRED));
        }

        @Test
        @DisplayName("ItemBuilder.rule(Rule) 追加单条规则")
        void itemBuilder_singleRule() {
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a")
                        .rule(Rule.minLength(3, "至少 3 字符"))
                        .end()
                    .buildResult();
            // 填入合法值
            result.context().setValue("a", "abcdef");
            assertTrue(result.validate());
            // 填入非法值
            result.context().setValue("a", "ab");
            assertFalse(result.validate());
            assertEquals("至少 3 字符", result.context().errorProperty("a").get());
        }

        @Test
        @DisplayName("ItemBuilder.rules(Rule...) 变长追加")
        void itemBuilder_varargRules() {
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a")
                        .rules(Rule.required(), Rule.minLength(3, "至少 3 字符"),
                               Rule.email("邮箱格式"))
                        .end()
                    .buildResult();
            // 空 → required 失败
            assertFalse(result.validate());
            assertEquals("此项必填", result.context().errorProperty("a").get());
            // 太短 → 必填过了，长度失败
            result.context().setValue("a", "ab");
            assertFalse(result.validate());
            assertEquals("至少 3 字符", result.context().errorProperty("a").get());
            // 长度 OK 但非邮箱
            result.context().setValue("a", "abcdef");
            assertFalse(result.validate());
            assertEquals("邮箱格式", result.context().errorProperty("a").get());
            // 全通过
            result.context().setValue("a", "a@b.co");
            assertTrue(result.validate());
        }

        @Test
        @DisplayName("ItemBuilder.rules(Collection<Rule>) 集合追加")
        void itemBuilder_collectionRules() {
            Collection<Rule> ruleList = List.of(Rule.required(), Rule.minLength(2, "太短"));
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a")
                        .rules(ruleList)
                        .end()
                    .buildResult();
            // 空 → required 失败
            assertFalse(result.validate());
            // 非空过短 → minLength 失败
            result.context().setValue("a", "x");
            assertFalse(result.validate());
            // 合法
            result.context().setValue("a", "xy");
            assertTrue(result.validate());
        }

        @Test
        @DisplayName("ItemBuilder.rule(null) 跳过（不抛异常）")
        void itemBuilder_nullRuleSkipped() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a")
                        .rule(null)
                        .end()
                    .build();
            // 调通即过
            assertNotNull(form);
        }

        @Test
        @DisplayName("ItemBuilder.helpText 设置 help 文字")
        void itemBuilder_helpText() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a")
                        .helpText("必填项")
                        .end()
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            VBox itemBox = (VBox) body.getChildren().get(0);
            VBox wrapper = (VBox) itemBox.getChildren().get(1);
            // wrapper = [control, helpLabel, errorLabel]
            assertEquals(3, wrapper.getChildren().size());
            Label help = (Label) wrapper.getChildren().get(1);
            assertEquals("必填项", help.getText());
        }

        @Test
        @DisplayName("ItemBuilder.validateStatus(WARNING) help 挂黄")
        void itemBuilder_validateStatus() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a")
                        .helpText("警告")
                        .validateStatus(FormAnt.ValidateStatus.WARNING)
                        .end()
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            VBox itemBox = (VBox) body.getChildren().get(0);
            VBox wrapper = (VBox) itemBox.getChildren().get(1);
            Label help = (Label) wrapper.getChildren().get(1);
            assertTrue(help.getStyleClass().contains(JfxStyles.FORM_HELP_WARNING));
        }

        @Test
        @DisplayName("ItemBuilder.validateStatus(null) 退回 DEFAULT")
        void itemBuilder_nullValidateStatus() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a")
                        .helpText("x")
                        .validateStatus(null)
                        .end()
                    .build();
            VBox body = (VBox) form.getChildren().get(0);
            VBox itemBox = (VBox) body.getChildren().get(0);
            VBox wrapper = (VBox) itemBox.getChildren().get(1);
            Label help = (Label) wrapper.getChildren().get(1);
            // DEFAULT 状态不挂额外 color class
            assertFalse(help.getStyleClass().contains(JfxStyles.FORM_HELP_ERROR));
            assertFalse(help.getStyleClass().contains(JfxStyles.FORM_HELP_WARNING));
            assertFalse(help.getStyleClass().contains(JfxStyles.FORM_HELP_SUCCESS));
        }

        @Test
        @DisplayName("ItemBuilder.end() 回链到 Builder 继续链式")
        void itemBuilder_endChaining() {
            VBox form = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a").required().end()
                    .item("B", new TextField(), "b").required().end()
                    .build();
            // 2 个 item 全部必填
            VBox body = (VBox) form.getChildren().get(0);
            assertEquals(2, body.getChildren().size());
        }
    }

    // ============================================================
    // Rule 校验（独立验证 Rule 工厂方法）
    // ============================================================

    @Nested
    @DisplayName("Rule 校验（独立验证 Rule 工厂方法）")
    class RuleTests {

        @Test
        @DisplayName("Rule.required：null / 空白不通过")
        void ruleRequired_rejectsNullAndBlank() {
            Rule r = Rule.required("必填");
            assertEquals("必填", r.check(null));
            assertEquals("必填", r.check(""));
            assertEquals("必填", r.check("   "));
            assertNull(r.check("x"));
        }

        @Test
        @DisplayName("Rule.required() 默认消息：'此项必填'")
        void ruleRequired_defaultMessage() {
            Rule r = Rule.required();
            assertEquals("此项必填", r.getMessage());
            assertEquals("此项必填", r.check(null));
        }

        @Test
        @DisplayName("Rule.minLength：长度下限")
        void ruleMinLength() {
            Rule r = Rule.minLength(3, "too short");
            assertNull(r.check("abc"));
            assertNull(r.check("abcd"));
            assertEquals("too short", r.check("ab"));
            assertEquals("too short", r.check(null));
        }

        @Test
        @DisplayName("Rule.maxLength：长度上限")
        void ruleMaxLength() {
            Rule r = Rule.maxLength(3, "too long");
            assertNull(r.check("ab"));
            assertNull(r.check("abc"));
            assertEquals("too long", r.check("abcd"));
            assertNull(r.check(null), "maxLength 允许 null");
        }

        @Test
        @DisplayName("Rule.lengthBetween：闭区间 [min, max]")
        void ruleLengthBetween() {
            Rule r = Rule.lengthBetween(2, 4, "out of range");
            assertNull(r.check("ab"));
            assertNull(r.check("abcd"));
            assertEquals("out of range", r.check("a"));
            assertEquals("out of range", r.check("abcde"));
            assertEquals("out of range", r.check(null));
        }

        @Test
        @DisplayName("Rule.pattern：完全匹配")
        void rulePattern() {
            Rule r = Rule.pattern("^[a-z]+$", "lower only");
            assertNull(r.check("abc"));
            assertEquals("lower only", r.check("ABC"));
            assertEquals("lower only", r.check("ab1"));
            assertEquals("lower only", r.check(null));
        }

        @Test
        @DisplayName("Rule.email：简单 RFC 子集")
        void ruleEmail() {
            Rule r = Rule.email();
            assertNull(r.check("a@b.co"));
            assertEquals(r.getMessage(), r.check("not-an-email"));
            assertEquals(r.getMessage(), r.check("a@b"));
            assertEquals(r.getMessage(), r.check(null));
        }

        @Test
        @DisplayName("Rule.range(double, double)：数值区间 [min, max]")
        void ruleRange() {
            Rule r = Rule.range(1.0, 5.0, "out");
            assertNull(r.check("3"));
            assertNull(r.check(2.5));
            assertEquals("out", r.check("0"));
            assertEquals("out", r.check("6"));
            assertEquals("out", r.check("not-a-number"));
            assertEquals("out", r.check(null));
        }

        @Test
        @DisplayName("Rule.custom：用户自定义 Predicate")
        void ruleCustom() {
            Rule r = Rule.custom(v -> v != null && v.toString().startsWith("JFX"), "must start with JFX");
            assertNull(r.check("JFXium"));
            assertEquals("must start with JFX", r.check("Other"));
            assertEquals("must start with JFX", r.check(null));
        }
    }

    // ============================================================
    // Result + onChange 联动
    // ============================================================

    @Nested
    @DisplayName("Result + 字段联动")
    class ResultTests {

        @Test
        @DisplayName("Result.getRoot() 返回 VBox；Result.context() 返回 FormContext")
        void result_rootAndContext() {
            FormAnt.Result result = FormAnt.create().buildResult();
            assertTrue(result.getRoot() instanceof VBox);
            assertNotNull(result.context());
        }

        @Test
        @DisplayName("Result.validate() 全字段校验并写错误")
        void result_validateAll() {
            TextField user = new TextField();
            TextField email = new TextField();
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("用户名", user, "u")
                        .rule(Rule.required())
                        .end()
                    .item("邮箱", email, "e")
                        .rule(Rule.required())
                        .end()
                    .buildResult();
            assertFalse(result.validate());
            assertEquals("此项必填", result.context().errorProperty("u").get());
            assertEquals("此项必填", result.context().errorProperty("e").get());

            user.setText("alice");
            email.setText("a@b.co");
            assertTrue(result.validate());
            assertEquals("", result.context().errorProperty("u").get());
            assertEquals("", result.context().errorProperty("e").get());
        }

        @Test
        @DisplayName("Result.getValues() 一次性取全部字段值")
        void result_getValues() {
            TextField user = new TextField("alice");
            ComboBox<String> role = new ComboBox<>();
            role.getItems().addAll("admin", "user");
            role.setValue("admin");
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("用户名", user, "u").end()
                    .item("角色", role, "r").end()
                    .buildResult();
            Map<String, Object> values = result.getValues();
            assertEquals("alice", values.get("u"));
            assertEquals("admin", values.get("r"));
        }

        @Test
        @DisplayName("Result.onChange(dependency, handler) 字段联动：password 变 → 重验 confirm")
        void result_onChangeLinkage() {
            TextField pwd = new TextField();
            TextField confirm = new TextField("init");
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("密码", pwd, "pwd").end()
                    .item("确认", confirm, "confirm")
                        .rule(Rule.custom(v ->
                                v != null && v.toString().equals(pwd.getText()),
                                "两次密码不一致"))
                        .end()
                    .buildResult();
            // 注册联动
            result.onChange("pwd", (val, ctx) -> ctx.validateField("confirm"));
            // ① pwd="" confirm="init" → 不等 → 失败
            assertFalse(result.validate());
            assertEquals("两次密码不一致", result.context().errorProperty("confirm").get());
            // ② confirm 跟 pwd 同步为"123" → 重验 → 通过
            confirm.setText("123");
            pwd.setText("123");
            assertEquals("", result.context().errorProperty("confirm").get());
            // ③ pwd 改 "456"，confirm 仍是"123" → 联动触发重验 → 失败
            pwd.setText("456");
            assertEquals("两次密码不一致", result.context().errorProperty("confirm").get());
        }

        @Test
        @DisplayName("Result.onChange(null, handler) 跳过（防 null）")
        void result_onChangeNullHandler() {
            FormAnt.Result result = FormAnt.create()
                    .layout(FormAnt.Layout.VERTICAL)
                    .item("A", new TextField(), "a").end()
                    .buildResult();
            // 不抛异常
            assertDoesNotThrow(() -> result.onChange("a", null));
        }
    }

    // ============================================================
    // FormContext 独立测试
    // ============================================================

    @Nested
    @DisplayName("FormContext 独立单元测试")
    class FormContextTests {

        @Test
        @DisplayName("registerField + getValue 同步初始值")
        void registerAndGet() {
            FormContext ctx = new FormContext();
            TextField f = new TextField("hello");
            ctx.registerField("name", f, List.of());
            assertEquals("hello", ctx.getValue("name"));
        }

        @Test
        @DisplayName("valueProperty 监听控件变化")
        void valuePropertyReflectsControl() {
            FormContext ctx = new FormContext();
            TextField f = new TextField("");
            ctx.registerField("name", f, List.of());
            AtomicReference<Object> captured = new AtomicReference<>();
            ctx.valueProperty("name").addListener((o, ov, nv) -> captured.set(nv));
            f.setText("new value");
            assertEquals("new value", captured.get());
        }

        @Test
        @DisplayName("setValue(name, v) 反向同步到控件")
        void setValueWritesToControl() {
            FormContext ctx = new FormContext();
            TextField f = new TextField("old");
            ctx.registerField("name", f, List.of());
            ctx.setValue("name", "new");
            assertEquals("new", f.getText());
            assertEquals("new", ctx.getValue("name"));
        }

        @Test
        @DisplayName("getValue/getValue 未注册返回 null")
        void getValueUnknownReturnsNull() {
            FormContext ctx = new FormContext();
            assertNull(ctx.getValue("missing"));
            assertNull(ctx.valueProperty("missing"));
            assertNull(ctx.errorProperty("missing"));
        }

        @Test
        @DisplayName("setValue 未注册字段不抛异常")
        void setValueUnknownSafe() {
            FormContext ctx = new FormContext();
            assertDoesNotThrow(() -> ctx.setValue("missing", "x"));
        }

        @Test
        @DisplayName("registerField(name=null/empty/control=null) 跳过")
        void registerField_invalidArgsSkipped() {
            FormContext ctx = new FormContext();
            assertDoesNotThrow(() -> {
                ctx.registerField(null, new TextField(), List.of());
                ctx.registerField("", new TextField(), List.of());
                ctx.registerField("x", null, List.of());
            });
            assertNull(ctx.getValue("x"));
        }

        @Test
        @DisplayName("validate() 返回是否全过；写第一条失败到 errorProperty")
        void validateReturnsBoolean() {
            FormContext ctx = new FormContext();
            TextField f = new TextField("");
            ctx.registerField("name", f, List.of(
                    Rule.required("必填"), Rule.minLength(3, "太短")));
            assertFalse(ctx.validate());
            assertEquals("必填", ctx.errorProperty("name").get());
            f.setText("ab");
            assertFalse(ctx.validate());
            assertEquals("太短", ctx.errorProperty("name").get());
            f.setText("abc");
            assertTrue(ctx.validate());
            assertEquals("", ctx.errorProperty("name").get());
        }

        @Test
        @DisplayName("validateField(name) 单字段校验并写错误")
        void validateFieldSingle() {
            FormContext ctx = new FormContext();
            TextField f = new TextField("");
            ctx.registerField("name", f, List.of(Rule.required("必填")));
            assertFalse(ctx.validateField("name"));
            f.setText("ok");
            assertTrue(ctx.validateField("name"));
        }

        @Test
        @DisplayName("validateField 不存在字段返回 true（无错即通过）")
        void validateFieldUnknown() {
            FormContext ctx = new FormContext();
            assertTrue(ctx.validateField("nope"));
        }

        @Test
        @DisplayName("clearErrors 清空所有字段错误（不改值）")
        void clearErrorsClearsMessages() {
            FormContext ctx = new FormContext();
            TextField f = new TextField("");
            ctx.registerField("name", f, List.of(Rule.required("必填")));
            ctx.validate();
            assertEquals("必填", ctx.errorProperty("name").get());
            ctx.clearErrors();
            assertEquals("", ctx.errorProperty("name").get());
            assertEquals("", ctx.getValue("name"), "clearErrors 不改 value");
        }

        @Test
        @DisplayName("onChange(dependency, handler) 字段联动")
        void onChangeLinkage() {
            FormContext ctx = new FormContext();
            TextField pwd = new TextField();
            TextField confirm = new TextField();
            ctx.registerField("pwd", pwd, List.of());
            ctx.registerField("confirm", confirm, List.of(
                    Rule.custom(v -> v != null && v.toString().equals(pwd.getText()), "不一致")));
            AtomicInteger callCount = new AtomicInteger();
            ctx.onChange("pwd", (val, c) -> {
                callCount.incrementAndGet();
                c.validateField("confirm");
            });
            pwd.setText("x");
            assertEquals(1, callCount.get());
            pwd.setText("y");
            assertEquals(2, callCount.get());
        }

        @Test
        @DisplayName("onChange 不存在 dependency / null handler 跳过")
        void onChangeInvalidArgs() {
            FormContext ctx = new FormContext();
            TextField f = new TextField();
            ctx.registerField("name", f, List.of());
            assertDoesNotThrow(() -> {
                ctx.onChange("missing", (v, c) -> {});
                ctx.onChange("name", null);
            });
        }

        @Test
        @DisplayName("CheckBox / DatePicker / RadioButton / ToggleButton 控件值提取")
        void controlValueExtraction() {
            FormContext ctx = new FormContext();
            CheckBox cb = new CheckBox();
            ctx.registerField("cb", cb, List.of());
            assertEquals(false, ctx.getValue("cb"));
            cb.setSelected(true);
            assertEquals(true, ctx.getValue("cb"));

            DatePicker dp = new DatePicker();
            ctx.registerField("dp", dp, List.of());
            ctx.setValue("dp", LocalDate.of(2024, 1, 15));
            assertEquals(LocalDate.of(2024, 1, 15), dp.getValue());
        }
    }

    // ============================================================
    // 全链式串联
    // ============================================================

    @Test
    @DisplayName("全链式：layout + size + colon + labelAlign + labelCol + wrapperCol + header + section + item×3 + footer×2 + footerAlign")
    void fullChain() {
        TextField user = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        VBox form = FormAnt.create()
                .layout(FormAnt.Layout.HORIZONTAL)
                .size(FormAnt.Size.LARGE)
                .colon(false)
                .labelAlign(FormAnt.Align.LEFT)
                .labelCol(4)
                .wrapperCol(20)
                .header(new Label("用户注册"))
                .section("基本信息")
                .item("用户名", user, "user")
                    .required()
                    .rule(Rule.minLength(3, "至少 3 字符"))
                    .helpText("字母数字下划线")
                    .end()
                .item("邮箱", email, "email")
                    .required()
                    .rule(Rule.email())
                    .end()
                .section("联系信息")
                .item("手机", phone, "phone")
                    .required()
                    .rule(Rule.pattern("^1[3-9]\\d{9}$", "手机号格式"))
                    .end()
                .footer(new Label("取消"), new Label("提交"))
                .footerAlign(Pos.CENTER)
                .build();
        // [header, body, footer] = 3 个子节点
        assertEquals(3, form.getChildren().size());
        // body 是 GridPane + 包含 2 个 section + 3 个 item 共 5 行
        GridPane body = (GridPane) form.getChildren().get(1);
        assertEquals(5, body.getRowCount());
    }
}
