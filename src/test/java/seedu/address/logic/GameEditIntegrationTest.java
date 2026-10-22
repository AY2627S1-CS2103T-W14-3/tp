package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
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
    public void execute_friendAndMyself_roundTripAndFailuresDoNotSave() throws Exception {
        Path file = directory.resolve("gamemates.json");
        JsonGameMatesStorage json = new JsonGameMatesStorage(file);
        ModelManager model = new ModelManager();
        Friend friend = new FriendBuilder().withName("Player").withGames(
                new GameBuilder().withGameName("Valorant").addMeta("Role", "Healer").build(),
                new GameBuilder().build()).build();
        model.addFriend(friend);
        model.setMyself(new FriendBuilder(friend).withName("Own Profile").build());
        LogicManager logic = new LogicManager(model,
                new StorageManager(json, new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        logic.execute("gameedit 1 g/Valorant n/VALORANT u/newPlayer m/Region:Asia:SG");
        logic.execute("gameedit myself g/Valorant n/My Game m/ u/");
        var restored = json.readGameMates().get();
        assertEquals(model.getGameMates(), restored);
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
