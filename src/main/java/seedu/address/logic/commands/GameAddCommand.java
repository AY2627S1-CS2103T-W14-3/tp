package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_USERNAME;

import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendTarget;
import seedu.address.model.game.Game;

/**
 * Adds a game and username to a displayed friend or the Myself profile.
 */
public class GameAddCommand extends Command {

    public static final String COMMAND_WORD = "gameadd";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a game to a friend or your own profile. "
            + "Parameters: FRIEND_INDEX|myself (use a positive integer for a friend, or myself for your own profile) "
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
     * Creates a command to add the game to a displayed friend or the Myself profile.
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

        FriendTarget target = FriendTargetResolver.resolve(model, targetIndex);
        Friend friend = target.friend();
        checkForDuplicate(friend);
        Set<Game> games = new HashSet<>(friend.getGames());
        games.add(toAdd);
        Friend updatedFriend = new Friend(friend.getName(), friend.getPhone(), friend.getEmail(),
                games, friend.getTags(), friend.isPinned());
        model.setFriend(target, updatedFriend);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                toAdd.getGameName(), friend.getName(), toAdd.getUsername()));
    }

    /** Rejects adding a game that the selected friend or profile already has. */
    private void checkForDuplicate(Friend friend) throws CommandException {
        if (friend.getGames().stream().anyMatch(existing -> existing.isSameGame(toAdd))) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_GAME, friend.getName(), toAdd.getGameName()));
        }
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
