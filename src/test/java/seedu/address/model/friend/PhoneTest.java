package seedu.address.model.friend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        String[] invalidPhones = {
            "", " ", "12", "+", "+1", "+0123", "+(0)123", "1234567890123456", "+1234567890123456",
            "１２３４５６７８９０", "1234567890\n", "\t1234567890", " 1234567890", "1234567890 ",
            "123-456-789a", "123/456/7890", "123  456 7890", "123\t4567890", "123--456-7890",
            "(123 456)7890", "((123))4567890", "(1234567890", "123)4567890", "()123",
            "123-456-7890 ext 123", "1234567890#123", "-123", "123-", "++6512345678", "65+12345678", "+ 123"
        };
        for (String phone : invalidPhones) {
            assertFalse(Phone.isValidPhone(phone), phone);
            assertThrows(IllegalArgumentException.class, Phone.MESSAGE_CONSTRAINTS, () -> new Phone(phone));
        }
    }

    @Test
    public void isValidPhone_supportedFormatting() {
        String[] validPhones = {
            "91234567", "+6591234567", "+65 9123-4567", "+65 (9123)-4567", "(123) 456-7890",
            "123 456 7890", "(123)", "(123)(456)7890", "+(65)91234567", "000",
            "123456789012345", "+12", "+123456789012345"
        };
        for (String phone : validPhones) {
            assertTrue(Phone.isValidPhone(phone), phone);
            assertEquals(phone, new Phone(phone).value);
        }
    }

    @Test
    public void isValidPhone_digitLimits_ignoreFormatting() {
        assertFalse(Phone.isValidPhone("(1)-2"));
        assertTrue(Phone.isValidPhone("(1)-2 3"));
        assertTrue(Phone.isValidPhone("(12345)-67890 12345"));
        assertFalse(Phone.isValidPhone("(12345)-67890 123456"));
        assertFalse(Phone.isValidPhone("+(1)"));
        assertTrue(Phone.isValidPhone("+(1)-2"));
        assertTrue(Phone.isValidPhone("+(12345)-67890 12345"));
        assertFalse(Phone.isValidPhone("+(12345)-67890 123456"));
    }

    @Test
    public void isValidPhone_malformedFormatting_returnsFalse() {
        String[] invalidPhones = {
            "123.456.7890", "+65.9123.4567", "123.456",
            "123.-456", "123 -456", "123- 456", "123..456", "123 .456", "123. 456",
            ".123", "123.", "+-123", "+.123", "+( 123)", "+(123 )", "(123-456)", "(123.456)",
            "(123)()456", "123()456", "123(456", "123(456))", "(123))456", "(123)+(456)",
            "123\r", "123\r\n", "123\u0085", "123\u2028", "123\u2029", "123\u00a0456",
            "123_456", "123,456", "123;456", "123\\456", "123*456", "123x456"
        };
        for (String phone : invalidPhones) {
            assertFalse(Phone.isValidPhone(phone), phone);
        }
    }

    @Test
    public void equals() {
        Phone phone = new Phone("1234567890");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("1234567890")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("9876543210")));
    }
}
