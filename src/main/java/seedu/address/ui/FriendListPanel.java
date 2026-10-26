package seedu.address.ui;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatcher;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import seedu.address.model.FriendListScope;
import seedu.address.model.friend.Friend;

/**
 * Panel containing the list of friends.
 */
public class FriendListPanel extends UiPart<Region> {
    private static final String FXML = "FriendListPanel.fxml";

    @FXML
    @SuppressWarnings("unused")
    private ListView<Friend> friendListView;

    @FXML
    private Label listHeading;

    /**
     * Creates a {@code FriendListPanel} that notifies a consumer when its selection changes.
     *
     * @param friendList the friends to display
     * @param onFriendSelected receives the selected friend, or {@code null} when the selection is cleared
     */
    public FriendListPanel(ObservableList<Friend> friendList, Consumer<Friend> onFriendSelected) {
        super(FXML);
        friendListView.setItems(friendList);
        friendListView.setCellFactory(_ -> new FriendListViewCell());
        preventMouseSelection();
        friendListView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) ->
                onFriendSelected.accept(newValue));
    }

    /** Prevents presses on a friend card from reaching the list's selection behavior. */
    private void preventMouseSelection() {
        EventDispatcher defaultDispatcher = friendListView.getEventDispatcher();
        friendListView.setEventDispatcher((event, tail) -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED && isPressOnFriendCard(event)) {
                event.consume();
                return event;
            }
            return defaultDispatcher.dispatchEvent(event, tail);
        });
    }

    private boolean isPressOnFriendCard(Event event) {
        EventTarget target = event.getTarget();
        while (target instanceof Node node) {
            if (node instanceof FriendListViewCell) {
                return true;
            }
            target = node.getParent();
        }
        return false;
    }

    /** Updates the heading even when the current list is empty. */
    public void setFriendListScope(FriendListScope scope) {
        listHeading.setText(switch (scope) {
            case ACTIVE -> "Active friends";
            case ARCHIVED -> "Archived friends";
            case SEARCH -> "Search results";
        });
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Friend} using a {@code FriendCard}.
     */
    private static class FriendListViewCell extends ListCell<Friend> {
        @Override
        protected void updateItem(Friend friend, boolean empty) {
            super.updateItem(friend, empty);

            if (empty || friend == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new FriendCard(friend, getIndex() + 1).getRoot());
            }
        }
    }

}
