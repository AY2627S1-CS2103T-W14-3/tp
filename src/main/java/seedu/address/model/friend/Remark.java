package seedu.address.model.friend;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Friend's remark in GameMates.
 * Guarantees: immutable
 */
public class Remark {

    public final String remark;

    /**
     * Constructs a {@code Remark}.
     *
     * @param remark A remark.
     */
    public Remark(String remark) {
        requireNonNull(remark);
        this.remark = remark;
    }

    @Override
    public String toString() {
        return remark;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Remark otherName)) {
            return false;
        }

        return remark.equals(otherName.remark);
    }

    @Override
    public int hashCode() {
        return remark.hashCode();
    }

}
