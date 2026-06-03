package org.openkawu.jfxium.jfxiumUiExample.pages.datadisplay;

import javafx.scene.Node;

import org.openkawu.jfxium.component.layout.VBoxAnt;
import org.openkawu.jfxium.jfxiumUiExample.util.Demos;
import org.openkawu.jfxium.template.PageTemplate;
import org.openkawu.jfxium.component.composite.SkeletonAnt;

/**
 * Skeleton 骨架屏 —— 基础形状 / 段落 / 头像 + 文本。
 */
public class SkeletonExamplePage extends VBoxAnt {

    public SkeletonExamplePage() {
        spacing(0).children(PageTemplate.create()
                .title("Skeleton 骨架屏")
                .description("内容加载过程中的占位效果，比 loading 转圈更平滑。")
                .sections(basicSection(), paragraphSection(), avatarSection())
                .padding(24)
                .build());
    }

    private Node basicSection() {
        Node demo = Demos.row(
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(160).height(16).build(),
                SkeletonAnt.create().variant(SkeletonAnt.Variant.ROUNDED).width(120).height(48).build(),
                SkeletonAnt.create().variant(SkeletonAnt.Variant.CIRCULAR).width(48).height(48).build()
        );
        String code = """
                SkeletonAnt.create().variant(SkeletonAnt.Variant.TEXT).width(160).height(16).build();
                SkeletonAnt.create().variant(SkeletonAnt.Variant.ROUNDED).width(120).height(48).build();
                SkeletonAnt.create().variant(SkeletonAnt.Variant.CIRCULAR).width(48).height(48).build();
                """;
        return Demos.sectionWithCode("1. 基础形状",
                "TEXT / ROUNDED / RECTANGULAR / CIRCULAR 四种形状。", code, demo);
    }

    private Node paragraphSection() {
        Node demo = SkeletonAnt.paragraph(3, 280, 14);
        String code = """
                // 多行段落骨架，最后一行自动收窄
                SkeletonAnt.paragraph(3, 280, 14);
                """;
        return Demos.sectionWithCode("2. 段落",
                "paragraph(lines, width, lineHeight) 生成多行文本占位。", code, demo);
    }

    private Node avatarSection() {
        Node demo = SkeletonAnt.avatarText();
        String code = """
                // 圆形头像 + 两行文本，模拟列表项加载态
                SkeletonAnt.avatarText();
                """;
        return Demos.sectionWithCode("3. 头像 + 文本",
                "avatarText() 组合圆形头像和文本行，适合列表项占位。", code, demo);
    }
}
