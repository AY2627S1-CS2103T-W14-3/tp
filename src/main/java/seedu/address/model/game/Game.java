package seedu.address.model.game;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Game in GameMates.
 * Guarantees: immutable; gameName is valid as declared in {@link #isValidGameName(String)}
 */

public class Game {

    public static final String MESSAGE_CONSTRAINTS =
            "Game names must contain 1 to 50 printable characters "
            + "and include at least one letter or digit.";

    public static final int MAX_GAME_NAME_LENGTH = 50;

    public final String gameName;

    /**
     * Constructs a {@code Game}.
     *
     * @param gameName A valid game name.
     */
    public Game(String gameName) {
        requireNonNull(gameName);
        checkArgument(isValidGameName(gameName), MESSAGE_CONSTRAINTS);
        this.gameName = gameName.strip();
    }

    /**
     * Returns true if a given string is a valid game name.
     */
    public static boolean isValidGameName(String test) {
        if (test == null) {
            return false;
        }

        String trimmedName = test.strip();

        return !trimmedName.isEmpty()
                && trimmedName.length() <= MAX_GAME_NAME_LENGTH
                && trimmedName.codePoints().anyMatch(Character::isLetterOrDigit)
                && trimmedName.codePoints().noneMatch(Character::isISOControl);
    }

    /**
     * Returns a form used for case-insensitive comparisons.
     */
    private String getNormalizedName() {
        return gameName.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Game otherGame)) {
            return false;
        }

        return getNormalizedName().equals(otherGame.getNormalizedName());
    }

    @Override
    public int hashCode() {
        return getNormalizedName().hashCode();
    }

    @Override
    public String toString() {
        return '[' + gameName + ']';
    }
}
