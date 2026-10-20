package seedu.address.logic.parser;

import static io.vavr.API.unchecked;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_USERNAME;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GameAddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;

/**
 * Parses arguments for adding a game to a friend.
 */
public class GameAddCommandParser implements Parser<GameAddCommand> {

    /**
     * Parses the given arguments and returns a GameAddCommand for execution.
     *
     * @throws ParseException if the input does not conform to the expected format.
     */
    @Override
    public GameAddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_GAME_NAME, PREFIX_USERNAME);
        if (!argMultimap.arePrefixesPresent(PREFIX_GAME_NAME)
                || argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, GameAddCommand.MESSAGE_USAGE));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_GAME_NAME, PREFIX_USERNAME);
        Index index = ParserUtil.parseIndex(argMultimap.getPreamble());
        GameName gameName = ParserUtil.parseGameName(argMultimap.getValue(PREFIX_GAME_NAME).get());
        Username username = argMultimap.getValue(PREFIX_USERNAME)
                .map(unchecked(ParserUtil::parseUsername)).getOrElse(Username.EMPTY);
        Game game = new Game(gameName, username);

        return new GameAddCommand(index, game);
    }
}
