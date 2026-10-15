package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_USERNAME;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.Game;

/**
 * Adds a game and username to a friend in the currently displayed list.
 */
public class GameAddCommand extends Command {

    public static final String COMMAND_WORD = "gameadd";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a game to a friend. "
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_GAME_NAME + "GAME_NAME "
            + PREFIX_USERNAME + "USER_NAME\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_GAME_NAME + "Valorant "
            + PREFIX_USERNAME + "benergy10";
    public static final String MESSAGE_SUCCESS = "Added %1$s to %2$s's games.\nUsername: %3$s";
    public static final String MESSAGE_DUPLICATE_GAME = "%1$s already has %2$s.";

    private final Index targetIndex;
    private final Game toAdd;

    /**
     * Creates a command to add the game to the friend at the displayed index.
     */
    public GameAddCommand(Index targetIndex, Game game) {
        requireNonNull(targetIndex);
        requireNonNull(game);
        this.targetIndex = targetIndex;
        toAdd = game;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Friend> displayedFriends = model.getFilteredFriendList();
        if (targetIndex.getZeroBased() >= displayedFriends.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
        }

        Friend friend = displayedFriends.get(targetIndex.getZeroBased());
        if (friend.getGames().stream().anyMatch(existing -> existing.isSameGame(toAdd))) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_GAME, friend.getName(), toAdd.getGameName()));
        }
        Set<Game> games = new HashSet<>(friend.getGames());
        games.add(toAdd);
        Friend updatedFriend = new Friend(friend.getName(), friend.getPhone(), friend.getEmail(),
                games, friend.getTags());
        model.setFriend(friend, updatedFriend);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                toAdd.getGameName(), friend.getName(), toAdd.getUsername()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GameAddCommand otherGameAddCommand)) {
            return false;
        }

        return targetIndex.equals(otherGameAddCommand.targetIndex)
                && toAdd.equals(otherGameAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("toAdd", toAdd)
                .toString();
    }
}
