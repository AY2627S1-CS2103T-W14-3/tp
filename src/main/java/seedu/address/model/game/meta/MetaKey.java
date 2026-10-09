package seedu.address.model.game.meta;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

import seedu.address.commons.util.StringUtil;

/**
 * Represents an immutable, trimmed metadata key in a game entry.
 * Guarantees: valid as declared in {@link #isValidMetaKey(String)}.
 */
public final class MetaKey {

    public static final String MESSAGE_CONSTRAINTS =
            "Metadata keys must contain 1 to 50 printable characters and at least one letter or digit."
                    + " Metadata keys cannot contain a colon.";
    public static final int MAX_LENGTH = 50;

    public final String value;

    /**
     * Constructs a {@code MetaKey}, stripping leading and trailing whitespace.
     */
    public MetaKey(String key) {
        requireNonNull(key);
        checkArgument(isValidMetaKey(key), MESSAGE_CONSTRAINTS);
        value = key.strip();
    }

    /**
     * Returns whether the given string is a valid metadata key after trimming.
     */
    public static boolean isValidMetaKey(String test) {
        return StringUtil.isValidTextField(test, MAX_LENGTH)
                && !test.contains(":");
    }

    private String getNormalizedKey() {
        return value.toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MetaKey otherKey)) {
            return false;
        }
        return getNormalizedKey().equals(otherKey.getNormalizedKey());
    }

    @Override
    public int hashCode() {
        return getNormalizedKey().hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
