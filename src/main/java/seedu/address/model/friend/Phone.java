package seedu.address.model.friend;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Friend's phone number in GameMates, with or without an international prefix such as +65.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS =
            "Enter a phone number like 91234567 (3-15 digits) or +6591234567 ('+', then a country code "
                    + "not starting with 0, 2-15 digits in total).";
    public static final String VALIDATION_REGEX = "(\\+[1-9][0-9]{1,14})|([0-9]{3,15})";
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
     * Returns true if a string is either a plain number of 3-15 digits (e.g. {@code 91234567}) or an E.164-style
     * international number (e.g. {@code +6591234567}).
     * Checks format only, not country-code assignment, national number lengths or whether the number is in use.
     * The parser trims surrounding whitespace before validation.
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
