package org.openkawu.jfxium.demo.showcase;

import javafx.application.Application;
import javafx.stage.Stage;
import org.openkawu.jfxium.demo.showcase.pages.ButtonPage;
import org.openkawu.jfxium.demo.showcase.pages.ButtonGroupPage;
import org.openkawu.jfxium.demo.showcase.pages.AlertPage;
import org.openkawu.jfxium.demo.showcase.pages.AnchorPage;
import org.openkawu.jfxium.demo.showcase.pages.AutoCompletePage;
import org.openkawu.jfxium.demo.showcase.pages.AvatarBadgePage;
import org.openkawu.jfxium.demo.showcase.pages.BreadcrumbPage;
import org.openkawu.jfxium.demo.showcase.pages.CalendarPage;
import org.openkawu.jfxium.demo.showcase.pages.CardPage;
import org.openkawu.jfxium.demo.showcase.pages.CarouselPage;
import org.openkawu.jfxium.demo.showcase.pages.CascaderPage;
import org.openkawu.jfxium.demo.showcase.pages.CheckBoxPage;
import org.openkawu.jfxium.demo.showcase.pages.CodeBlockPage;
import org.openkawu.jfxium.demo.showcase.pages.CollapsePage;
import org.openkawu.jfxium.demo.showcase.pages.ComboBoxPage;
import org.openkawu.jfxium.demo.showcase.pages.CrudTemplatePage;
import org.openkawu.jfxium.demo.showcase.pages.DatePickerPage;
import org.openkawu.jfxium.demo.showcase.pages.DescriptionsPage;
import org.openkawu.jfxium.demo.showcase.pages.DrawerPage;
import org.openkawu.jfxium.demo.showcase.pages.DropdownPage;
import org.openkawu.jfxium.demo.showcase.pages.EmptyPage;
import org.openkawu.jfxium.demo.showcase.pages.FeedbackPage;
import org.openkawu.jfxium.demo.showcase.pages.FormPage;
import org.openkawu.jfxium.demo.showcase.pages.I18nPage;
import org.openkawu.jfxium.demo.showcase.pages.ImagePage;
import org.openkawu.jfxium.demo.showcase.pages.InputNumberPage;
import org.openkawu.jfxium.demo.showcase.pages.InputPage;
import org.openkawu.jfxium.demo.showcase.pages.ListPage;
import org.openkawu.jfxium.demo.showcase.pages.LoginDashboardTemplatePage;
import org.openkawu.jfxium.demo.showcase.pages.MentionsPage;
import org.openkawu.jfxium.demo.showcase.pages.MenuPage;
import org.openkawu.jfxium.demo.showcase.pages.ModalPage;
import org.openkawu.jfxium.demo.showcase.pages.PaginationPage;
import org.openkawu.jfxium.demo.showcase.pages.PopconfirmPage;
import org.openkawu.jfxium.demo.showcase.pages.ProgressPage;
import org.openkawu.jfxium.demo.showcase.pages.RadioPage;
import org.openkawu.jfxium.demo.showcase.pages.ResultPage;
import org.openkawu.jfxium.demo.showcase.pages.SegmentedPage;
import org.openkawu.jfxium.demo.showcase.pages.SelectableTextPage;
import org.openkawu.jfxium.demo.showcase.pages.SkeletonPage;
import org.openkawu.jfxium.demo.showcase.pages.SliderPage;
import org.openkawu.jfxium.demo.showcase.pages.SpinPage;
import org.openkawu.jfxium.demo.showcase.pages.SpinnerPage;
import org.openkawu.jfxium.demo.showcase.pages.SplitBarPage;
import org.openkawu.jfxium.demo.showcase.pages.StatisticPage;
import org.openkawu.jfxium.demo.showcase.pages.StepsPage;
import org.openkawu.jfxium.demo.showcase.pages.SwitchPage;
import org.openkawu.jfxium.demo.showcase.pages.TablePage;
import org.openkawu.jfxium.demo.showcase.pages.TabsPage;
import org.openkawu.jfxium.demo.showcase.pages.TagPage;
import org.openkawu.jfxium.demo.showcase.pages.TextAreaPage;
import org.openkawu.jfxium.demo.showcase.pages.TimePickerPage;
import org.openkawu.jfxium.demo.showcase.pages.TimelinePage;
import org.openkawu.jfxium.demo.showcase.pages.TooltipPage;
import org.openkawu.jfxium.demo.showcase.pages.TransferPage;
import org.openkawu.jfxium.demo.showcase.pages.TreePage;
import org.openkawu.jfxium.demo.showcase.pages.TreeSelectPage;
import org.openkawu.jfxium.demo.showcase.pages.UploadPage;
import org.openkawu.jfxium.demo.showcase.pages.WatermarkPage;

