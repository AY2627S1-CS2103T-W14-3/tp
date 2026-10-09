package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;

/**
 * Tests the required index, input boundaries, and error messages for deleting friends.
 */
public class DeleteCommandParserTest {

    private final DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_FRIEND));
        assertParseSuccess(parser, "  1 ", new DeleteCommand(INDEX_FIRST_FRIEND));
        assertParseSuccess(parser, "0".repeat(100) + "1", new DeleteCommand(INDEX_FIRST_FRIEND));
        assertParseSuccess(parser, String.valueOf(Integer.MAX_VALUE),
                new DeleteCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(parser, "", ParserUtil.MESSAGE_MISSING_INDEX);
        assertParseFailure(parser, " \t\n ", ParserUtil.MESSAGE_MISSING_INDEX);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String[] invalidArgs = {"a", "0", "-1", "+1", "1.5", "1 2", "1 n/Melody",
            "2147483648", "1".repeat(50)};
        for (String args : invalidArgs) {
            assertParseFailure(parser, args, ParserUtil.MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
