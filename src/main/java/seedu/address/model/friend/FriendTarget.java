package seedu.address.model.friend;

import static java.util.Objects.requireNonNull;

/**
 * A resolved command target that retains its friend and storage location independently of list filters.
 */
public sealed interface FriendTarget {
    /** Returns the friend captured when this target was resolved. */
    Friend friend();

    /** Returns whether this target selects the separately stored Myself profile. */
    boolean isMyself();

    /** A target selecting the separately stored Myself profile. */
    record Myself(Friend friend) implements FriendTarget {
        /** Creates a target capturing the current Myself profile. */
        public Myself {
            requireNonNull(friend);
        }

        @Override
        public boolean isMyself() {
            return true;
        }
    }

    /** A target selecting a specific friend from the friend list. */
    record ListedFriend(Friend friend) implements FriendTarget {
        /** Creates a target capturing the selected friend. */
        public ListedFriend {
            requireNonNull(friend);
        }

        @Override
        public boolean isMyself() {
            return false;
        }
    }
}
