package seedu.address.model.game.meta;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

/**
 * Represents an immutable key-value pair describing a friend's game entry.
 * Key identity is case-insensitive; values are case-sensitive.
 */
public final class Meta {

    public static final String MESSAGE_CONSTRAINTS = "Metadata must be in the format Key:Value.";
    public static final String MESSAGE_DUPLICATE_KEY = "Metadata keys must be unique within a game entry.";

    private final MetaKey key;
    private final MetaValue value;

    /**
     * Constructs a metadata pair with a validated key and value.
     */
    public Meta(MetaKey key, MetaValue value) {
        requireAllNonNull(key, value);
        this.key = key;
        this.value = value;
    }

    public MetaKey getKey() {
        return key;
    }

    public MetaValue getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Meta otherMeta)) {
            return false;
        }
        return key.equals(otherMeta.key) && value.equals(otherMeta.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return key + ":" + value;
    }
}
