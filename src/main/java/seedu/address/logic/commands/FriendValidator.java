package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import io.vavr.control.Option;
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
            if (candidate.getName().isEmpty()) {
                throw new CommandException(Messages.MESSAGE_MYSELF_NOT_INITIALIZED);
            }
            if (model.findFriend(candidate).isDefined()) {
                throw new CommandException(Messages.MESSAGE_PROFILE_NAME_CONFLICT);
            }
        } else {
            checkForDuplicateMyself(model, candidate);
            if (!target.friend().isSameFriend(candidate)) {
                checkForDuplicateFriend(model, candidate);
            }
        }
    }

    /** Rejects an uninitialized friend target, such as a Myself profile with an empty name. */
    static void checkFriendInitialized(FriendTarget target) throws CommandException {
        requireAllNonNull(target);
        if (target.isMyself() && target.friend().getName().isEmpty()) {
            throw new CommandException(Messages.MESSAGE_MYSELF_NOT_INITIALIZED);
        }
    }

    private static void checkForDuplicateFriend(Model model, Friend candidate) throws CommandException {
        Option<Friend> existing = model.findFriend(candidate);
        if (existing.isDefined()) {
            throw new CommandException(existing.get().isArchived()
                    ? Messages.MESSAGE_DUPLICATE_ARCHIVED_FRIEND : Messages.MESSAGE_DUPLICATE_FRIEND);
        }
    }

    private static void checkForDuplicateMyself(Model model, Friend candidate) throws CommandException {
        if (model.getGameMates().getMyself().isSameFriend(candidate)) {
            throw new CommandException(Messages.MESSAGE_DUPLICATE_MYSELF);
        }
    }
}
