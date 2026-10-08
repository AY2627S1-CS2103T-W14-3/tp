package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.GameMates;
import seedu.address.model.Model;

/**
 * Clears the friend list while preserving the user profile.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Friend list has been cleared!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        GameMates cleared = new GameMates();
        cleared.setMyself(model.getGameMates().getMyself());
        model.setGameMates(cleared);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
