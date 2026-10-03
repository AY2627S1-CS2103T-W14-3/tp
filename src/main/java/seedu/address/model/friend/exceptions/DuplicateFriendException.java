package seedu.address.model.friend.exceptions;

/**
 * Signals that the operation will result in duplicate Friends (Friends are considered duplicates if they have the same
 * identity).
 */
public class DuplicateFriendException extends RuntimeException {
    public DuplicateFriendException() {
        super("Operation would result in duplicate friends");
    }
}
