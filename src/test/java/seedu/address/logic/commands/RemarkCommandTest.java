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
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Remark;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

public class RemarkCommandTest {

    private Model model = new ModelManager(getTypicalGameMates(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemarkCommand(null, Remark.EMPTY));
        assertThrows(NullPointerException.class, () -> new RemarkCommand(INDEX_FIRST_FRIEND, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new RemarkCommand(INDEX_FIRST_FRIEND, Remark.EMPTY).execute(null);
        });
    }

    @Test
    public void execute_addRemark_preservesOtherDetails() {
        Friend original = model.getFilteredFriendList().get(0);
        Friend withGames = new FriendBuilder(original)
                .withGames(new GameBuilder().withGameName("Valorant").withUsername("player").build()).build();
        model.setFriend(original, withGames);
        assertRemarkChange(INDEX_FIRST_FRIEND, "Likes to swim.", RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS);
    }

    @Test
    public void execute_existingRemark_overwritesRemark() {
        Friend original = model.getFilteredFriendList().get(0);
        model.setFriend(original, new FriendBuilder(original).withRemark("Old remark").build());
        assertRemarkChange(INDEX_FIRST_FRIEND, "New remark", RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS);
    }

    @Test
    public void execute_emptyRemark_clearsRemark() {
        Friend original = model.getFilteredFriendList().get(0);
        model.setFriend(original, new FriendBuilder(original).withRemark("Old remark").build());
        assertRemarkChange(INDEX_FIRST_FRIEND, "", RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS);
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndShowsAllFriendsAfterwards() {
        Friend original = model.getFilteredFriendList().get(INDEX_SECOND_FRIEND.getZeroBased());
        showFriendAtIndex(model, INDEX_SECOND_FRIEND);
        assertRemarkChange(INDEX_FIRST_FRIEND, "Filtered friend", RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS);
        assertEquals(new Remark("Filtered friend"),
                model.getGameMates().getFriendList().get(INDEX_SECOND_FRIEND.getZeroBased()).getRemark());
        assertEquals(original.getName(), model.getGameMates().getFriendList()
                .get(INDEX_SECOND_FRIEND.getZeroBased()).getName());
        assertEquals(model.getGameMates().getFriendList(), model.getFilteredFriendList());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredFriendList().size() + 1);
        assertCommandFailure(new RemarkCommand(invalidIndex, new Remark("text")), model,
                Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFriendAtIndex(model, INDEX_SECOND_FRIEND);
        assertCommandFailure(new RemarkCommand(INDEX_SECOND_FRIEND, new Remark("text")), model,
                Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_FRIEND, new Remark("text"));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new RemarkCommand(INDEX_FIRST_FRIEND, new Remark("text"))));
        assertFalse(command.equals(new RemarkCommand(INDEX_SECOND_FRIEND, new Remark("text"))));
        assertFalse(command.equals(new RemarkCommand(INDEX_FIRST_FRIEND, Remark.EMPTY)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("text"));
    }

    private void assertRemarkChange(Index index, String remark, String message) {
        Friend original = model.getFilteredFriendList().get(index.getZeroBased());
        Friend editedFriend = new FriendBuilder(original).withRemark(remark).build();
        Model expectedModel = new ModelManager(model.getGameMates(), new UserPrefs());
        expectedModel.setFriend(original, editedFriend);
        assertCommandSuccess(new RemarkCommand(index, new Remark(remark)), model,
                String.format(message, Messages.format(editedFriend)), expectedModel);
    }
}
