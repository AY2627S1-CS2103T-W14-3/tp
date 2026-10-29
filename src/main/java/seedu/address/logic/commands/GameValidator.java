package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.Game;

/** Validates game identity conflicts for add and edit commands. */
final class GameValidator {

    static final String MESSAGE_DUPLICATE_GAME = "%1$s already has %2$s.";

    private GameValidator() {
    }

    /** Rejects adding a game whose name conflicts with an existing game. */
    static void checkCanAdd(Friend friend, Game candidate) throws CommandException {
        requireAllNonNull(friend, candidate);
        checkForDuplicateGame(friend, null, candidate);
    }

    /** Rejects game name conflicts while allowing an edited game to retain its own identity. */
    static void checkCanEdit(Friend friend, Game original, Game candidate) throws CommandException {
        requireAllNonNull(friend, original, candidate);
        checkForDuplicateGame(friend, original, candidate);
    }

    private static void checkForDuplicateGame(Friend friend, Game original, Game candidate)
            throws CommandException {
        if (friend.getGames().stream().anyMatch(game -> game != original && game.isSameGame(candidate))) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_GAME,
                    friend.getName(), candidate.getGameName()));
        }
    }
}
