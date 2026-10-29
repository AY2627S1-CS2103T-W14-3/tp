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

    public static final String MESSAGE_EMPTY_ARCHIVE =
            "No archived friends. Use the command \"list\", then \"archive FRIEND_INDEX\" to archive a friend.";
    public static final String MESSAGE_NO_MATCH = "No archived friends match this game filter.";

    private final Predicate<Friend> predicate;
    private final boolean filteredByGame;

    /** Creates an unfiltered archive listing. */
    public ArchiveListCommand() {
        predicate = PREDICATE_SHOW_ALL_FRIENDS;
        filteredByGame = false;
    }

    /** Creates an archive listing filtered by the given game. */
    public ArchiveListCommand(GameNameMatchesPredicate predicate) {
        this.predicate = requireNonNull(predicate);
        filteredByGame = true;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateArchivedFriendList(predicate);
        int count = model.getFilteredFriendList().size();
        if (count == 0) {
            return new CommandResult(filteredByGame ? MESSAGE_NO_MATCH : MESSAGE_EMPTY_ARCHIVE);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, count));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ArchiveListCommand otherCommand
                && predicate.equals(otherCommand.predicate);
    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
