package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME_NAME;

import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GameDeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.game.GameName;

/** Parses arguments for deleting a game. */
public class GameDeleteCommandParser implements Parser<GameDeleteCommand> {
    @Override
    public GameDeleteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GAME_NAME);
        if (!arePrefixesPresent(argMultimap, PREFIX_GAME_NAME) || argMultimap.getPreamble().isEmpty()
                || argMultimap.getValue(PREFIX_GAME_NAME).get().trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GameDeleteCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GAME_NAME);
        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());
        GameName gameName = ParserUtil.parseGameName(argMultimap.getValue(PREFIX_GAME_NAME).get());
        return new GameDeleteCommand(index, gameName);
    }

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isDefined());
    }
}
