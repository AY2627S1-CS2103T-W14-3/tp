package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;

class FriendCardTest {

    @Test
    void pinnedFriend_displaysPinnedBadge() {
        FxTestUtil.runAndWait(() -> {
            Friend friend = new FriendBuilder().withPinned(true).build();
            Label pinnedBadge = getPinnedBadge(new FriendCard(friend, 1));

            assertEquals("Pinned", pinnedBadge.getText());
            assertTrue(pinnedBadge.isVisible());
            assertTrue(pinnedBadge.isManaged());
        });
    }

    @Test
    void unpinnedFriend_hidesPinnedBadge() {
        FxTestUtil.runAndWait(() -> {
            Friend friend = new FriendBuilder().withPinned(false).build();
            Label pinnedBadge = getPinnedBadge(new FriendCard(friend, 1));

            assertFalse(pinnedBadge.isVisible());
            assertFalse(pinnedBadge.isManaged());
        });
    }

    @Test
    void longPinnedName_atNarrowCardWidths_keepsBadgeFullyVisible() {
        FxTestUtil.runAndWait(() -> {
            for (int width : new int[]{240, 280, 340}) {
                Friend friend = new FriendBuilder().withName("A".repeat(100)).withPinned(true).build();
                FriendCard card = new FriendCard(friend, 999);
                VBox container = new VBox(card.getRoot());
                Scene scene = new Scene(container, width, 300);
                scene.getStylesheets().add(FriendCard.class.getResource("/view/DarkTheme.css").toExternalForm());
                container.applyCss();
                container.layout();

                Label badge = getPinnedBadge(card);
                assertTrue(badge.getWidth() >= badge.prefWidth(-1) - 0.1);
                assertTrue(badge.localToScene(badge.getBoundsInLocal()).getMaxX() <= width);
                Label name = (Label) card.getRoot().lookup("#name");
                assertTrue(name.getWidth() < name.prefWidth(-1));
            }
        });
    }

    private Label getPinnedBadge(FriendCard card) {
        return (Label) card.getRoot().lookup("#pinned");
    }
}
