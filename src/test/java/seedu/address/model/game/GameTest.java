package seedu.address.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GameTest {

    private static final GameName VALORANT = new GameName("Valorant");
    private static final GameName MINECRAFT = new GameName("Minecraft");
    private static final Username SHADOW_STRIKER = new Username("ShadowStrikerXx");
    private static final Username PIXEL_MINER = new Username("PixelMiner");

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Game(null, SHADOW_STRIKER));
        assertThrows(NullPointerException.class, () -> new Game(VALORANT, null));
    }

    @Test
    public void getters_returnFields() {
        Game game = new Game(VALORANT, SHADOW_STRIKER);

        assertEquals(VALORANT, game.getGameName());
        assertEquals(SHADOW_STRIKER, game.getUsername());
    }

    @Test
    public void isSameGame() {
        Game game = new Game(VALORANT, SHADOW_STRIKER);

        // same object -> returns true
        assertTrue(game.isSameGame(game));

        // null -> returns false
        assertFalse(game.isSameGame(null));

        // same game name, different username -> returns true
        assertTrue(game.isSameGame(new Game(new GameName("VALORANT"), PIXEL_MINER)));

        // different game name -> returns false
        assertFalse(game.isSameGame(new Game(MINECRAFT, SHADOW_STRIKER)));
    }

    @Test
    public void equals() {
        Game game = new Game(VALORANT, SHADOW_STRIKER);

        // same values -> returns true
        assertEquals(new Game(new GameName("VALORANT"), new Username(" ShadowStrikerXx ")), game);

        // null -> returns false
        assertNotEquals(null, game);

        // different game name -> returns false
        assertNotEquals(new Game(MINECRAFT, SHADOW_STRIKER), game);

        // different username -> returns false
        assertNotEquals(new Game(VALORANT, PIXEL_MINER), game);
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        Game firstGame = new Game(VALORANT, SHADOW_STRIKER);
        Game secondGame = new Game(new GameName(" VALORANT "), new Username(" ShadowStrikerXx "));

        assertEquals(firstGame.hashCode(), secondGame.hashCode());
    }

    @Test
    public void toString_returnsFieldsInExpectedFormat() {
        Game game = new Game(VALORANT, SHADOW_STRIKER);
        String expected = Game.class.getCanonicalName()
                + "{gameName=Valorant, username=ShadowStrikerXx}";

        assertEquals(expected, game.toString());
    }
}
