package seedu.address.logic.parser;

import seedu.address.logic.commands.UnpinCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses input arguments and creates an {@link UnpinCommand}. */
public class UnpinCommandParser implements Parser<UnpinCommand> {

    @Override
    public UnpinCommand parse(String args) throws ParseException {
        return new UnpinCommand(ParserUtil.parseIndex(args));
    }
}
