package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME;

import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GameListCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.game.GameName;

/** Parses a displayed friend index or Myself selector and at most one game-name filter. */
public class GameListCommandParser implements Parser<GameListCommand> {

    // The shared tokenizer ignores unknown prefixes, so reject them locally before tokenizing.
    private static final Pattern PARAMETER_PREFIX = Pattern.compile("(?<!\\S)([^\\s/]+/)");

    @Override
    public GameListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (PARAMETER_PREFIX.matcher(args).results()
                .anyMatch(prefix -> !prefix.group(1).equals(PREFIX_GAME.getPrefix()))) {
            throw new ParseException(GameListCommand.MESSAGE_USAGE);
        }

        // Adapt whitespace-delimited prefixes to the shared tokenizer's literal-space boundary.
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args.replaceAll("(?<!\\S)g/", " g/"), PREFIX_GAME);
        String indexValue = argMultimap.getPreamble();
        if (indexValue.isEmpty() || indexValue.split("\\s+").length != 1) {
            throw new ParseException(GameListCommand.MESSAGE_USAGE);
        }
        Index index;
        try {
            index = ParserUtil.parseIndexOrMyself(indexValue);
        } catch (ParseException e) {
            throw new ParseException(GameListCommand.MESSAGE_INVALID_INDEX, e);
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GAME);
        } catch (ParseException e) {
            throw new ParseException(GameListCommand.MESSAGE_REPEATED_GAME, e);
        }
        if (argMultimap.getValue(PREFIX_GAME).isEmpty()) {
            return new GameListCommand(index);
        }

        // Validate the raw value too: tokenizer trim() would discard invalid boundary controls such as NUL.
        String gameValue = args.substring(args.indexOf(PREFIX_GAME.getPrefix())
                + PREFIX_GAME.getPrefix().length()).strip();
        if (!GameName.isValidGameName(gameValue)) {
            throw new ParseException(String.format(GameListCommand.MESSAGE_INVALID_GAME_NAME, gameValue));
        }
        return new GameListCommand(index, ParserUtil.parseGameName(argMultimap.getValue(PREFIX_GAME).get()));
    }
}
