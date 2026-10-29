package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.EditCommand.MESSAGE_EMPTY_LIST;
import static seedu.address.logic.commands.EditCommand.MESSAGE_INDEX_NOT_FOUND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_FRIEND;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_FRIEND;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.TypicalFriends;

/** Tests pinning a displayed friend. */
public class PinCommandTest {

    @Test
    public void execute_validIndex_pinsFriendAndMovesItToTop() {
        Model model = new ModelManager(TypicalFriends.getTypicalGameMates(), new UserPrefs());
        Friend friendToPin = model.getFilteredFriendList().get(INDEX_SECOND_FRIEND.getZeroBased());
        Model expectedModel = new ModelManager(model.getGameMates(), new UserPrefs());
        expectedModel.setFriend(friendToPin, PinCommand.copyWithPinnedState(friendToPin, true));

        assertCommandSuccess(new PinCommand(INDEX_SECOND_FRIEND), model,
                String.format(PinCommand.MESSAGE_SUCCESS, friendToPin.getName()), expectedModel);
        assertTrue(model.getFilteredFriendList().getFirst().isPinned());
        assertEquals(friendToPin.getName(), model.getFilteredFriendList().getFirst().getName());
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        Model emptyModel = new ModelManager(new GameMates(), new UserPrefs());
        assertCommandFailure(new PinCommand(INDEX_FIRST_FRIEND), emptyModel,
                MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager(TypicalFriends.getTypicalGameMates(), new UserPrefs());
        Index invalidIndex = Index.fromOneBased(model.getFilteredFriendList().size() + 1);
        assertCommandFailure(new PinCommand(invalidIndex), model, String.format(MESSAGE_INDEX_NOT_FOUND,
                invalidIndex.getOneBased(), model.getFilteredFriendList().size()));
    }

    @Test
    public void execute_alreadyPinned_throwsCommandException() {
        Friend pinnedFriend = new FriendBuilder(TypicalFriends.ALICE).withPinned(true).build();
        GameMates gameMates = new GameMates();
        gameMates.addFriend(pinnedFriend);
        Model model = new ModelManager(gameMates, new UserPrefs());

        assertCommandFailure(new PinCommand(INDEX_FIRST_FRIEND), model,
                String.format(PinCommand.MESSAGE_ALREADY_PINNED, pinnedFriend.getName()));
    }

    @Test
    public void execute_sixthPin_throwsCommandException() {
        GameMates gameMates = new GameMates();
        for (int i = 1; i <= PinCommand.MAX_PINNED_FRIENDS; i++) {
            gameMates.addFriend(new FriendBuilder().withName("Pinned Friend " + i).withPinned(true).build());
        }
        Friend unpinnedFriend = new FriendBuilder().withName("Unpinned Friend").build();
        gameMates.addFriend(unpinnedFriend);
        Model model = new ModelManager(gameMates, new UserPrefs());

        assertCommandFailure(new PinCommand(Index.fromOneBased(PinCommand.MAX_PINNED_FRIENDS + 1)), model,
                PinCommand.MESSAGE_MAX_PINNED_FRIENDS);
    }

    @Test
    public void execute_archivedFriendInArchiveView_throwsCommandException() {
        Friend archivedFriend = new FriendBuilder(TypicalFriends.ALICE).withArchived(true)
                .withPinned(false).build();
        GameMates gameMates = new GameMates();
        gameMates.addFriend(archivedFriend);
        Model model = new ModelManager(gameMates, new UserPrefs());
        model.updateArchivedFriendList(friend -> true);

        assertCommandFailure(new PinCommand(INDEX_FIRST_FRIEND), model,
                PinCommand.MESSAGE_ARCHIVED_FRIEND);
    }

    @Test
    public void execute_archivedFriendInSearchResults_throwsCommandException() {
        Friend archivedFriend = new FriendBuilder(TypicalFriends.ALICE).withArchived(true)
                .withPinned(false).build();
        GameMates gameMates = new GameMates();
        gameMates.addFriend(archivedFriend);
        Model model = new ModelManager(gameMates, new UserPrefs());
        model.updateSearchFriendList(friend -> true);

        assertCommandFailure(new PinCommand(INDEX_FIRST_FRIEND), model,
                PinCommand.MESSAGE_ARCHIVED_FRIEND);
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PinCommand(null));
    }

    @Test
    public void copyWithPinnedState_preservesDetails() {
        Friend pinnedFriend = PinCommand.copyWithPinnedState(TypicalFriends.ALICE, true);
        assertTrue(pinnedFriend.isPinned());
        assertTrue(pinnedFriend.isSameFriend(TypicalFriends.ALICE));
        assertEquals(TypicalFriends.ALICE.getGames(), pinnedFriend.getGames());
    }

    @Test
    public void equals() {
        PinCommand pinFirst = new PinCommand(INDEX_FIRST_FRIEND);
        assertTrue(pinFirst.equals(pinFirst));
        assertTrue(pinFirst.equals(new PinCommand(INDEX_FIRST_FRIEND)));
        assertFalse(pinFirst.equals(new PinCommand(INDEX_THIRD_FRIEND)));
        assertFalse(pinFirst.equals(null));
        assertFalse(pinFirst.equals(1));
    }

    @Test
    public void toStringMethod() {
        PinCommand pinCommand = new PinCommand(INDEX_FIRST_FRIEND);
        assertEquals(PinCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_FRIEND + "}",
                pinCommand.toString());
    }
}
