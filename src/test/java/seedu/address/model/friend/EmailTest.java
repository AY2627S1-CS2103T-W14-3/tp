package seedu.address.model.friend;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmailTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    public void constructor_invalidEmail_throwsIllegalArgumentException() {
        String invalidEmail = "";
        assertThrows(IllegalArgumentException.class, () -> new Email(invalidEmail));
    }

    @Test
    public void isValidEmail() {
        // null email
        assertThrows(NullPointerException.class, () -> Email.isValidEmail(null));

        // blank email
        assertFalse(Email.isValidEmail("")); // empty string
        assertFalse(Email.isValidEmail(" ")); // spaces only

        // invalid email
        String[] invalidEmails = {
            "peterjackexample.com", "@", "@example.com", "peterjack@", "peter@@example.com",
            ".peter@example.com", "peter.@example.com", "peter..jack@example.com",
            "peter jack@example.com", "peter@example com", "peter@example.com\n", "peter\t@example.com",
            "peter@-example.com", "peter@example-.com", "peter@exam_ple.com", "peter@example..com",
            "peter@.example.com", "peter@example.com.", "péter@example.com", "peter@例子.com",
            "\"peter\"@example.com", "peter@[127.0.0.1]", " peter@example.com", "peter@example.com "
        };
        for (String email : invalidEmails) {
            assertFalse(Email.isValidEmail(email), email);
        }

        // valid email
        assertTrue(Email.isValidEmail("a@b")); // minimum length
        assertTrue(Email.isValidEmail("!#$%&'*+/=?^_`{|}~-@example.com")); // all supported special characters
        assertTrue(Email.isValidEmail("peter@my-domain.example")); // internal hyphen
        assertTrue(Email.isValidEmail("peter@xn--bcher-kva.example")); // ASCII-encoded international domain
        assertTrue(Email.isValidEmail("PeterJack_1190@example.com")); // underscore in local part
        assertTrue(Email.isValidEmail("PeterJack.1190@example.com")); // period in local part
        assertTrue(Email.isValidEmail("PeterJack+1190@example.com")); // '+' symbol in local part
        assertTrue(Email.isValidEmail("PeterJack-1190@example.com")); // hyphen in local part
        assertTrue(Email.isValidEmail("a@bc")); // minimal
        assertTrue(Email.isValidEmail("test@localhost")); // alphabets only
        assertTrue(Email.isValidEmail("123@145")); // numeric local part and domain name
        assertTrue(Email.isValidEmail("a1+be.d@example1.com")); // mixture of alphanumeric and special characters
        assertTrue(Email.isValidEmail("peter_jack@very-very-very-long-example.com")); // long domain name
        assertTrue(Email.isValidEmail("if.you.dream.it_you.can.do.it@example.com")); // long local part
        assertTrue(Email.isValidEmail("e1234567@u.nus.edu")); // more than one period in domain
    }

    @Test
    public void isValidEmail_lengthBoundaries() {
        assertTrue(Email.isValidEmail("a".repeat(64) + "@example.com"));
        assertFalse(Email.isValidEmail("a".repeat(65) + "@example.com"));
        assertTrue(Email.isValidEmail("a@" + "b".repeat(63) + ".com"));
        assertFalse(Email.isValidEmail("a@" + "b".repeat(64) + ".com"));
        assertFalse(Email.isValidEmail("a@example." + "b".repeat(64)));

        String maxLengthEmail = "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63)
                + "." + "d".repeat(61);
        assertTrue(Email.isValidEmail(maxLengthEmail)); // 254 characters
        assertFalse(Email.isValidEmail(maxLengthEmail + "d")); // 255 characters, otherwise valid
    }

    @Test
    public void equals() {
        Email email = new Email("valid@email");

        // same values -> returns true
        assertTrue(email.equals(new Email("valid@email")));

        // same object -> returns true
        assertTrue(email.equals(email));

        // null -> returns false
        assertFalse(email.equals(null));

        // different types -> returns false
        assertFalse(email.equals(5.0f));

        // different values -> returns false
        assertFalse(email.equals(new Email("other.valid@email")));
    }
}
