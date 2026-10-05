package seedu.address.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class GameNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GameName(null));
    }

    @Test
    public void constructor_invalidGameName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new GameName(""));
    }

    @Test
    public void isValidGameName() {
        // invalid game names
        assertFalse(GameName.isValidGameName(null));
        assertFalse(GameName.isValidGameName("")); // empty string
        assertFalse(GameName.isValidGameName("   ")); // spaces only
        assertFalse(GameName.isValidGameName("!!!")); // no letter or digit
        assertFalse(GameName.isValidGameName("A".repeat(GameName.MAX_GAME_NAME_LENGTH + 1))); // 51 characters
        assertFalse(GameName.isValidGameName("Valorant\nMinecraft")); // contains newline

        // valid game names
        assertTrue(GameName.isValidGameName("Valorant"));
        assertTrue(GameName.isValidGameName("12345")); // digits are allowed
        assertTrue(GameName.isValidGameName("Counter-Strike 2")); // punctuation and spaces are allowed
        assertTrue(GameName.isValidGameName("  League of Legends  ")); // outer spaces are trimmed
        assertTrue(GameName.isValidGameName("Pokémon")); // printable Unicode characters are allowed
    }

    @Test
    public void constructor_trimsOuterSpaces() {
        GameName gameName = new GameName("  Valorant  ");

        assertEquals("Valorant", gameName.value);
    }

    @Test
    public void equals() {
        GameName gameName = new GameName("Valorant");

        assertEquals(new GameName("VALORANT"), gameName);
        assertEquals(new GameName(" valorant "), gameName);
        assertNotEquals(new GameName("Minecraft"), gameName);
        assertNotEquals(null, gameName);
    }

    @Test
    public void hashCode_sameNormalizedName_returnsSameHashCode() {
        GameName firstGameName = new GameName("Valorant");
        GameName secondGameName = new GameName(" VALORANT ");

        assertEquals(firstGameName.hashCode(), secondGameName.hashCode());
    }

    @Test
    public void toString_returnsValue() {
        assertEquals("Valorant", new GameName("Valorant").toString());
    }
}
