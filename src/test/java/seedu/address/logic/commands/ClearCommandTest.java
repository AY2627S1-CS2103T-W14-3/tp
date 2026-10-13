package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalFriends.ALICE;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;

import org.junit.jupiter.api.Test;

import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_customMyself_preservesProfile() {
        Model model = new ModelManager(getTypicalGameMates(), new UserPrefs());
        model.setMyself(ALICE);
        Model expectedModel = new ModelManager();
        expectedModel.setMyself(ALICE);

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_emptyGameMates_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyGameMates_success() {
        Model model = new ModelManager(getTypicalGameMates(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalGameMates(), new UserPrefs());
        expectedModel.setGameMates(new GameMates());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
