package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

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
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_FRIEND_SUCCESS = "Successfully deleted %1$s.";
    public static final String MESSAGE_MISSING_INDEX = "Error: Please provide a friend index.";
    public static final String MESSAGE_INVALID_INDEX =
            "Error: Please enter a positive integer as the friend index.";
    public static final String MESSAGE_INDEX_NOT_FOUND = "Error: Friend index does not exist.";

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
        List<Friend> lastShownList = model.getFilteredFriendList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INDEX_NOT_FOUND);
        }

        Friend friendToDelete = lastShownList.get(targetIndex.getZeroBased());
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
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
