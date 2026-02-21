package io.github.kylekreuter.tamboui.spring.css;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.buffer.Cell;
import dev.tamboui.css.cascade.CssStyleResolver;
import dev.tamboui.css.engine.StyleEngine;
import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.style.Style;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.DefaultRenderContext;
import dev.tamboui.toolkit.elements.DockElement;
import dev.tamboui.toolkit.elements.Panel;
import dev.tamboui.toolkit.elements.TextElement;

import io.github.kylekreuter.tamboui.spring.core.UtilityCssLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Diagnostic test to trace exactly where CSS styles are lost.
 */
class CssDiagnosticTest {

    private StyleEngine styleEngine;
    private DefaultRenderContext context;

    @BeforeEach
    void setUp() {
        styleEngine = StyleEngine.create();
        UtilityCssLoader loader = new UtilityCssLoader("META-INF/tamboui-spring/utility.tcss");
        loader.loadInto(styleEngine);

        context = DefaultRenderContext.createEmpty();
        context.setStyleEngine(styleEngine);
    }

    /**
     * Step 1: Verify CSS resolution works at all.
     */
    @Test
    void step1_cssResolutionWorks() {
        Panel panel = Toolkit.panel("Test");
        panel.addClass("text-cyan", "border-rounded");

        // Direct CSS resolution
        var resolved = context.resolveStyle(panel);
        System.out.println("Step1: resolveStyle present? " + resolved.isPresent());
        if (resolved.isPresent()) {
            CssStyleResolver r = resolved.get();
            Style s = r.toStyle();
            System.out.println("Step1: toStyle().fg() = " + s.fg());
            System.out.println("Step1: borderType = " + r.borderType());
            System.out.println("Step1: borderColor = " + r.borderColor());
            assertThat(s.fg()).as("CSS should resolve fg for .text-cyan").isPresent();
        }
        assertThat(resolved).as("CSS resolution should return a resolver").isPresent();
    }

    /**
     * Step 2: Render a standalone TextElement with CSS — this SHOULD work.
     */
    @Test
    void step2_textElementCssWorks() {
        Buffer buffer = Buffer.empty(new Rect(0, 0, 20, 1));
        Frame frame = Frame.forTesting(buffer);

        TextElement text = Toolkit.text("Hello");
        text.addClass("text-cyan");
        text.render(frame, new Rect(0, 0, 20, 1), context);

        Cell cell = buffer.get(0, 0);
        System.out.println("Step2: TextElement cell(0,0) = '" + cell.symbol() + "' fg=" + cell.style().fg());
        assertThat(cell.style().fg()).isPresent();
        assertThat(cell.style().fg().get()).isEqualTo(Color.CYAN);
    }

    /**
     * Step 3: Render a Panel directly — check BOTH border and content cells.
     */
    @Test
    void step3_panelDirectRender() {
        Buffer buffer = Buffer.empty(new Rect(0, 0, 20, 5));
        Frame frame = Frame.forTesting(buffer);

        Panel panel = Toolkit.panel("Test");
        panel.addClass("text-cyan", "border-rounded");
        panel.add(Toolkit.text("Hello"));

        panel.render(frame, new Rect(0, 0, 20, 5), context);

        // Check border cell (0,0) — top-left corner
        Cell borderCell = buffer.get(0, 0);
        System.out.println("Step3: Border cell(0,0) = '" + borderCell.symbol()
                + "' fg=" + borderCell.style().fg()
                + " bg=" + borderCell.style().bg()
                + " full=" + borderCell.style());

        // Check content cell (1,1) — text content inside panel
        Cell contentCell = buffer.get(1, 1);
        System.out.println("Step3: Content cell(1,1) = '" + contentCell.symbol()
                + "' fg=" + contentCell.style().fg()
                + " bg=" + contentCell.style().bg()
                + " full=" + contentCell.style());

        // Check empty interior cell (5,1) — area that was setStyle'd
        Cell interiorCell = buffer.get(5, 1);
        System.out.println("Step3: Interior cell(5,1) = '" + interiorCell.symbol()
                + "' fg=" + interiorCell.style().fg()
                + " full=" + interiorCell.style());

        // Check top border horizontal cell (1,0) — not corner
        Cell topBorderH = buffer.get(1, 0);
        System.out.println("Step3: TopBorderH cell(1,0) = '" + topBorderH.symbol()
                + "' fg=" + topBorderH.style().fg()
                + " full=" + topBorderH.style());

        // Content should have cyan from CSS
        assertThat(contentCell.symbol()).isEqualTo("H");
        assertThat(contentCell.style().fg()).as("Content text should have cyan fg").isPresent();
    }

