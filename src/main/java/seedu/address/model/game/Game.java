package seedu.address.model.game;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a game entry in GameMates.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Game {

    // Identity field
    private final GameName gameName;

    // Data field
    private final Username username;

    /**
     * Every field must be present and not null.
     */
    public Game(GameName gameName, Username username) {
        requireAllNonNull(gameName, username);
        this.gameName = gameName;
        this.username = username;
    }

    public GameName getGameName() {
        return gameName;
    }

    public Username getUsername() {
        return username;
    }

    /**
     * Returns true if both game entries refer to the same game.
     * This defines a weaker notion of equality between two games.
     */
    public boolean isSameGame(Game otherGame) {
        if (otherGame == this) {
            return true;
        }

        return otherGame != null
                && otherGame.getGameName().equals(getGameName());
    }

    /**
     * Returns true if both game entries have the same fields.
     * This defines a stronger notion of equality between two games.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Game otherGame)) {
            return false;
        }

        return gameName.equals(otherGame.gameName)
                && username.equals(otherGame.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameName, username);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("gameName", gameName)
                .add("username", username)
                .toString();
    }
}
