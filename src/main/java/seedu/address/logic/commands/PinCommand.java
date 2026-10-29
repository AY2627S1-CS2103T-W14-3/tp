package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.commands.EditCommand.MESSAGE_EMPTY_LIST;
import static seedu.address.logic.commands.EditCommand.MESSAGE_INDEX_NOT_FOUND;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.UniqueFriendList;

/** Pins a friend to the top of the friend list. */
public class PinCommand extends Command {

    public static final String COMMAND_WORD = "pin";
    public static final int MAX_PINNED_FRIENDS = UniqueFriendList.MAX_PINNED_FRIENDS;
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Pins the friend identified by the index number used in the displayed friend list.\n"
            + "Parameters: FRIEND_INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Pinned %1$s.";
    public static final String MESSAGE_ALREADY_PINNED = "%1$s is already pinned.";
    public static final String MESSAGE_MAX_PINNED_FRIENDS = "You can pin at most " + MAX_PINNED_FRIENDS + " friends.";

    private final Index targetIndex;

    /** Creates a command to pin the friend at the given displayed index. */
    public PinCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        int displayedFriendCount = model.getFilteredFriendList().size();
        if (displayedFriendCount == 0) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }
        Friend friendToPin = FriendTargetResolver.resolve(model, targetIndex, () -> String.format(
                MESSAGE_INDEX_NOT_FOUND, targetIndex.getOneBased(), displayedFriendCount)).friend();
        if (friendToPin.isPinned()) {
            throw new CommandException(String.format(MESSAGE_ALREADY_PINNED, friendToPin.getName()));
        }
        if (model.getGameMates().getFriendList().stream().filter(Friend::isPinned).count() >= MAX_PINNED_FRIENDS) {
            throw new CommandException(MESSAGE_MAX_PINNED_FRIENDS);
        }

        model.setFriend(friendToPin, copyWithPinnedState(friendToPin, true));
        return new CommandResult(String.format(MESSAGE_SUCCESS, friendToPin.getName()));
    }

    static Friend copyWithPinnedState(Friend friend, boolean pinned) {
        return friend.withPinned(pinned);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof PinCommand otherPinCommand && targetIndex.equals(otherPinCommand.targetIndex);
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
