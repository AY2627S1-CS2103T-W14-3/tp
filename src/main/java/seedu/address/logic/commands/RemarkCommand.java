package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Remark;

/**
 * Changes the remark of an existing friend in GameMates.
 */
public class RemarkCommand extends Command {

    private final Index index;
    private final Remark remark;

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Changes the remark of the friend identified "
            + "by the index number used in the displayed friend list. "
            + "An existing remark will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_REMARK + "[REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_REMARK + "Good at Valorant";

    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to Friend: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from Friend: %1$s";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Friend> lastShownList = model.getFilteredFriendList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
        }

        Friend friendToEdit = lastShownList.get(index.getZeroBased());
        Friend editedFriend = new Friend(
                friendToEdit.getName(), friendToEdit.getPhone(), friendToEdit.getEmail(),
                friendToEdit.getAddress(), remark, friendToEdit.getTags());

        model.setFriend(friendToEdit, editedFriend);
        model.updateFilteredFriendList(PREDICATE_SHOW_ALL_FRIENDS);
        return new CommandResult(generateSuccessMessage(friendToEdit));
    }

    /**
     * Generates a command execution success message based on whether
     * the remark is added to or removed from
     * {@code friendToEdit}.
     */
    private String generateSuccessMessage(Friend friendToEdit) {
        String message = !remark.remark.isEmpty() ? MESSAGE_ADD_REMARK_SUCCESS : MESSAGE_DELETE_REMARK_SUCCESS;
        return String.format(message, Messages.format(friendToEdit));
    }

    /**
     * @param index of the friend in the filtered person list to edit the remark
     * @param remark of the friend to be updated to
     */
    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);

        this.index = index;
        this.remark = remark;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand e)) {
            return false;
        }

        return index.equals(e.index)
                && remark.equals(e.remark);
    }
}
