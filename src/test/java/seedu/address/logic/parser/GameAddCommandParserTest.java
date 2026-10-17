package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.GameAddCommand;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;

/**
 * Tests gameadd syntax, trimming, and input validation.
 */
public class GameAddCommandParserTest {
    private final GameAddCommandParser parser = new GameAddCommandParser();

    @Test
    public void parse_myselfSelector_success() {
        GameAddCommand expected = new GameAddCommand(Index.myself(),
                new Game(new GameName("Valorant"), new Username("player")));
        assertParseSuccess(parser, "  MySeLf   u/player g/Valorant", expected);
    }

    @Test
    public void parse_validArguments_success() throws Exception {
        GameAddCommand expected = new GameAddCommand(Index.fromOneBased(2),
                new Game(new GameName("Valorant"), new Username("benergy10")));
        assertParseSuccess(parser, " 2 g/ Valorant   u/ benergy10   ", expected);
        assertParseSuccess(parser, " 2 u/benergy10 g/Valorant", expected);
        assertEquals(expected, new GameMatesParser().parseCommand("gameadd 2 g/Valorant u/benergy10"));
        assertParseSuccess(parser, " 1 g/" + "a".repeat(50) + " u/" + "b".repeat(50),
                new GameAddCommand(Index.fromOneBased(1),
                        new Game(new GameName("a".repeat(50)), new Username("b".repeat(50)))));
    }

    @Test
    public void parse_missingParameters_failure() {
        for (String input : new String[]{"", " 1", " 1 g/Valorant", " 1 u/player", " g/Valorant u/player",
            "myself g/Valorant", "myself u/player"}) {
            assertParseFailure(parser, input,
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, GameAddCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String index : new String[]{"zero", "0", "-1", "1.5", "2147483648", "1 extra", "myself 1"}) {
            assertParseFailure(parser, " " + index + " g/Valorant u/player", ParserUtil.MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_invalidGameOrUsername_failure() {
        for (String invalid : new String[]{"", "!!!", "a".repeat(51), "a\u0000b"}) {
            assertParseFailure(parser, " 1 g/" + invalid + " u/player",
                    GameName.MESSAGE_CONSTRAINTS);
            assertParseFailure(parser, " 1 g/Valorant u/" + invalid,
                    Username.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, "myself g/ u/player", GameName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "myself g/Valorant u/", Username.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefixes_failure() {
        assertParseFailure(parser, " 1 g/Valorant g/Minecraft u/player",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GAME_NAME));
        assertParseFailure(parser, " myself g/Valorant u/player u/other",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_USERNAME));
    }
}
