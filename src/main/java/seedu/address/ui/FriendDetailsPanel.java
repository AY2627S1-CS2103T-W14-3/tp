package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.GameName;

/**
 * A panel that displays the complete details of the user profile or one friend.
 */
public class FriendDetailsPanel extends UiPart<Region> {

    private static final String FXML = "FriendDetailsPanel.fxml";

    @FXML
    @SuppressWarnings("unused")
    private Label title;
    @FXML
    @SuppressWarnings("unused")
    private Label emptyState;
    @FXML
    @SuppressWarnings("unused")
    private VBox detailsContainer;
    @FXML
    @SuppressWarnings("unused")
    private Label name;
    @FXML
    @SuppressWarnings("unused")
    private Label phone;
    @FXML
    @SuppressWarnings("unused")
    private Label email;
    @FXML
    @SuppressWarnings("unused")
    private FlowPane tags;
    @FXML
    @SuppressWarnings("unused")
    private VBox games;

    /**
     * Creates a details panel for the user profile or a friend.
     *
     * @param friend the person whose details to display
     * @param isMyself whether {@code friend} is the user profile
     */
    public FriendDetailsPanel(Friend friend, boolean isMyself) {
        super(FXML);
        setFriend(friend, isMyself);
    }

    /**
     * Shows {@code friend}'s details. The default profile is represented by an empty state.
     */
    public void setFriend(Friend friend, boolean isMyself) {
        setFriend(friend, isMyself, null);
    }

    /**
     * Shows the original friend's details, optionally filtering only the rendered game cards.
     * A null filter shows all games, including for the user profile.
     */
    public void setFriend(Friend friend, boolean isMyself, GameName gameFilter) {
        requireNonNull(friend);
        title.setText(isMyself ? "Your details" : "Friend details");

        boolean hasNoProfileDetails = isMyself && Friend.DEFAULT_MYSELF.equals(friend);
        emptyState.setVisible(hasNoProfileDetails);
        emptyState.setManaged(hasNoProfileDetails);
        detailsContainer.setVisible(!hasNoProfileDetails);
        detailsContainer.setManaged(!hasNoProfileDetails);

        if (hasNoProfileDetails) {
            return;
        }

        name.setText(friend.getName().fullName);
        phone.setText("Phone: " + (friend.getPhone().isEmpty() ? "Not provided" : friend.getPhone().value));
        email.setText("Email: " + (friend.getEmail().isEmpty() ? "Not provided" : friend.getEmail().value));

        tags.getChildren().setAll(friend.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .map(tag -> {
                    Label label = new TagLabel(tag.tagName);
                    label.getStyleClass().add("details-tag");
                    return label;
                })
                .toList());
        if (tags.getChildren().isEmpty()) {
            tags.getChildren().add(createLabel("No tags added", "details-empty-value"));
        }

        games.getChildren().setAll(friend.getGames().stream()
                .filter(game -> gameFilter == null || gameFilter.equals(game.getGameName()))
                .sorted(Comparator.comparing(game -> game.getGameName().value))
                .map(GameDetailsCard::new)
                .map(GameDetailsCard::getRoot)
                .toList());
        if (games.getChildren().isEmpty()) {
            String emptyMessage = gameFilter == null ? "No games added" : "No matching game: " + gameFilter;
            games.getChildren().add(createLabel(emptyMessage, "details-empty-value"));
        }
    }

    private Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }
}
