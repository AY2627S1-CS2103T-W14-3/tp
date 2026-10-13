package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFriendAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_FRIEND;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalGameMates(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Friend friendToDelete = model.getFilteredFriendList().get(INDEX_FIRST_FRIEND.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_FRIEND);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_FRIEND_SUCCESS,
                friendToDelete.getName());

        ModelManager expectedModel = new ModelManager(model.getGameMates(), new UserPrefs());
        expectedModel.deleteFriend(friendToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredFriendList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showFriendAtIndex(model, INDEX_FIRST_FRIEND);

        Friend friendToDelete = model.getFilteredFriendList().get(INDEX_FIRST_FRIEND.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_FRIEND);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_FRIEND_SUCCESS,
                friendToDelete.getName());

        Model expectedModel = new ModelManager(model.getGameMates(), new UserPrefs());
        expectedModel.deleteFriend(friendToDelete);
        showNoFriend(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_firstFilteredIndex_deletesDisplayedFriendInsteadOfFirstStoredFriend() {
        Friend friendToDelete = model.getFilteredFriendList().get(INDEX_SECOND_FRIEND.getZeroBased());
        showFriendAtIndex(model, INDEX_SECOND_FRIEND);
        Model expectedModel = new ModelManager(model.getGameMates(), new UserPrefs());
        expectedModel.deleteFriend(friendToDelete);
        showNoFriend(expectedModel);
        assertCommandSuccess(new DeleteCommand(INDEX_FIRST_FRIEND), model,
                "Successfully deleted " + friendToDelete.getName() + ".", expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFriendAtIndex(model, INDEX_FIRST_FRIEND);

        Index outOfBoundIndex = INDEX_SECOND_FRIEND;
        // ensures that outOfBoundIndex is still in bounds of GameMates list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getGameMates().getFriendList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteCommand(null));
    }

    @Test
    public void execute_emptyList_throwsCommandException() {
        Model emptyModel = new ModelManager(new GameMates(), new UserPrefs());
        assertCommandFailure(new DeleteCommand(INDEX_FIRST_FRIEND), emptyModel,
                Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyFilteredList_doesNotDeleteHiddenFriends() {
        showNoFriend(model);
        assertCommandFailure(new DeleteCommand(INDEX_FIRST_FRIEND), model,
                Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    @Test
    public void execute_lastDisplayedIndex_success() {
        Index lastIndex = Index.fromOneBased(model.getFilteredFriendList().size());
        Friend friendToDelete = model.getFilteredFriendList().get(lastIndex.getZeroBased());
        Model expectedModel = new ModelManager(model.getGameMates(), new UserPrefs());
        expectedModel.deleteFriend(friendToDelete);
        assertCommandSuccess(new DeleteCommand(lastIndex), model,
                "Successfully deleted " + friendToDelete.getName() + ".", expectedModel);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_FRIEND);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_FRIEND);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_FRIEND);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different friend -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoFriend(Model model) {
        model.updateFilteredFriendList(p -> false);

        assertTrue(model.getFilteredFriendList().isEmpty());
    }
}
