package seedu.address.ui;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.friend.Friend;

/**
 * Panel containing the list of friends.
 */
public class FriendListPanel extends UiPart<Region> {
    private static final String FXML = "FriendListPanel.fxml";

    @FXML
    @SuppressWarnings("unused")
    private ListView<Friend> friendListView;

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
        friendListView.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) ->
                onFriendSelected.accept(newValue));
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
