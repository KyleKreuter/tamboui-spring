package io.github.kylekreuter.tamboui.spring.css;

import dev.tamboui.buffer.Buffer;
import dev.tamboui.buffer.Cell;
import dev.tamboui.css.engine.StyleEngine;
import dev.tamboui.layout.Constraint;
import dev.tamboui.layout.Rect;
import dev.tamboui.style.Color;
import dev.tamboui.terminal.Frame;
import dev.tamboui.toolkit.Toolkit;
import dev.tamboui.toolkit.element.DefaultRenderContext;
import dev.tamboui.toolkit.element.Element;
import dev.tamboui.toolkit.elements.DockElement;
import dev.tamboui.toolkit.elements.Panel;
import dev.tamboui.toolkit.elements.TextElement;

import io.github.kylekreuter.tamboui.spring.core.UtilityCssLoader;
import io.github.kylekreuter.tamboui.spring.core.WidgetToElementConverter;
import io.github.kylekreuter.tamboui.spring.template.tags.PanelTagHandler;
import io.github.kylekreuter.tamboui.spring.template.tags.TextTagHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that mimics the full runtime pipeline:
 * DockElement containing Panels with CSS classes, rendered through
 * DefaultRenderContext with StyleEngine.
 */
class CssFullPipelineTest {

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
    void dockWithCssPanels_shouldApplyColors() {
        // Build element tree like the WidgetToElementConverter does
        Panel topPanel = Toolkit.panel("Dashboard");
        topPanel.addClass("border-rounded", "text-cyan");
        topPanel.add(Toolkit.text("Status: Running"));

        Panel centerPanel = Toolkit.panel("Content");
        centerPanel.addClass("border-rounded", "text-green");
        centerPanel.add(Toolkit.text("Hello World"));

        DockElement dock = Toolkit.dock();
        dock.top(topPanel, Constraint.length(3));
        dock.center(centerPanel);

        // Render the full tree
        dock.render(frame, new Rect(0, 0, 80, 24), context);

        // Check top panel border has cyan color
        Cell topBorder = buffer.get(0, 0);
        System.out.println("Top border symbol: '" + topBorder.symbol() + "'");
        System.out.println("Top border fg: " + topBorder.style().fg());
        assertThat(topBorder.style().fg()).as("Top panel border should be cyan").isPresent();
        assertThat(topBorder.style().fg().get()).isEqualTo(Color.CYAN);

        // Check center panel border has green color (starts at row 3)
        Cell centerBorder = buffer.get(0, 3);
        System.out.println("Center border symbol: '" + centerBorder.symbol() + "'");
        System.out.println("Center border fg: " + centerBorder.style().fg());
        assertThat(centerBorder.style().fg()).as("Center panel border should be green").isPresent();
        assertThat(centerBorder.style().fg().get()).isEqualTo(Color.GREEN);
    }

    @Test
    void converterCreatedElements_shouldApplyColors() {
        // Simulate what the WidgetToElementConverter does
        WidgetToElementConverter converter = new WidgetToElementConverter();

        // Create a PanelWidget like PanelTagHandler would
        var panelWidget = new PanelTagHandler.PanelWidget(
                dev.tamboui.widgets.block.Block.builder()
                        .borders(dev.tamboui.widgets.block.Borders.ALL)
                        .title("Test Panel")
                        .build(),
                "Test Panel",
                "border-rounded text-cyan"
        );

        // Create a TextWidget like TextTagHandler would
        var textWidget = new TextTagHandler.TextWidget("Hello", "text-green bold");
        panelWidget.children().add(textWidget);

        Element element = converter.convert(panelWidget);

        // Render
        element.render(frame, new Rect(0, 0, 40, 5), context);

        // Check border has cyan color
        Cell borderCell = buffer.get(0, 0);
        System.out.println("Converter panel border symbol: '" + borderCell.symbol() + "'");
        System.out.println("Converter panel border fg: " + borderCell.style().fg());
        assertThat(borderCell.style().fg()).as("Panel from converter should have cyan border").isPresent();
        assertThat(borderCell.style().fg().get()).isEqualTo(Color.CYAN);

        // Check text inside panel has green color (row 1, col 1 due to padding)
        // Panel with borders: content starts at (1,1)
        Cell textCell = buffer.get(1, 1);
        System.out.println("Converter text symbol: '" + textCell.symbol() + "'");
        System.out.println("Converter text fg: " + textCell.style().fg());
        assertThat(textCell.symbol()).isEqualTo("H");
        assertThat(textCell.style().fg()).as("Text from converter should have green fg").isPresent();
        assertThat(textCell.style().fg().get()).isEqualTo(Color.GREEN);
    }
}
