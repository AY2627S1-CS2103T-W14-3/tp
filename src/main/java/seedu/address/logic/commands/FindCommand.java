package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.NameContainsKeywordsPredicate;

/**
 * Finds and lists currently displayed friends whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds currently displayed friends whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

    private final NameContainsKeywordsPredicate predicate;

    public FindCommand(NameContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        List<Friend> displayedFriends = List.copyOf(model.getFilteredFriendList());
        model.updateSearchFriendList(friend -> predicate.test(friend)
                && displayedFriends.stream().anyMatch(friend::isSameFriend));
        List<Friend> matchingFriends = model.getFilteredFriendList();
        return new CommandResult(
                String.format(Messages.MESSAGE_FRIENDS_LISTED_OVERVIEW, matchingFriends.size()),
                matchingFriends.size() == 1 ? matchingFriends.getFirst() : null);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
