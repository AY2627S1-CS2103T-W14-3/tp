package seedu.address.model.game;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a player's username for a game in GameMates.
 * Guarantees: immutable; is empty or valid as declared in {@link #isValidUsername(String)}
 */
public class Username {

    public static final String MESSAGE_CONSTRAINTS =
            "Usernames must contain 1 to 50 printable characters "
                    + "and include at least one letter or digit.";

    public static final int MAX_USERNAME_LENGTH = 50;

    /** The empty value representing an omitted username. */
    public static final Username EMPTY = new Username();

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

    /** Constructs an empty username without accepting blank user input as valid. */
    private Username() {
        value = "";
    }

    /** Returns whether the username has been omitted. */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * Returns true if a given string is a valid username.
     */
    public static boolean isValidUsername(String test) {
        return StringUtil.isValidTextField(test, MAX_USERNAME_LENGTH);
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
