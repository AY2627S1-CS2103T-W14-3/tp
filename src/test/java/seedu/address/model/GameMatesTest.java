package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.ALICE;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.exceptions.DuplicateFriendException;
import seedu.address.testutil.FriendBuilder;

public class GameMatesTest {

    private final GameMates gameMates = new GameMates();

    @Test
    public void constructor() {
        assertEquals(List.of(), gameMates.getFriendList());
    }

    @Test
    public void myself_isSeparateFromFriends() {
        gameMates.setMyself(ALICE);
        assertFalse(gameMates.hasFriend(ALICE));
        assertTrue(gameMates.getFriendList().isEmpty());
        gameMates.addFriend(ALICE);
        gameMates.removeFriend(ALICE);
        assertEquals(ALICE, gameMates.getMyself());
    }

    @Test
    public void myself_copyResetAndEquality_includeProfile() {
        gameMates.setMyself(ALICE);
        GameMates copy = new GameMates(gameMates);
        assertEquals(gameMates, copy);
        assertEquals(gameMates.hashCode(), copy.hashCode());
        copy.setMyself(new FriendBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build());
        assertFalse(gameMates.equals(copy));
        gameMates.resetData(copy);
        assertEquals(copy, gameMates);
    }

    @Test
    public void setMyself_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> gameMates.setMyself(null));
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> gameMates.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyGameMates_replacesData() {
        GameMates newData = getTypicalGameMates();
        gameMates.resetData(newData);
        assertEquals(newData, gameMates);
    }

    @Test
    public void resetData_withDuplicateFriends_throwsDuplicateFriendException() {
        // Two friends with the same identity fields
        Friend editedAlice = new FriendBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Friend> newFriends = List.of(ALICE, editedAlice);
        GameMatesStub newData = new GameMatesStub(newFriends);

        assertThrows(DuplicateFriendException.class, () -> gameMates.resetData(newData));
    }

    @Test
    public void hasFriend_nullFriend_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> gameMates.hasFriend(null));
    }

    @Test
    public void hasFriend_friendNotInGameMates_returnsFalse() {
        assertFalse(gameMates.hasFriend(ALICE));
    }

    @Test
    public void hasFriend_friendInGameMates_returnsTrue() {
        gameMates.addFriend(ALICE);
        assertTrue(gameMates.hasFriend(ALICE));
    }

    @Test
    public void hasFriend_friendWithSameIdentityFieldsInGameMates_returnsTrue() {
        gameMates.addFriend(ALICE);
        Friend editedAlice = new FriendBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(gameMates.hasFriend(editedAlice));
    }

    @Test
    public void getFriendList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> gameMates.getFriendList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = GameMates.class.getCanonicalName() + "{friends=" + gameMates.getFriendList()
                + ", myself=" + gameMates.getMyself() + "}";
        assertEquals(expected, gameMates.toString());
    }

    /**
     * A stub ReadOnlyGameMates whose friends list can violate interface constraints.
     */
    private static class GameMatesStub implements ReadOnlyGameMates {
        private final ObservableList<Friend> friends = FXCollections.observableArrayList();

        GameMatesStub(Collection<Friend> friends) {
            this.friends.setAll(friends);
        }

        @Override
        public Friend getMyself() {
            return new GameMates().getMyself();
        }

        @Override
        public ObservableList<Friend> getFriendList() {
            return friends;
        }
    }

}
