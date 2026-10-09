package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Remark;

/**
 * Changes the remark of an existing friend in GameMates.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the remark of the friend identified "
            + "by the index number used in the displayed friend list. "
            + "Existing remark will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) " + PREFIX_REMARK + "[REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_REMARK + "Likes to swim.";

    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to friend: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from friend: %1$s";

    private final Index index;
    private final Remark remark;

    /**
     * @param index of the friend in the displayed list to edit
     * @param remark to replace the friend's existing remark with
     */
    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);
        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Friend> lastShownList = model.getFilteredFriendList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
        }

        Friend friendToEdit = lastShownList.get(index.getZeroBased());
        Friend editedFriend = new Friend(friendToEdit.getName(), friendToEdit.getPhone(), friendToEdit.getEmail(),
                friendToEdit.getGames(), remark, friendToEdit.getTags());
        model.setFriend(friendToEdit, editedFriend);
        model.updateFilteredFriendList(PREDICATE_SHOW_ALL_FRIENDS);

        String message = remark.value.isEmpty() ? MESSAGE_DELETE_REMARK_SUCCESS : MESSAGE_ADD_REMARK_SUCCESS;
        return new CommandResult(String.format(message, Messages.format(editedFriend)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof RemarkCommand otherCommand
                && index.equals(otherCommand.index)
                && remark.equals(otherCommand.remark);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("remark", remark)
                .toString();
    }
}
