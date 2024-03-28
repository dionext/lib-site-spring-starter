package com.dionext.site.dto;

public class ImageDrawInfo {
    private String imagePath;
    private int width = -1;
    private int height = -1;
    private String title;
    private String href;
    private boolean blank;
    private boolean noExt;
    private boolean inDiv;
    private boolean tumb;
    private boolean imgCard;
    private Align align = Align.values()[0];
    private String style;
    private String addClass;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isBlank() {
        return blank;
    }

    public void setBlank(boolean blank) {
        this.blank = blank;
    }

    public boolean isNoExt() {
        return noExt;
    }

    public void setNoExt(boolean noExt) {
        this.noExt = noExt;
    }

    public boolean isInDiv() {
        return inDiv;
    }

    public void setInDiv(boolean inDiv) {
        this.inDiv = inDiv;
    }

    public boolean isTumb() {
        return tumb;
    }

    public void setTumb(boolean tumb) {
        this.tumb = tumb;
    }

    public boolean isImgCard() {
        return imgCard;
    }

    public void setImgCard(boolean imgCard) {
        this.imgCard = imgCard;
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getAddClass() {
        return addClass;
    }

    public void setAddClass(String addClass) {
        this.addClass = addClass;
    }
}