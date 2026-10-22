package seedu.address.logic.parser;

import seedu.address.logic.commands.ArchiveListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.friend.GameNameMatchesPredicate;

/** Parses the optional game filter for the ArchiveListCommand command. */
public class ArchiveListCommandParser implements Parser<ArchiveListCommand> {
    @Override
    public ArchiveListCommand parse(String args) throws ParseException {
        return ParserUtil.parseOptionalGameFilter(args, ArchiveListCommand.MESSAGE_USAGE)
                .map(game -> new ArchiveListCommand(new GameNameMatchesPredicate(game)))
                .getOrElse(new ArchiveListCommand());
    }
}
