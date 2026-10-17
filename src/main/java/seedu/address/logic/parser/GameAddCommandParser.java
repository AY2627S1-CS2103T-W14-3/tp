package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_META;
import static seedu.address.logic.parser.CliSyntax.PREFIX_USERNAME;

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GameAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.model.game.meta.Meta;

/**
 * Parses input arguments into a {@link GameAddCommand} for a displayed friend or the Myself profile.
 */
public class GameAddCommandParser implements Parser<GameAddCommand> {

    /**
     * Parses a positive displayed friend index or the case-insensitive {@value Index#MYSELF_KEYWORD}
     * selector, followed by a required game name and username and optional metadata pairs.
     * The keyword selects {@link Index#myself()}.
     *
     * @param args command arguments without the command word.
     * @return the command to add the specified game and username to the selected friend or profile.
     * @throws ParseException if a required argument is missing, the selector or a field value is invalid,
     *     a game-name or username prefix is repeated, or metadata keys are duplicated.
     */
    @Override
    public GameAddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_GAME_NAME, PREFIX_USERNAME, PREFIX_META);
        if (!arePrefixesPresent(argMultimap, PREFIX_GAME_NAME, PREFIX_USERNAME)
                || argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GameAddCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GAME_NAME, PREFIX_USERNAME);
        Index index = ParserUtil.parseIndexOrMyself(argMultimap.getPreamble());
        GameName gameName = ParserUtil.parseGameName(argMultimap.getValue(PREFIX_GAME_NAME).get());
        Username username = ParserUtil.parseUsername(argMultimap.getValue(PREFIX_USERNAME).get());
        Set<Meta> metas = ParserUtil.parseMetas(argMultimap.getAllValues(PREFIX_META));
        Game game = new Game(gameName, username, metas);

        return new GameAddCommand(index, game);
    }

    /**
     * Returns true if all required prefixes have values present.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isDefined());
    }
}
