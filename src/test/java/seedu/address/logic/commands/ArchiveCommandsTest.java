package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.GameMatesParser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.FriendListScope;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

/** Exercises archive commands with real parsing, filtering and storage. */
public class ArchiveCommandsTest {
    @TempDir
    public Path tempDir;

    private final GameMatesParser parser = new GameMatesParser();
    private final ModelManager model = new ModelManager();
    private final Friend alice = new FriendBuilder().withName("Alice Tan").withGames(
            new GameBuilder().withGameName("Valorant").withUsername("alice123")
                    .addMeta("Role", "Support").build()).build();
    private final Friend bob = new FriendBuilder().withName("Bob Lim").build();

    @Test
    public void archive_restartUnarchive_preservesEveryFieldAndProfile() throws Exception {
        model.addFriend(alice);
        Friend profile = model.getGameMates().getMyself();
        assertEquals("Archived Alice Tan.", execute("archive 1").getFeedbackToUser());
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(alice.withArchived(true), model.getGameMates().getFriendList().getFirst());

        JsonGameMatesStorage storage = new JsonGameMatesStorage(tempDir.resolve("friends.json"));
        storage.saveGameMates(model.getGameMates());
        ModelManager restarted = new ModelManager(storage.readGameMates().get(), new UserPrefs());
        assertTrue(restarted.getFilteredFriendList().isEmpty());
        parser.parseCommand("archivelist").execute(restarted);
        assertEquals(List.of(alice.withArchived(true)), restarted.getFilteredFriendList());
        assertEquals("Restored Alice Tan.",
                parser.parseCommand("unarchive 1").execute(restarted).getFeedbackToUser());
        assertTrue(restarted.getFilteredFriendList().isEmpty());
        assertEquals(FriendListScope.ARCHIVED, restarted.getFriendListScope());
        parser.parseCommand("list").execute(restarted);
        assertEquals(List.of(alice), restarted.getFilteredFriendList());
        assertEquals(profile, restarted.getGameMates().getMyself());
        storage.saveGameMates(restarted.getGameMates());
        assertEquals(restarted.getGameMates(), storage.readGameMates().get());
    }

    @Test
    public void archive_pinnedFriend_clearsPinAndDoesNotRestoreItAfterRestart() throws Exception {
        model.addFriend(alice.withPinned(true));
        execute("archive 1");
        assertEquals(List.of(alice.withArchived(true)), model.getGameMates().getFriendList());

        JsonGameMatesStorage storage = new JsonGameMatesStorage(tempDir.resolve("pinned.json"));
        storage.saveGameMates(model.getGameMates());
        ModelManager restarted = new ModelManager(storage.readGameMates().get(), new UserPrefs());
        parser.parseCommand("archivelist").execute(restarted);
        assertEquals(List.of(alice.withArchived(true)), restarted.getFilteredFriendList());
        parser.parseCommand("unarchive 1").execute(restarted);
        assertEquals(List.of(alice), restarted.getGameMates().getFriendList());
    }

