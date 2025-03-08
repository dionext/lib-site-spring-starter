package com.dionext.utils;

import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.Set;

public class CmMarkdownUtils {

    // Custom NodeRenderer to convert all headers to <h3>
    static class CustomHeaderRenderer implements NodeRenderer {
        private final HtmlWriter htmlWriter;

        public CustomHeaderRenderer(HtmlNodeRendererContext context) {
            this.htmlWriter = context.getWriter();
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes() {
            // Handle all types of headers
            return Set.of(Heading.class);
        }

        @Override
        public void render(Node node) {
            // Render all headers as <h3>
            htmlWriter.tag("h4");
            for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
                if (child instanceof Text) {
                    htmlWriter.text(((Text) child).getLiteral());
                }
            }
            htmlWriter.tag("/h4");
        }
    }

    static public String markdownToHtml(String markdown) {
        if (markdown == null) return null;
        if (markdown.isEmpty() || markdown.isBlank()) return "";

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        // Create a renderer with the custom header renderer
        HtmlRenderer renderer = HtmlRenderer.builder()
                .nodeRendererFactory(context -> new CustomHeaderRenderer(context))
                .build();
        //HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }

}
