package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.GameEditCommand;
import seedu.address.logic.commands.GameEditCommand.EditGameDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;

public class GameEditCommandParserTest {
    private final GameEditCommandParser parser = new GameEditCommandParser();

    @Test
    public void parse_quotedSelector_supportsEveryPrefixAndLiteralQuotes() {
        for (String name : new String[]{"PUBG g/Game", "PUBG n/Game", "PUBG u/Game", "PUBG m/Game",
            "\"Quoted Game\"", "Game \\ Path"}) {
            String quoted = name.replace("\\", "\\\\").replace("\"", "\\\"");
            assertParseSuccess(parser, " 1 g/\"" + quoted + "\" u/player",
                    new GameEditCommand(Index.fromOneBased(1), new GameName(name),
                            new EditGameDescriptor(null, new Username("player"), null)));
        }
    }

    @Test
    public void parse_quotedValues_keepPrefixesLiteralAndDecodeEscapes() throws Exception {
        GameEditCommand expected = new GameEditCommand(Index.myself(), new GameName("PUBG n/Game"),
                new EditGameDescriptor(new GameName("New g/Game n/Name u/User m/Meta"),
                        new Username("player n/name"), ParserUtil.parseMetas(Set.of("Role:uses m/value"))));
        assertParseSuccess(parser, " myself g/\"PUBG n/Game\" n/\"New g/Game n/Name u/User m/Meta\""
                + " u/\"player n/name\" m/\"Role:uses m/value\"", expected);
        assertParseSuccess(parser, " 2 g/Valorant n/\"The \\\"Game\\\" \\\\ Edition\"",
                command(new GameName("The \"Game\" \\ Edition"), null, null));
        assertParseSuccess(parser, " 2 g/Valorant n/Players' Game",
                command(new GameName("Players' Game"), null, null));
    }

    @Test
    public void parse_quotedValues_stillValidateFieldsAndDuplicatePrefixes() {
        assertParseFailure(parser, " 2 g/\"Valorant\"", GameEditCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, " 2 g/Valorant n/\"\"", GameName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 2 g/\"Valorant\" g/\"Other\" u/player",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GAME_NAME));
        assertParseFailure(parser, " 2 g/Valorant m/\"Role:Tank\" m/\"role:Tank\"", Meta.MESSAGE_DUPLICATE_KEY);
    }

    @Test
    public void parse_malformedQuotedValues_failure() {
        for (String input : new String[]{" 2 g/\"PUBG n/Game u/player", " 2 g/\"PUBG\"extra u/player",
            " 2 g/Valorant n/\"Game\\q\"", " 2 g/Valorant u/\"player\\", " 2 g/\"PUBG\" extra u/player"}) {
            assertParseFailure(parser, input, ArgumentTokenizer.MESSAGE_INVALID_QUOTED_VALUE);
        }
    }

    @Test
    public void parse_validFieldsAndRouting_success() throws Exception {
        GameEditCommand expected = new GameEditCommand(Index.myself(), new GameName("Stardew Valley"),
                new EditGameDescriptor(new GameName("New Game"), new Username("player"),
                        ParserUtil.parseMetas(Set.of("Role:Healer", "Region:Asia:SG"))));
        String input = " MySeLf m/Role:Healer n/New Game g/ Stardew Valley u/player m/Region:Asia:SG";
        assertParseSuccess(parser, input, expected);
        assertEquals(expected, new GameMatesParser().parseCommand("gameedit" + input));
        assertParseSuccess(parser, " 2 g/Valorant u/player", command(null, new Username("player"), null));
        assertParseSuccess(parser, " 2 g/Valorant n/VALORANT", command(new GameName("VALORANT"), null, null));
        assertParseSuccess(parser, " 2 g/Valorant m/", command(null, null, Set.of()));
        assertParseSuccess(parser, " 2 g/Valorant u/", command(null, Username.EMPTY, null));
        assertParseSuccess(parser, " 2 g/Valorant m/ role : Healer ",
                command(null, null, Set.of(new Meta(new MetaKey("role"), new MetaValue("Healer")))));
    }

    @Test
    public void parse_missingOrInvalidTargetAndSelector_failure() {
        for (String input : new String[]{"", " g/Valorant u/player", " 2 u/player"}) {
            assertParseFailure(parser, input,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, GameEditCommand.MESSAGE_USAGE));
        }
        for (String target : new String[]{"0", "-1", "2147483648", "2 extra", "myself extra"}) {
            assertParseFailure(parser, " " + target + " g/Valorant u/player", ParserUtil.MESSAGE_INVALID_INDEX);
        }
        assertParseFailure(parser, " 2 g/ u/player", GameName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 2 g/Valorant", GameEditCommand.MESSAGE_NOT_EDITED);
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_invalidFieldsAndMetadata_failure() {
        for (String field : new String[]{"n/", "n/!!!", "n/" + "a".repeat(51)}) {
            assertParseFailure(parser, " 2 g/Valorant " + field, GameName.MESSAGE_CONSTRAINTS);
        }
        for (String value : new String[]{"!!!", "a".repeat(51)}) {
            assertParseFailure(parser, " 2 g/Valorant u/" + value, Username.MESSAGE_CONSTRAINTS);
        }
        for (String pairs : new String[]{"m/Role:Healer m/role:Healer", "m/Role:Healer m/ ROLE :Tank"}) {
            assertParseFailure(parser, " 2 g/Valorant " + pairs, Meta.MESSAGE_DUPLICATE_KEY);
        }
        for (String pairs : new String[]{"m/Role", "m/ m/Role:Healer", "m/Role:Healer m/", "m/ m/"}) {
            assertParseFailure(parser, " 2 g/Valorant " + pairs, Meta.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, " 2 g/Valorant m/:value", MetaKey.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 2 g/Valorant m/role:", MetaValue.MESSAGE_CONSTRAINTS);
        assertThrows(ParseException.class, () -> parser.parse(" 2 g/Valorant m/role:a\u0000b"));
    }

    @Test
    public void parse_duplicateSingletons_failureEvenForIdenticalValues() {
        assertParseFailure(parser, " 2 g/Valorant g/Valorant u/player",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GAME_NAME));
        assertParseFailure(parser, " 2 g/Valorant n/New n/New",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
        assertParseFailure(parser, " 2 g/Valorant u/player u/player",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_USERNAME));
    }

    private GameEditCommand command(GameName name, Username username, Set<Meta> metas) {
        return new GameEditCommand(Index.fromOneBased(2), new GameName("Valorant"),
                new EditGameDescriptor(name, username, metas));
    }
}
