package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.friend.Remark;

public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validArgs_returnsRemarkCommand() {
        RemarkCommand expected = new RemarkCommand(INDEX_FIRST_FRIEND, new Remark("Likes to swim."));
        assertParseSuccess(parser, " 1 r/Likes to swim.", expected);
        assertParseSuccess(parser, " \t 1 \t r/  Likes to swim.  ", expected);
        assertParseSuccess(parser, " 1 r/玩家: likes n/Alice and e/mail", new RemarkCommand(INDEX_FIRST_FRIEND,
                new Remark("玩家: likes n/Alice and e/mail")));
        assertParseSuccess(parser, " " + Integer.MAX_VALUE + " r/text",
                new RemarkCommand(Index.fromOneBased(Integer.MAX_VALUE), new Remark("text")));
    }

    @Test
    public void parse_emptyOrAbsentRemark_returnsClearRemarkCommand() {
        RemarkCommand expected = new RemarkCommand(INDEX_FIRST_FRIEND, Remark.EMPTY);
        assertParseSuccess(parser, " 1 r/", expected);
        assertParseSuccess(parser, " 1 r/   ", expected);
        assertParseSuccess(parser, " 1", expected);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        for (String args : new String[] {"", " r/text", " 0 r/text", " -1 r/text", " +1 r/text", " a r/text",
            " 1.5 r/text", " 1 2 r/text", " 2147483648 r/text", " 1 text"}) {
            assertParseFailure(parser, args, expectedMessage);
        }
    }

    @Test
    public void parse_duplicateRemarkPrefix_throwsParseException() {
        assertParseFailure(parser, " 1 r/first r/second",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
