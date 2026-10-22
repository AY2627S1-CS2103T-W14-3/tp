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
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.FriendListView;
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
        assertEquals(FriendListView.ARCHIVED, restarted.getFriendListView());
        parser.parseCommand("list").execute(restarted);
        assertEquals(List.of(alice), restarted.getFilteredFriendList());
        assertEquals(profile, restarted.getGameMates().getMyself());
        storage.saveGameMates(restarted.getGameMates());
        assertEquals(restarted.getGameMates(), storage.readGameMates().get());
    }

    @Test
    public void archiveAndUnarchive_useFilteredDisplayedIndices() throws Exception {
        model.addFriend(bob);
        model.addFriend(alice);
        execute("list g/Valorant");
        execute("archive 1");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListView.ACTIVE, model.getFriendListView());
        assertEquals(List.of(bob, alice.withArchived(true)), model.getGameMates().getFriendList());
        execute("list");
        execute("archive 1");
        execute("archivelist ALICE missing");
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
        assertEquals(FriendListView.SEARCH, model.getFriendListView());
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
        assertEquals(FriendListView.SEARCH, model.getFriendListView());
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_STATUS, () -> execute("archive 1"));
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_STATUS, () -> execute("archive 2"));
        execute("unarchive 2");
        execute("unarchive 1");
        assertEquals(List.of(bob, alice), model.getFilteredFriendList());
        assertEquals(FriendListView.SEARCH, model.getFriendListView());
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
        for (String viewCommand : List.of("archivelist", "archivelist Nobody")) {
            execute(viewCommand);
            FriendListView view = model.getFriendListView();
            List<Friend> displayed = List.copyOf(model.getFilteredFriendList());
            for (String archiveCommand : List.of("archive 1", "archive 99")) {
                assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_VIEW, ()
                        -> execute(archiveCommand));
                assertEquals(view, model.getFriendListView());
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
            assertThrows(CommandException.class, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX, ()
                    -> execute("archive 99"));
            assertEquals(displayed, model.getFilteredFriendList());
            assertEquals(FriendListView.SEARCH, model.getFriendListView());
            assertEquals(List.of(alice, bob.withArchived(true)), model.getGameMates().getFriendList());
        }
    }

    @Test
    public void globalSearch_replacesPreviousFiltersAndKeepsScopeForEmptyResults() throws Exception {
        model.addFriend(alice.withArchived(true));
        model.addFriend(bob);
        execute("archivelist Nobody");
        assertEquals(alice.withArchived(true), execute("find ALICE").getFriendToShow().get());
        execute("find Bo");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListView.SEARCH, model.getFriendListView());
        execute("find Bob");
        assertEquals(List.of(bob), model.getFilteredFriendList());
        execute("list g/Valorant");
        execute("find Alice Bob");
        assertEquals(List.of(alice.withArchived(true), bob), model.getFilteredFriendList());
        assertThrows(ParseException.class, () -> execute("find"));
        assertEquals(FriendListView.SEARCH, model.getFriendListView());
        assertEquals(List.of(alice.withArchived(true), bob), model.getFilteredFriendList());
        execute("archivelist bob");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListView.ARCHIVED, model.getFriendListView());
    }

    @Test
    public void globalSearch_edit_preservesSearchScopeAndReevaluatesNames() throws Exception {
        model.addFriend(alice.withArchived(true));
        model.addFriend(bob);
        execute("find Alice");
        execute("edit 1 p/91234567");
        assertEquals(FriendListView.SEARCH, model.getFriendListView());
        assertTrue(model.getFilteredFriendList().getFirst().isArchived());
        assertEquals(1, model.getFilteredFriendList().size());
        execute("edit 1 n/Carol");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListView.SEARCH, model.getFriendListView());
        execute("find Carol");
        assertTrue(model.getFilteredFriendList().getFirst().isArchived());
    }

    @Test
    public void archivedFriend_editAndGameCommands_preserveArchiveStatus() throws Exception {
        model.addFriend(alice.withArchived(true));
        execute("archivelist alice");
        assertEquals(alice.withArchived(true), execute("gamelist 1").getFriendToShow().get());
        execute("gameadd 1 g/Minecraft u/aliceMC");
        assertTrue(model.getFilteredFriendList().getFirst().isArchived());
        execute("gamedel 1 g/Minecraft");
        assertEquals(alice.withArchived(true), model.getFilteredFriendList().getFirst());
        execute("edit 1 p/91234567");
        assertEquals(FriendListView.ARCHIVED, model.getFriendListView());
        Friend updated = model.getFilteredFriendList().getFirst();
        assertTrue(updated.isArchived());
        assertEquals("91234567", updated.getPhone().value);
        assertEquals(alice.getGames(), updated.getGames());
    }

    @Test
    public void archiveCommands_invalidTargets_leaveDataAndViewUnchanged() throws Exception {
        model.addFriend(alice);
        assertThrows(CommandException.class, UnarchiveCommand.MESSAGE_WRONG_STATUS, () -> execute("unarchive 1"));
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX, ()
                -> execute("archive 2"));
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_MYSELF, ()
                -> new ArchiveCommand(Index.myself()).execute(model));
        assertThrows(CommandException.class, UnarchiveCommand.MESSAGE_MYSELF, ()
                -> new UnarchiveCommand(Index.myself()).execute(model));
        assertEquals(List.of(alice), model.getGameMates().getFriendList());
        execute("archive 1");
        execute("archivelist");
        assertThrows(CommandException.class, ArchiveCommand.MESSAGE_WRONG_VIEW, () -> execute("archive 1"));
        assertEquals(List.of(alice.withArchived(true)), model.getFilteredFriendList());
        execute("archivelist nobody");
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertEquals(FriendListView.ARCHIVED, model.getFriendListView());
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX, ()
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
        assertEquals(FriendListView.ACTIVE, model.getFriendListView());
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
