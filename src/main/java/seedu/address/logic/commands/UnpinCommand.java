package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.commands.EditCommand.MESSAGE_EMPTY_LIST;
import static seedu.address.logic.commands.EditCommand.MESSAGE_INDEX_NOT_FOUND;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;

/** Removes a friend from the pinned group at the top of the friend list. */
public class UnpinCommand extends Command {

    public static final String COMMAND_WORD = "unpin";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unpins the friend identified by the index number used in the displayed friend list.\n"
            + "Parameters: FRIEND_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Unpinned %1$s.";
    public static final String MESSAGE_NOT_PINNED = "%1$s is not pinned.";

    private final Index targetIndex;

    /** Creates a command to unpin the friend at the given displayed index. */
    public UnpinCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        int displayedFriendCount = model.getFilteredFriendList().size();
        if (displayedFriendCount == 0) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }
        Friend friendToUnpin = FriendTargetResolver.resolve(model, targetIndex, () -> String.format(
                MESSAGE_INDEX_NOT_FOUND, targetIndex.getOneBased(), displayedFriendCount)).friend();
        if (!friendToUnpin.isPinned()) {
            throw new CommandException(String.format(MESSAGE_NOT_PINNED, friendToUnpin.getName()));
        }

        model.setFriend(friendToUnpin, PinCommand.copyWithPinnedState(friendToUnpin, false));
        return new CommandResult(String.format(MESSAGE_SUCCESS, friendToUnpin.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof UnpinCommand otherUnpinCommand && targetIndex.equals(otherUnpinCommand.targetIndex);
    }

    @Override
    public int hashCode() {
        return targetIndex.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