    /**
     * Step 4: Compare — Panel with PROGRAMMATIC .cyan() vs CSS .text-cyan.
     */
    @Test
    void step4_programmaticVsCss() {
        // Programmatic cyan
        Buffer buf1 = Buffer.empty(new Rect(0, 0, 20, 5));
        Frame frame1 = Frame.forTesting(buf1);

        Panel panel1 = Toolkit.panel("Programmatic");
        panel1.cyan().rounded();
        panel1.add(Toolkit.text("Hello"));
        panel1.render(frame1, new Rect(0, 0, 20, 5), context);

        Cell prog_border = buf1.get(0, 0);
        Cell prog_content = buf1.get(1, 1);
        System.out.println("Step4 PROG: Border(0,0) = '" + prog_border.symbol()
                + "' fg=" + prog_border.style().fg());
        System.out.println("Step4 PROG: Content(1,1) = '" + prog_content.symbol()
                + "' fg=" + prog_content.style().fg());

        // CSS cyan
        Buffer buf2 = Buffer.empty(new Rect(0, 0, 20, 5));
        Frame frame2 = Frame.forTesting(buf2);

        Panel panel2 = Toolkit.panel("CSS");
        panel2.addClass("text-cyan", "border-rounded");
        panel2.add(Toolkit.text("Hello"));
        panel2.render(frame2, new Rect(0, 0, 20, 5), context);

        Cell css_border = buf2.get(0, 0);
        Cell css_content = buf2.get(1, 1);
        System.out.println("Step4 CSS: Border(0,0) = '" + css_border.symbol()
                + "' fg=" + css_border.style().fg());
        System.out.println("Step4 CSS: Content(1,1) = '" + css_content.symbol()
                + "' fg=" + css_content.style().fg());

        // Both should have cyan text
        System.out.println("Step4 PROG: border style = " + prog_border.style());
        System.out.println("Step4 CSS: border style = " + css_border.style());
    }

    /**
     * Step 5: Panel in DockElement — check text content, not just borders.
     */
    @Test
    void step5_panelInDock_checkContent() {
        Buffer buffer = Buffer.empty(new Rect(0, 0, 80, 24));
        Frame frame = Frame.forTesting(buffer);

        Panel topPanel = Toolkit.panel("Dashboard");
        topPanel.addClass("text-cyan");
        topPanel.add(Toolkit.text("Status: Running"));

        DockElement dock = Toolkit.dock();
        dock.top(topPanel, Constraint.length(3));
        dock.center(Toolkit.text("center"));

        dock.render(frame, new Rect(0, 0, 80, 24), context);

        // Check text content inside panel (1,1) — should be cyan
        Cell contentCell = buffer.get(1, 1);
        System.out.println("Step5: Content(1,1) = '" + contentCell.symbol()
                + "' fg=" + contentCell.style().fg());

        // Check border
        Cell borderCell = buffer.get(0, 0);
        System.out.println("Step5: Border(0,0) = '" + borderCell.symbol()
                + "' fg=" + borderCell.style().fg());

        // Print a few more cells to understand the layout
        for (int y = 0; y < 4; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < 20; x++) {
                Cell c = buffer.get(x, y);
                sb.append(c.symbol());
            }
            System.out.println("Step5: row " + y + ": '" + sb + "'");
        }

        // The TEXT content inside the panel should have cyan fg
        assertThat(contentCell.style().fg()).as("Text inside panel should inherit cyan from CSS").isPresent();
    }
}
