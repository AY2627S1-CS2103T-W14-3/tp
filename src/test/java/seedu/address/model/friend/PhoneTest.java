package seedu.address.model.friend;

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

        // Invalid formats, including national numbers and unsupported separators/extensions.
        String[] invalidPhones = {
            "", " ", "+", "+1", "12", "+0", "+06591234567", "++6591234567", "+65+91234567", "9123+4567",
            "９１２３４５６７", "91234567\n", "\t91234567", "+65９１２３４５６７", "9123456a", "-91234567",
            "+65 91234567", "+65(9123)4567", "+65-9123-4567", "+65.91234567", "+6591234567;123",
            "+6591234567#123", "+6591234567 ext 123", "+659123456a", "+６５９１２３４５６７",
            "+65\t91234567", "+6591234567\n", " +6591234567", "+6591234567 "
        };
        for (String phone : invalidPhones) {
            assertFalse(Phone.isValidPhone(phone), phone);
        }

        // Valid international formats; assignment and national number lengths are not checked.
        assertTrue(Phone.isValidPhone("+6591234567"));
        assertTrue(Phone.isValidPhone("+14155552671"));
        assertTrue(Phone.isValidPhone("+442071838750"));

        // Valid plain numbers without a '+' prefix
        assertTrue(Phone.isValidPhone("91234567"));
        assertTrue(Phone.isValidPhone("911"));
        assertTrue(Phone.isValidPhone("006591234567"));
    }

    @Test
    public void isValidPhone_lengthBoundaries() {
        assertTrue(Phone.isValidPhone("+12")); // minimum accepted syntax length, not a verified number
        assertTrue(Phone.isValidPhone("+" + "1".repeat(15))); // '+' is excluded from the digit limit
        assertFalse(Phone.isValidPhone("+" + "1".repeat(16)));
        assertTrue(Phone.isValidPhone("123")); // minimum plain number length
        assertFalse(Phone.isValidPhone("12"));
        assertTrue(Phone.isValidPhone("1".repeat(15)));
        assertFalse(Phone.isValidPhone("1".repeat(16)));
    }

    @Test
    public void equals() {
        Phone phone = new Phone("+6591234567");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("+6591234567")));

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("+6598765432")));
    }
}
