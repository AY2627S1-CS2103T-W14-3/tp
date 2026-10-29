package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;

/** Resolves an existing game by name for game edit and delete commands. */
final class GameTargetResolver {

    static final String MESSAGE_GAME_NOT_FOUND = "%1$s does not have %2$s.";
    static final String MESSAGE_AMBIGUOUS_GAME = "More than one entry matches %s. Check the stored game data.";

    private GameTargetResolver() {
    }

    /** Finds a unique matching game, reporting missing or ambiguous names before any mutation. */
    static Game resolve(Friend friend, GameName gameName) throws CommandException {
        requireAllNonNull(friend, gameName);
        List<Game> matches = friend.getGames().stream()
                .filter(game -> game.getGameName().equals(gameName)).toList();
        if (matches.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_GAME_NOT_FOUND, friend.getName(), gameName));
        }
        if (matches.size() > 1) {
            // This should never happen, but we check for it anyway to avoid accidental data loss.
            throw new CommandException(String.format(MESSAGE_AMBIGUOUS_GAME, gameName));
        }
        return matches.getFirst();
    }
}
