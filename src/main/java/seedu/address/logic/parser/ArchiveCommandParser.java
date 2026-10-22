package seedu.address.logic.parser;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a displayed friend index for the archive command. */
public class ArchiveCommandParser implements Parser<ArchiveCommand> {
    @Override
    public ArchiveCommand parse(String args) throws ParseException {
        return new ArchiveCommand(ParserUtil.parseIndex(args));
    }
}
