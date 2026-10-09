package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME;

import java.util.Optional;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.friend.GameNameMatchesPredicate;
import seedu.address.model.game.GameName;

/**
 * Parses input arguments and creates a new {@code ListCommand} object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code args} in the context of {@code ListCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public ListCommand parse(String args) throws ParseException {
        if (args.trim().isEmpty()) {
            return new ListCommand();
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GAME);
        Optional<String> gameNameValue = argMultimap.getValue(PREFIX_GAME);
        if (!argMultimap.getPreamble().isEmpty()
                || gameNameValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GAME);
        if (gameNameValue.get().isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }

        GameName gameName = ParserUtil.parseGameName(gameNameValue.get());
        return new ListCommand(new GameNameMatchesPredicate(gameName));
    }
}
