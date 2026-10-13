package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

class FriendDetailsPanelTest {

    @Test
    void setFriend_defaultProfile_showsEmptyState() {
        FxTestUtil.runAndWait(() -> {
            FriendDetailsPanel panel = new FriendDetailsPanel(Friend.DEFAULT_MYSELF, true);

            assertEquals("Your details", getLabel(panel, "#title").getText());
            assertTrue(getLabel(panel, "#emptyState").isVisible());
            assertFalse(getDetailsContainer(panel).isVisible());
        });
    }

    @Test
    void setFriend_friendWithDetails_showsAllDetails() {
        FxTestUtil.runAndWait(() -> {
            Friend friend = new FriendBuilder().withName("Alice Pauline").withPhone("91234567")
                    .withEmail("alice@example.com").withTags("friends", "competitive")
                    .withGames(new GameBuilder().withGameName("Valorant").withUsername("alice123").build(),
                            new GameBuilder().withGameName("Apex").withUsername("alice456").build()).build();
            FriendDetailsPanel panel = new FriendDetailsPanel(friend, false);

            assertEquals("Friend details", getLabel(panel, "#title").getText());
            assertFalse(getLabel(panel, "#emptyState").isVisible());
            assertEquals("Alice Pauline", getLabel(panel, "#name").getText());
            assertEquals("Phone: 91234567", getLabel(panel, "#phone").getText());
            assertEquals("Email: alice@example.com", getLabel(panel, "#email").getText());
            assertEquals("competitive", getFirstLabelText(getFlowPane(panel, "#tags")));
            assertEquals("Apex: alice456", getFirstLabelText(getFlowPane(panel, "#games")));
        });
    }

    @Test
    void setFriend_friendWithoutTagsOrGames_showsEmptyDetailMessages() {
        FxTestUtil.runAndWait(() -> {
            FriendDetailsPanel panel = new FriendDetailsPanel(new FriendBuilder().withTags().build(), false);

            assertEquals("No tags added", getFirstLabelText(getFlowPane(panel, "#tags")));
            assertEquals("No games added", getFirstLabelText(getFlowPane(panel, "#games")));
        });
    }

    private Label getLabel(FriendDetailsPanel panel, String selector) {
        return (Label) panel.getRoot().lookup(selector);
    }

    private VBox getDetailsContainer(FriendDetailsPanel panel) {
        return (VBox) panel.getRoot().lookup("#detailsContainer");
    }

    private FlowPane getFlowPane(FriendDetailsPanel panel, String selector) {
        return (FlowPane) panel.getRoot().lookup(selector);
    }

    private String getFirstLabelText(FlowPane flowPane) {
        return ((Label) flowPane.getChildren().getFirst()).getText();
    }
}
