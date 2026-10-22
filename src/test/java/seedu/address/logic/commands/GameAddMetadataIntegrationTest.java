package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.logic.parser.GameMatesParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.Game;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

/** Tests metadata from command input through model updates and JSON persistence. */
public class GameAddMetadataIntegrationTest {
    @TempDir
    public Path testFolder;

    @Test
    public void execute_friendWithMetadata_preservesDetailsAndSurvivesReload() throws Exception {
        assertMetadataAddition("1");
    }

    @Test
    public void execute_myselfWithMetadata_preservesDetailsAndSurvivesReload() throws Exception {
        assertMetadataAddition("myself");
    }

    @Test
    public void execute_metadataWithoutUsername_survivesReloadForFriendAndMyself() throws Exception {
        Model model = new ModelManager();
        model.addFriend(new FriendBuilder().withName("Alex").build());
        GameMatesParser parser = new GameMatesParser();
        Game expected = new GameBuilder().withGameName("Valorant").withoutUsername()
                .addMeta("Role", "Support").build();

        for (String selector : List.of("1", "myself")) {
            CommandResult result = parser.parseCommand("gameadd " + selector + " g/Valorant m/Role:Support")
                    .execute(model);
            String name = selector.equals("myself") ? model.getGameMates().getMyself().getName().toString() : "Alex";
            assertEquals("Added Valorant to " + name + "'s games.\nUsername: Not provided\nMetadata:\n - Role: Support",
                    result.getFeedbackToUser());
        }

        assertEquals(List.of(expected), List.copyOf(model.getFilteredFriendList().getFirst().getGames()));
        assertEquals(List.of(expected), List.copyOf(model.getGameMates().getMyself().getGames()));
        JsonGameMatesStorage storage = new JsonGameMatesStorage(testFolder.resolve("metadata-without-username.json"));
        storage.saveGameMates(model.getGameMates());
        assertEquals(model.getGameMates(), storage.readGameMates().get());
    }

    @Test
    public void execute_invalidMetadata_preservesModelFilterAndSavedData() throws Exception {
        Model model = new ModelManager();
        Game existing = new GameBuilder().addMeta("Role", "Builder").build();
        Friend original = new FriendBuilder().withName("Alex").withGames(existing).build();
        Friend hidden = new FriendBuilder().withName("Hidden").build();
        model.addFriend(hidden);
        model.addFriend(original);
        model.setMyself(original);
        model.updateFilteredFriendList(friend -> friend.getName().equals(original.getName()));
        GameMates before = new GameMates(model.getGameMates());
        Path path = testFolder.resolve("invalid-metadata.json");
        JsonGameMatesStorage storage = new JsonGameMatesStorage(path);
        storage.saveGameMates(before);
        byte[] savedData = Files.readAllBytes(path);
        LogicManager logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(testFolder.resolve("prefs.json"))));
        String[][] invalidInputs = {
            {"m/Role:Support m/", Meta.MESSAGE_CONSTRAINTS},
            {"m/Role:Support m/Level", Meta.MESSAGE_CONSTRAINTS},
            {"m/Role:Support m/:20", MetaKey.MESSAGE_CONSTRAINTS},
            {"m/Role:Support m/Level: ", MetaValue.MESSAGE_CONSTRAINTS},
            {"m/Role:Support m/Level:20 m/ ROLE :Healer", Meta.MESSAGE_DUPLICATE_KEY},
            {"m/Role:Support m/Role:Support", Meta.MESSAGE_DUPLICATE_KEY}
        };

        for (String selector : List.of("1", "myself")) {
            for (String[] invalid : invalidInputs) {
                ParseException failure = assertThrows(ParseException.class, () -> logic.execute(
                        "gameadd " + selector + " g/Valorant u/player " + invalid[0]));
                assertEquals(invalid[1], failure.getMessage());
                assertEquals(before, model.getGameMates());
                assertEquals(List.of(original), model.getFilteredFriendList());
                assertArrayEquals(savedData, Files.readAllBytes(path));
            }
            logic.execute("gameadd " + selector + " g/Valorant u/player m/Role:Support");
            assertEquals(2, (selector.equals("myself") ? model.getGameMates().getMyself()
                    : model.getFilteredFriendList().getFirst()).getGames().size());
            model.setGameMates(before);
            storage.saveGameMates(before);
        }
    }

    @Test
    public void execute_metadataKeysAreScopedToEachGameEntry() throws Exception {
        Model model = new ModelManager();
        Game existing = new GameBuilder().addMeta("Role", "Builder").build();
        model.addFriend(new FriendBuilder().withName("Alex").withGames(existing).build());
        model.addFriend(new FriendBuilder().withName("Bob").build());
        GameMatesParser parser = new GameMatesParser();
        Game added = new GameBuilder().withGameName("Valorant").withUsername("player")
                .addMeta("Role", "Support").build();

        for (String selector : List.of("1", "2", "myself")) {
            parser.parseCommand("gameadd " + selector + " g/Valorant u/player m/Role:Support").execute(model);
        }

        assertEquals(new FriendBuilder(model.getFilteredFriendList().get(0)).withGames(existing, added).build(),
                model.getFilteredFriendList().get(0));
        assertEquals(List.of(added), List.copyOf(model.getFilteredFriendList().get(1).getGames()));
        assertEquals(List.of(added), List.copyOf(model.getGameMates().getMyself().getGames()));
    }

    private void assertMetadataAddition(String selector) throws Exception {
        Model model = new ModelManager();
        GameMatesParser parser = new GameMatesParser();
        Game existing = new GameBuilder().addMeta("Mode", "Creative").build();
        Friend original = new FriendBuilder().withName("Alex").withTags("teammate").withGames(existing).build();
        Friend hidden = new FriendBuilder().withName("Hidden").build();
        model.addFriend(hidden);
        model.addFriend(original);
        model.setMyself(original);
        model.updateFilteredFriendList(friend -> friend.getName().equals(original.getName()));
        Game added = new GameBuilder().withGameName("Valorant").withUsername("player")
                .addMeta("Role", "Support:Mid").addMeta("level", "20").build();
        Friend updated = new FriendBuilder(original).withGames(existing, added).build();

        CommandResult result = parser.parseCommand("gameadd " + selector
                + " g/Valorant u/player m/Role:Support:Mid m/level:20")
                .execute(model);
        assertEquals("Added Valorant to Alex's games.\nUsername: player\nMetadata:\n - level: 20\n - Role: Support:Mid",
                result.getFeedbackToUser());

        boolean isMyself = selector.equals("myself");
        assertEquals(isMyself ? original : updated, model.getFilteredFriendList().getFirst());
        assertEquals(isMyself ? updated : original, model.getGameMates().getMyself());
        assertEquals(List.of(hidden, isMyself ? original : updated), model.getGameMates().getFriendList());
        assertEquals(1, model.getFilteredFriendList().size());
        assertCommandFailure(parser.parseCommand("gameadd " + selector + " g/valorant u/other m/Role:Healer"),
                model, "Alex already has valorant.");

        JsonGameMatesStorage storage = new JsonGameMatesStorage(testFolder.resolve(selector + ".json"));
        storage.saveGameMates(model.getGameMates());
        assertEquals(model.getGameMates(), storage.readGameMates().get());
    }
}
