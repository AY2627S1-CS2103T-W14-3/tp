package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.GameEditCommand.EditGameDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.GameMatesParser;
import seedu.address.model.FriendListScope;
import seedu.address.model.GameMates;
import seedu.address.model.ModelManager;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.FriendTarget;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.model.game.meta.Meta;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

public class GameEditCommandTest {
    private final Game original = new GameBuilder().withGameName("Valorant").withUsername("oldPlayer")
            .addMeta("Role", "Healer").addMeta("Region", "Asia").build();
    private final Game other = new GameBuilder().withGameName("Minecraft").addMeta("Mode", "Survival").build();
    private final Friend friend = new FriendBuilder().withName("Player").withPhone("12345678")
            .withEmail("player@example.com").withTags("team").withGames(original, other).build();
    private final ModelManager model = new ModelManager();

    @Test
    public void execute_pinnedFriend_preservesPinOrderAndOtherFields() throws Exception {
        CountingModel counted = new CountingModel();
        Friend unpinned = new FriendBuilder().withName("Unpinned").build();
        Friend firstPinned = new FriendBuilder().withName("First Pinned").withPinned(true).build();
        Friend target = friend.withPinned(true);
        counted.addFriend(unpinned);
        counted.addFriend(firstPinned);
        counted.addFriend(target);
        CommandResult result = new GameMatesParser().parseCommand(
                "gameedit 2 g/Valorant n/New Game u/newPlayer m/Role:Tank").execute(counted);
        Friend updated = counted.getFilteredFriendList().get(1);
        Game replacement = new GameBuilder().withGameName("New Game").withUsername("newPlayer")
                .addMeta("Role", "Tank").build();
        assertEquals(new FriendBuilder(target).withGames(replacement, other).build(), updated);
        assertEquals(List.of(firstPinned, updated, unpinned), counted.getFilteredFriendList());
        assertEquals(FriendListScope.ACTIVE, counted.getFriendListScope());
        assertEquals(1, counted.updates);
        assertSame(updated, result.getFriendToShow().orElseThrow());
        assertThrows(CommandException.class, () -> new GameMatesParser().parseCommand(
                "gameedit 2 g/New Game n/Minecraft").execute(counted));
        assertEquals(List.of(firstPinned, updated, unpinned), counted.getFilteredFriendList());
        assertEquals(1, counted.updates);
    }

    @Test
    public void execute_archivedFriend_preservesArchiveStatusScopeAndOtherFields() throws Exception {
        Friend hidden = new FriendBuilder().withName("Hidden").build();
        model.addFriend(hidden);
        model.addFriend(friend.withArchived(true));
        for (FriendListScope scope : new FriendListScope[]{FriendListScope.ARCHIVED, FriendListScope.SEARCH}) {
            if (scope == FriendListScope.ARCHIVED) {
                model.updateArchivedFriendList(target -> target.getName().equals(friend.getName()));
            } else {
                model.updateSearchFriendList(target -> target.getName().equals(friend.getName()));
            }
            CommandResult result = execute("gameedit 1 g/Valorant u/newPlayer");
            Friend updated = model.getFilteredFriendList().getFirst();
            assertTrue(updated.isArchived());
            assertEquals(scope, model.getFriendListScope());
            assertEquals(1, model.getFilteredFriendList().size());
            assertSame(hidden, model.getGameMates().getFriendList().getFirst());
            assertSame(updated, result.getFriendToShow().orElseThrow());
            assertEquals(friend.getName(), updated.getName());
            assertEquals(friend.getPhone(), updated.getPhone());
            assertEquals(friend.getEmail(), updated.getEmail());
            assertEquals(friend.getTags(), updated.getTags());
            assertEquals(original.getMetas(), game(updated, "Valorant").getMetas());
            assertTrue(updated.getGames().contains(other));
            assertThrows(CommandException.class, () -> execute("gameedit 1 g/Valorant n/Minecraft"));
            assertSame(updated, model.getFilteredFriendList().getFirst());
            assertEquals(scope, model.getFriendListScope());
        }
    }

    @Test
    public void execute_displayedIndex_preservesFieldsFilterAndOtherGamesWithOneUpdate() throws Exception {
        CountingModel counted = new CountingModel();
        Friend hidden = new FriendBuilder().withName("Hidden").build();
        counted.addFriend(hidden);
        counted.addFriend(friend);
        counted.updateFilteredFriendList(f -> f.getName().equals(friend.getName()));
        Friend profile = counted.getGameMates().getMyself();
        CommandResult result = new GameMatesParser().parseCommand("gameedit 1 g/valorant u/newPlayer").execute(counted);
        Friend updated = counted.getFilteredFriendList().getFirst();
        assertEquals(1, counted.updates);
        assertEquals(1, counted.getFilteredFriendList().size());
        assertSame(hidden, counted.getGameMates().getFriendList().getFirst());
        assertSame(profile, counted.getGameMates().getMyself());
        assertEquals(friend.getName(), updated.getName());
        assertEquals(friend.getPhone(), updated.getPhone());
        assertEquals(friend.getEmail(), updated.getEmail());
        assertEquals(friend.getTags(), updated.getTags());
        assertTrue(updated.getGames().contains(other));
        assertEquals(new GameBuilder(original).withUsername("newPlayer").build(), game(updated, "Valorant"));
        assertSame(updated, result.getFriendToShow().orElseThrow());
        assertTrue(result.getFeedbackToUser().contains("Username: newPlayer"));
        counted.updateFilteredFriendList(f -> true);
        assertEquals(2, counted.getFilteredFriendList().size());
    }

