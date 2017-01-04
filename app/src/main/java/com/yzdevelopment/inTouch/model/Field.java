package com.yzdevelopment.inTouch.model;

public class Field {
    private String field_name;
    private String field_value;
    private boolean selected;

    public Field(String field_name, String field_value, int selected) {
        this.field_name = field_name;
        this.field_value = field_value;
        this.selected = selected != 0;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getField_value() {
        return field_value;
    }

    public void setField_value(String field_value) {
        this.field_value = field_value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
