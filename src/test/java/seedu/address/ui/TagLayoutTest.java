package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;

class TagLayoutTest {
    private static final String LONG_TAG = "A".repeat(50);

    @Test
    void friendCard_longTag_wrapsWhenResized() {
        FxTestUtil.runAndWait(() -> assertTagsFit(new FriendCard(createFriend(), 1).getRoot()));
    }

    @Test
    void friendDetails_longTag_wrapsWhenResized() {
        FxTestUtil.runAndWait(() -> assertTagsFit(new FriendDetailsPanel(createFriend(), false).getRoot()));
    }

    @Test
    void friendList_firstAddOfMultipleLongTags_doesNotOverlapContactDetails() {
        FxTestUtil.runAndWait(() -> {
            ObservableList<Friend> friends = FXCollections.observableArrayList();
            Region root = new FriendListPanel(friends, friend -> {}).getRoot();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(TagLayoutTest.class.getResource("/view/DarkTheme.css").toExternalForm());
            root.resize(300, 800);
            root.applyCss();
            root.layout();

            friends.add(new FriendBuilder().withTags("a".repeat(30), "b".repeat(50), "c".repeat(50),
                    "d".repeat(50), "e".repeat(50)).build());
            root.applyCss();
            root.layout();

            Region card = (Region) root.lookup("#cardPane");
            FlowPane tags = (FlowPane) card.lookup("#tags");
            Label phone = (Label) card.lookup("#phone");
            Label email = (Label) card.lookup("#email");
            assertTrue(phone.getBoundsInParent().getMinY() >= tags.getBoundsInParent().getMaxY());
            assertTrue(email.getBoundsInParent().getMinY() >= phone.getBoundsInParent().getMaxY());
            assertTrue(email.localToScene(email.getBoundsInLocal()).getMaxY()
                    <= card.localToScene(0, card.getHeight()).getY());
            for (Node tag : tags.getChildren()) {
                assertTrue(tag.getBoundsInParent().getMaxY() <= tags.getHeight(),
                        "Tag extends below the tag pane on first add");
            }
        });
    }

    private Friend createFriend() {
        return new FriendBuilder().withTags(LONG_TAG, "friends").build();
    }

    private void assertTagsFit(Region root) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(TagLayoutTest.class.getResource("/view/DarkTheme.css").toExternalForm());
        root.applyCss();
        FlowPane tags = (FlowPane) root.lookup("#tags");
        double wideHeight = layoutTags(root, tags, 600);
        double narrowHeight = layoutTags(root, tags, 240);
        assertTrue(narrowHeight > wideHeight);
        assertEquals(wideHeight, layoutTags(root, tags, 600));
    }

    private double layoutTags(Region root, FlowPane tags, int width) {
        root.resize(width, root.prefHeight(width));
        root.layout();

        Label longTag = (Label) tags.getChildren().getFirst();
        Label shortTag = (Label) tags.getChildren().get(1);
        assertEquals(LONG_TAG, longTag.getText());
        assertTrue(longTag.getBoundsInParent().getMinY() >= 0);
        assertTrue(longTag.getBoundsInParent().getMaxX() <= tags.getWidth());
        assertTrue(longTag.getHeight() >= longTag.prefHeight(longTag.getWidth()));
        if (shortTag.getBoundsInParent().getMinX() < longTag.getBoundsInParent().getMaxX()) {
            assertTrue(shortTag.getBoundsInParent().getMinY() >= longTag.getBoundsInParent().getMaxY());
        }
        assertTrue(shortTag.getBoundsInParent().getMaxY() <= tags.getHeight());
        assertTrue(root.getBoundsInLocal().getHeight() <= root.getHeight());
        return longTag.getHeight();
    }
}
