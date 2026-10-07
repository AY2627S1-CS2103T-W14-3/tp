package seedu.address.model.game;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a game's name in GameMates.
 * Guarantees: immutable; is valid as declared in {@link #isValidGameName(String)}
 */
public class GameName {

    public static final String MESSAGE_CONSTRAINTS =
            "Game names must contain 1 to 50 printable characters "
                    + "and include at least one letter or digit.";

    public static final int MAX_GAME_NAME_LENGTH = 50;

    public final String value;

    /**
     * Constructs a {@code GameName}.
     *
     * @param gameName A valid game name.
     */
    public GameName(String gameName) {
        requireNonNull(gameName);
        checkArgument(isValidGameName(gameName), MESSAGE_CONSTRAINTS);
        value = gameName.strip();
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

    private String getNormalizedName() {
        return value.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof GameName otherGameName)) {
            return false;
        }

        return getNormalizedName().equals(otherGameName.getNormalizedName());
    }

    @Override
    public int hashCode() {
        return getNormalizedName().hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
