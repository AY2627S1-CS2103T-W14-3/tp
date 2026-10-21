package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.commands.EditCommand.MESSAGE_EMPTY_LIST;
import static seedu.address.logic.commands.EditCommand.MESSAGE_INDEX_NOT_FOUND;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;

/** Shows a displayed friend's games without changing the friend list or stored data. */
public class GameListCommand extends Command {

    public static final String COMMAND_WORD = "gamelist";
    public static final String MESSAGE_USAGE = "Invalid command format. Usage: gamelist <FRIEND_INDEX> [g/GAME_NAME]";
    public static final String MESSAGE_INVALID_INDEX =
            "Invalid friend index. Enter a positive integer between 1 and " + Integer.MAX_VALUE + " (inclusive).";
    public static final String MESSAGE_INVALID_GAME_NAME = "Invalid game name %1$s. "
            + "Enter 1–50 printable characters containing at least one letter or digit.";
    public static final String MESSAGE_REPEATED_GAME = "Only one game can be filtered per command. "
            + "Run gamelist separately for each game.";
    public static final String MESSAGE_ALL_GAMES = "Listed %1$d game(s) that %2$s plays:";
    public static final String MESSAGE_MATCHING_GAME = "Showing %1$s for %2$s:";
    public static final String MESSAGE_NO_MATCH = "%1$s does not have %2$s.";
    public static final String MESSAGE_NO_GAMES = "%1$s has no games added yet.";

    private final Index friendIndex;
    private final GameName gameFilter;

    /** Creates a command showing all games for the friend at {@code friendIndex}. */
    public GameListCommand(Index friendIndex) {
        this(friendIndex, null);
    }

    /** Creates a command with an optional game filter; {@code null} shows all games. */
    public GameListCommand(Index friendIndex, GameName gameFilter) {
        this.friendIndex = requireNonNull(friendIndex);
        this.gameFilter = gameFilter;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Friend> displayedFriends = model.getFilteredFriendList();
        if (displayedFriends.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_LIST);
        }
        if (friendIndex.getZeroBased() >= displayedFriends.size()) {
            throw new CommandException(String.format(MESSAGE_INDEX_NOT_FOUND,
                    friendIndex.getOneBased(), displayedFriends.size()));
        }

        Friend friend = displayedFriends.get(friendIndex.getZeroBased());
        String feedback;
        if (gameFilter == null) {
            feedback = friend.getGames().isEmpty()
                    ? String.format(MESSAGE_NO_GAMES, friend.getName())
                    : String.format(MESSAGE_ALL_GAMES, friend.getGames().size(), friend.getName());
        } else {
            feedback = friend.getGames().stream()
                    .map(Game::getGameName)
                    .filter(gameFilter::equals)
                    .findFirst()
                    .map(gameName -> String.format(MESSAGE_MATCHING_GAME, gameName, friend.getName()))
                    .orElseGet(() -> String.format(MESSAGE_NO_MATCH, friend.getName(), gameFilter));
        }
        return new CommandResult(feedback, friend, gameFilter);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof GameListCommand otherCommand
                && friendIndex.equals(otherCommand.friendIndex)
                && Objects.equals(gameFilter, otherCommand.gameFilter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friendIndex, gameFilter);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("friendIndex", friendIndex)
                .add("gameFilter", gameFilter)
                .toString();
    }
}
