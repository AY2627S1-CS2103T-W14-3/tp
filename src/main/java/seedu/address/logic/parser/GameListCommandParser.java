package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GameListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a displayed friend index or Myself selector and at most one game-name filter. */
public class GameListCommandParser implements Parser<GameListCommand> {

    @Override
    public GameListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GAME);
        Index index = parseIndex(argMultimap.getPreamble());
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GAME);
        if (argMultimap.getValue(PREFIX_GAME).isEmpty()) {
            return new GameListCommand(index);
        }
        return new GameListCommand(index, ParserUtil.parseGameName(argMultimap.getValue(PREFIX_GAME).get()));
    }

    private static Index parseIndex(String indexValue) throws ParseException {
        if (indexValue.isEmpty() || indexValue.split("\\s+").length != 1) {
            throw new ParseException(GameListCommand.MESSAGE_USAGE);
        }
        return ParserUtil.parseIndexOrMyself(indexValue);
    }
}
