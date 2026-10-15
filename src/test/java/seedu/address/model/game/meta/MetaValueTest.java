package seedu.address.model.game.meta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests validation, normalization and equality of metadata values.
 */
public class MetaValueTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MetaValue(null));
        assertFalse(MetaValue.isValidMetaValue(null));
    }

    @Test
    public void validation_invalidInputs_rejected() {
        String[] invalid = {"", "   ", "!!!", "A".repeat(201), "a\nb", "a\tb", "a\u0000b",
            "a\u007fb", "a\u0085b"};
        for (String input : invalid) {
            assertFalse(MetaValue.isValidMetaValue(input));
            assertThrows(IllegalArgumentException.class, () -> new MetaValue(input));
        }
    }

    @Test
    public void validation_validInputs_accepted() {
        String[] valid = {"A", "1", "Preferred role!", "Pokémon", "角色", "a😀", "A".repeat(200),
            "  " + "A".repeat(200) + "  ", "\uD801\uDC00".repeat(100), "Time:18:00"};
        for (String input : valid) {
            assertTrue(MetaValue.isValidMetaValue(input));
            assertEquals(input.strip(), new MetaValue(input).value);
        }
        assertFalse(MetaValue.isValidMetaValue("\uD801\uDC00".repeat(100) + "A"));
    }

    @Test
    public void validation_nonControlUnicode_accepted() {
        for (String input : new String[] {"a\u200bb", "a\u2028b", "a\u2029b", "a\ud800b", "a\u0378b"}) {
            assertTrue(MetaValue.isValidMetaValue(input));
        }
    }

    @Test
    public void equalsAndHashCode_normalizedFields_consistent() {
        MetaValue field = new MetaValue("Role");
        MetaValue equalField = new MetaValue(" Role ");
        assertEquals(field, field);
        assertEquals(field, equalField);
        assertEquals(field.hashCode(), equalField.hashCode());
        assertNotEquals(field, new MetaValue("Other"));
        assertNotEquals(field, null);
        assertNotEquals(field, "Role");
        assertNotEquals(field, new MetaValue("ROLE"));
        assertEquals("Role", field.toString());
    }
}
