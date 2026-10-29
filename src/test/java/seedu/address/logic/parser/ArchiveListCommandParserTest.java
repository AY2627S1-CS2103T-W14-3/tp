package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ArchiveListCommand;
import seedu.address.model.friend.GameNameMatchesPredicate;
import seedu.address.model.game.GameName;

public class ArchiveListCommandParserTest {

    private final ArchiveListCommandParser parser = new ArchiveListCommandParser();

    @Test
    public void parse_noArguments_returnsUnfilteredArchiveListCommand() {
        assertParseSuccess(parser, "   ", new ArchiveListCommand());
    }

    @Test
    public void parse_validGameName_returnsFilteredArchiveListCommand() {
        ArchiveListCommand expectedCommand = new ArchiveListCommand(
                new GameNameMatchesPredicate(new GameName("Stardew Valley")));

        assertParseSuccess(parser, " g/Stardew Valley", expectedCommand);
        assertParseSuccess(parser, "  g/  Stardew Valley  ", expectedCommand);
    }

    @Test
    public void parse_missingGamePrefix_throwsParseException() {
        assertParseFailure(parser, " Valorant",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_nameSearchOrPositionalArguments_throwsParseException() {
        for (String args : new String[]{" Alice", " n/Alice", " Alice g/Valorant"}) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveListCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_emptyGameName_throwsParseException() {
        assertParseFailure(parser, " g/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateGamePrefix_throwsParseException() {
        assertParseFailure(parser, " g/Valorant g/Minecraft",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GAME));
        assertParseFailure(parser, " g/Valorant g/",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GAME));
    }

    @Test
    public void parse_invalidGameName_throwsParseException() {
        assertParseFailure(parser, " g/!!!", GameName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_quotedGameNameWithPrefix_success() {
        assertParseSuccess(parser, " g/\"Version g/2\"",
                new ArchiveListCommand(new GameNameMatchesPredicate(new GameName("Version g/2"))));
    }
}
