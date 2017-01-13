package com.yzdevelopment.inTouch.model;

public class CheckboxInfo {
    private String key;
    private String value;
    private boolean selected;

    public CheckboxInfo(String key, String value, boolean selected) {
        this.key = key;
        this.value = value;
        this.selected = selected;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
