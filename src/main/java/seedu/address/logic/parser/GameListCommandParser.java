package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GameListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.game.GameName;

/** Parses a displayed friend index and at most one game-name filter. */
public class GameListCommandParser implements Parser<GameListCommand> {

    // Recognize parameter tokens after any whitespace, including unknown prefixes.
    private static final Pattern PARAMETER_PREFIX = Pattern.compile("(?<!\\S)([^\\s/]+/)");

    @Override
    public GameListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher prefixes = PARAMETER_PREFIX.matcher(args);
        int gamePrefixCount = 0;
        int firstPrefixStart = args.length();
        int gameValueStart = args.length();
        while (prefixes.find()) {
            if (!prefixes.group(1).equals(PREFIX_GAME.getPrefix())) {
                throw new ParseException(GameListCommand.MESSAGE_USAGE);
            }
            if (gamePrefixCount == 0) {
                firstPrefixStart = prefixes.start();
                gameValueStart = prefixes.end();
            }
            gamePrefixCount++;
        }

        String indexValue = args.substring(0, firstPrefixStart).trim();
        if (indexValue.isEmpty() || indexValue.split("\\s+").length != 1) {
            throw new ParseException(GameListCommand.MESSAGE_USAGE);
        }
        Index index;
        try {
            index = ParserUtil.parseIndex(indexValue);
        } catch (ParseException e) {
            throw new ParseException(GameListCommand.MESSAGE_INVALID_INDEX, e);
        }

        if (gamePrefixCount > 1) {
            throw new ParseException(GameListCommand.MESSAGE_REPEATED_GAME);
        }
        if (gamePrefixCount == 0) {
            return new GameListCommand(index);
        }

        String gameValue = args.substring(gameValueStart).strip();
        if (!GameName.isValidGameName(gameValue)) {
            throw new ParseException(String.format(GameListCommand.MESSAGE_INVALID_GAME_NAME, gameValue));
        }
        return new GameListCommand(index, new GameName(gameValue));
    }
}
