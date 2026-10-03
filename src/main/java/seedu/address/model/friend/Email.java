package seedu.address.model.friend;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.regex.Pattern;

/**
 * Represents a Friend's email in GameMates.
 * Guarantees: immutable; is valid as declared in {@link #isValidEmail(String)}
 */
public class Email {

    public static final String MESSAGE_CONSTRAINTS =
            "Enter an email like name@example.com (up to 254 characters, no spaces). "
                    + "See the User Guide for the full format.";

    // RFC 5322 dot-atom syntax, with RFC 5321 domain labels and SMTP length limits.
    // This practical ASCII subset excludes quoted local parts and address literals.
    private static final String ATOM_REGEX = "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+";
    private static final String LOCAL_PART_REGEX = ATOM_REGEX + "(?:\\." + ATOM_REGEX + ")*";
    private static final String DOMAIN_LABEL_REGEX = "[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?";
    public static final String VALIDATION_REGEX = LOCAL_PART_REGEX + "@" + DOMAIN_LABEL_REGEX
            + "(?:\\." + DOMAIN_LABEL_REGEX + ")*";
    private static final Pattern VALIDATION_PATTERN = Pattern.compile(VALIDATION_REGEX);

    public final String value;

    /**
     * Constructs an {@code Email}.
     *
     * @param email A valid email address.
     */
    public Email(String email) {
        requireNonNull(email);
        checkArgument(isValidEmail(email), MESSAGE_CONSTRAINTS);
        value = email;
    }

    /**
     * Returns true if the string is an ASCII dot-atom email with valid domain labels and SMTP length limits.
     * Checks syntax only; the domain and mailbox are not verified. The parser trims surrounding whitespace.
     */
    public static boolean isValidEmail(String test) {
        requireNonNull(test);
        int localPartLength = test.indexOf('@');
        // SMTP paths are limited to 256 octets, including the surrounding '<' and '>'.
        return test.length() <= 254 && localPartLength >= 1 && localPartLength <= 64
                && VALIDATION_PATTERN.matcher(test).matches();
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
        if (!(other instanceof Email otherEmail)) {
            return false;
        }

        return value.equals(otherEmail.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
