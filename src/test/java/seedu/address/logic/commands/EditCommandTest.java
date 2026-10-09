package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFriendAtIndex;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_FRIEND;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditFriendDescriptor;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.EditFriendDescriptorBuilder;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalGameMates(), new UserPrefs());

    @Test
    public void execute_contactEdit_preservesGamesAndMetadata() {
        Friend original = model.getFilteredFriendList().get(0);
        Friend withGames = new FriendBuilder(original)
                .withGames(new GameBuilder().withGameName("Valorant").withUsername("player")
                        .addMeta("Role", "Support").addMeta("Availability", "Weekends:20:00").build()).build();
        model.setFriend(original, withGames);
        Friend editedFriend = new FriendBuilder(withGames).withPhone(VALID_PHONE_BOB).build();
        EditCommand command = new EditCommand(INDEX_FIRST_FRIEND,
                new EditFriendDescriptorBuilder().withPhone(VALID_PHONE_BOB).build());
        Model expectedModel = new ModelManager(new GameMates(model.getGameMates()), new UserPrefs());
        expectedModel.setFriend(withGames, editedFriend);
        assertCommandSuccess(command, model,
                String.format(EditCommand.MESSAGE_EDIT_FRIEND_SUCCESS, Messages.format(editedFriend)), expectedModel);
    }

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Friend editedFriend = new FriendBuilder().build();
        EditFriendDescriptor descriptor = new EditFriendDescriptorBuilder(editedFriend).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_FRIEND, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_FRIEND_SUCCESS, Messages.format(editedFriend));

        Model expectedModel = new ModelManager(new GameMates(model.getGameMates()), new UserPrefs());
        expectedModel.setFriend(model.getFilteredFriendList().get(0), editedFriend);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastFriend = Index.fromOneBased(model.getFilteredFriendList().size());
        Friend lastFriend = model.getFilteredFriendList().get(indexLastFriend.getZeroBased());

        FriendBuilder friendInList = new FriendBuilder(lastFriend);
        Friend editedFriend = friendInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditFriendDescriptor descriptor = new EditFriendDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastFriend, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_FRIEND_SUCCESS, Messages.format(editedFriend));

        Model expectedModel = new ModelManager(new GameMates(model.getGameMates()), new UserPrefs());
        expectedModel.setFriend(lastFriend, editedFriend);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_FRIEND, new EditFriendDescriptor());
        Friend editedFriend = model.getFilteredFriendList().get(INDEX_FIRST_FRIEND.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_FRIEND_SUCCESS, Messages.format(editedFriend));

        Model expectedModel = new ModelManager(new GameMates(model.getGameMates()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showFriendAtIndex(model, INDEX_FIRST_FRIEND);

        Friend friendInFilteredList = model.getFilteredFriendList().get(INDEX_FIRST_FRIEND.getZeroBased());
        Friend editedFriend = new FriendBuilder(friendInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_FRIEND,
                new EditFriendDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_FRIEND_SUCCESS, Messages.format(editedFriend));

        Model expectedModel = new ModelManager(new GameMates(model.getGameMates()), new UserPrefs());
        expectedModel.setFriend(model.getFilteredFriendList().get(0), editedFriend);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateFriendUnfilteredList_failure() {
        Friend firstFriend = model.getFilteredFriendList().get(INDEX_FIRST_FRIEND.getZeroBased());
        EditFriendDescriptor descriptor = new EditFriendDescriptorBuilder(firstFriend).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_FRIEND, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_FRIEND);
    }

    @Test
    public void execute_duplicateFriendFilteredList_failure() {
        showFriendAtIndex(model, INDEX_FIRST_FRIEND);

        // edit friend in filtered list into a duplicate in GameMates
        Friend friendInList = model.getGameMates().getFriendList().get(INDEX_SECOND_FRIEND.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_FRIEND,
                new EditFriendDescriptorBuilder(friendInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_FRIEND);
    }

    @Test
    public void execute_invalidFriendIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredFriendList().size() + 1);
        EditFriendDescriptor descriptor = new EditFriendDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of GameMates
     */
    @Test
    public void execute_invalidFriendIndexFilteredList_failure() {
        showFriendAtIndex(model, INDEX_FIRST_FRIEND);
        Index outOfBoundIndex = INDEX_SECOND_FRIEND;
        // ensures that outOfBoundIndex is still in bounds of GameMates list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getGameMates().getFriendList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditFriendDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_FRIEND, DESC_AMY);

        // same values -> returns true
        EditFriendDescriptor copyDescriptor = new EditFriendDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_FRIEND, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_FRIEND, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_FRIEND, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditFriendDescriptor editFriendDescriptor = new EditFriendDescriptor();
        EditCommand editCommand = new EditCommand(index, editFriendDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editFriendDescriptor="
                + editFriendDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
