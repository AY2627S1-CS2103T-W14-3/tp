package seedu.address.model.friend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.ALICE;
import static seedu.address.testutil.TypicalFriends.BOB;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.testutil.FriendBuilder;

public class FriendTest {

    @Test
    public void games_immutableAndDefensivelyCopied() {
        Game game = new Game(new GameName("Valorant"), new Username("alice"));
        Set<Game> games = new HashSet<>(Set.of(game));
        Friend friend = new Friend(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                games, ALICE.getTags());
        games.clear();
        assertEquals(Set.of(game), friend.getGames());
        assertThrows(UnsupportedOperationException.class, () -> friend.getGames().clear());
        assertEquals(friend, new FriendBuilder(friend).build());
    }

    @Test
    public void constructor_nullGamesOrEntries_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Friend(ALICE.getName(), ALICE.getPhone(),
                ALICE.getEmail(), null, ALICE.getTags()));
        Set<Game> games = new HashSet<>();
        games.add(null);
        assertThrows(NullPointerException.class, () -> new Friend(ALICE.getName(), ALICE.getPhone(),
                ALICE.getEmail(), games, ALICE.getTags()));
    }

    @Test
    public void equalsAndHashCode_includeGamesButIdentityDoesNot() {
        Game first = new Game(new GameName("Valorant"), new Username("alice"));
        Game second = new Game(new GameName("Minecraft"), new Username("alice2"));
        Friend friend = new FriendBuilder(ALICE).withGames(first, second).build();
        Friend reordered = new FriendBuilder(ALICE).withGames(second, first).build();
        assertEquals(friend, reordered);
        assertEquals(friend.hashCode(), reordered.hashCode());
        assertEquals(Objects.hash(friend.getName(), friend.getPhone(), friend.getEmail(),
                friend.getGames(), friend.getTags()), friend.hashCode());
        assertFalse(friend.equals(ALICE));
        assertTrue(friend.isSameFriend(ALICE));
        Friend changedUsername = new FriendBuilder(ALICE)
                .withGames(new Game(new GameName("Valorant"), new Username("other")), second).build();
        assertFalse(friend.equals(changedUsername));
    }

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
                .withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSameFriend(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new FriendBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSameFriend(editedAlice));

        // name differs in case, all other attributes same -> returns true
        Friend editedBob = new FriendBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertTrue(BOB.isSameFriend(editedBob));

        // name has trailing spaces, all other attributes same -> returns true
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new FriendBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertTrue(BOB.isSameFriend(editedBob));
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

        // different tags -> returns false
        editedAlice = new FriendBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        Friend friend = new FriendBuilder(ALICE)
                .withGames(new Game(new GameName("Valorant"), new Username("alice"))).build();
        String expected = Friend.class.getCanonicalName() + "{name=" + friend.getName() + ", phone=" + friend.getPhone()
                + ", email=" + friend.getEmail() + ", games=" + friend.getGames() + ", tags=" + friend.getTags() + "}";
        assertEquals(expected, friend.toString());
    }
}
