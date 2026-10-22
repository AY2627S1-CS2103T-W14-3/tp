package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.GameNameMatchesPredicate;

/** Shows archived friends, optionally filtered by game. */
public class ArchiveListCommand extends Command {
    public static final String COMMAND_WORD = "archivelist";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists archived friends, optionally filtered by game (case-insensitive).\n"
            + "Parameters: [g/GAME_NAME]\n"
            + "Example: " + COMMAND_WORD + " g/Valorant";
    public static final String MESSAGE_SUCCESS = "Listed %1$d archived friend(s).";

    private final Predicate<Friend> predicate;

    public ArchiveListCommand() {
        predicate = PREDICATE_SHOW_ALL_FRIENDS;
    }

    public ArchiveListCommand(GameNameMatchesPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateArchivedFriendList(predicate);
        return new CommandResult(String.format(MESSAGE_SUCCESS, model.getFilteredFriendList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ArchiveListCommand otherCommand
                && predicate.equals(otherCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
