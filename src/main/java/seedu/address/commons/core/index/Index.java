package seedu.address.commons.core.index;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a zero-based or one-based friend index, or the separately stored Myself profile.
 *
 * {@code Index} should be used right from the start (when parsing in a new user input), so that if the current
 * component wants to communicate with another component, it can send an {@code Index} to avoid having to know what
 * base the other component is using for its index. However, after receiving the {@code Index}, that component can
 * convert it back to an int if the index will not be passed to a different component again.
 */
public class Index {
    public static final String MYSELF_KEYWORD = "myself";

    private final int zeroBasedIndex;

    private Index() {
        zeroBasedIndex = -1;
    }

    /**
     * Index can only be created by calling {@link Index#fromZeroBased(int)} or
     * {@link Index#fromOneBased(int)}. Use {@link Index#myself()} to select the Myself profile.
     */
    private Index(int zeroBasedIndex) {
        if (zeroBasedIndex < 0) {
            throw new IndexOutOfBoundsException();
        }

        this.zeroBasedIndex = zeroBasedIndex;
    }

    public int getZeroBased() {
        if (isMyself()) {
            throw new IllegalStateException("Myself has no friend-list index.");
        }
        return zeroBasedIndex;
    }

    public int getOneBased() {
        return getZeroBased() + 1;
    }

    /** Creates an index selecting the separately stored Myself profile. */
    public static Index myself() {
        return new Index();
    }

    /** Returns whether this index selects the Myself profile rather than a friend. */
    public boolean isMyself() {
        return zeroBasedIndex == -1;
    }

    /**
     * Creates a new {@code Index} using a zero-based index.
     */
    public static Index fromZeroBased(int zeroBasedIndex) {
        return new Index(zeroBasedIndex);
    }

    /**
     * Creates a new {@code Index} using a one-based index.
     */
    public static Index fromOneBased(int oneBasedIndex) {
        return new Index(oneBasedIndex - 1);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Index otherIndex)) {
            return false;
        }

        return zeroBasedIndex == otherIndex.zeroBasedIndex;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(zeroBasedIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("zeroBasedIndex", zeroBasedIndex).toString();
    }
}
