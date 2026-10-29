package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.GameDeleteCommand;
import seedu.address.model.game.GameName;

public class GameDeleteCommandParserTest {
    private final GameDeleteCommandParser parser = new GameDeleteCommandParser();

    @Test
    public void parse_myselfSelector_success() {
        GameDeleteCommand expected = new GameDeleteCommand(Index.myself(), new GameName("Minecraft"));
        assertParseSuccess(parser, "  MySeLf   g/Minecraft", expected);
    }

    @Test
    public void parse_validArguments_success() throws Exception {
        GameDeleteCommand expected = new GameDeleteCommand(Index.fromOneBased(2), new GameName("Minecraft"));
        assertParseSuccess(parser, " 2 g/Minecraft ", expected);
        assertParseSuccess(parser, "2 g/ Minecraft", expected);
        assertParseSuccess(parser, "2 g/MINECRAFT", new GameDeleteCommand(Index.fromOneBased(2),
                new GameName("MINECRAFT")));
    }

    @Test
    public void parse_missingParameters_failure() {
        for (String input : new String[]{"", "1", "g/Minecraft", "1 g/", "myself", "myself g/"}) {
            assertParseFailure(parser, input,
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, GameDeleteCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_missingIndexWithGamePrefix_failure() {
        // The leading whitespace lets the tokenizer recognize g/ without an index before it.
        for (String input : new String[]{" g/Minecraft", " \t g/Minecraft"}) {
            assertParseFailure(parser, input,
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, GameDeleteCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String index : new String[]{"zero", "0", "-1", "1.5", "2147483648", "1 extra", "myself 1"}) {
            assertParseFailure(parser, index + " g/Minecraft", String.format(ParserUtil.MESSAGE_INVALID_INDEX, index));
        }
    }

    @Test
    public void parse_invalidGameName_failure() {
        for (String invalid : new String[]{"!!!", "a".repeat(51), "a\u0000b"}) {
            assertParseFailure(parser, "1 g/" + invalid, GameName.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_duplicateGamePrefix_failure() {
        assertParseFailure(parser, "myself g/Minecraft g/Valorant",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GAME_NAME));
    }

    @Test
    public void parse_quotedGameNameWithPrefix_success() {
        assertParseSuccess(parser, " 1 g/\"Version g/2\"",
                new GameDeleteCommand(Index.fromOneBased(1), new GameName("Version g/2")));
    }
}
