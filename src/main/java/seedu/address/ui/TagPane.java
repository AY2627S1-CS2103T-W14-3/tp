package seedu.address.ui;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

/**
 * A tag pane that measures wrapped labels using the proposed width, including before its first layout.
 */
public class TagPane extends FlowPane {

    @Override
    protected double computePrefHeight(double width) {
        double availableWidth = width < 0 ? getPrefWrapLength() : width - snappedLeftInset() - snappedRightInset();
        constrainTags(availableWidth);
        return super.computePrefHeight(width);
    }

    @Override
    protected void layoutChildren() {
        constrainTags(getWidth() - snappedLeftInset() - snappedRightInset());
        super.layoutChildren();
    }

    private void constrainTags(double width) {
        for (Node child : getChildren()) {
            if (child instanceof TagLabel tag) {
                tag.setAvailableWidth(Math.max(0, width));
            }
        }
    }
}
