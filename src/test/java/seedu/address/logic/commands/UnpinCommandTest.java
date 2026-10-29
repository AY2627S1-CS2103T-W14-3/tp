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

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.TypicalFriends;

/** Tests unpinning a displayed friend. */
public class UnpinCommandTest {

    @Test
    public void execute_validIndex_unpinsFriend() {
        Friend pinnedFriend = new FriendBuilder(TypicalFriends.ALICE).withPinned(true).build();
        Friend unpinnedFriend = TypicalFriends.BENSON;
        GameMates gameMates = new GameMates();
        gameMates.addFriend(pinnedFriend);
        gameMates.addFriend(unpinnedFriend);
        Model model = new ModelManager(gameMates, new UserPrefs());
        Model expectedModel = new ModelManager(model.getGameMates(), new UserPrefs());
        expectedModel.setFriend(pinnedFriend, PinCommand.copyWithPinnedState(pinnedFriend, false));

        assertCommandSuccess(new UnpinCommand(INDEX_FIRST_FRIEND), model,
                String.format(UnpinCommand.MESSAGE_SUCCESS, pinnedFriend.getName()), expectedModel);
        assertFalse(model.getGameMates().getFriendList().getFirst().isPinned());
    }

    @Test
    public void execute_notPinned_throwsCommandException() {
        Model model = new ModelManager(TypicalFriends.getTypicalGameMates(), new UserPrefs());
        Friend friend = model.getFilteredFriendList().getFirst();
        assertCommandFailure(new UnpinCommand(INDEX_FIRST_FRIEND), model,
                String.format(UnpinCommand.MESSAGE_NOT_PINNED, friend.getName()));
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        Model emptyModel = new ModelManager(new GameMates(), new UserPrefs());
        assertCommandFailure(new UnpinCommand(INDEX_FIRST_FRIEND), emptyModel,
                MESSAGE_EMPTY_LIST);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = new ModelManager(TypicalFriends.getTypicalGameMates(), new UserPrefs());
        Index invalidIndex = Index.fromOneBased(model.getFilteredFriendList().size() + 1);
        assertCommandFailure(new UnpinCommand(invalidIndex), model, String.format(MESSAGE_INDEX_NOT_FOUND,
                invalidIndex.getOneBased(), model.getFilteredFriendList().size()));
    }

    @Test
    public void execute_archivedFriendInArchiveView_throwsCommandException() {
        Friend archivedFriend = new FriendBuilder(TypicalFriends.ALICE).withArchived(true)
                .withPinned(false).build();
        GameMates gameMates = new GameMates();
        gameMates.addFriend(archivedFriend);
        Model model = new ModelManager(gameMates, new UserPrefs());
        model.updateArchivedFriendList(friend -> true);

        assertCommandFailure(new UnpinCommand(INDEX_FIRST_FRIEND), model,
                UnpinCommand.MESSAGE_ARCHIVED_FRIEND);
    }

    @Test
    public void execute_archivedFriendInSearchResults_throwsCommandException() {
        Friend archivedFriend = new FriendBuilder(TypicalFriends.ALICE).withArchived(true)
                .withPinned(false).build();
        GameMates gameMates = new GameMates();
        gameMates.addFriend(archivedFriend);
        Model model = new ModelManager(gameMates, new UserPrefs());
        model.updateSearchFriendList(friend -> true);

        assertCommandFailure(new UnpinCommand(INDEX_FIRST_FRIEND), model,
                UnpinCommand.MESSAGE_ARCHIVED_FRIEND);
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UnpinCommand(null));
    }

    @Test
    public void equals() {
        UnpinCommand unpinFirst = new UnpinCommand(INDEX_FIRST_FRIEND);
        assertTrue(unpinFirst.equals(unpinFirst));
        assertTrue(unpinFirst.equals(new UnpinCommand(INDEX_FIRST_FRIEND)));
        assertFalse(unpinFirst.equals(new UnpinCommand(INDEX_SECOND_FRIEND)));
        assertFalse(unpinFirst.equals(null));
        assertFalse(unpinFirst.equals(1));
    }

    @Test
    public void toStringMethod() {
        UnpinCommand unpinCommand = new UnpinCommand(INDEX_FIRST_FRIEND);
        assertEquals(UnpinCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_FRIEND + "}",
                unpinCommand.toString());
    }
}
