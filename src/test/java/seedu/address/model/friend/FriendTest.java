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
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.game.Game;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

public class FriendTest {

    @Test
    public void constructor_mutatingInputSets_doesNotChangeFriendOrDerivedCopies() {
        Game game = new GameBuilder().withGameName("Valorant").build();
        Tag tag = new Tag("duo");
        Set<Game> games = new HashSet<>(Set.of(game));
        Set<Tag> tags = new HashSet<>(Set.of(tag));
        Friend friend = new Friend(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), games, tags);
        List<Friend> copies = List.of(friend, friend.withArchived(true), friend.withPinned(true),
                friend.withGames(Set.of(game)));
        int originalHash = friend.hashCode();

        games.clear();
        tags.clear();

        for (Friend copy : copies) {
            assertEquals(Set.of(game), copy.getGames());
            assertEquals(Set.of(tag), copy.getTags());
            assertThrows(UnsupportedOperationException.class, () -> copy.getGames().clear());
            assertThrows(UnsupportedOperationException.class, () -> copy.getTags().clear());
        }
        assertEquals(originalHash, friend.hashCode());
    }

    @Test
    public void withGames_mutatingReplacementSet_doesNotChangeFriend() {
        Game game = new GameBuilder().build();
        Set<Game> games = new HashSet<>(Set.of(game));
        Friend updated = ALICE.withGames(games);
        games.clear();
        assertEquals(Set.of(game), updated.getGames());
    }

    @Test
    public void constructor_nullTagEntry_throwsNullPointerException() {
        Set<Tag> tags = new HashSet<>();
        tags.add(null);
        assertThrows(NullPointerException.class, () -> new Friend(ALICE.getName(), ALICE.getPhone(),
                ALICE.getEmail(), tags));
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
        Game first = new GameBuilder().withGameName("Valorant").withUsername("alice").build();
        Game second = new GameBuilder().withGameName("Minecraft").withUsername("alice2").build();
        Friend friend = new FriendBuilder(ALICE).withGames(first, second).build();
        Friend reordered = new FriendBuilder(ALICE).withGames(second, first).build();
        assertEquals(friend, reordered);
        assertEquals(friend.hashCode(), reordered.hashCode());
        assertEquals(Objects.hash(friend.getName(), friend.getPhone(), friend.getEmail(),
                friend.getGames(), friend.getTags(), friend.isArchived(), friend.isPinned()), friend.hashCode());
        assertFalse(friend.equals(ALICE));
        assertTrue(friend.isSameFriend(ALICE));
        Friend changedUsername = new FriendBuilder(ALICE)
                .withGames(new GameBuilder().withGameName("Valorant").withUsername("other").build(), second).build();
        assertFalse(friend.equals(changedUsername));
    }

    @Test
    public void withGames_preservesContactFieldsAndEveryPinAndArchiveStatus() {
        Game originalGame = new GameBuilder().withGameName("Valorant").addMeta("Role", "Healer").build();
        Game replacementGame = new GameBuilder(originalGame).withUsername("newPlayer").build();
        for (boolean archived : new boolean[]{false, true}) {
            for (boolean pinned : new boolean[]{false, true}) {
                Friend original = new FriendBuilder(ALICE).withGames(originalGame)
                        .withArchived(archived).withPinned(pinned).build();
                Friend updated = original.withGames(Set.of(replacementGame));
                assertEquals(new FriendBuilder(original).withGames(replacementGame).build(), updated);
                assertEquals(Set.of(originalGame), original.getGames());
            }
        }
    }

    @Test
    public void pinnedFriend_hasDistinctEqualityButSameIdentity() {
        Friend pinnedAlice = new FriendBuilder(ALICE).withPinned(true).build();
        assertTrue(pinnedAlice.isPinned());
        assertFalse(pinnedAlice.equals(ALICE));
        assertTrue(pinnedAlice.isSameFriend(ALICE));
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
    public void archiveStatus_changesValueEqualityButPreservesIdentityAndDetails() {
        Friend archived = ALICE.withArchived(true);
        assertTrue(archived.isArchived());
        assertFalse(ALICE.equals(archived));
        assertTrue(ALICE.isSameFriend(archived));
        assertEquals(ALICE, archived.withArchived(false));
        assertEquals(archived, archived.withArchived(true));
        assertEquals(archived.hashCode(), ALICE.withArchived(true).hashCode());
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
                .withGames(new GameBuilder().withGameName("Valorant").withUsername("alice").build()).build();
        String expected = Friend.class.getCanonicalName() + "{name=" + friend.getName() + ", phone=" + friend.getPhone()
                + ", email=" + friend.getEmail() + ", games=" + friend.getGames() + ", tags=" + friend.getTags()
                + ", archived=false, pinned=false}";
        assertEquals(expected, friend.toString());
    }
}
