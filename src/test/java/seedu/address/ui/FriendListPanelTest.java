package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import seedu.address.model.FriendListView;
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

    @Test
    void archiveHeading_emptyList_stillIdentifiesView() {
        FxTestUtil.runAndWait(() -> {
            FriendListPanel panel = new FriendListPanel(FXCollections.observableArrayList(), friend -> {});
            Label heading = (Label) panel.getRoot().lookup("#listHeading");
            assertEquals("Active friends", heading.getText());
            panel.setFriendListView(FriendListView.ARCHIVED);
            assertEquals("Archived friends", heading.getText());
            panel.setFriendListView(FriendListView.ACTIVE);
            assertEquals("Active friends", heading.getText());
            panel.setFriendListView(FriendListView.SEARCH);
            assertEquals("Search results", heading.getText());
        });
    }

    @Test
    void friendCard_showsArchiveBadgeOnlyForArchivedFriends() {
        FxTestUtil.runAndWait(() -> {
            Friend friend = new FriendBuilder().build();
            FriendCard activeCard = new FriendCard(friend, 1);
            Label activeBadge = (Label) activeCard.getRoot().lookup("#archivedBadge");
            assertFalse(activeBadge.isVisible());
            assertFalse(activeBadge.isManaged());
            FriendCard archivedCard = new FriendCard(friend.withArchived(true), 2);
            Label archivedBadge = (Label) archivedCard.getRoot().lookup("#archivedBadge");
            assertEquals("Archived", archivedBadge.getText());
            assertTrue(archivedBadge.isVisible());
            assertTrue(archivedBadge.isManaged());
        });
    }

    @SuppressWarnings("unchecked")
    private ListView<Friend> getListView(FriendListPanel panel) {
        return (ListView<Friend>) panel.getRoot().lookup("#friendListView");
    }
}
