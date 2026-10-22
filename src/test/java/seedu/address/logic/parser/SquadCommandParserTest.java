package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.SquadCommand;
import seedu.address.model.game.GameName;

public class SquadCommandParserTest {
    private final SquadCommandParser parser = new SquadCommandParser();

    @Test
    public void parse_validArguments_acceptsEitherOrderAndWhitespace() throws Exception {
        SquadCommand expected = new SquadCommand(new GameName("Stardew Valley"), 3);
        assertParseSuccess(parser, " g/Stardew Valley n/3", expected);
        assertParseSuccess(parser, " n/3 g/ Stardew Valley  ", expected);
        assertParseSuccess(parser, " g/\tStardew Valley\t n/\t3\t", expected);
        assertEquals(expected, new GameMatesParser().parseCommand("squad g/Stardew Valley n/3"));
        assertParseSuccess(parser, " g/" + "a".repeat(50) + " n/20",
                new SquadCommand(new GameName("a".repeat(50)), 20));
        assertParseSuccess(parser, " g/Valorant n/1", new SquadCommand(new GameName("Valorant"), 1));
        assertParseSuccess(parser, " g/Valorant n/２", new SquadCommand(new GameName("Valorant"), 2));
    }

    @Test
    public void parse_missingOrExtraArguments_reportsUsage() {
        for (String input : new String[]{"", "g/Valorant", "n/3", "g/ n/3", "g/Valorant n/",
            "g/   n/3", "g/Valorant n/  ", "extra g/Valorant n/3", "x/extra g/Valorant n/3",
            "G/Valorant n/3"}) {
            assertParseFailure(parser, " " + input, SquadCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_invalidSize_reportsSizeError() {
        for (String size : new String[]{"0", "21", "-1", "+2", "2.5", "abc", "2147483648",
            "1 extra", "2\u00003", "3 x/extra"}) {
            assertParseFailure(parser, " g/Valorant n/" + size, SquadCommand.MESSAGE_INVALID_SIZE);
        }
    }

    @Test
    public void parse_invalidGame_reportsGameError() {
        for (String game : new String[]{"!!!", "a".repeat(51), "a\u0000b", "a\nb"}) {
            assertParseFailure(parser, " g/" + game + " n/3",
                    String.format(SquadCommand.MESSAGE_INVALID_GAME, game));
        }
    }

    @Test
    public void parse_duplicatePrefixes_rejectedEvenForIdenticalValues() {
        assertParseFailure(parser, " g/Valorant g/Valorant n/3",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GAME));
        assertParseFailure(parser, " g/Valorant n/3 n/3",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
    }
}
