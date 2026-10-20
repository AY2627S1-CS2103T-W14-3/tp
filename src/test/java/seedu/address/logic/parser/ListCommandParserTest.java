package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ORDER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SORT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ListCommand;
import seedu.address.model.friend.FriendSortCriterion;
import seedu.address.model.friend.GameNameMatchesPredicate;
import seedu.address.model.game.GameName;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_noArguments_returnsUnfilteredListCommand() {
        assertParseSuccess(parser, "   ", new ListCommand());
    }

    @Test
    public void parse_validGameName_returnsFilteredListCommand() {
        ListCommand expectedCommand = new ListCommand(
                new GameNameMatchesPredicate(new GameName("Stardew Valley")));

        assertParseSuccess(parser, " g/Stardew Valley", expectedCommand);
        assertParseSuccess(parser, "  g/  Stardew Valley  ", expectedCommand);
    }

    @Test
    public void parse_validSort_returnsSortedListCommand() {
        assertParseSuccess(parser, " s/name", new ListCommand(FriendSortCriterion.NAME));
        assertParseSuccess(parser, " s/GAME", new ListCommand(FriendSortCriterion.GAME));
        assertParseSuccess(parser, " g/Stardew Valley s/name", new ListCommand(
                new GameNameMatchesPredicate(new GameName("Stardew Valley")), FriendSortCriterion.NAME));
        assertParseSuccess(parser, " s/game g/Stardew Valley", new ListCommand(
                new GameNameMatchesPredicate(new GameName("Stardew Valley")), FriendSortCriterion.GAME));
        assertParseSuccess(parser, " s/name o/DESC", new ListCommand(FriendSortCriterion.NAME, true));
        assertParseSuccess(parser, " g/Stardew Valley s/game o/asc", new ListCommand(
                new GameNameMatchesPredicate(new GameName("Stardew Valley")), FriendSortCriterion.GAME, false));
    }

    @Test
    public void parse_missingGamePrefix_throwsParseException() {
        assertParseFailure(parser, " Valorant",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyGameName_throwsParseException() {
        assertParseFailure(parser, " g/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateGamePrefix_throwsParseException() {
        assertParseFailure(parser, " g/Valorant g/Minecraft",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GAME));
        assertParseFailure(parser, " g/Valorant g/",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GAME));
    }

    @Test
    public void parse_invalidSort_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " s/", expectedMessage);
        assertParseFailure(parser, " s/age", expectedMessage);
        assertParseFailure(parser, " sort/name", expectedMessage);
    }

    @Test
    public void parse_invalidOrder_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " o/desc", expectedMessage);
        assertParseFailure(parser, " s/name o/", expectedMessage);
        assertParseFailure(parser, " s/name o/reverse", expectedMessage);
    }

    @Test
    public void parse_duplicateSortPrefix_throwsParseException() {
        assertParseFailure(parser, " s/name s/game",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SORT));
        assertParseFailure(parser, " s/name s/",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SORT));
        assertParseFailure(parser, " s/name o/asc o/desc",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ORDER));
    }

    @Test
    public void parse_invalidGameName_throwsParseException() {
        assertParseFailure(parser, " g/!!!", GameName.MESSAGE_CONSTRAINTS);
    }
}
