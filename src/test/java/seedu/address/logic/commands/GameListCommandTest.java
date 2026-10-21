package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_FRIEND;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.GameNameMatchesPredicate;
import seedu.address.model.friend.NameContainsKeywordsPredicate;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

public class GameListCommandTest {

    private final Game valorant = new GameBuilder().withGameName("Valorant").withUsername("alice123").build();
    private final Game minecraft = new GameBuilder().withGameName("Minecraft").build();
    private final Friend alice = new FriendBuilder().withName("Alice").withTags("teammate")
            .withGames(valorant, minecraft).build();
    private final Friend bob = new FriendBuilder().withName("Bob").withGames(minecraft).build();
    private final Friend carl = new FriendBuilder().withName("Carl").build();
    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        GameMates gameMates = new GameMates();
        gameMates.addFriend(alice);
        gameMates.addFriend(bob);
        gameMates.addFriend(carl);
        gameMates.setMyself(new FriendBuilder().withName("Joash").withGames(valorant).build());
        model = new ModelManager(gameMates, new UserPrefs());
        expectedModel = new ModelManager(gameMates, new UserPrefs());
    }

    @Test
    public void execute_noFilter_listsAllGames() {
        assertCommandSuccess(new GameListCommand(INDEX_FIRST_FRIEND), model,
                new CommandResult("Listed 2 game(s) that Alice plays:", alice), expectedModel);
    }

    @Test
    public void execute_matchingFilter_returnsOriginalFriendAndFilter() throws Exception {
        GameName filter = new GameName("valorant");
        GameListCommand command = new GameListCommand(INDEX_FIRST_FRIEND, filter);
        assertCommandSuccess(command, model,
                new CommandResult("Showing Valorant for Alice:", alice, filter), expectedModel);
        CommandResult result = command.execute(model);
        assertSame(alice, result.getFriendToShow().orElseThrow());
        assertEquals(2, result.getFriendToShow().orElseThrow().getGames().size());
    }

    @Test
    public void execute_unmatchedOrPartialFilter_successfulEmptyResult() {
        for (String name : List.of("Val", "Fortnite")) {
            GameName filter = new GameName(name);
            assertCommandSuccess(new GameListCommand(INDEX_FIRST_FRIEND, filter), model,
                    new CommandResult("Alice does not have " + name + ".", alice, filter), expectedModel);
        }
    }

    @Test
    public void execute_friendWithNoGames_successfulEmptyResult() {
        Index index = Index.fromOneBased(3);
        assertCommandSuccess(new GameListCommand(index), model,
                new CommandResult("Carl has no games added yet.", carl), expectedModel);
        GameName filter = new GameName("Valorant");
        assertCommandSuccess(new GameListCommand(index, filter), model,
                new CommandResult("Carl does not have Valorant.", carl, filter), expectedModel);
    }

    @Test
    public void execute_afterFind_usesDisplayedIndexAndPreservesFilter() {
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Bob"));
        new FindCommand(predicate).execute(model);
        expectedModel.updateSearchFriendList(predicate);

        assertCommandSuccess(new GameListCommand(INDEX_FIRST_FRIEND), model,
                new CommandResult("Listed 1 game(s) that Bob plays:", bob), expectedModel);
        assertEquals(List.of(bob), model.getFilteredFriendList());
    }

    @Test
    public void execute_afterGameBasedFriendFilter_usesDisplayedIndexAndPreservesFilter() {
        GameMates gameMates = new GameMates(model.getGameMates());
        gameMates.setFriends(List.of(carl, bob, alice));
        model = new ModelManager(gameMates, new UserPrefs());
        expectedModel = new ModelManager(gameMates, new UserPrefs());
        GameNameMatchesPredicate predicate = new GameNameMatchesPredicate(new GameName("Minecraft"));
        new ListCommand(predicate).execute(model);
        expectedModel.updateFilteredFriendList(predicate);
        GameName filter = new GameName("minecraft");

        assertCommandSuccess(new GameListCommand(INDEX_FIRST_FRIEND, filter), model,
                new CommandResult("Showing Minecraft for Bob:", bob, filter), expectedModel);
        assertEquals(List.of(bob, alice), model.getFilteredFriendList());
    }

    @Test
    public void execute_outOfRangeIndex_failure() {
        assertCommandFailure(new GameListCommand(Index.fromOneBased(4)), model,
                "No friend exists at index 4. Choose an index from 1 to 3.");
        assertCommandFailure(new GameListCommand(Index.fromOneBased(Integer.MAX_VALUE)), model,
                "No friend exists at index 2147483647. Choose an index from 1 to 3.");
        new FindCommand(new NameContainsKeywordsPredicate(List.of("Bob"))).execute(model);
        assertCommandFailure(new GameListCommand(INDEX_SECOND_FRIEND), model,
                "No friend exists at index 2. Choose an index from 1 to 1.");
    }

    @Test
    public void execute_emptyDisplayedList_failure() {
        Model emptyModel = new ModelManager();
        assertCommandFailure(new GameListCommand(INDEX_FIRST_FRIEND), emptyModel,
                "No friends are currently displayed. Use list to show all friends.");
        new FindCommand(new NameContainsKeywordsPredicate(List.of("Unknown"))).execute(model);
        assertCommandFailure(new GameListCommand(INDEX_FIRST_FRIEND, new GameName("Valorant")), model,
                "No friends are currently displayed. Use list to show all friends.");
    }

    @Test
    public void execute_unfilteredAfterFiltered_clearsGameFilterPayload() throws Exception {
        new GameListCommand(INDEX_FIRST_FRIEND, new GameName("Valorant")).execute(model);
        assertCommandSuccess(new GameListCommand(INDEX_FIRST_FRIEND), model,
                new CommandResult("Listed 2 game(s) that Alice plays:", alice), expectedModel);
        assertEquals(expectedModel.getGameMates().getMyself(), model.getGameMates().getMyself());
    }

    @Test
    public void equalsAndHashCode() {
        GameListCommand command = new GameListCommand(INDEX_FIRST_FRIEND, new GameName("Valorant"));
        GameListCommand copy = new GameListCommand(INDEX_FIRST_FRIEND, new GameName("valorant"));
        assertEquals(command, command);
        assertEquals(command, copy);
        assertEquals(command.hashCode(), copy.hashCode());
        assertNotEquals(command, new GameListCommand(INDEX_SECOND_FRIEND, new GameName("Valorant")));
        assertNotEquals(command, new GameListCommand(INDEX_FIRST_FRIEND, new GameName("Minecraft")));
        assertNotEquals(command, new GameListCommand(INDEX_FIRST_FRIEND));
        assertNotEquals(command, null);
        assertNotEquals(command, "gamelist");
    }

    @Test
    public void toStringMethod() {
        GameListCommand command = new GameListCommand(INDEX_FIRST_FRIEND, new GameName("Valorant"));
        assertEquals(GameListCommand.class.getCanonicalName() + "{friendIndex=" + INDEX_FIRST_FRIEND
                + ", gameFilter=Valorant}", command.toString());
    }
}
