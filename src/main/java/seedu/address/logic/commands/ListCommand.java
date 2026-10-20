package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendSortCriterion;
import seedu.address.model.friend.GameNameMatchesPredicate;

/**
 * Lists all friends in GameMates to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all friends, or only friends who play "
            + "the specified game (case-insensitive), optionally sorted by name or game.\n"
            + "Parameters: [g/GAME_NAME] [s/NAME|GAME] [o/ASC|DESC]\n"
            + "Example: " + COMMAND_WORD + " g/Valorant s/name o/desc";

    public static final String MESSAGE_SUCCESS = "Listed all friends.";

    private final Predicate<Friend> predicate;
    private final boolean isFilteredByGame;
    private final FriendSortCriterion sortCriterion;
    private final boolean isDescending;

    /**
     * Creates a command that lists all friends.
     */
    public ListCommand() {
        this((FriendSortCriterion) null, false);
    }

    /**
     * Creates a command that lists all friends sorted by the given criterion.
     */
    public ListCommand(FriendSortCriterion sortCriterion) {
        this(sortCriterion, false);
    }

    /**
     * Creates a command that lists all friends sorted by the given criterion and direction.
     */
    public ListCommand(FriendSortCriterion sortCriterion, boolean isDescending) {
        predicate = PREDICATE_SHOW_ALL_FRIENDS;
        isFilteredByGame = false;
        this.sortCriterion = sortCriterion;
        this.isDescending = isDescending;
    }

    /**
     * Creates a command that lists friends matching the given game predicate.
     */
    public ListCommand(GameNameMatchesPredicate predicate) {
        this(predicate, null, false);
    }

    /**
     * Creates a command that lists friends matching the given game predicate, sorted by the given criterion.
     */
    public ListCommand(GameNameMatchesPredicate predicate, FriendSortCriterion sortCriterion) {
        this(predicate, sortCriterion, false);
    }

    /**
     * Creates a command that lists friends matching the given game predicate, sorted by criterion and direction.
     */
    public ListCommand(GameNameMatchesPredicate predicate, FriendSortCriterion sortCriterion, boolean isDescending) {
        this.predicate = requireNonNull(predicate);
        isFilteredByGame = true;
        this.sortCriterion = sortCriterion;
        this.isDescending = isDescending;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredFriendList(predicate);
        if (sortCriterion == null) {
            model.clearFriendListSort();
        } else {
            model.sortFilteredFriendList(sortCriterion, isDescending);
        }

        if (!isFilteredByGame) {
            return new CommandResult(MESSAGE_SUCCESS);
        }

        return new CommandResult(
                String.format(Messages.MESSAGE_FRIENDS_LISTED_OVERVIEW, model.getFilteredFriendList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListCommand otherListCommand)) {
            return false;
        }

        return predicate.equals(otherListCommand.predicate)
                && isFilteredByGame == otherListCommand.isFilteredByGame
                && sortCriterion == otherListCommand.sortCriterion
                && isDescending == otherListCommand.isDescending;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .add("isFilteredByGame", isFilteredByGame)
                .add("sortCriterion", sortCriterion)
                .add("isDescending", isDescending)
                .toString();
    }
}
