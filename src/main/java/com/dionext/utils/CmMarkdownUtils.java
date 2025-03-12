package com.dionext.utils;

import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class CmMarkdownUtils {

    static public String markdownToHtml(String markdown) {
        return markdownToHtml(markdown, null);
    }

    static public String markdownToHtml(String markdown, Class rendererClass) {
        if (markdown == null) return null;
        if (markdown.isEmpty() || markdown.isBlank()) return "";

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = null;
        if (rendererClass != null) {
            // Create a renderer with the custom header renderer
            renderer = HtmlRenderer.builder()
                    .nodeRendererFactory(context ->
                    {
                        try {
                            return (NodeRenderer) rendererClass.getDeclaredConstructor(HtmlNodeRendererContext.class).newInstance(context);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    //new CustomHeaderRenderer(context))
                    .build();
        }
        else {
            renderer = HtmlRenderer.builder().build();
        }
        return renderer.render(document);
    }

}
