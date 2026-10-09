package seedu.address.model.game.meta;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Represents an immutable, trimmed metadata value in a game entry.
 * Guarantees: valid as declared in {@link #isValidMetaValue(String)}.
 */
public final class MetaValue {

    public static final String MESSAGE_CONSTRAINTS =
            "Metadata values must contain 1 to 200 printable characters and at least one letter or digit.";
    public static final int MAX_LENGTH = 200;

    public final String value;

    /**
     * Constructs a {@code MetaValue}, stripping leading and trailing whitespace.
     */
    public MetaValue(String value) {
        requireNonNull(value);
        checkArgument(isValidMetaValue(value), MESSAGE_CONSTRAINTS);
        this.value = value.strip();
    }

    /**
     * Returns whether the given string is a valid metadata value after trimming.
     */
    public static boolean isValidMetaValue(String test) {
        return StringUtil.isValidTextField(test, MAX_LENGTH);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MetaValue otherValue)) {
            return false;
        }
        return value.equals(otherValue.value);
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
