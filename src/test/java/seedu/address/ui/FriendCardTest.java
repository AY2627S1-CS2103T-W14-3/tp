package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
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

    private Label getPinnedBadge(FriendCard card) {
        return (Label) card.getRoot().lookup("#pinned");
    }
}
