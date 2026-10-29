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
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;
import seedu.address.testutil.GameBuilder;

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
        for (String input : new String[]{"", " 1", " 1 u/player", " g/Valorant u/player",
            "myself u/player", " 1 m/Role:Support",
            " myself u/player m/Role:Support", " g/Valorant u/player m/Role:Support"}) {
            assertParseFailure(parser, input,
                    String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, GameAddCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_omittedUsername_returnsGameWithEmptyUsername() {
        assertParseSuccess(parser, " 1 g/Valorant",
                new GameAddCommand(Index.fromOneBased(1), new Game(new GameName("Valorant"), Username.EMPTY)));
    }

    @Test
    public void parse_metadataWithoutUsername_success() {
        Game game = new GameBuilder().withGameName("Valorant").withoutUsername()
                .addMeta("Role", "Support").addMeta("Level", "20").build();
        for (String selector : new String[]{"1", "MySeLf"}) {
            Index index = selector.equals("1") ? Index.fromOneBased(1) : Index.myself();
            GameAddCommand expected = new GameAddCommand(index, game);
            assertParseSuccess(parser, " " + selector + " g/Valorant m/Role:Support m/Level:20", expected);
            assertParseSuccess(parser, " " + selector + " m/Role:Support g/Valorant m/Level:20", expected);
            assertParseFailure(parser, " " + selector + " g/Valorant u/ m/Role:Support",
                    Username.MESSAGE_CONSTRAINTS);
            assertParseFailure(parser, " " + selector + " g/Valorant m/Role:Support m/role:Healer",
                    Meta.MESSAGE_DUPLICATE_KEY);
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
        assertParseFailure(parser, " 1 m/Role:Support g/Valorant g/Minecraft u/player",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_GAME_NAME));
        assertParseFailure(parser, " myself g/Valorant u/player m/Role:Support u/other",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_USERNAME));
    }

    @Test
    public void parse_metadata_success() throws Exception {
        Game game = new GameBuilder().withGameName("Valorant").withUsername("player")
                .addMeta("Role", "Support:Mid").addMeta("Level", "20").build();
        GameAddCommand expected = new GameAddCommand(Index.fromOneBased(1), game);
        assertParseSuccess(parser, " 1 g/Valorant u/player m/ Role : Support:Mid   m/Level:20", expected);
        assertParseSuccess(parser, " 1 m/Role:Support:Mid u/player m/Level:20 g/Valorant", expected);
        assertEquals(expected, new GameMatesParser()
                .parseCommand("gameadd 1 g/Valorant u/player m/Role:Support:Mid m/Level:20"));
        assertParseSuccess(parser, " myself m/Role:Support:Mid g/Valorant m/Level:20 u/player",
                new GameAddCommand(Index.myself(), game));
    }

    @Test
    public void parse_metadataAtLengthLimits_success() {
        Game game = new GameBuilder().withGameName("Valorant").withUsername("player")
                .addMeta("k".repeat(50), "v".repeat(200)).build();
        assertParseSuccess(parser, " 1 g/Valorant u/player m/" + "k".repeat(50) + ":" + "v".repeat(200),
                new GameAddCommand(Index.fromOneBased(1), game));
    }

    @Test
    public void parse_invalidMetadata_failure() {
        for (String invalid : new String[]{"", "Role", "Role=Support"}) {
            assertParseFailure(parser, " 1 g/Valorant u/player m/" + invalid, Meta.MESSAGE_CONSTRAINTS);
        }
        for (String invalid : new String[]{"", "   ", "\u2003", "!!!", "k".repeat(51), "a\u0000b", "a\tb", "a\nb"}) {
            assertParseFailure(parser, " 1 g/Valorant u/player m/" + invalid + ":Support",
                    MetaKey.MESSAGE_CONSTRAINTS);
        }
        for (String invalid : new String[]{"", "   ", "\u2003", "!!!", ":", "v".repeat(201),
            "a\u0000b", "a\tb", "a\nb"}) {
            assertParseFailure(parser, " myself g/Valorant u/player m/Role:" + invalid,
                    MetaValue.MESSAGE_CONSTRAINTS);
        }
        assertParseFailure(parser, " 1 g/Valorant u/player m/Role:Support m/invalid", Meta.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateMetadataKeys_failure() {
        for (String duplicate : new String[]{"Role:Support", "Role:Healer", " role :Support", "ROLE:Support",
            "\u2003Role\u2003:Support", "Level:20 m/role:Healer"}) {
            assertParseFailure(parser, " 1 g/Valorant u/player m/Role:Support m/" + duplicate,
                    Meta.MESSAGE_DUPLICATE_KEY);
            assertParseFailure(parser, " myself g/Valorant u/player m/Role:Support m/" + duplicate,
                    Meta.MESSAGE_DUPLICATE_KEY);
        }
    }

    @Test
    public void parse_emptyMetadataFlagAmongValidPairs_failure() {
        for (String metadata : new String[]{"m/ m/Role:Support", "m/Role:Support m/",
            "m/Role:Support m/ m/Level:20", "m/   ", "m/:"}) {
            assertParseFailure(parser, " 1 g/Valorant u/player " + metadata,
                    metadata.equals("m/:") ? MetaKey.MESSAGE_CONSTRAINTS : Meta.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_metadataPreservesValueColonsAndLiteralSlashes_success() {
        for (String value : new String[]{":Support", "Support:", "Support::Mid", "https://example.com/a:b",
            "Support/Healer", "team/m/member", "角色"}) {
            Game game = new GameBuilder().withGameName("Valorant").withUsername("player")
                    .addMeta("Role", value).build();
            assertParseSuccess(parser, " 1 g/Valorant u/player m/Role:" + value,
                    new GameAddCommand(Index.fromOneBased(1), game));
        }
    }

    @Test
    public void parse_metadataKeysWithSameValuesOrDistinctInternalSpaces_success() {
        Game game = new GameBuilder().withGameName("Valorant").withUsername("player")
                .addMeta("Role", "Support").addMeta("Style", "Support")
                .addMeta("Play style", "Casual").addMeta("Play  style", "Competitive").build();
        assertParseSuccess(parser, " 1 g/Valorant u/player m/Role:Support m/Style:Support"
                + " m/Play style:Casual m/Play  style:Competitive", new GameAddCommand(Index.fromOneBased(1), game));
    }

    @Test
    public void parse_quotedValuesWithPrefix_success() {
        assertParseSuccess(parser, " 1 g/\"Version u/2\" u/player", new GameAddCommand(Index.fromOneBased(1),
                new Game(new GameName("Version u/2"), new Username("player"))));
    }
}
