package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.game.Game;

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

    /** Creates a card for {@code game}. */
    public GameDetailsCard(Game game) {
        super(FXML);
        requireNonNull(game);
        gameName.setText(game.getGameName().value);
        username.setText("Username: "
                + (game.getUsername().isEmpty() ? "Not provided" : game.getUsername().value));
    }
}