    @Test
    public void archiveAndUnarchive_useFilteredDisplayedIndices() throws Exception {
        model.addFriend(bob);
        model.addFriend(alice);
        execute("list g/Valorant");
        execute("archive 1");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListScope.ACTIVE, model.getFriendListScope());
        assertEquals(List.of(bob, alice.withArchived(true)), model.getGameMates().getFriendList());
        execute("list");
        execute("archive 1");
        execute("archivelist g/valorant");
        assertEquals(List.of(alice.withArchived(true)), model.getFilteredFriendList());
        execute("unarchive 1");
        assertTrue(model.getFilteredFriendList().isEmpty());
        execute("archivelist");
        assertEquals(List.of(bob.withArchived(true)), model.getFilteredFriendList());
    }

    @Test
    public void listAndFind_switchToTheirRespectiveScopes() throws Exception {
        model.addFriend(alice.withArchived(true));
        model.addFriend(bob);
        execute("archivelist");
        execute("find Alice Bob");
        assertEquals(List.of(alice.withArchived(true), bob), model.getFilteredFriendList());
        assertEquals(FriendListScope.SEARCH, model.getFriendListScope());
        execute("archivelist");
        execute("list g/Valorant");
        assertTrue(model.getFilteredFriendList().isEmpty());
        execute("archivelist");
        execute("list");
        assertEquals(List.of(bob), model.getFilteredFriendList());
    }

    @Test
    public void mixedSearch_archiveAndUnarchive_keepResultsAndDisplayedIndices() throws Exception {
        model.addFriend(bob.withArchived(true));
        model.addFriend(alice);
        execute("find Alice Bob");
        assertEquals(List.of(bob.withArchived(true), alice), model.getFilteredFriendList());
        execute("archive 2");
        assertEquals(List.of(bob.withArchived(true), alice.withArchived(true)), model.getFilteredFriendList());
        assertEquals(FriendListScope.SEARCH, model.getFriendListScope());
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_STATUS, () -> execute("archive 1"));
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_STATUS, () -> execute("archive 2"));
        execute("unarchive 2");
        execute("unarchive 1");
        assertEquals(List.of(bob, alice), model.getFilteredFriendList());
        assertEquals(FriendListScope.SEARCH, model.getFriendListScope());
        assertThrows(CommandException.class, UnarchiveCommand.MESSAGE_WRONG_STATUS, () -> execute("unarchive 1"));
        execute("archivelist");
        assertTrue(model.getFilteredFriendList().isEmpty());
        execute("list");
        assertEquals(List.of(bob, alice), model.getFilteredFriendList());
    }

    @Test
    public void archive_wrongView_reportsGuidanceBeforeIndexValidation() throws Exception {
        model.addFriend(alice);
        model.addFriend(bob.withArchived(true));
        for (String viewCommand : List.of("archivelist", "archivelist g/Unknown")) {
            execute(viewCommand);
            FriendListScope view = model.getFriendListScope();
            List<Friend> displayed = List.copyOf(model.getFilteredFriendList());
            for (String archiveCommand : List.of("archive 1", "archive 99")) {
                assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_VIEW, ()
                        -> execute(archiveCommand));
                assertEquals(view, model.getFriendListScope());
                assertEquals(displayed, model.getFilteredFriendList());
                assertEquals(List.of(alice, bob.withArchived(true)), model.getGameMates().getFriendList());
            }
        }
        execute("list");
        execute("archive 1");
        assertEquals(List.of(alice.withArchived(true), bob.withArchived(true)), model.getGameMates().getFriendList());
    }

    @Test
    public void archive_searchInvalidIndex_preservesResultsAndData() throws Exception {
        model.addFriend(alice);
        model.addFriend(bob.withArchived(true));
        for (String search : List.of("find Alice Bob", "find Nobody")) {
            execute(search);
            List<Friend> displayed = List.copyOf(model.getFilteredFriendList());
            String expected = displayed.isEmpty() ? EditCommand.MESSAGE_EMPTY_LIST
                    : String.format(EditCommand.MESSAGE_INDEX_NOT_FOUND, 99, displayed.size());
            assertThrows(CommandException.class, expected, () -> execute("archive 99"));
            assertEquals(displayed, model.getFilteredFriendList());
            assertEquals(FriendListScope.SEARCH, model.getFriendListScope());
            assertEquals(List.of(alice, bob.withArchived(true)), model.getGameMates().getFriendList());
        }
    }

    @Test
    public void globalSearch_replacesPreviousFiltersAndKeepsScopeForEmptyResults() throws Exception {
        model.addFriend(alice.withArchived(true));
        model.addFriend(bob);
        execute("archivelist g/Unknown");
        assertEquals(alice.withArchived(true), execute("find ALICE").getFriendToShow().get());
        execute("find Bo");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListScope.SEARCH, model.getFriendListScope());
        execute("find Bob");
        assertEquals(List.of(bob), model.getFilteredFriendList());
        execute("list g/Valorant");
        execute("find Alice Bob");
        assertEquals(List.of(alice.withArchived(true), bob), model.getFilteredFriendList());
        assertThrows(ParseException.class, () -> execute("find"));
        assertEquals(FriendListScope.SEARCH, model.getFriendListScope());
        assertEquals(List.of(alice.withArchived(true), bob), model.getFilteredFriendList());
        execute("archivelist g/Minecraft");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListScope.ARCHIVED, model.getFriendListScope());
    }

    @Test
    public void globalSearch_edit_preservesSearchScopeAndReevaluatesNames() throws Exception {
        model.addFriend(alice.withArchived(true));
        model.addFriend(bob);
        execute("find Alice");
        execute("edit 1 p/91234567");
        assertEquals(FriendListScope.SEARCH, model.getFriendListScope());
        assertTrue(model.getFilteredFriendList().getFirst().isArchived());
        assertEquals(1, model.getFilteredFriendList().size());
        execute("edit 1 n/Carol");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListScope.SEARCH, model.getFriendListScope());
        execute("find Carol");
        assertTrue(model.getFilteredFriendList().getFirst().isArchived());
    }

    @Test
    public void archiveList_gameFilter_matchesArchivedFriendsOnlyAndReplacesPreviousFilter() throws Exception {
        Friend archived = new FriendBuilder().withName("Carol").withGames(
                new GameBuilder().withGameName("Stardew Valley").build()).build().withArchived(true);
        Friend active = new FriendBuilder().withName("Dave").withGames(
                new GameBuilder().withGameName("Stardew Valley").build()).build();
        model.addFriend(alice.withArchived(true));
        model.addFriend(archived);
        model.addFriend(active);
        execute("find Alice");
        assertEquals("Listed 1 archived friend(s).",
                execute("archivelist g/  stardew valley ").getFeedbackToUser());
        assertEquals(List.of(archived), model.getFilteredFriendList());
        assertEquals(FriendListScope.ARCHIVED, model.getFriendListScope());
        for (String invalid : List.of("archivelist Alice", "archivelist g/", "archivelist g/!!!",
                "archivelist g/Valorant g/Minecraft")) {
            assertThrows(ParseException.class, () -> execute(invalid));
            assertEquals(List.of(archived), model.getFilteredFriendList());
            assertEquals(FriendListScope.ARCHIVED, model.getFriendListScope());
        }
        execute("archivelist g/Stardew");
        assertTrue(model.getFilteredFriendList().isEmpty());
        execute("archivelist");
        assertEquals(List.of(alice.withArchived(true), archived), model.getFilteredFriendList());
    }

    @Test
    public void archivedFriend_editAndGameCommands_preserveArchiveStatus() throws Exception {
        model.addFriend(alice.withArchived(true));
        execute("archivelist g/Valorant");
        assertEquals(alice.withArchived(true), execute("gamelist 1").getFriendToShow().get());
        execute("gameadd 1 g/Minecraft u/aliceMC");
        assertTrue(model.getFilteredFriendList().getFirst().isArchived());
        execute("gamedel 1 g/Minecraft");
        assertEquals(alice.withArchived(true), model.getFilteredFriendList().getFirst());
        execute("edit 1 p/91234567");
        assertEquals(FriendListScope.ARCHIVED, model.getFriendListScope());
        Friend updated = model.getFilteredFriendList().getFirst();
        assertTrue(updated.isArchived());
        assertEquals("91234567", updated.getPhone().value);
        assertEquals(alice.getGames(), updated.getGames());
    }

    @Test
    public void archiveCommands_invalidTargets_leaveDataAndViewUnchanged() throws Exception {
        model.addFriend(alice);
        assertThrows(CommandException.class, UnarchiveCommand.MESSAGE_WRONG_VIEW, () -> execute("unarchive 1"));
        assertThrows(CommandException.class, UnarchiveCommand.MESSAGE_WRONG_VIEW, () -> execute("unarchive 99"));
        assertThrows(CommandException.class, String.format(EditCommand.MESSAGE_INDEX_NOT_FOUND, 2, 1), ()
                -> execute("archive 2"));
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_MYSELF, ()
                -> new ArchiveCommand(Index.myself()).execute(model));
        assertThrows(CommandException.class, UnarchiveCommand.MESSAGE_MYSELF, ()
                -> new UnarchiveCommand(Index.myself()).execute(model));
        assertEquals(List.of(alice), model.getGameMates().getFriendList());
        execute("archive 1");
        execute("archivelist");
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_VIEW, () -> execute("archive 1"));
        assertThrows(CommandException.class, String.format(EditCommand.MESSAGE_INDEX_NOT_FOUND, 2, 1), ()
                -> execute("unarchive 2"));
        assertEquals(List.of(alice.withArchived(true)), model.getFilteredFriendList());
        execute("archivelist g/Unknown");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListScope.ARCHIVED, model.getFriendListScope());
        assertThrows(CommandException.class, EditCommand.MESSAGE_EMPTY_LIST, ()
                -> execute("unarchive 1"));
    }

    @Test
    public void archiveCommands_invalidSyntax_rejected() {
        for (String word : List.of("archive", "unarchive")) {
            for (String args : List.of("", "0", "-1", "myself", "abc", "2147483648", "1 2", "1 n/Alice")) {
                assertThrows(ParseException.class, () -> parser.parseCommand(word + " " + args));
            }
        }
    }

    @Test
    public void archiveParsers_reportSpecificMissingAndInvalidIndexErrors() {
        for (String word : List.of("archive", "unarchive")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_MISSING_INDEX, () -> parser.parseCommand(word));
            assertThrows(ParseException.class, ParserUtil.MESSAGE_MISSING_INDEX, ()
                    -> parser.parseCommand(word + "   "));
            for (String value : List.of("-1", "0", "abc", "2147483648", "myself", "1 2")) {
                assertThrows(ParseException.class, String.format(ParserUtil.MESSAGE_INVALID_INDEX, value), ()
                        -> parser.parseCommand(word + " " + value));
            }
        }
    }

    @Test
    public void archivedNames_remainReservedUntilDeleted() throws Exception {
        model.addFriend(alice.withArchived(true));
        model.addFriend(bob);
        assertThrows(CommandException.class, Messages.MESSAGE_DUPLICATE_ARCHIVED_FRIEND, ()
                -> execute("add n/Alice Tan"));
        assertThrows(CommandException.class, Messages.MESSAGE_DUPLICATE_ARCHIVED_FRIEND, ()
                -> execute("edit 1 n/Alice Tan"));
        execute("archivelist");
        execute("delete 1");
        execute("add n/Alice Tan");
        assertEquals(2, model.getGameMates().getFriendList().size());
        assertEquals(FriendListScope.ACTIVE, model.getFriendListScope());
    }

    @Test
    public void archiveList_emptyFeedback_distinguishesEmptyArchiveFromGameFilter() throws Exception {
        model.addFriend(bob);
        assertEquals(ArchiveListCommand.MESSAGE_EMPTY_ARCHIVE, execute("archivelist").getFeedbackToUser());
        assertEquals(FriendListScope.ARCHIVED, model.getFriendListScope());
        assertEquals(ArchiveListCommand.MESSAGE_NO_MATCH,
                execute("archivelist g/Valorant").getFeedbackToUser());
        model.addFriend(alice.withArchived(true));
        assertEquals(ArchiveListCommand.MESSAGE_NO_MATCH,
                execute("archivelist g/Minecraft").getFeedbackToUser());
        assertEquals("Listed 1 archived friend(s).", execute("archivelist g/Valorant").getFeedbackToUser());
        assertEquals("Listed 1 archived friend(s).", execute("archivelist").getFeedbackToUser());
        execute("unarchive 1");
        assertEquals(ArchiveListCommand.MESSAGE_EMPTY_ARCHIVE, execute("archivelist").getFeedbackToUser());
    }

    @Test
    public void duplicateActiveName_withUnrelatedArchivedFriend_stillReportsActiveDuplicate() throws Exception {
        model.addFriend(alice.withArchived(true));
        model.addFriend(bob);
        assertThrows(CommandException.class, Messages.MESSAGE_DUPLICATE_FRIEND, () -> execute("add n/Bob Lim"));
        assertThrows(CommandException.class, Messages.MESSAGE_DUPLICATE_ARCHIVED_FRIEND, ()
                -> execute("add n/Alice Tan"));
        assertEquals(List.of(alice.withArchived(true), bob), model.getGameMates().getFriendList());
    }

    @Test
    public void clear_removesBothStatusesAndPreservesMyself() throws Exception {
        model.addFriend(alice.withArchived(true));
        model.addFriend(bob);
        Friend profile = model.getGameMates().getMyself();
        execute("archivelist");
        assertEquals(ClearCommand.MESSAGE_SUCCESS, execute("clear").getFeedbackToUser());
        assertTrue(model.getGameMates().getFriendList().isEmpty());
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(profile, model.getGameMates().getMyself());
    }

    private CommandResult execute(String input) throws Exception {
        return parser.parseCommand(input).execute(model);
    }
}
