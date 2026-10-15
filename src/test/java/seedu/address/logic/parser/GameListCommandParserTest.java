package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.GameListCommand;
import seedu.address.model.game.GameName;

public class GameListCommandParserTest {

    private final GameListCommandParser parser = new GameListCommandParser();

    @Test
    public void parse_validIndices_success() {
        assertParseSuccess(parser, " 1 ", new GameListCommand(INDEX_FIRST_FRIEND));
        assertParseSuccess(parser, "\t 2 \n", new GameListCommand(Index.fromOneBased(2)));
        assertParseSuccess(parser, "2147483647", new GameListCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_optionalGameFilter_success() {
        GameListCommand expected = new GameListCommand(INDEX_FIRST_FRIEND, new GameName("Stardew Valley"));
        assertParseSuccess(parser, "1 g/Stardew Valley", expected);
        assertParseSuccess(parser, " \t1 \tg/  Stardew Valley  \n", expected);
        assertParseSuccess(parser, "\n1\ng/  Stardew Valley  \n", expected);
        assertParseSuccess(parser, "1 g/stardew valley", expected);
        assertParseSuccess(parser, "2 g/minecraft",
                new GameListCommand(Index.fromOneBased(2), new GameName("Minecraft")));
    }

    @Test
    public void parse_validGameNameBoundaries_success() {
        for (String name : new String[]{"A", "1", "a".repeat(50), "Tom Clancy's Rainbow Six: Siege", "游戏"}) {
            assertParseSuccess(parser, "1 g/" + name,
                    new GameListCommand(INDEX_FIRST_FRIEND, new GameName(name)));
        }
    }

    @Test
    public void parse_missingIndex_failure() {
        for (String input : new String[]{"", " \t\n ", "g/Valorant", " g/Valorant", "\tg/Valorant"}) {
            assertParseFailure(parser, input, GameListCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_invalidIndex_failure() {
        for (String index : new String[]{"abc", "0", "-1", "+1", "1.5", "2147483648", "999999999999999"}) {
            assertParseFailure(parser, index, GameListCommand.MESSAGE_INVALID_INDEX);
            assertParseFailure(parser, index + " g/Valorant", GameListCommand.MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_extraOrUnknownParameters_failure() {
        for (String input : new String[]{"1 2", "1 extra", "1 extra g/Valorant", "1 x/Valorant",
            "1 g/Valorant x/foo", "1 g/Valorant\tp/foo", "1 g/Valorant unknown/value",
            "1 n/Alice g/Valorant", "1 /foo", "1 G/Valorant"}) {
            assertParseFailure(parser, input, GameListCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_repeatedGameFilter_failure() {
        for (String input : new String[]{"1 g/Valorant g/Minecraft", "1 g/Valorant g/Valorant",
            "1 g/ g/Valorant", "1 g/Valorant\tg/Valorant", "1 g/Valorant\ng/Valorant", "1 g/ g/"}) {
            assertParseFailure(parser, input, GameListCommand.MESSAGE_REPEATED_GAME);
        }
    }

    @Test
    public void parse_invalidGameName_failure() {
        for (String name : new String[]{"", "   ", "!!!", "a".repeat(51), "Game\tName", "Game\nName",
            "a\u007fb", "\u0000Game", "Game\u0000"}) {
            assertParseFailure(parser, "1 g/" + name,
                    String.format(GameListCommand.MESSAGE_INVALID_GAME_NAME, name.strip()));
        }
    }
}
