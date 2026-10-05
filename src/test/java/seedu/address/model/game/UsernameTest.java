package seedu.address.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class UsernameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Username(null));
    }

    @Test
    public void constructor_invalidUsername_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Username(""));
    }

    @Test
    public void isValidUsername() {
        // invalid usernames
        assertFalse(Username.isValidUsername(null));
        assertFalse(Username.isValidUsername("")); // empty string
        assertFalse(Username.isValidUsername("   ")); // spaces only
        assertFalse(Username.isValidUsername("!!!")); // no letter or digit
        assertFalse(Username.isValidUsername("A".repeat(Username.MAX_USERNAME_LENGTH + 1))); // 51 characters
        assertFalse(Username.isValidUsername("Shadow\nStriker")); // contains newline

        // valid usernames
        assertTrue(Username.isValidUsername("ShadowStrikerXx"));
        assertTrue(Username.isValidUsername("12345")); // digits are allowed
        assertTrue(Username.isValidUsername("player_one#2")); // punctuation is allowed
        assertTrue(Username.isValidUsername("  PixelMiner  ")); // outer spaces are trimmed
        assertTrue(Username.isValidUsername("PokémonPlayer")); // printable Unicode characters are allowed
    }

    @Test
    public void constructor_trimsOuterSpaces() {
        Username username = new Username("  ShadowStrikerXx  ");

        assertEquals("ShadowStrikerXx", username.value);
    }

    @Test
    public void equals() {
        Username username = new Username("ShadowStrikerXx");

        assertEquals(new Username(" ShadowStrikerXx "), username);
        assertNotEquals(new Username("SHADOWSTRIKERXX"), username);
        assertNotEquals(new Username("PixelMiner"), username);
        assertNotEquals(null, username);
    }

    @Test
    public void hashCode_sameUsername_returnsSameHashCode() {
        Username firstUsername = new Username("ShadowStrikerXx");
        Username secondUsername = new Username(" ShadowStrikerXx ");

        assertEquals(firstUsername.hashCode(), secondUsername.hashCode());
    }

    @Test
    public void toString_returnsValue() {
        assertEquals("ShadowStrikerXx", new Username("ShadowStrikerXx").toString());
    }
}
