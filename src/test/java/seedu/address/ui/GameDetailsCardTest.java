package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seedu.address.testutil.GameBuilder;

class GameDetailsCardTest {

    @Test
    void metadataWithoutUsername_displaysPlaceholderAndMetadata() {
        FxTestUtil.runAndWait(() -> {
            GameDetailsCard card = new GameDetailsCard(new GameBuilder().withoutUsername()
                    .addMeta("Role", "Support").build());

            assertEquals("Username: Not provided", ((Label) card.getRoot().lookup("#username")).getText());
            VBox metadata = (VBox) card.getRoot().lookup("#metadata");
            assertTrue(metadata.isVisible());
            assertEquals("Role: Support", ((Label) metadata.getChildren().getFirst()).getText());
        });
    }

    @Test
    void metadata_preservesTextAndSortsKeysIgnoringCase() {
        FxTestUtil.runAndWait(() -> {
            GameDetailsCard card = new GameDetailsCard(new GameBuilder()
                    .addMeta("Role", "Support:Mid").addMeta("level", "20").build());
            VBox metadata = (VBox) card.getRoot().lookup("#metadata");

            assertTrue(metadata.isVisible());
            assertTrue(metadata.isManaged());
            assertEquals(List.of("level: 20", "Role: Support:Mid"), metadata.getChildren().stream()
                    .map(node -> ((Label) node).getText()).toList());
        });
    }

    @Test
    void noMetadata_hidesContainerWithoutTakingSpace() {
        FxTestUtil.runAndWait(() -> {
            GameDetailsCard card = new GameDetailsCard(new GameBuilder().build());
            VBox metadata = (VBox) card.getRoot().lookup("#metadata");

            assertTrue(metadata.getChildren().isEmpty());
            assertFalse(metadata.isVisible());
            assertFalse(metadata.isManaged());
        });
    }

    @Test
    void longMetadata_wrapsWithoutClippingAtNarrowWidth() {
        FxTestUtil.runAndWait(() -> {
            GameDetailsCard card = new GameDetailsCard(new GameBuilder()
                    .addMeta("K".repeat(50), "V".repeat(200)).addMeta("Role", "Support").build());
            VBox root = (VBox) card.getRoot();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(GameDetailsCard.class.getResource("/view/DarkTheme.css").toExternalForm());
            root.applyCss();
            root.resize(600, root.prefHeight(600));
            root.layout();
            VBox metadata = (VBox) root.lookup("#metadata");
            Label longField = (Label) metadata.getChildren().getFirst();
            double wideHeight = longField.getHeight();

            root.resize(240, root.prefHeight(240));
            root.layout();

            assertTrue(longField.getHeight() > wideHeight);
            assertTrue(longField.getWidth() <= metadata.getWidth());
            assertTrue(longField.getHeight() >= longField.prefHeight(longField.getWidth()));
            assertTrue(metadata.getChildren().get(1).getBoundsInParent().getMinY()
                    >= longField.getBoundsInParent().getMaxY());
            assertTrue(metadata.getBoundsInParent().getMaxY() <= root.getHeight());
        });
    }
}
