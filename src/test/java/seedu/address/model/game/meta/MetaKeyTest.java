package seedu.address.model.game.meta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests validation, normalization and equality of metadata keys.
 */
public class MetaKeyTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MetaKey(null));
        assertFalse(MetaKey.isValidMetaKey(null));
    }

    @Test
    public void validation_invalidInputs_rejected() {
        String[] invalid = {"", "   ", "!!!", "A".repeat(51), "a\nb", "a\tb", "a\u0000b",
            "a\u007fb", "a\u0085b", "Role:Type"};
        for (String input : invalid) {
            assertFalse(MetaKey.isValidMetaKey(input));
            assertThrows(IllegalArgumentException.class, () -> new MetaKey(input));
        }
    }

    @Test
    public void validation_validInputs_accepted() {
        String[] valid = {"A", "1", "Preferred role!", "Pokémon", "角色", "a😀", "A".repeat(50),
            "  " + "A".repeat(50) + "  ", "\uD801\uDC00".repeat(25)};
        for (String input : valid) {
            assertTrue(MetaKey.isValidMetaKey(input));
            assertEquals(input.strip(), new MetaKey(input).value);
        }
        assertFalse(MetaKey.isValidMetaKey("\uD801\uDC00".repeat(25) + "A"));
    }

    @Test
    public void validation_nonControlUnicode_accepted() {
        for (String input : new String[] {"a\u200bb", "a\u2028b", "a\u2029b", "a\ud800b", "a\u0378b"}) {
            assertTrue(MetaKey.isValidMetaKey(input));
        }
    }

    @Test
    public void equalsAndHashCode_turkishLocale_remainsCaseInsensitive() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            MetaKey upperCase = new MetaKey("TITLE");
            MetaKey lowerCase = new MetaKey(" title ");
            assertEquals(upperCase, lowerCase);
            assertEquals(upperCase.hashCode(), lowerCase.hashCode());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void equalsAndHashCode_normalizedFields_consistent() {
        MetaKey field = new MetaKey("Role");
        MetaKey equalField = new MetaKey(" ROLE ");
        assertEquals(field, field);
        assertEquals(field, equalField);
        assertEquals(field.hashCode(), equalField.hashCode());
        assertNotEquals(field, new MetaKey("Other"));
        assertNotEquals(field, null);
        assertNotEquals(field, "Role");

        assertEquals("Role", field.toString());
    }
}
