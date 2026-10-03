package seedu.address.model.friend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.ALICE;
import static seedu.address.testutil.TypicalFriends.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.friend.exceptions.DuplicateFriendException;
import seedu.address.model.friend.exceptions.FriendNotFoundException;
import seedu.address.testutil.FriendBuilder;

public class UniqueFriendListTest {

    private final UniqueFriendList uniqueFriendList = new UniqueFriendList();

    @Test
    public void contains_nullFriend_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueFriendList.contains(null));
    }

    @Test
    public void contains_friendNotInList_returnsFalse() {
        assertFalse(uniqueFriendList.contains(ALICE));
    }

    @Test
    public void contains_friendInList_returnsTrue() {
        uniqueFriendList.add(ALICE);
        assertTrue(uniqueFriendList.contains(ALICE));
    }

    @Test
    public void contains_friendWithSameIdentityFieldsInList_returnsTrue() {
        uniqueFriendList.add(ALICE);
        Friend editedAlice = new FriendBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(uniqueFriendList.contains(editedAlice));
    }

    @Test
    public void add_nullFriend_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueFriendList.add(null));
    }

    @Test
    public void add_duplicateFriend_throwsDuplicateFriendException() {
        uniqueFriendList.add(ALICE);
        assertThrows(DuplicateFriendException.class, () -> uniqueFriendList.add(ALICE));
    }

    @Test
    public void setFriend_nullTargetFriend_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueFriendList.setFriend(null, ALICE));
    }

    @Test
    public void setFriend_nullEditedFriend_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueFriendList.setFriend(ALICE, null));
    }

    @Test
    public void setFriend_targetFriendNotInList_throwsFriendNotFoundException() {
        assertThrows(FriendNotFoundException.class, () -> uniqueFriendList.setFriend(ALICE, ALICE));
    }

    @Test
    public void setFriend_editedFriendIsSameFriend_success() {
        uniqueFriendList.add(ALICE);
        uniqueFriendList.setFriend(ALICE, ALICE);
        UniqueFriendList expectedUniqueFriendList = new UniqueFriendList();
        expectedUniqueFriendList.add(ALICE);
        assertEquals(expectedUniqueFriendList, uniqueFriendList);
    }

    @Test
    public void setFriend_editedFriendHasSameIdentity_success() {
        uniqueFriendList.add(ALICE);
        Friend editedAlice = new FriendBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        uniqueFriendList.setFriend(ALICE, editedAlice);
        UniqueFriendList expectedUniqueFriendList = new UniqueFriendList();
        expectedUniqueFriendList.add(editedAlice);
        assertEquals(expectedUniqueFriendList, uniqueFriendList);
    }

    @Test
    public void setFriend_editedFriendHasDifferentIdentity_success() {
        uniqueFriendList.add(ALICE);
        uniqueFriendList.setFriend(ALICE, BOB);
        UniqueFriendList expectedUniqueFriendList = new UniqueFriendList();
        expectedUniqueFriendList.add(BOB);
        assertEquals(expectedUniqueFriendList, uniqueFriendList);
    }

    @Test
    public void setFriend_editedFriendHasNonUniqueIdentity_throwsDuplicateFriendException() {
        uniqueFriendList.add(ALICE);
        uniqueFriendList.add(BOB);
        assertThrows(DuplicateFriendException.class, () -> uniqueFriendList.setFriend(ALICE, BOB));
    }

    @Test
    public void remove_nullFriend_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueFriendList.remove(null));
    }

    @Test
    public void remove_friendDoesNotExist_throwsFriendNotFoundException() {
        assertThrows(FriendNotFoundException.class, () -> uniqueFriendList.remove(ALICE));
    }

    @Test
    public void remove_existingFriend_removesFriend() {
        uniqueFriendList.add(ALICE);
        uniqueFriendList.remove(ALICE);
        UniqueFriendList expectedUniqueFriendList = new UniqueFriendList();
        assertEquals(expectedUniqueFriendList, uniqueFriendList);
    }

    @Test
    public void setFriends_nullUniqueFriendList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueFriendList.setFriends((UniqueFriendList) null));
    }

    @Test
    public void setFriends_uniqueFriendList_replacesOwnListWithProvidedUniqueFriendList() {
        uniqueFriendList.add(ALICE);
        UniqueFriendList expectedUniqueFriendList = new UniqueFriendList();
        expectedUniqueFriendList.add(BOB);
        uniqueFriendList.setFriends(expectedUniqueFriendList);
        assertEquals(expectedUniqueFriendList, uniqueFriendList);
    }

    @Test
    public void setFriends_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueFriendList.setFriends((List<Friend>) null));
    }

    @Test
    public void setFriends_list_replacesOwnListWithProvidedList() {
        uniqueFriendList.add(ALICE);
        List<Friend> friendList = List.of(BOB);
        uniqueFriendList.setFriends(friendList);
        UniqueFriendList expectedUniqueFriendList = new UniqueFriendList();
        expectedUniqueFriendList.add(BOB);
        assertEquals(expectedUniqueFriendList, uniqueFriendList);
    }

    @Test
    public void setFriends_listWithDuplicateFriends_throwsDuplicateFriendException() {
        List<Friend> listWithDuplicateFriends = List.of(ALICE, ALICE);
        assertThrows(DuplicateFriendException.class, () -> uniqueFriendList.setFriends(listWithDuplicateFriends));
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
            -> uniqueFriendList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void toStringMethod() {
        assertEquals(uniqueFriendList.asUnmodifiableObservableList().toString(), uniqueFriendList.toString());
    }
}
