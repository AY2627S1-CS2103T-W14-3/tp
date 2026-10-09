package seedu.address.model.friend;

import static java.util.Objects.requireNonNull;

/**
 * Represents an optional remark about a friend. Empty remarks are allowed.
 * Guarantees: immutable and not null.
 */
public class Remark {

    public static final Remark EMPTY = new Remark("");

    public final String value;

    /**
     * Constructs a {@code Remark} without imposing restrictions on its contents.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        value = remark;
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

        return other instanceof Remark otherRemark && value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
