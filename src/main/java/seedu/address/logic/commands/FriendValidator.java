package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendTarget;

/** Validates friend identity conflicts for add and edit commands. */
final class FriendValidator {

    private FriendValidator() {
    }

    /** Rejects adding a friend whose identity conflicts with a friend or the Myself profile. */
    static void checkCanAdd(Model model, Friend candidate) throws CommandException {
        requireAllNonNull(model, candidate);
        checkForDuplicateFriend(model, candidate);
        checkForDuplicateMyself(model, candidate);
    }

    /** Rejects identity conflicts while allowing an edited target to retain its own identity. */
    static void checkCanEdit(Model model, FriendTarget target, Friend candidate) throws CommandException {
        requireAllNonNull(model, target, candidate);
        if (target.isMyself()) {
            checkForDuplicateFriend(model, candidate);
        } else {
            checkForDuplicateMyself(model, candidate);
            if (!target.friend().isSameFriend(candidate)) {
                checkForDuplicateFriend(model, candidate);
            }
        }
    }

    private static void checkForDuplicateFriend(Model model, Friend candidate) throws CommandException {
        if (model.hasFriend(candidate)) {
            throw new CommandException(Messages.MESSAGE_DUPLICATE_FRIEND);
        }
    }

    private static void checkForDuplicateMyself(Model model, Friend candidate) throws CommandException {
        if (model.getGameMates().getMyself().isSameFriend(candidate)) {
            throw new CommandException(Messages.MESSAGE_DUPLICATE_MYSELF);
        }
    }
}
