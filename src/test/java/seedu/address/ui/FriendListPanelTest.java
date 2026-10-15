package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;

class FriendListPanelTest {

    @Test
    void selectionChanged_notifiesConsumer() {
        FxTestUtil.runAndWait(() -> {
            Friend amy = new FriendBuilder().withName("Amy").build();
            Friend ben = new FriendBuilder().withName("Ben").build();
            ObservableList<Friend> friends = FXCollections.observableArrayList(amy, ben);
            AtomicReference<Friend> selectedFriend = new AtomicReference<>();
            FriendListPanel panel = new FriendListPanel(friends, selectedFriend::set);

            ListView<Friend> listView = getListView(panel);
            assertEquals(friends, listView.getItems());

            listView.getSelectionModel().select(ben);
            assertEquals(ben, selectedFriend.get());

            listView.getSelectionModel().clearSelection();
            assertNull(selectedFriend.get());
        });
    }

    @SuppressWarnings("unchecked")
    private ListView<Friend> getListView(FriendListPanel panel) {
        return (ListView<Friend>) panel.getRoot().lookup("#friendListView");
    }
}
