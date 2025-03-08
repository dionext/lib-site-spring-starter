package com.dionext.utils;



import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class FmMarkdownUtils {

    static public String markdownToHtml(String markdown) {
        if (markdown == null) return null;
        if (markdown.isEmpty() || markdown.isBlank()) return "";
        // Configure Flexmark options (optional)
        MutableDataSet options = new MutableDataSet();

        // Create a Parser and HtmlRenderer
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // Parse Markdown and render to HTML
        Node document = parser.parse(markdown);
        String html = renderer.render(document);

        return html;
    }
    static public String htmlToMarkdown(String html) {
        if (html == null) return null;
        if (html.isEmpty() || html.isBlank()) return "";
        // Convert HTML to Markdown
        String markdown = FlexmarkHtmlConverter.builder().build().convert(html);

        return markdown;//todo
    }
}
