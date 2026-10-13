package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.GameNameMatchesPredicate;

/**
 * Lists all friends in GameMates to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all friends, or only friends who play "
            + "the specified game (case-insensitive).\n"
            + "Parameters: [g/GAME_NAME]\n"
            + "Example: " + COMMAND_WORD + " g/Valorant";

    public static final String MESSAGE_SUCCESS = "Listed all friends.";

    private final Predicate<Friend> predicate;
    private final boolean isFilteredByGame;

    /**
     * Creates a command that lists all friends.
     */
    public ListCommand() {
        predicate = PREDICATE_SHOW_ALL_FRIENDS;
        isFilteredByGame = false;
    }

    /**
     * Creates a command that lists friends matching the given game predicate.
     */
    public ListCommand(GameNameMatchesPredicate predicate) {
        this.predicate = requireNonNull(predicate);
        isFilteredByGame = true;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredFriendList(predicate);

        if (!isFilteredByGame) {
            return new CommandResult(MESSAGE_SUCCESS);
        }

        return new CommandResult(
                String.format(Messages.MESSAGE_FRIENDS_LISTED_OVERVIEW, model.getFilteredFriendList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListCommand otherListCommand)) {
            return false;
        }

        return predicate.equals(otherListCommand.predicate)
                && isFilteredByGame == otherListCommand.isFilteredByGame;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .add("isFilteredByGame", isFilteredByGame)
                .toString();
    }
}
