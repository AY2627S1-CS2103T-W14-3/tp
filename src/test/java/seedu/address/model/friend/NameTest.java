package seedu.address.model.friend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("a".repeat(Name.MAX_LENGTH + 1))); // exceeds maximum length
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("O'Connor-Jane")); // punctuation is not allowed
        assertFalse(Name.isValidName("李明")); // non-ASCII characters are not allowed
        assertFalse(Name.isValidName("peter\tjack")); // only spaces are allowed as whitespace
        assertFalse(Name.isValidName("peter\njack"));

        // valid name
        assertTrue(Name.isValidName("a")); // minimum length
        assertTrue(Name.isValidName("a".repeat(Name.MAX_LENGTH))); // maximum length
        assertTrue(Name.isValidName("  " + "a".repeat(Name.MAX_LENGTH) + "  ")); // trimmed before checking length
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
    }

    @Test
    public void constructor_whitespace_trimsAndPreservesCase() {
        assertEquals("Melody", new Name("  Melody  ").fullName);
    }

    @Test
    public void equals_ignoresCaseAndSurroundingWhitespace() {
        Name name = new Name("Melody");
        Name equivalentName = new Name("  melody  ");
        assertEquals(name, equivalentName);
        assertEquals(name.hashCode(), equivalentName.hashCode());
    }

    @Test
    public void equals_ignoresCaseAndRepeatedSpace() {
        Name name = new Name("John Doe");
        Name variant = new Name("john   doe ");
        assertTrue(name.equals(variant));
        assertTrue(name.equals(new Name("john doe")));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
