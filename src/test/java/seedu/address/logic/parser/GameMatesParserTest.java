package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.ArchiveListCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditFriendDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.GameListCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.GameNameMatchesPredicate;
import seedu.address.model.friend.NameContainsKeywordsPredicate;
import seedu.address.model.game.GameName;
import seedu.address.testutil.EditFriendDescriptorBuilder;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.FriendUtil;

public class GameMatesParserTest {

    private final GameMatesParser parser = new GameMatesParser();

    @Test
    public void parseCommand_add() throws Exception {
        Friend friend = new FriendBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(FriendUtil.getAddCommand(friend));
        assertEquals(new AddCommand(friend), command);
    }

    @Test
    public void parseCommand_archiveAndUnarchive_preserveIndexAndCommandType() throws Exception {
        ArchiveCommand archive = new ArchiveCommand(Index.fromOneBased(2));
        UnarchiveCommand unarchive = new UnarchiveCommand(Index.fromOneBased(2));
        assertEquals(archive, parser.parseCommand("archive   2 "));
        assertEquals(unarchive, parser.parseCommand("unarchive   2 "));
        assertNotEquals(archive, parser.parseCommand("archive 1"));
        assertNotEquals(unarchive, parser.parseCommand("unarchive 1"));
        assertNotEquals(archive, unarchive);
        assertNotEquals(unarchive, archive);
        assertNotEquals(archive, null);
        assertNotEquals(unarchive, null);
        assertEquals(archive, archive);
        assertEquals(unarchive, unarchive);
    }

    @Test
    public void parseCommand_archiveList_preservesOptionalGameFilter() throws Exception {
        ArchiveListCommand all = new ArchiveListCommand();
        ArchiveListCommand filtered = new ArchiveListCommand(
                new GameNameMatchesPredicate(new GameName("Stardew Valley")));
        assertEquals(all, parser.parseCommand("archivelist"));
        assertEquals(all, parser.parseCommand("archivelist   "));
        assertEquals(filtered, parser.parseCommand("archivelist   g/  Stardew Valley "));
        assertNotEquals(all, filtered);
        assertNotEquals(filtered, parser.parseCommand("archivelist g/Valorant"));
        assertNotEquals(filtered, parser.parseCommand("find Alice Bob"));
        assertNotEquals(filtered, null);
        assertEquals(filtered, filtered);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_FRIEND.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_FRIEND), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Friend friend = new FriendBuilder().build();
        EditFriendDescriptor descriptor = new EditFriendDescriptorBuilder(friend).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_FRIEND.getOneBased() + " " + FriendUtil.getEditFriendDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_FRIEND, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_gameList() throws Exception {
        assertEquals(new GameListCommand(INDEX_FIRST_FRIEND), parser.parseCommand("gamelist 1"));
        assertEquals(new GameListCommand(INDEX_FIRST_FRIEND, new GameName("Stardew Valley")),
                parser.parseCommand("gamelist 1 g/Stardew Valley"));
        assertThrows(ParseException.class, GameListCommand.MESSAGE_USAGE, () -> parser.parseCommand("gamelist"));
    }

    @Test
    public void parseCommand_overflowingIndex_reportsSupportedRange() {
        for (String index : List.of("2147483648", "9223372036854775808", "9".repeat(1000))) {
            for (String command : List.of("delete " + index, "edit " + index + " n/Alice",
                    "gameadd " + index + " g/Valorant u/Alice", "gamedel " + index + " g/Valorant")) {
                assertThrows(ParseException.class,
                        "Index must be a positive integer between 1 and 2147483647 (inclusive).", ()
                            -> parser.parseCommand(command));
            }
            for (String command : List.of("gamelist " + index, "gamelist " + index + " g/Valorant")) {
                assertThrows(ParseException.class,
                        "Invalid friend index. Enter a positive integer between 1 and 2147483647 (inclusive).", ()
                            -> parser.parseCommand(command));
            }
        }
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertEquals(new ListCommand(), parser.parseCommand(ListCommand.COMMAND_WORD));
        assertEquals(new ListCommand(new GameNameMatchesPredicate(new GameName("Valorant"))),
                parser.parseCommand(ListCommand.COMMAND_WORD + " g/Valorant"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
