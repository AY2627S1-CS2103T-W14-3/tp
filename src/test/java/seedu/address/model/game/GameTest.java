package seedu.address.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Game(null));
    }

    @Test
    public void constructor_invalidGameName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Game(""));
    }

    @Test
    public void isValidGameName() {
        // null game name
        assertFalse(Game.isValidGameName(null));

        // invalid game names
        assertFalse(Game.isValidGameName("")); // empty string
        assertFalse(Game.isValidGameName("   ")); // spaces only
        assertFalse(Game.isValidGameName("!!!")); // no letter or digit
        assertFalse(Game.isValidGameName("A".repeat(Game.MAX_GAME_NAME_LENGTH + 1))); // 51 characters
        assertFalse(Game.isValidGameName("Valorant\nMinecraft")); // contains newline

        // valid game names
        assertTrue(Game.isValidGameName("Valorant"));
        assertTrue(Game.isValidGameName("12345")); // digits are allowed
        assertTrue(Game.isValidGameName("Counter-Strike 2")); // punctuation and spaces are allowed
        assertTrue(Game.isValidGameName("  League of Legends  ")); // outer spaces are trimmed
        assertTrue(Game.isValidGameName("Pokémon")); // printable Unicode characters are allowed
    }

    @Test
    public void constructor_trimsOuterSpaces() {
        Game game = new Game("  Valorant  ");

        assertEquals("Valorant", game.gameName);
    }

    @Test
    public void equals() {
        Game game = new Game("Valorant");

        // same value -> returns true
        assertEquals(new Game("Valorant"), game);

        // same value, different capitalisation -> returns true
        assertEquals(new Game("VALORANT"), game);

        // same value, with outer spaces -> returns true
        assertEquals(new Game(" valorant "), game);

        // null -> returns false
        assertNotEquals(null, game);

        // different type -> returns false
        Object differentType = 5.0f;
        assertNotEquals(differentType, game);

        // different value -> returns false
        assertNotEquals(new Game("Minecraft"), game);
    }

    @Test
    public void hashCode_sameNormalizedName_returnsSameHashCode() {
        Game firstGame = new Game("Valorant");
        Game secondGame = new Game(" VALORANT ");

        assertEquals(firstGame.hashCode(), secondGame.hashCode());
    }

    @Test
    public void toString_returnsBracketedGameName() {
        Game game = new Game("Valorant");

        assertEquals("[Valorant]", game.toString());
    }
}
