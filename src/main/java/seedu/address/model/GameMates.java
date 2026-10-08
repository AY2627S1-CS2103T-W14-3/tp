package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.UniqueFriendList;

/**
 * Wraps all data at the GameMates level.
 * Friend-list duplicates are not allowed (by .isSameFriend comparison).
 * The user profile is stored separately and does not participate in friend identity checks.
 */
public class GameMates implements ReadOnlyGameMates {

    private final UniqueFriendList friends = new UniqueFriendList();
    private Friend myself = Friend.DEFAULT_MYSELF;

    public GameMates() {}

    /**
     * Creates a GameMates using the Friends in the {@code toBeCopied}
     */
    public GameMates(ReadOnlyGameMates toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    @Override
    public Friend getMyself() {
        return myself;
    }

    /** Replaces the user profile independently of the friend list. */
    public void setMyself(Friend myself) {
        this.myself = requireNonNull(myself);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the friend list with {@code friends}.
     * {@code friends} must not contain duplicate friends.
     */
    public void setFriends(List<Friend> friends) {
        this.friends.setFriends(friends);
    }

    /**
     * Resets the existing data of this {@code GameMates} with {@code newData}.
     */
    public void resetData(ReadOnlyGameMates newData) {
        requireNonNull(newData);

        setFriends(newData.getFriendList());
        setMyself(newData.getMyself());
    }

    //// friend-level operations

    /**
     * Returns true if a friend with the same identity as {@code friend} exists in GameMates.
     */
    public boolean hasFriend(Friend friend) {
        requireNonNull(friend);
        return friends.contains(friend);
    }

    /**
     * Adds a friend to GameMates.
     * The friend must not already exist in GameMates.
     */
    public void addFriend(Friend p) {
        friends.add(p);
    }

    /**
     * Replaces the given friend {@code target} in the list with {@code editedFriend}.
     * {@code target} must exist in GameMates.
     * The friend identity of {@code editedFriend} must not be the same as another existing friend
     * in GameMates.
     */
    public void setFriend(Friend target, Friend editedFriend) {
        requireNonNull(editedFriend);

        friends.setFriend(target, editedFriend);
    }

    /**
     * Removes {@code key} from this {@code GameMates}.
     * {@code key} must exist in GameMates.
     */
    public void removeFriend(Friend key) {
        friends.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("friends", friends)
                .add("myself", myself)
                .toString();
    }

    @Override
    public ObservableList<Friend> getFriendList() {
        return friends.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GameMates otherGameMates)) {
            return false;
        }

        return friends.equals(otherGameMates.friends) && myself.equals(otherGameMates.myself);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friends, myself);
    }
}
