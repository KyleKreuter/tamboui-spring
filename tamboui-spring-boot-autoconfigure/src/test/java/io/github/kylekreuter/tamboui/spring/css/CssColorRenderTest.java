package io.github.kylekreuter.tamboui.spring.css;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.buffer.Cell;
import dev.tamboui.css.engine.StyleEngine;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.DefaultRenderContext;
import dev.tamboui.toolkit.elements.Panel;
import dev.tamboui.toolkit.elements.TextElement;

import io.github.kylekreuter.tamboui.spring.core.UtilityCssLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CssColorRenderTest {

    private StyleEngine styleEngine;
    private DefaultRenderContext context;
    private Buffer buffer;
    private Frame frame;

    @BeforeEach
    void setUp() {
        styleEngine = StyleEngine.create();
        UtilityCssLoader loader = new UtilityCssLoader("META-INF/tamboui-spring/utility.tcss");
        loader.loadInto(styleEngine);

        context = DefaultRenderContext.createEmpty();
        context.setStyleEngine(styleEngine);

        Rect area = new Rect(0, 0, 80, 24);
        buffer = Buffer.empty(area);
        frame = Frame.forTesting(buffer);
    }

    @Test
    void textWithCyanClass_shouldRenderWithCyanForeground() {
        TextElement text = Toolkit.text("Hello");
        text.addClass("text-cyan");

        text.render(frame, new Rect(0, 0, 10, 1), context);

        Cell cell = buffer.get(0, 0);
        assertThat(cell.symbol()).isEqualTo("H");
        assertThat(cell.style().fg()).isPresent();
        assertThat(cell.style().fg().get()).isEqualTo(Color.CYAN);
    }

    @Test
    void textWithGreenClassAndBold_shouldRenderWithGreenAndBold() {
        TextElement text = Toolkit.text("World");
        text.addClass("text-green", "bold");

        text.render(frame, new Rect(0, 0, 10, 1), context);

        Cell cell = buffer.get(0, 0);
        assertThat(cell.symbol()).isEqualTo("W");
        assertThat(cell.style().fg()).isPresent();
        assertThat(cell.style().fg().get()).isEqualTo(Color.GREEN);
    }

    @Test
    void panelWithTextCyan_shouldRenderBorderWithCyanColor() {
        Panel panel = Toolkit.panel("Test");
        panel.addClass("text-cyan", "border-rounded");

        panel.render(frame, new Rect(0, 0, 20, 5), context);

        // The border char at (0,0) should have cyan foreground
        Cell borderCell = buffer.get(0, 0);
        assertThat(borderCell.style().fg()).isPresent();
        assertThat(borderCell.style().fg().get()).isEqualTo(Color.CYAN);
    }

    @Test
    void textWithNoClass_shouldHaveNoForegroundColor() {
        TextElement text = Toolkit.text("Plain");

        text.render(frame, new Rect(0, 0, 10, 1), context);

        Cell cell = buffer.get(0, 0);
        assertThat(cell.symbol()).isEqualTo("P");
        // No CSS class = no color applied
        assertThat(cell.style().fg()).isEmpty();
    }
}
