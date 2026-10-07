package seedu.address.model.game;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a player's username for a game in GameMates.
 * Guarantees: immutable; is valid as declared in {@link #isValidUsername(String)}
 */
public class Username {

    public static final String MESSAGE_CONSTRAINTS =
            "Usernames must contain 1 to 50 printable characters "
                    + "and include at least one letter or digit.";

    public static final int MAX_USERNAME_LENGTH = 50;

    public final String value;

    /**
     * Constructs a {@code Username}.
     *
     * @param username A valid username.
     */
    public Username(String username) {
        requireNonNull(username);
        checkArgument(isValidUsername(username), MESSAGE_CONSTRAINTS);
        value = username.strip();
    }

    /**
     * Returns true if a given string is a valid username.
     */
    public static boolean isValidUsername(String test) {
        if (test == null) {
            return false;
        }

        String trimmedUsername = test.strip();

        return !trimmedUsername.isEmpty()
                && trimmedUsername.length() <= MAX_USERNAME_LENGTH
                && trimmedUsername.codePoints().anyMatch(Character::isLetterOrDigit)
                && trimmedUsername.codePoints().noneMatch(Character::isISOControl);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Username otherUsername)) {
            return false;
        }

        return value.equals(otherUsername.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
