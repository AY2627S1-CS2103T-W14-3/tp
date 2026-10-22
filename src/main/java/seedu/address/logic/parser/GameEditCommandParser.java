package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_META;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_USERNAME;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GameEditCommand;
import seedu.address.logic.commands.GameEditCommand.EditGameDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.model.game.meta.Meta;

/** Parses game edits using existing field validation and metadata key normalization. */
public class GameEditCommandParser implements Parser<GameEditCommand> {
    @Override
    public GameEditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenizeWithQuotedValues(args,
                PREFIX_GAME_NAME, PREFIX_NAME, PREFIX_USERNAME, PREFIX_META);
        if (arguments.getPreamble().isEmpty() || !arguments.arePrefixesPresent(PREFIX_GAME_NAME)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GameEditCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_GAME_NAME, PREFIX_NAME, PREFIX_USERNAME);
        Index index = ParserUtil.parseIndexOrMyself(arguments.getPreamble());
        GameName selector = ParserUtil.parseGameName(arguments.getValue(PREFIX_GAME_NAME).get());
        GameName name = arguments.getValue(PREFIX_NAME).isDefined()
                ? ParserUtil.parseGameName(arguments.getValue(PREFIX_NAME).get()) : null;
        Username username = null;
        if (arguments.arePrefixesPresent(PREFIX_USERNAME)) {
            String value = arguments.getValue(PREFIX_USERNAME).get();
            username = value.isEmpty() ? Username.EMPTY : ParserUtil.parseUsername(value);
        }
        List<String> pairs = arguments.getAllValues(PREFIX_META);
        Set<Meta> metas = null;
        if (!pairs.isEmpty()) {
            metas = pairs.size() == 1 && pairs.getFirst().isEmpty() ? Set.of() : ParserUtil.parseMetas(pairs);
        }
        EditGameDescriptor changes = new EditGameDescriptor(name, username, metas);
        if (!changes.isAnyFieldEdited()) {
            throw new ParseException(GameEditCommand.MESSAGE_NOT_EDITED);
        }
        return new GameEditCommand(index, selector, changes);
    }
}
