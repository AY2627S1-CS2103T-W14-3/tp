package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;

/**
 * Deletes a friend identified using its displayed index from GameMates.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the friend identified by the index number used in the displayed friend list.\n"
            + "Parameters: FRIEND_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_FRIEND_SUCCESS = "Successfully deleted %1$s.";

    private final Index targetIndex;

    /**
     * Creates a command to delete the friend at the given displayed index.
     */
    public DeleteCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Friend friendToDelete = FriendTargetResolver.resolve(model, targetIndex).friend();
        model.deleteFriend(friendToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_FRIEND_SUCCESS, friendToDelete.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteCommand.targetIndex);
    }

    @Override
    public int hashCode() {
        return targetIndex.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
