package seedu.address.model.friend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.testutil.FriendBuilder;

public class GameNameMatchesPredicateTest {

    private static final Game VALORANT = new Game(new GameName("Valorant"), new Username("playerOne"));
    private static final Game MINECRAFT = new Game(new GameName("Minecraft"), new Username("builderTwo"));

    @Test
    public void test_friendHasMatchingGame_returnsTrue() {
        GameNameMatchesPredicate predicate = new GameNameMatchesPredicate(new GameName("VALORANT"));

        assertTrue(predicate.test(new FriendBuilder().withGames(VALORANT).build()));
        assertTrue(predicate.test(new FriendBuilder().withGames(MINECRAFT, VALORANT).build()));
    }

    @Test
    public void test_friendDoesNotHaveMatchingGame_returnsFalse() {
        GameNameMatchesPredicate predicate = new GameNameMatchesPredicate(new GameName("Valorant"));
        Game usernameOnlyMatch = new Game(new GameName("Dota 2"), new Username("Valorant"));

        assertFalse(predicate.test(new FriendBuilder().build()));
        assertFalse(predicate.test(new FriendBuilder().withGames(MINECRAFT).build()));
        assertFalse(predicate.test(new FriendBuilder().withGames(usernameOnlyMatch).build()));
        assertFalse(new GameNameMatchesPredicate(new GameName("Val"))
                .test(new FriendBuilder().withGames(VALORANT).build()));
    }

    @Test
    public void equals() {
        GameNameMatchesPredicate predicate = new GameNameMatchesPredicate(new GameName("Valorant"));
        GameNameMatchesPredicate sameGame = new GameNameMatchesPredicate(new GameName("VALORANT"));
        GameNameMatchesPredicate differentGame = new GameNameMatchesPredicate(new GameName("Minecraft"));

        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(sameGame));
        assertFalse(predicate.equals(differentGame));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(1));
    }

    @Test
    public void toStringMethod() {
        GameName gameName = new GameName("Valorant");
        GameNameMatchesPredicate predicate = new GameNameMatchesPredicate(gameName);
        String expected = GameNameMatchesPredicate.class.getCanonicalName() + "{gameName=" + gameName + "}";

        assertEquals(expected, predicate.toString());
    }
}