/**
 * Showcase Demo 主入口。
 *
 * <p>每个组件一页，集中展示其全部姿态、API 用法、最佳实践。
 * 源码即文档（每个 Section 内嵌可复制粘贴的代码块）。</p>
 *
 * <p>添加新组件展示：</p>
 * <ol>
 *   <li>在 {@code pages/} 下新建 XxxPage（实现 ShowcasePage 接口）</li>
 *   <li>在本类 {@link #start(Stage)} 注册：{@code .register(new XxxPage())}</li>
 * </ol>
 */
public class ShowcaseDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.close();

        ShowcaseFrame frame = new ShowcaseFrame();

        // 通用
        frame.register(new ButtonPage());
        frame.register(new ButtonGroupPage());
        frame.register(new SwitchPage());

        // 导航
        frame.register(new MenuPage());
        frame.register(new PaginationPage());
        frame.register(new TabsPage());
        frame.register(new StepsPage());
        frame.register(new DropdownPage());
        frame.register(new BreadcrumbPage());
        frame.register(new AnchorPage());

        // 布局
        frame.register(new CardPage());
        frame.register(new SplitBarPage());

        // 数据录入
        frame.register(new InputPage());
        frame.register(new InputNumberPage());
        frame.register(new TextAreaPage());
        frame.register(new RadioPage());
        frame.register(new CheckBoxPage());
        frame.register(new ComboBoxPage());
        frame.register(new AutoCompletePage());
        frame.register(new MentionsPage());
        frame.register(new CascaderPage());
        frame.register(new TreeSelectPage());
        frame.register(new DatePickerPage());
        frame.register(new TimePickerPage());
        frame.register(new SliderPage());
        frame.register(new SegmentedPage());
        frame.register(new UploadPage());
        frame.register(new TransferPage());
        frame.register(new FormPage());

        // 数据展示
        frame.register(new TablePage());
        frame.register(new TagPage());
        frame.register(new SelectableTextPage());
        frame.register(new AvatarBadgePage());
        frame.register(new TreePage());
        frame.register(new StatisticPage());
        frame.register(new ListPage());
        frame.register(new TimelinePage());
        frame.register(new DescriptionsPage());
        frame.register(new CarouselPage());
        frame.register(new CalendarPage());
        frame.register(new CodeBlockPage());
        frame.register(new CollapsePage());
        frame.register(new ImagePage());
        frame.register(new EmptyPage());

        // 反馈
        frame.register(new ModalPage());
        frame.register(new DrawerPage());
        frame.register(new AlertPage());
        frame.register(new FeedbackPage());
        frame.register(new TooltipPage());
        frame.register(new PopconfirmPage());
        frame.register(new ProgressPage());
        frame.register(new SkeletonPage());
        frame.register(new SpinPage());
        frame.register(new SpinnerPage());
        frame.register(new ResultPage());

        // 其他
        frame.register(new WatermarkPage());
        frame.register(new I18nPage());

        // 业务模板（M19.16）
        frame.register(new CrudTemplatePage());
        frame.register(new LoginDashboardTemplatePage());

        // 后续在这里继续 register(...) 添加新组件页

        frame.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
