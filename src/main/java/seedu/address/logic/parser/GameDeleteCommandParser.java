package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME_NAME;

import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GameDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.game.GameName;

/**
 * Parses input arguments into a {@link GameDeleteCommand} for a displayed friend or the Myself profile.
 */
public class GameDeleteCommandParser implements Parser<GameDeleteCommand> {

    /**
     * Parses a positive displayed friend index or the case-insensitive {@value Index#MYSELF_KEYWORD}
     * selector, followed by a required game name. The keyword selects {@link Index#myself()}.
     *
     * @param args command arguments without the command word.
     * @return the command to delete the specified game from the selected friend or profile.
     * @throws ParseException if a required argument is missing, the selector or game name is invalid,
     *     or the game-name prefix is repeated.
     */
    @Override
    public GameDeleteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GAME_NAME);
        if (!arePrefixesPresent(argMultimap, PREFIX_GAME_NAME) || argMultimap.getPreamble().isEmpty()
                || argMultimap.getValue(PREFIX_GAME_NAME).get().trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GameDeleteCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GAME_NAME);
        Index index = ParserUtil.parseIndexOrMyself(argMultimap.getPreamble());
        GameName gameName = ParserUtil.parseGameName(argMultimap.getValue(PREFIX_GAME_NAME).get());
        return new GameDeleteCommand(index, gameName);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isDefined());
    }
}
