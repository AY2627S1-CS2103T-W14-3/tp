package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.GameMates;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

public class SquadMetadataIntegrationTest {
    @TempDir
    public Path directory;

    @Test
    public void execute_routedConditions_preserveModelAndPersistedData() throws Exception {
        ModelManager model = new ModelManager();
        model.addFriend(new FriendBuilder().withName("Hidden").build());
        model.addFriend(new FriendBuilder().withName("Healer").withGames(new GameBuilder()
                .withGameName("Valorant").addMeta("Role", "Healer").addMeta("Region", "Asia").build()).build());
        model.updateFilteredFriendList(friend -> friend.getName().fullName.equals("Healer"));
        Path file = directory.resolve("gamemates.json");
        JsonGameMatesStorage json = new JsonGameMatesStorage(file);
        json.saveGameMates(model.getGameMates());
        String saved = Files.readString(file);
        GameMates before = new GameMates(model.getGameMates());
        var displayed = new ArrayList<>(model.getFilteredFriendList());
        LogicManager logic = new LogicManager(model,
                new StorageManager(json, new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        var result = logic.execute("squad g/valorant n/2 m/role:Healer m/Region:Asia");
        assertEquals("Found squad of 2 people:\n1. Me\n2. Healer (username: Amy123, index: 1)",
                result.getFeedbackToUser());
        assertTrue(result.isPreserveDetails());
        assertThrows(CommandException.class, "Could not find a squad of 2 people.", () ->
                logic.execute("squad g/Valorant n/2 m/Role:Tank"));
        assertThrows(CommandException.class, "Could not find a squad of 3 people.", () ->
                logic.execute("squad g/Valorant n/3 m/Role:Healer"));
        assertEquals("Found squad of 1 person:\n1. Me",
                logic.execute("squad g/Valorant n/1 m/Role:Tank").getFeedbackToUser());
        assertEquals(before, model.getGameMates());
        assertEquals(displayed, model.getFilteredFriendList());
        assertEquals(saved, Files.readString(file));
        assertEquals(before, json.readGameMates().get());
    }
}
