package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Supplier;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.FriendTarget;

/** Resolves command targets and translates invalid indices into command feedback. */
final class FriendTargetResolver {

    private FriendTargetResolver() {
    }

    /** Resolves a target using the standard invalid-index message. */
    static FriendTarget resolve(Model model, Index index) throws CommandException {
        return resolve(model, index, () -> Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    /** Resolves a target, generating command-specific feedback only if the index is invalid. */
    static FriendTarget resolve(Model model, Index index, Supplier<String> invalidIndexMessage)
            throws CommandException {
        requireAllNonNull(model, index, invalidIndexMessage);
        try {
            return model.resolveFriend(index);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandException(invalidIndexMessage.get());
        }
    }
}
