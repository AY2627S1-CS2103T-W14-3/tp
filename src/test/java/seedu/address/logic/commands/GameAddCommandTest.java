package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFriendAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.testutil.FriendBuilder;

/**
 * Tests game additions, duplicate detection, and displayed-list indexing.
 */
public class GameAddCommandTest {
    private final Model model = new ModelManager(getTypicalGameMates(), new UserPrefs());
    private final Game game = new Game(new GameName("Valorant"), new Username("benergy10"));
    private final Index first = Index.fromOneBased(1);

    @Test
    public void execute_validFriend_preservesOtherFieldsAndGames() {
        Friend original = model.getFilteredFriendList().get(0);
        Game existing = new Game(new GameName("Minecraft"), new Username("builder"));
        Friend withGame = new FriendBuilder(original).withGames(existing).build();
        model.setFriend(original, withGame);
        Friend updated = new FriendBuilder(withGame).withGames(existing, game).build();
        Model expected = new ModelManager(new GameMates(model.getGameMates()), new UserPrefs());
        expected.setFriend(withGame, updated);
        assertCommandSuccess(new GameAddCommand(first, game), model,
                "Added Valorant to " + original.getName() + "'s games.\nUsername: benergy10", expected);
    }

    @Test
    public void execute_filteredList_targetsDisplayedFriendAndPreservesFilter() {
        Index second = Index.fromOneBased(2);
        Friend target = model.getFilteredFriendList().get(1);
        showFriendAtIndex(model, second);
        Model expected = new ModelManager(new GameMates(model.getGameMates()), new UserPrefs());
        expected.setFriend(target, new FriendBuilder(target).withGames(game).build());
        showFriendAtIndex(expected, second);
        assertCommandSuccess(new GameAddCommand(first, game), model,
                "Added Valorant to " + target.getName() + "'s games.\nUsername: benergy10", expected);
        assertEquals(1, model.getFilteredFriendList().size());
    }

    @Test
    public void execute_duplicateNameWithDifferentUsername_failure() {
        Friend original = model.getFilteredFriendList().get(0);
        model.setFriend(original, new FriendBuilder(original).withGames(game).build());
        Game duplicate = new Game(new GameName(" valorant  "), new Username("other"));
        assertCommandFailure(new GameAddCommand(first, duplicate), model,
                original.getName() + " already has valorant.");
        assertCommandFailure(new GameAddCommand(first, game), model,
                original.getName() + " already has Valorant.");
    }

    @Test
    public void execute_invalidDisplayedIndex_failure() {
        int size = model.getFilteredFriendList().size();
        assertCommandFailure(new GameAddCommand(Index.fromOneBased(size + 1), game), model,
                Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
        showFriendAtIndex(model, first);
        assertCommandFailure(new GameAddCommand(Index.fromOneBased(2), game), model,
                Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
        model.updateFilteredFriendList(friend -> false);
        assertCommandFailure(new GameAddCommand(first, game), model, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }

    @Test
    public void equalsAndToString() {
        GameAddCommand command = new GameAddCommand(first, game);
        assertEquals(command, new GameAddCommand(first, game));
        assertEquals(command, command);
        assertNotEquals(null, command);
        assertNotEquals("gameadd", command);
        assertNotEquals(command, new GameAddCommand(Index.fromOneBased(2), game));
        assertNotEquals(command, new GameAddCommand(first,
                new Game(new GameName("Minecraft"), new Username("player"))));
        assertEquals(GameAddCommand.class.getCanonicalName() + "{targetIndex=" + first + ", toAdd=" + game + "}",
                command.toString());
        assertThrows(NullPointerException.class, () -> new GameAddCommand(null, game));
        assertThrows(NullPointerException.class, () -> new GameAddCommand(first, null));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
}
