package seedu.address.model.friend.exceptions;

/** Signals that an operation would exceed the maximum number of pinned friends. */
public class PinnedFriendLimitExceededException extends RuntimeException {
    public PinnedFriendLimitExceededException() {
        super("Operation would exceed the maximum number of pinned friends");
    }
}
