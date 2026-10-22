package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
        assertEquals(before, model.getGameMates());
        assertEquals(displayed, model.getFilteredFriendList());
        assertEquals(saved, Files.readString(file));
        assertEquals(before, json.readGameMates().get());
    }
}
