package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.friend.Friend;

/**
 * Unmodifiable view of a GameMates
 */
public interface ReadOnlyGameMates {

    /** Returns the user profile, which is separate from the friend list. */
    Friend getMyself();

    /**
     * Returns an unmodifiable view of the friends list.
     * This list will not contain any duplicate friends.
     */
    ObservableList<Friend> getFriendList();

}
