package seedu.address.model.friend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_unrestrictedText_preservesValue() {
        for (String value : new String[] {"", " ", "Likes swimming!", "玩家", "Quotes: \"hello\"\nNext line"}) {
            Remark remark = new Remark(value);
            assertEquals(value, remark.value);
            assertEquals(value, remark.toString());
        }
    }

    @Test
    public void equalsAndHashCode() {
        Remark remark = new Remark("Likes swimming");
        Remark copy = new Remark("Likes swimming");
        assertTrue(remark.equals(remark));
        assertTrue(remark.equals(copy));
        assertEquals(remark.hashCode(), copy.hashCode());
        assertEquals(new Remark(""), Remark.EMPTY);
        assertFalse(remark.equals(new Remark("Likes football")));
        assertFalse(remark.equals(null));
        assertFalse(remark.equals("Likes swimming"));
    }
}
