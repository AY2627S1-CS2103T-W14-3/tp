package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.NameContainsKeywordsPredicate;

/** Shows archived friends, optionally matching any name keyword. */
public class ArchiveListCommand extends Command {
    public static final String COMMAND_WORD = "archivelist";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists archived friends, optionally matching any name keyword (case-insensitive).\n"
            + "Parameters: [KEYWORD [MORE_KEYWORDS]...]\n"
            + "Example: " + COMMAND_WORD + " alice";
    public static final String MESSAGE_SUCCESS = "Listed %1$d archived friend(s).";

    private final Predicate<Friend> predicate;

    public ArchiveListCommand() {
        predicate = PREDICATE_SHOW_ALL_FRIENDS;
    }

    public ArchiveListCommand(NameContainsKeywordsPredicate predicate) {
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
