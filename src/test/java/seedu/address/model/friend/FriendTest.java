package seedu.address.model.friend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.ALICE;
import static seedu.address.testutil.TypicalFriends.BOB;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.FriendBuilder;

public class FriendTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Friend friend = new FriendBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> friend.getTags().remove(0));
    }

    @Test
    public void isSameFriend() {
        // same object -> returns true
        assertTrue(ALICE.isSameFriend(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameFriend(null));

        // same name, all other attributes different -> returns true
        Friend editedAlice = new FriendBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSameFriend(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new FriendBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSameFriend(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Friend editedBob = new FriendBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSameFriend(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new FriendBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSameFriend(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Friend aliceCopy = new FriendBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different friend -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Friend editedAlice = new FriendBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new FriendBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new FriendBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new FriendBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new FriendBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Friend.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
