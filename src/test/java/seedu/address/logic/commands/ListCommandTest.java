package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_FRIENDS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFriendAtIndex;
import static seedu.address.testutil.TypicalFriends.ALICE;
import static seedu.address.testutil.TypicalFriends.BENSON;
import static seedu.address.testutil.TypicalFriends.CARL;
import static seedu.address.testutil.TypicalFriends.DANIEL;
import static seedu.address.testutil.TypicalFriends.getTypicalGameMates;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.GameNameMatchesPredicate;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.testutil.FriendBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalGameMates(), new UserPrefs());
        expectedModel = new ModelManager(model.getGameMates(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showFriendAtIndex(model, INDEX_FIRST_FRIEND);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_gameFilter_showsMatchingFriends() {
        Game valorant = new Game(new GameName("Valorant"), new Username("playerOne"));
        Game minecraft = new Game(new GameName("Minecraft"), new Username("builderTwo"));
        Friend playsMultipleGames = new FriendBuilder(ALICE).withGames(valorant, minecraft).build();
        Friend playsMatchingGame = new FriendBuilder(BENSON).withGames(
                new Game(new GameName("VALORANT"), new Username("playerThree"))).build();
        Friend playsDifferentGame = new FriendBuilder(CARL).withGames(minecraft).build();
        Friend hasNoGames = new FriendBuilder(DANIEL).build();
        GameMates gameMates = gameMatesWith(
                playsMultipleGames, playsMatchingGame, playsDifferentGame, hasNoGames);
        model = new ModelManager(gameMates, new UserPrefs());
        expectedModel = new ModelManager(gameMates, new UserPrefs());
        GameNameMatchesPredicate predicate = new GameNameMatchesPredicate(new GameName("valorant"));
        expectedModel.updateFilteredFriendList(predicate);

        String expectedMessage = String.format(MESSAGE_FRIENDS_LISTED_OVERVIEW, 2);
        assertCommandSuccess(new ListCommand(predicate), model, expectedMessage, expectedModel);
        assertEquals(List.of(playsMultipleGames, playsMatchingGame), model.getFilteredFriendList());
    }

    @Test
    public void execute_gameFilterHasNoMatches_showsEmptyList() {
        GameNameMatchesPredicate predicate = new GameNameMatchesPredicate(new GameName("Unknown Game"));
        expectedModel.updateFilteredFriendList(predicate);

        String expectedMessage = String.format(MESSAGE_FRIENDS_LISTED_OVERVIEW, 0);
        assertCommandSuccess(new ListCommand(predicate), model, expectedMessage, expectedModel);
        assertEquals(List.of(), model.getFilteredFriendList());
    }

    @Test
    public void execute_unfilteredListAfterGameFilter_showsEverything() throws Exception {
        new ListCommand(new GameNameMatchesPredicate(new GameName("Unknown Game"))).execute(model);

        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        GameNameMatchesPredicate valorant = new GameNameMatchesPredicate(new GameName("Valorant"));
        GameNameMatchesPredicate minecraft = new GameNameMatchesPredicate(new GameName("Minecraft"));
        ListCommand command = new ListCommand(valorant);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new ListCommand(
                new GameNameMatchesPredicate(new GameName("VALORANT")))));
        assertFalse(command.equals(new ListCommand(minecraft)));
        assertFalse(command.equals(new ListCommand()));
        assertFalse(command.equals(null));
        assertFalse(command.equals(1));
    }

    @Test
    public void toStringMethod() {
        GameNameMatchesPredicate predicate = new GameNameMatchesPredicate(new GameName("Valorant"));
        ListCommand command = new ListCommand(predicate);
        String expected = ListCommand.class.getCanonicalName()
                + "{predicate=" + predicate + ", isFilteredByGame=true}";

        assertEquals(expected, command.toString());
    }

    private static GameMates gameMatesWith(Friend... friends) {
        GameMates gameMates = new GameMates();
        for (Friend friend : friends) {
            gameMates.addFriend(friend);
        }
        return gameMates;
    }
}
