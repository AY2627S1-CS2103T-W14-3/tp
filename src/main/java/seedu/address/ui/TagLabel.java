package seedu.address.ui;

import javafx.scene.control.Label;

/**
 * A tag label that wraps within its containing tag pane as the available width changes.
 */
class TagLabel extends Label {

    TagLabel(String text) {
        super(text);
        setWrapText(true);
        setMinWidth(0);
    }

    void setAvailableWidth(double width) {
        setMaxWidth(width);
        setMinHeight(prefHeight(width));
    }
}
