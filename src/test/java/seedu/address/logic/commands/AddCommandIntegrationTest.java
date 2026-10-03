package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalGameMates(), new UserPrefs());
    }

    @Test
    public void execute_newFriend_success() {
        Friend validFriend = new FriendBuilder().build();

        Model expectedModel = new ModelManager(model.getGameMates(), new UserPrefs());
        expectedModel.addFriend(validFriend);

        assertCommandSuccess(new AddCommand(validFriend), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validFriend)),
                expectedModel);
    }

    @Test
    public void execute_duplicateFriend_throwsCommandException() {
        Friend friendInList = model.getGameMates().getFriendList().get(0);
        assertCommandFailure(new AddCommand(friendInList), model,
                AddCommand.MESSAGE_DUPLICATE_FRIEND);
    }

}
