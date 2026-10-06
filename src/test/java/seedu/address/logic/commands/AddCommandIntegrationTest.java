package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.GameMatesParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Address;
import seedu.address.model.friend.Email;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;
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
                String.format(AddCommand.MESSAGE_SUCCESS, validFriend.getName(),
                        validFriend.getPhone(), validFriend.getEmail()),
                expectedModel);
    }

    @Test
    public void execute_nameOnlyCommand_addsFriendAndShowsMissingDetails() throws Exception {
        model.updateFilteredFriendList(friend -> false);
        new GameMatesParser().parseCommand("add n/  Melody  ").execute(model);
        Friend added = model.getFilteredFriendList().getLast();
        assertEquals(new Friend(new Name("Melody"), Phone.empty(), Email.empty(), Address.empty(),
                java.util.Set.of()), added);
        assertEquals(model.getGameMates().getFriendList().size(), model.getFilteredFriendList().size());
    }

    @Test
    public void execute_nameOnlyCommand_successMessage() throws Exception {
        CommandResult result = new GameMatesParser().parseCommand("add n/Melody").execute(model);
        assertEquals("Added Melody.\n  - Phone number: Not provided\n  - Email: Not provided",
                result.getFeedbackToUser());
    }

    @Test
    public void execute_duplicateNameWithCaseAndRepeatedSpaces_rejectedEvenWhenHidden() throws Exception {
        new GameMatesParser().parseCommand("add n/Melody Tan").execute(model);
        model.updateFilteredFriendList(friend -> false);
        AddCommand duplicate = (AddCommand) new GameMatesParser().parseCommand("add n/melody   tan");
        assertCommandFailure(duplicate, model, AddCommand.MESSAGE_DUPLICATE_FRIEND);
    }

    @Test
    public void execute_sharedContactDetailsDifferentNames_success() throws Exception {
        GameMatesParser parser = new GameMatesParser();
        parser.parseCommand("add n/Melody p/91234567 e/shared@example.com").execute(model);
        parser.parseCommand("add n/Joash p/91234567 e/shared@example.com").execute(model);
        assertEquals("Joash", model.getFilteredFriendList().getLast().getName().fullName);
    }

    @Test
    public void execute_duplicateFriend_throwsCommandException() {
        Friend friendInList = model.getGameMates().getFriendList().get(0);
        assertCommandFailure(new AddCommand(friendInList), model,
                AddCommand.MESSAGE_DUPLICATE_FRIEND);
    }

}
