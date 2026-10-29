package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME_NAME;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendTarget;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;

/** Deletes a game from a displayed friend or the Myself profile. */
public class GameDeleteCommand extends Command {

    public static final String COMMAND_WORD = "gamedel";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes a game from a friend or your own profile. "
            + "Parameters: FRIEND_INDEX|myself (use a positive integer for a friend, or myself for your own profile) "
            + PREFIX_GAME_NAME + "GAME_NAME\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_GAME_NAME + "Valorant";
    public static final String MESSAGE_SUCCESS = "Deleted game %1$s from %2$s.";

    private final Index targetIndex;
    private final GameName gameName;

    /**
     * Creates a command to remove the game from a displayed friend or the Myself profile.
     */
    public GameDeleteCommand(Index targetIndex, GameName gameName) {
        this.targetIndex = requireNonNull(targetIndex);
        this.gameName = requireNonNull(gameName);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        FriendTarget target = FriendTargetResolver.resolve(model, targetIndex);
        FriendValidator.checkFriendInitialized(target);
        Friend friend = target.friend();
        Game toDelete = GameTargetResolver.resolve(friend, gameName);
        Set<Game> games = new HashSet<>(friend.getGames());
        games.remove(toDelete);
        model.setFriend(target, friend.withGames(games));
        return new CommandResult(String.format(MESSAGE_SUCCESS, gameName, friend.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GameDeleteCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex) && gameName.equals(otherCommand.gameName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex, gameName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).add("gameName", gameName).toString();
    }
}
