package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.ArchiveListCommand;
import seedu.address.model.friend.NameContainsKeywordsPredicate;

/** Parses optional archive name keywords using the same matching rules as find. */
public class ArchiveListCommandParser implements Parser<ArchiveListCommand> {
    @Override
    public ArchiveListCommand parse(String args) {
        String trimmedArgs = args.trim();
        return trimmedArgs.isEmpty() ? new ArchiveListCommand()
                : new ArchiveListCommand(new NameContainsKeywordsPredicate(List.of(trimmedArgs.split("\\s+"))));
    }
}
