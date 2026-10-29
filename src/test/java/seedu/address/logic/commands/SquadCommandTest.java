package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.friend.Friend;
import seedu.address.model.game.GameName;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

public class SquadCommandTest {
    private static final GameName VALORANT = new GameName("Valorant");
    private final Model model = new ModelManager();

    @Test
    public void execute_sizeOne_alwaysIncludesMe() throws CommandException {
        assertCommandSuccess(new SquadCommand(VALORANT, 1), model, "Found squad of 1 person:\n1. Me",
                new ModelManager());
        model.setMyself(player("Own Profile", "Minecraft", "builder"));
        assertEquals("Found squad of 1 person:\n1. Me",
                new SquadCommand(VALORANT, 1).execute(model).getFeedbackToUser());
        model.setMyself(player("Own Profile", "VALORANT", "myUsername"));
        assertEquals("Found squad of 1 person:\n1. Me (username: myUsername)",
                new SquadCommand(VALORANT, 1).execute(model).getFeedbackToUser());
    }

    @Test
    public void execute_filteredFriends_usesDisplayedIndicesAndPreservesState() throws CommandException {
        Friend hidden = player("Hidden", "Valorant", "hidden");
        Friend alpha = player("Alpha", "VALORANT", "alpha");
        Friend nonPlayer = player("Other", "Minecraft", "builder");
        Friend beta = player("Beta", "Valorant", "beta");
        model.addFriend(hidden);
        model.addFriend(alpha);
        model.addFriend(nonPlayer);
        model.addFriend(beta);
        model.setMyself(player("Own Profile", "Valorant", "own"));
        model.updateFilteredFriendList(friend -> !friend.equals(hidden));
        GameMates before = new GameMates(model.getGameMates());
        List<Friend> displayedBefore = new ArrayList<>(model.getFilteredFriendList());

        assertEquals("Found squad of 3 people:\n1. Me (username: own)"
                + "\n2. Alpha (username: alpha, index: 1)\n3. Beta (username: beta, index: 3)",
                new SquadCommand(new GameName("valorant"), 3, new Random(0)).execute(model).getFeedbackToUser());
        assertEquals(before, model.getGameMates());
        assertEquals(displayedBefore, model.getFilteredFriendList());
        assertCommandFailure(new SquadCommand(VALORANT, 4), model, "Could not find a squad of 4 people.");
        assertEquals(before, model.getGameMates());
        assertEquals(displayedBefore, model.getFilteredFriendList());
    }

    @Test
    public void execute_notEnoughCandidates_throwsCommandException() {
        assertCommandFailure(new SquadCommand(VALORANT, 2), model, "Could not find a squad of 2 people.");
        model.addFriend(player("Partial", "Val", "player"));
        assertCommandFailure(new SquadCommand(VALORANT, 2), model, "Could not find a squad of 2 people.");
        model.addFriend(player("Exact", "Valorant", "player"));
        model.updateFilteredFriendList(friend -> false);
        assertCommandFailure(new SquadCommand(VALORANT, 2), model, "Could not find a squad of 2 people.");
    }

    @Test
    public void execute_maximumSize_includesNineteenDistinctFriendsInOrder() throws CommandException {
        for (int i = 1; i <= 19; i++) {
            model.addFriend(player("Player " + i, "Valorant", "user" + i));
        }
        List<String> rows = new SquadCommand(VALORANT, 20, new Random(0))
                .execute(model).getFeedbackToUser().lines().toList();
        assertEquals(21, rows.size());
        assertEquals("1. Me", rows.get(1));
        for (int i = 1; i <= 19; i++) {
            assertEquals((i + 1) + ". Player " + i + " (username: user" + i + ", index: " + i + ")",
                    rows.get(i + 1));
        }
    }

    @Test
    public void execute_controlledRandomness_selectsDistinctSubsetThenSorts() throws CommandException {
        for (int i = 1; i <= 4; i++) {
            model.addFriend(player("Player " + i, "Valorant", "user" + i));
        }
        // Fisher-Yates with zero as each chosen position rotates the first candidate to the end.
        Random random = new Random(0) {
            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };
        String result = new SquadCommand(VALORANT, 3, random).execute(model).getFeedbackToUser();
        assertEquals("Found squad of 3 people:\n1. Me\n2. Player 2 (username: user2, index: 2)"
                + "\n3. Player 3 (username: user3, index: 3)", result);
        assertEquals(new SquadCommand(VALORANT, 3, new Random(42)).execute(model),
                new SquadCommand(VALORANT, 3, new Random(42)).execute(model));
    }

    @Test
    public void constructorValidationAndEquality() {
        assertThrows(IllegalArgumentException.class, () -> new SquadCommand(VALORANT, 0));
        assertThrows(IllegalArgumentException.class, () -> new SquadCommand(VALORANT, 21));
        assertThrows(NullPointerException.class, () -> new SquadCommand(null, 1));
        assertThrows(NullPointerException.class, () -> new SquadCommand(VALORANT, 1, null));
        SquadCommand command = new SquadCommand(VALORANT, 3);
        assertThrows(NullPointerException.class, () -> command.execute(null));
        assertEquals(command, new SquadCommand(new GameName("valorant"), 3));
        assertNotEquals(command, new SquadCommand(VALORANT, 2));
        assertNotEquals(command, new SquadCommand(new GameName("Minecraft"), 3));
        assertNotEquals(command, null);
        assertNotEquals(command, "squad");
        assertTrue(command.toString().contains("size=3"));
    }

    private Friend player(String name, String game, String username) {
        return new FriendBuilder().withName(name).withGames(
                new GameBuilder().withGameName(game).withUsername(username).build()).build();
    }
}
