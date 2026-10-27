package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import seedu.address.model.FriendListScope;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;

class FriendListPanelTest {

    @Test
    @SuppressWarnings("unchecked")
    void selectionChanged_notifiesConsumer() {
        FxTestUtil.runAndWait(() -> {
            Friend amy = new FriendBuilder().withName("Amy").build();
            Friend ben = new FriendBuilder().withName("Ben").build();
            ObservableList<Friend> friends = FXCollections.observableArrayList(amy, ben);
            AtomicReference<Friend> selectedFriend = new AtomicReference<>();
            FriendListPanel panel = new FriendListPanel(friends, selectedFriend::set);

            ListView<Friend> listView = getListView(panel);
            assertEquals(friends, listView.getItems());
            assertFalse(listView.isMouseTransparent());

            new Scene(panel.getRoot());
            panel.getRoot().resize(500, 500);
            panel.getRoot().applyCss();
            panel.getRoot().layout();
            ListCell<Friend> cell = listView.lookupAll(".list-cell").stream()
                    .map(node -> (ListCell<Friend>) node)
                    .filter(listCell -> amy.equals(listCell.getItem()))
                    .findFirst()
                    .orElseThrow();
            listView.getSelectionModel().select(ben);
            assertEquals(ben, selectedFriend.get());

            Node friendCard = cell.getGraphic();
            friendCard.fireEvent(newMousePressedEvent());
            assertEquals(ben, listView.getSelectionModel().getSelectedItem());
            assertEquals(ben, selectedFriend.get());

            AtomicBoolean listViewPressReceived = new AtomicBoolean();
            listView.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> listViewPressReceived.set(true));
            listView.fireEvent(newMousePressedEvent());
            assertTrue(listViewPressReceived.get());

            AtomicBoolean friendCardReleaseReceived = new AtomicBoolean();
            listView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> friendCardReleaseReceived.set(true));
            friendCard.fireEvent(newMouseEvent(MouseEvent.MOUSE_RELEASED));
            assertTrue(friendCardReleaseReceived.get());

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
            panel.setFriendListScope(FriendListScope.ARCHIVED);
            assertEquals("Archived friends", heading.getText());
            panel.setFriendListScope(FriendListScope.ACTIVE);
            assertEquals("Active friends", heading.getText());
            panel.setFriendListScope(FriendListScope.SEARCH);
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

    private MouseEvent newMousePressedEvent() {
        return newMouseEvent(MouseEvent.MOUSE_PRESSED);
    }

    private MouseEvent newMouseEvent(EventType<MouseEvent> eventType) {
        return new MouseEvent(eventType, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, true, false, false, null);
    }
}
