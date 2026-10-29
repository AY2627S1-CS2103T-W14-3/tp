package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.GameMatesParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.storage.JsonGameMatesStorage;
import seedu.address.testutil.FriendBuilder;

/** Tests complete game commands targeting the separately stored Myself profile. */
public class GameMyselfIntegrationTest {
    @TempDir
    public Path testFolder;

    private final Model model = new ModelManager();
    private final GameMatesParser parser = new GameMatesParser();
    private final Game valorant = new Game(new GameName("Valorant"), new Username("player"));
    private final Game minecraft = new Game(new GameName("Minecraft"), new Username("builder"));

    @Test
    public void execute_myselfWithoutFriends_preservesOtherDetailsAndSurvivesReload() throws Exception {
        Friend original = new FriendBuilder().withName("Alex").withTags("teammate").withGames(minecraft).build();
        model.setMyself(original);

        CommandResult added = parser.parseCommand("gameadd MySeLf g/Valorant u/player").execute(model);

        Friend withGame = new FriendBuilder(original).withGames(minecraft, valorant).build();
        assertEquals(withGame, model.getGameMates().getMyself());
        assertEquals("Added Valorant to Alex's games.\nUsername: player", added.getFeedbackToUser());
        assertTrue(model.getFilteredFriendList().isEmpty());
        JsonGameMatesStorage storage = new JsonGameMatesStorage(testFolder.resolve("myself.json"));
        storage.saveGameMates(model.getGameMates());
        model.setGameMates(storage.readGameMates().get());
        assertEquals(withGame, model.getGameMates().getMyself());

        CommandResult deleted = parser.parseCommand("gamedel MYSELF g/valorant").execute(model);

        assertEquals(original, model.getGameMates().getMyself());
        assertEquals("Deleted game valorant from Alex.", deleted.getFeedbackToUser());
        assertTrue(model.getFilteredFriendList().isEmpty());
        storage.saveGameMates(model.getGameMates());
        assertEquals(model.getGameMates(), storage.readGameMates().get());
    }

    @Test
    public void execute_unnamedProfile_requiresInitializationAndPreservesFriendsAndFilter() throws Exception {
        Friend sameName = new FriendBuilder().withName("Myself").withGames(valorant).build();
        Friend displayed = new FriendBuilder().withName("Melody").build();
        model.addFriend(sameName);
        model.addFriend(displayed);
        model.updateFilteredFriendList(friend -> friend.equals(displayed));
        for (String command : List.of("gamelist myself", "gameadd myself g/Valorant",
                "gameedit myself g/Valorant u/player", "gamedel myself g/Valorant")) {
            assertCommandFailure(parser.parseCommand(command), model, Messages.MESSAGE_MYSELF_NOT_INITIALIZED);
        }
        parser.parseCommand("edit myself n/Alex").execute(model);
        Friend original = model.getGameMates().getMyself();

        parser.parseCommand("gameadd myself g/Valorant u/player").execute(model);
        assertEquals(new FriendBuilder(original).withGames(valorant).build(), model.getGameMates().getMyself());
        assertEquals(List.of(sameName, displayed), model.getGameMates().getFriendList());
        assertEquals(List.of(displayed), model.getFilteredFriendList());

        model.updateFilteredFriendList(friend -> false);
        parser.parseCommand("gamedel myself g/Valorant").execute(model);
        assertEquals(original, model.getGameMates().getMyself());
        assertEquals(List.of(sameName, displayed), model.getGameMates().getFriendList());
        assertTrue(model.getFilteredFriendList().isEmpty());
    }

    @Test
    public void execute_duplicateOrMissingMyselfGame_rejectsWithoutChangingData() throws Exception {
        model.setMyself(new FriendBuilder().withName("Alex").withGames(valorant).build());
        Friend friend = new FriendBuilder().withGames(minecraft).build();
        model.addFriend(friend);
        model.updateFilteredFriendList(target -> false);
        assertCommandFailure(parser.parseCommand("gameadd myself g/valorant u/other"), model,
                "Alex already has valorant.");
        assertCommandFailure(parser.parseCommand("gamedel myself g/Minecraft"), model,
                "Alex does not have Minecraft.");
    }
}
