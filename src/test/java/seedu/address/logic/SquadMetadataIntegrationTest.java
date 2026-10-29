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
import seedu.address.model.FriendListScope;
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
    public void execute_gameMetadataCommandsAndSortedPinnedFriends_preserveStorage() throws Exception {
        ModelManager model = new ModelManager();
        Path file = directory.resolve("routed-metadata.json");
        JsonGameMatesStorage json = new JsonGameMatesStorage(file);
        LogicManager logic = new LogicManager(model,
                new StorageManager(json, new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        logic.execute("add n/Alpha");
        logic.execute("add n/Beta");
        logic.execute("add n/Cross Game");
        logic.execute("gameadd 1 g/Valorant m/Role:Healer m/Region:Asia");
        logic.execute("gameadd 2 g/Valorant m/role:Tank m/REGION:Asia");
        logic.execute("gameadd 3 g/Valorant m/Role:Healer");
        logic.execute("gameadd 3 g/Minecraft m/Region:Asia");
        logic.execute("pin 2");
        logic.execute("gameedit 1 g/Valorant m/role:Healer m/REGION:Asia");
        logic.execute("list s/name o/desc");
        GameMates before = new GameMates(model.getGameMates());
        var displayed = new ArrayList<>(model.getFilteredFriendList());
        String saved = Files.readString(file);
        var result = logic.execute("squad g/valorant n/3 m/role:Healer m/region:Asia");
        assertEquals("Found squad of 3 people:\n1. Me\n2. Beta (index: 2)\n3. Alpha (index: 3)",
                result.getFeedbackToUser());
        assertTrue(result.isPreserveDetails());
        assertEquals(FriendListScope.ACTIVE, model.getFriendListScope());
        assertEquals(before, model.getGameMates());
        assertEquals(displayed, model.getFilteredFriendList());
        assertEquals(saved, Files.readString(file));
        logic.execute("archive 2");
        logic.execute("archivelist");
        before = new GameMates(model.getGameMates());
        saved = Files.readString(file);
        assertEquals("Found squad of 2 people:\n1. Me\n2. Beta (index: 1)",
                logic.execute("squad g/Valorant n/2 m/Role:Healer m/Region:Asia").getFeedbackToUser());
        assertEquals(FriendListScope.ARCHIVED, model.getFriendListScope());
        assertEquals(before, model.getGameMates());
        assertTrue(model.getFilteredFriendList().getFirst().isPinned());
        assertTrue(model.getFilteredFriendList().getFirst().isArchived());
        assertEquals(saved, Files.readString(file));
        assertEquals(before, json.readGameMates().get());
    }

    @Test
    public void execute_literalPrefixes_matchStoredValuesWithoutMutatingData() throws Exception {
        ModelManager model = new ModelManager();
        Path file = directory.resolve("quoted-metadata.json");
        LogicManager logic = new LogicManager(model, new StorageManager(new JsonGameMatesStorage(file),
                new JsonUserPrefsStorage(directory.resolve("preferences.json"))));
        logic.execute("add n/Alpha");
        logic.execute("gameadd 1 g/Valorant m/Note:Prefer n/3");
        logic.execute("gameadd 1 g/\"Version n/2\" m/\"Note:Prefer n/3\"");
        GameMates before = new GameMates(model.getGameMates());
        String saved = Files.readString(file);
        for (String game : new String[]{"Valorant", "\"Version n/2\""}) {
            var result = logic.execute("squad g/" + game + " n/2 m/\"Note:Prefer n/3\"");
            assertEquals("Found squad of 2 people:\n1. Me\n2. Alpha (index: 1)", result.getFeedbackToUser());
            assertTrue(result.isPreserveDetails());
            assertEquals(before, model.getGameMates());
            assertEquals(saved, Files.readString(file));
        }
    }

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
