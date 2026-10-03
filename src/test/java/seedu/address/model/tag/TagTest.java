package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        // invalid tag names
        String[] invalidTagNames = {
            "", " ", "best friend", " friend", "friend ", "friend\n", "#friend", "best-friend", "best_friend",
            "friend!", "caf\u00e9", "\u670b\u53cb", "\uff11\uff12\uff13"
        };
        for (String tagName : invalidTagNames) {
            assertFalse(Tag.isValidTagName(tagName), tagName);
        }

        // valid tag names
        assertTrue(Tag.isValidTagName("a")); // single character
        assertTrue(Tag.isValidTagName("friend")); // letters only
        assertTrue(Tag.isValidTagName("FRIEND")); // upper case
        assertTrue(Tag.isValidTagName("123")); // digits only
        assertTrue(Tag.isValidTagName("team7")); // letters and digits
    }

    @Test
    public void equals() {
        Tag tag = new Tag("friend");
        assertEquals(tag, new Tag("friend"));
        assertNotEquals(tag, new Tag("family"));
        assertNotEquals(tag, null);
    }

}
