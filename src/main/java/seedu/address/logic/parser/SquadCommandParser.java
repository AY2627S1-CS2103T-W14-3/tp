package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.SquadCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.game.GameName;

/** Parses a game name and the total squad size, including the user. */
public class SquadCommandParser implements Parser<SquadCommand> {
    @Override
    public SquadCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_GAME, PREFIX_NAME);
        if (!argMultimap.arePrefixesPresent(PREFIX_GAME, PREFIX_NAME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(SquadCommand.MESSAGE_USAGE);
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GAME, PREFIX_NAME);
        String gameValue = argMultimap.getValue(PREFIX_GAME).get();
        String sizeValue = argMultimap.getValue(PREFIX_NAME).get();
        if (gameValue.isBlank() || sizeValue.isBlank()) {
            throw new ParseException(SquadCommand.MESSAGE_USAGE);
        }
        if (!GameName.isValidGameName(gameValue)) {
            throw new ParseException(String.format(SquadCommand.MESSAGE_INVALID_GAME, gameValue));
        }
        if (!StringUtil.isNonZeroUnsignedInteger(sizeValue)) {
            throw new ParseException(SquadCommand.MESSAGE_INVALID_SIZE);
        }
        int size = Integer.parseInt(sizeValue);
        if (size > SquadCommand.MAX_SIZE) {
            throw new ParseException(SquadCommand.MESSAGE_INVALID_SIZE);
        }
        return new SquadCommand(ParserUtil.parseGameName(gameValue), size);
    }
}
