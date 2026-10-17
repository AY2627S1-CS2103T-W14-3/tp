package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.game.Game;
import seedu.address.model.game.meta.Meta;

/**
 * A card that displays the details for one game entry.
 *
 * Additional game properties can be added to this card without changing the friend details panel.
 */
public class GameDetailsCard extends UiPart<Region> {
    private static final String FXML = "GameDetailsCard.fxml";

    @FXML
    @SuppressWarnings("unused")
    private Label gameName;
    @FXML
    @SuppressWarnings("unused")
    private Label username;
    @FXML
    @SuppressWarnings("unused")
    private VBox metadata;

    /** Creates a card for {@code game}. */
    public GameDetailsCard(Game game) {
        super(FXML);
        requireNonNull(game);
        gameName.setText(game.getGameName().value);
        username.setText("Username: "
                + (game.getUsername().isEmpty() ? "Not provided" : game.getUsername().value));
        metadata.getChildren().setAll(game.getMetas().stream()
                .sorted(Comparator.comparing(meta -> meta.getKey().value, String.CASE_INSENSITIVE_ORDER))
                .map(this::createMetadataLabel)
                .toList());
        boolean hasMetadata = !game.getMetas().isEmpty();
        metadata.setVisible(hasMetadata);
        metadata.setManaged(hasMetadata);
    }

    private Label createMetadataLabel(Meta meta) {
        Label label = new Label(meta.getKey() + ": " + meta.getValue());
        label.getStyleClass().add("details-game-metadata");
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setMinHeight(Region.USE_PREF_SIZE);
        return label;
    }
}
