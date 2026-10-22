package seedu.address.logic.parser;

import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses input arguments and creates a {@link PinCommand}. */
public class PinCommandParser implements Parser<PinCommand> {

    @Override
    public PinCommand parse(String args) throws ParseException {
        return new PinCommand(ParserUtil.parseIndex(args));
    }
}
