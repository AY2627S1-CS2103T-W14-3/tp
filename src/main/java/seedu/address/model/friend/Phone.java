package seedu.address.model.friend;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Friend's phone number in GameMates, preserving the entered formatting.
 * Guarantees: immutable; is empty or valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers must contain 3-15 ASCII digits, or '+' followed by 2-15 ASCII digits "
                    + "with the first digit not 0. Digit groups may be enclosed in balanced parentheses "
                    + "and separated by a single hyphen or space. "
                    + "Examples: 91234567, +65 9123-4567, (123) 456-7890. Extensions are not accepted.";
    public static final String VALIDATION_REGEX =
            "^(?:\\+(?=\\(?[1-9])(?=(?:[^0-9]*[0-9]){2,15}[^0-9]*$)"
                    + "|(?=(?:[^0-9]*[0-9]){3,15}[^0-9]*$))"
                    + "(?:[0-9]+|\\([0-9]+\\))(?:[- ]?(?:[0-9]+|\\([0-9]+\\)))*$";
    /** The empty value representing an omitted phone number. */
    public static final Phone EMPTY = new Phone();
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Constructs an empty phone number without accepting blank user input as valid.
     */
    private Phone() {
        value = "";
    }

    /**
     * Returns whether this phone number is empty, representing an omitted detail.
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * Returns true if a string contains 3-15 ASCII digits, or '+' followed by 2-15 ASCII digits
     * with a non-zero first digit. Digit groups may be enclosed in balanced, non-nested parentheses
     * and separated by a single hyphen or space. Formatting is preserved; extensions are rejected.
     * Checks format only, not country-code assignment, national number lengths or whether a number is in use.
     * The parser trims surrounding whitespace before validation; stored data must already match this format.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