    @Test
    public void execute_myselfWithEmptyVisibleList_caseOnlyRenamePersistsSpelling() throws Exception {
        model.setMyself(friend);
        model.updateFilteredFriendList(f -> false);
        CommandResult result = execute("gameedit MYSELF g/VALORANT n/valorant");
        Game updated = game(model.getGameMates().getMyself(), "Valorant");
        assertEquals("valorant", updated.getGameName().value);
        assertEquals(original.getUsername(), updated.getUsername());
        assertEquals(original.getMetas(), updated.getMetas());
        assertTrue(model.getFilteredFriendList().isEmpty());
        assertTrue(result.getFriendToShow().isEmpty());
        assertTrue(result.getFeedbackToUser().contains("game: valorant"));
    }

    @Test
    public void execute_replaceAndClearMetadata_preservesOmittedFields() throws Exception {
        model.addFriend(friend);
        execute("gameedit 1 g/Valorant m/Level:30 m/Region:Asia:SG");
        Game replacement = game(model.getFilteredFriendList().getFirst(), "Valorant");
        assertEquals(Set.of("Level:30", "Region:Asia:SG"), replacement.getMetas().stream()
                .map(Meta::toString).collect(Collectors.toSet()));
        assertEquals(original.getGameName(), replacement.getGameName());
        assertEquals(original.getUsername(), replacement.getUsername());
        execute("gameedit 1 g/Valorant m/");
        assertTrue(game(model.getFilteredFriendList().getFirst(), "Valorant").getMetas().isEmpty());
        assertEquals(2, model.getFilteredFriendList().getFirst().getGames().size());
        execute("gameedit 1 g/Valorant n/New Game u/newPlayer m/Role:Tank");
        assertEquals("newPlayer", game(model.getFilteredFriendList().getFirst(), "New Game").getUsername().value);
    }

    @Test
    public void execute_clearAndOmitUsername_preservesMetadataAndOtherFields() throws Exception {
        model.addFriend(friend);
        CommandResult result = execute("gameedit 1 g/Valorant u/");
        assertTrue(result.getFeedbackToUser().contains("Username: Not provided"));
        assertTrue(game(model.getFilteredFriendList().getFirst(), "Valorant").getUsername().isEmpty());
        execute("gameedit 1 g/Valorant n/New Game");
        Game renamed = game(model.getFilteredFriendList().getFirst(), "New Game");
        assertTrue(renamed.getUsername().isEmpty());
        assertEquals(original.getMetas(), renamed.getMetas());
        assertTrue(model.getFilteredFriendList().getFirst().getGames().contains(other));
    }

    @Test
    public void execute_errors_leaveEveryFieldUnchanged() throws Exception {
        model.addFriend(friend);
        for (String input : new String[]{"gameedit 1 g/Valorant n/MINECRAFT", "gameedit 1 g/Val u/newPlayer",
            "gameedit 2 g/Valorant u/newPlayer",
            "gameedit 1 g/Valorant n/New m/Role:Tank m/role:Tank", "gameedit 1 g/Valorant m/ m/Role:Tank"}) {
            GameMates before = new GameMates(model.getGameMates());
            assertThrows(Exception.class, () -> execute(input));
            assertEquals(before, model.getGameMates());
            assertSame(friend, model.getFilteredFriendList().getFirst());
        }
        model.updateFilteredFriendList(f -> false);
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX, () ->
                execute("gameedit 1 g/Valorant u/player"));
    }

    @Test
    public void execute_ambiguousStoredGame_rejectsWithoutMutation() {
        Friend ambiguous = new FriendBuilder(friend).withGames(original,
                new GameBuilder(original).withUsername("anotherPlayer").build()).build();
        model.addFriend(ambiguous);
        assertThrows(CommandException.class, String.format(GameEditCommand.MESSAGE_AMBIGUOUS_GAME, "Valorant"), () ->
                execute("gameedit 1 g/Valorant u/player"));
        assertSame(ambiguous, model.getFilteredFriendList().getFirst());
    }

    @Test
    public void descriptorAndCommand_copyMetadataAndCompareChanges() {
        Set<Meta> mutable = new HashSet<>(original.getMetas());
        EditGameDescriptor descriptor = new EditGameDescriptor(null, null, mutable);
        mutable.clear();
        assertEquals(original.getMetas(), descriptor.metas());
        assertThrows(UnsupportedOperationException.class, () -> descriptor.metas().clear());
        GameEditCommand command = new GameEditCommand(Index.myself(), original.getGameName(), descriptor);
        assertEquals(command, new GameEditCommand(Index.myself(), new GameName("VALORANT"), descriptor));
        assertEquals(command, command);
        assertNotEquals(command, new GameEditCommand(Index.fromOneBased(1), original.getGameName(), descriptor));
        assertNotEquals(command, new GameEditCommand(Index.myself(), new GameName("Minecraft"), descriptor));
        assertNotEquals(command, new GameEditCommand(Index.myself(), original.getGameName(),
                new EditGameDescriptor(null, new Username("player"), null)));
        assertNotEquals(command, null);
        assertTrue(command.toString().contains("existingGame=Valorant"));
        assertThrows(IllegalArgumentException.class, () -> new GameEditCommand(Index.myself(),
                original.getGameName(), new EditGameDescriptor(null, null, null)));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    private CommandResult execute(String input) throws Exception {
        return new GameMatesParser().parseCommand(input).execute(model);
    }

    private Game game(Friend target, String name) {
        return target.getGames().stream().filter(g -> g.getGameName().equals(new GameName(name)))
                .findFirst().orElseThrow();
    }

    private static class CountingModel extends ModelManager {
        private int updates;

        @Override
        public void setFriend(FriendTarget target, Friend updated) {
            updates++;
            super.setFriend(target, updated);
        }
    }
}
