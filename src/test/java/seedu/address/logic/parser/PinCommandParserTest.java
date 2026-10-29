package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.PinCommand;

/** Tests parsing arguments for {@link PinCommand}. */
public class PinCommandParserTest {

    private final PinCommandParser parser = new PinCommandParser();

    @Test
    public void parse_validArgs_returnsPinCommand() {
        assertParseSuccess(parser, "1", new PinCommand(INDEX_FIRST_FRIEND));
        assertParseSuccess(parser, "  1 ", new PinCommand(INDEX_FIRST_FRIEND));
        assertParseSuccess(parser, String.valueOf(Integer.MAX_VALUE),
                new PinCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        for (String args : new String[] {"", " ", "myself", "0", "-1", "+1", "1.5", "1 2", "2147483648"}) {
            String expectedMessage = args.trim().isEmpty()
                    ? ParserUtil.MESSAGE_MISSING_INDEX : String.format(ParserUtil.MESSAGE_INVALID_INDEX, args.trim());
            assertParseFailure(parser, args, expectedMessage);
        }
    }

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
