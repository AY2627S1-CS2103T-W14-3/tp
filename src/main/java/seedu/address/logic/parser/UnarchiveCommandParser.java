package seedu.address.logic.parser;

import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a displayed friend index for the unarchive command. */
public class UnarchiveCommandParser implements Parser<UnarchiveCommand> {
    @Override
    public UnarchiveCommand parse(String args) throws ParseException {
        return new UnarchiveCommand(ParserUtil.parseIndex(args));
    }
}
