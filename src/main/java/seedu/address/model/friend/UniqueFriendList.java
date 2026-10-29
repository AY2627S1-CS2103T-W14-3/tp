package seedu.address.model.friend;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import io.vavr.control.Option;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.friend.exceptions.DuplicateFriendException;
import seedu.address.model.friend.exceptions.FriendNotFoundException;
import seedu.address.model.friend.exceptions.PinnedFriendLimitExceededException;

/**
 * A list of friends that enforces uniqueness between its elements and does not allow nulls.
 * A friend is considered unique by comparing using {@code Friend#isSameFriend(Friend)}. As such, adding and updating of
 * friends uses Friend#isSameFriend(Friend) for equality so as to ensure that the friend being added or updated is
 * unique in terms of identity in the UniqueFriendList. However, the removal of a friend uses Friend#equals(Object) so
 * as to ensure that the friend with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Friend#isSameFriend(Friend)
 */
public class UniqueFriendList implements Iterable<Friend> {

    public static final int MAX_PINNED_FRIENDS = 5;

    private final ObservableList<Friend> internalList = FXCollections.observableArrayList();
    private final ObservableList<Friend> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent friend as the given argument.
     */
    public boolean contains(Friend toCheck) {
        return findFriend(toCheck).isDefined();
    }

    /** Returns the stored friend with the same identity, or none when no match exists. */
    public Option<Friend> findFriend(Friend toCheck) {
        requireNonNull(toCheck);
        return Option.ofOptional(internalList.stream().filter(toCheck::isSameFriend).findFirst());
    }

    /**
     * Adds a friend to the list.
     * The friend must not already exist in the list.
     */
    public void add(Friend toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateFriendException();
        }
        verifyPinnedFriendLimitAfterAddition(toAdd);
        internalList.add(toAdd);
    }

    /**
     * Replaces the friend {@code target} in the list with {@code editedFriend}.
     * {@code target} must exist in the list.
     * The friend identity of {@code editedFriend} must not be the same as another existing friend in the list.
     */
    public void setFriend(Friend target, Friend editedFriend) {
        requireAllNonNull(target, editedFriend);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new FriendNotFoundException();
        }

        if (!target.isSameFriend(editedFriend) && contains(editedFriend)) {
            throw new DuplicateFriendException();
        }

        verifyPinnedFriendLimitAfterReplacement(target, editedFriend);

        internalList.set(index, editedFriend);
    }

    /**
     * Removes the equivalent friend from the list.
     * The friend must exist in the list.
     */
    public void remove(Friend toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new FriendNotFoundException();
        }
    }

    public void setFriends(UniqueFriendList replacement) {
        requireNonNull(replacement);
        verifyPinnedFriendLimit(replacement.internalList);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code friends}.
     * {@code friends} must not contain duplicate friends.
     */
    public void setFriends(List<Friend> friends) {
        requireAllNonNull(friends);
        if (!friendsAreUnique(friends)) {
            throw new DuplicateFriendException();
        }
        verifyPinnedFriendLimit(friends);

        internalList.setAll(friends);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Friend> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Friend> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueFriendList otherUniqueFriendList)) {
            return false;
        }

        return internalList.equals(otherUniqueFriendList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    /**
     * Returns true if {@code friends} contains only unique friends.
     */
    private boolean friendsAreUnique(List<Friend> friends) {
        for (int i = 0; i < friends.size() - 1; i++) {
            for (int j = i + 1; j < friends.size(); j++) {
                if (friends.get(i).isSameFriend(friends.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Throws if the given friend list exceeds the pinned friend limit. */
    private void verifyPinnedFriendLimit(List<Friend> friends) {
        long pinnedFriendCount = friends.stream().filter(Friend::isPinned).count();
        if (pinnedFriendCount > MAX_PINNED_FRIENDS) {
            throw new PinnedFriendLimitExceededException();
        }
    }

    /** Throws if adding {@code friend} to this list would exceed the pinned friend limit. */
    private void verifyPinnedFriendLimitAfterAddition(Friend friend) {
        long pinnedFriendCount = internalList.stream().filter(Friend::isPinned).count()
                + (friend.isPinned() ? 1 : 0);
        if (pinnedFriendCount > MAX_PINNED_FRIENDS) {
            throw new PinnedFriendLimitExceededException();
        }
    }

    /** Throws if replacing {@code target} with {@code replacement} would exceed the pinned friend limit. */
    private void verifyPinnedFriendLimitAfterReplacement(Friend target, Friend replacement) {
        long pinnedFriendCount = internalList.stream().filter(Friend::isPinned).count()
                - (target.isPinned() ? 1 : 0)
                + (replacement.isPinned() ? 1 : 0);
        if (pinnedFriendCount > MAX_PINNED_FRIENDS) {
            throw new PinnedFriendLimitExceededException();
        }
    }
}
