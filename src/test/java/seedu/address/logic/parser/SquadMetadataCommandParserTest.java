package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SquadCommand;
import seedu.address.model.game.GameName;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;

public class SquadMetadataCommandParserTest {
    private final SquadCommandParser parser = new SquadCommandParser();

    @Test
    public void parse_conditionsAnyOrderAndRouting_success() throws Exception {
        SquadCommand expected = new SquadCommand(new GameName("Stardew Valley"),
                ParserUtil.parseMetas(List.of("Role:Healer", "Region:Asia:SG")), 3);
        String args = " m/ Role : Healer  n/3 g/Stardew Valley m/Region:Asia:SG";
        assertParseSuccess(parser, args, expected);
        assertEquals(expected, new GameMatesParser().parseCommand("squad" + args));
        assertParseSuccess(parser, " g/Valorant n/1 m/Role:Healer",
                new SquadCommand(new GameName("Valorant"), ParserUtil.parseMetas(List.of("Role:Healer")), 1));
        assertParseSuccess(parser, " g/Valorant n/3", new SquadCommand(new GameName("Valorant"), 3));
    }

    @Test
    public void parse_quotedPrefixes_preserveGameAndMetadataValues() throws Exception {
        SquadCommand expected = new SquadCommand(new GameName("Version n/2"),
                ParserUtil.parseMetas(List.of("Note:Prefer n/3")), 2);
        assertParseSuccess(parser, " g/\"Version n/2\" n/2 m/\"Note:Prefer n/3\"", expected);
        assertParseFailure(parser, " g/Valorant n/2 m/\"Note:Prefer n/3",
                ArgumentTokenizer.MESSAGE_UNCLOSED_QUOTE);
        assertParseFailure(parser, " g/Valorant n/1 m/\"Note:Prefer n/3\" m/\"note:Prefer n/3\"",
                Meta.MESSAGE_DUPLICATE_KEY);
    }

    @Test
    public void parse_duplicateNormalizedKeys_rejectedEvenForSameValues() {
        for (String pairs : new String[]{"m/Role:Healer m/role:Healer", "m/Role:Healer m/ ROLE :Tank"}) {
            assertParseFailure(parser, " g/Valorant n/3 " + pairs, Meta.MESSAGE_DUPLICATE_KEY);
        }
    }

    @Test
    public void parse_emptyOrMalformedMetadata_rejectedAlsoForSizeOne() {
        for (String pair : new String[]{"", "Role", "   ", "Role:Healer m/"}) {
            assertParseFailure(parser, " g/Valorant n/1 m/" + pair, Meta.MESSAGE_CONSTRAINTS);
        }
        for (String key : new String[]{"", "!!!", "a".repeat(51), "a\u0000b"}) {
            assertParseFailure(parser, " g/Valorant n/3 m/" + key + ":Healer", MetaKey.MESSAGE_CONSTRAINTS);
        }
        for (String value : new String[]{"", "!!!", "a".repeat(201), "a\u0000b"}) {
            assertParseFailure(parser, " g/Valorant n/3 m/Role:" + value, MetaValue.MESSAGE_CONSTRAINTS);
        }
    }
}
