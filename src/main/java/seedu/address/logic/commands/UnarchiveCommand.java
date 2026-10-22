package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;

/** Changes the archive status of a friend selected from the displayed list. */
public class UnarchiveCommand extends Command {
    public static final String COMMAND_WORD = "unarchive";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Selects an archived friend by its displayed index.\n"
            + "Parameters: FRIEND_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Restored %1$s.";
    public static final String MESSAGE_WRONG_STATUS = "This friend is not archived. "
            + "Use archivelist to select an archived friend.";
    public static final String MESSAGE_MYSELF = "Your Myself profile cannot be archived or unarchived.";

    private final Index targetIndex;

    public UnarchiveCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (targetIndex.isMyself()) {
            throw new CommandException(MESSAGE_MYSELF);
        }
        if (targetIndex.getZeroBased() >= model.getFilteredFriendList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
        }
        Friend friend = model.getFilteredFriendList().get(targetIndex.getZeroBased());
        if (friend.isArchived() == false) {
            throw new CommandException(MESSAGE_WRONG_STATUS);
        }
        model.setFriend(friend, friend.withArchived(false));
        return new CommandResult(String.format(MESSAGE_SUCCESS, friend.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof UnarchiveCommand otherCommand
                && targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
