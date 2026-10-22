package seedu.address.logic.parser;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.friend.GameNameMatchesPredicate;

/** Parses the optional game filter for the ListCommand command. */
public class ListCommandParser implements Parser<ListCommand> {
    @Override
    public ListCommand parse(String args) throws ParseException {
        return ParserUtil.parseOptionalGameFilter(args, ListCommand.MESSAGE_USAGE)
                .map(game -> new ListCommand(new GameNameMatchesPredicate(game)))
                .getOrElse(new ListCommand());
    }
}
