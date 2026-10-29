package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.FriendListScope;
import seedu.address.model.ModelManager;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.Game;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

/** Exercises routing, commands, model replacement and existing JSON persistence together. */
public class GameEditIntegrationTest {
    @TempDir
    public Path directory;

    @Test
    public void execute_prefixLikeGameNames_editOnlyQuotedTargetAndPersist() throws Exception {
        Path file = directory.resolve("quoted-games.json");
        JsonGameMatesStorage json = new JsonGameMatesStorage(file);
        ModelManager model = new ModelManager();
        model.addFriend(new FriendBuilder().withName("Player").withGames().build());
        LogicManager logic = new LogicManager(model,
                new StorageManager(json, new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        logic.execute("gameadd 1 g/PUBG u/plain");
        logic.execute("gameadd 1 g/PUBG n/Game u/special");
        Game plain = model.getFilteredFriendList().getFirst().getGames().stream()
                .filter(game -> game.getGameName().value.equals("PUBG")).findFirst().orElseThrow();
        logic.execute("gameedit 1 g/\"PUBG n/Game\" u/updated");
        assertTrue(model.getFilteredFriendList().getFirst().getGames().contains(plain));
        assertTrue(model.getFilteredFriendList().getFirst().getGames().stream().anyMatch(game ->
                game.getGameName().value.equals("PUBG n/Game") && game.getUsername().value.equals("updated")));
        logic.execute("gameedit 1 g/\"PUBG n/Game\" n/\"PUBG m/Edition\"");
        model.setMyself(new FriendBuilder(model.getFilteredFriendList().getFirst()).withName("Own Profile").build());
        model.updateFilteredFriendList(friend -> false);
        logic.execute("gameedit myself g/\"PUBG m/Edition\" m/\"Note:uses n/text\"");
        assertEquals(model.getGameMates(), json.readGameMates().get());
        String saved = Files.readString(file);
        assertThrows(ParseException.class, () -> logic.execute("gameedit myself g/\"PUBG m/Edition u/oops"));
        assertEquals(saved, Files.readString(file));
        assertEquals(model.getGameMates(), json.readGameMates().get());
    }

    @Test
    public void execute_archivedFriendAndMyself_remainUnpinnedAndFailuresDoNotSave() throws Exception {
        Path file = directory.resolve("gamemates.json");
        JsonGameMatesStorage json = new JsonGameMatesStorage(file);
        ModelManager model = new ModelManager();
        Friend friend = new FriendBuilder().withName("Player").withGames(
                new GameBuilder().withGameName("Valorant").addMeta("Role", "Healer").build(),
                new GameBuilder().build()).withArchived(true).build();
        model.addFriend(friend);
        model.setMyself(new FriendBuilder(friend).withName("Own Profile").build());
        LogicManager logic = new LogicManager(model,
                new StorageManager(json, new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        logic.execute("archivelist");
        logic.execute("gameedit 1 g/Valorant n/VALORANT u/newPlayer m/Region:Asia:SG");
        logic.execute("gameedit myself g/Valorant n/My Game m/ u/");
        var restored = json.readGameMates().get();
        assertEquals(model.getGameMates(), restored);
        assertTrue(restored.getFriendList().getFirst().isArchived());
        assertFalse(restored.getFriendList().getFirst().isPinned());
        assertEquals(FriendListScope.ARCHIVED, model.getFriendListScope());
        Game edited = restored.getFriendList().getFirst().getGames().stream()
                .filter(game -> game.getGameName().value.equals("VALORANT")).findFirst().orElseThrow();
        assertEquals("newPlayer", edited.getUsername().value);
        assertEquals("Region:Asia:SG", edited.getMetas().iterator().next().toString());
        assertTrue(restored.getMyself().getGames().stream()
                .anyMatch(game -> game.getGameName().value.equals("My Game") && game.getMetas().isEmpty()
                        && game.getUsername().isEmpty()));
        String saved = Files.readString(file);
        assertThrows(ParseException.class, () -> logic.execute("gameedit 1 g/Valorant m/Role:Tank m/role:Tank"));
        assertThrows(CommandException.class, () -> logic.execute("gameedit 1 g/Valorant n/Minecraft"));
        assertEquals(saved, Files.readString(file));
        assertEquals(restored, model.getGameMates());
    }
}
