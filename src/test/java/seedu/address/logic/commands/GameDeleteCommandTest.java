package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
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

public class GameDeleteCommandTest {
    private final Model model = new ModelManager(getTypicalGameMates(), new UserPrefs());

    @Test
    public void execute_existingGameCaseInsensitive_success() {
        Friend original = model.getFilteredFriendList().getFirst();
        Game minecraft = new Game(new GameName("Minecraft"), new Username("builder"));
        Friend withGame = new FriendBuilder(original).withGames(minecraft).build();
        model.setFriend(original, withGame);
        Model expected = new ModelManager(new GameMates(model.getGameMates()), new UserPrefs());
        expected.setFriend(withGame, new FriendBuilder(withGame).withGames().build());

        assertCommandSuccess(new GameDeleteCommand(Index.fromOneBased(1), new GameName("minecraft")), model,
                "Deleted game minecraft from " + original.getName() + ".", expected);
    }

    @Test
    public void execute_missingGame_failure() {
        Friend friend = model.getFilteredFriendList().getFirst();
        assertCommandFailure(new GameDeleteCommand(Index.fromOneBased(1), new GameName("Minecraft")), model,
                String.format(GameDeleteCommand.MESSAGE_GAME_NOT_FOUND, friend.getName(), "Minecraft"));
    }

    @Test
    public void execute_invalidFriendIndex_failure() {
        assertCommandFailure(new GameDeleteCommand(Index.fromOneBased(model.getFilteredFriendList().size() + 1),
                new GameName("Minecraft")), model, Messages.MESSAGE_INVALID_FRIEND_DISPLAYED_INDEX);
    }
}
