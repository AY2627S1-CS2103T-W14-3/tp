package seedu.address.model.friend;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Friend's name in GameMates.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 100;
    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain alphanumeric characters and spaces, and should not be blank. "
                    + "Maximum length is " + MAX_LENGTH + " characters.";

    /*
     * The first character of the name must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

    public final String fullName;

    /**
     * Constructs a {@code Name}, trimming leading and trailing whitespace.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name.trim();
    }

    /**
     * Returns true if a given string is a valid name, i.e. alphanumeric characters and spaces only, and at most
     * {@value #MAX_LENGTH} characters long. Leading and trailing whitespace is ignored.
     */
    public static boolean isValidName(String test) {
        String trimmedName = test.trim();
        return trimmedName.length() <= MAX_LENGTH && trimmedName.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.toLowerCase(Locale.ROOT).equals(otherName.fullName.toLowerCase(Locale.ROOT));
    }

    @Override
    public int hashCode() {
        return fullName.toLowerCase(Locale.ROOT).hashCode();
    }

}
