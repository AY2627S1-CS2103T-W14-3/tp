package seedu.address.model.game.meta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests the metadata pair's composition and value semantics.
 */
public class MetaTest {

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Meta(null, new MetaValue("Support")));
        assertThrows(NullPointerException.class, () -> new Meta(new MetaKey("Role"), null));
    }

    @Test
    public void gettersAndToString_returnTrimmedFields() {
        Meta meta = new Meta(new MetaKey(" Role "), new MetaValue(" Support:Mid "));
        assertEquals(new MetaKey("Role"), meta.getKey());
        assertEquals(new MetaValue("Support:Mid"), meta.getValue());
        assertEquals("Role:Support:Mid", meta.toString());
    }

    @Test
    public void equalsAndHashCode_compareBothFields() {
        Meta meta = new Meta(new MetaKey("Role"), new MetaValue("Support"));
        Meta equalMeta = new Meta(new MetaKey(" role "), new MetaValue(" Support "));
        assertEquals(meta, meta);
        assertEquals(meta, equalMeta);
        assertEquals(meta.hashCode(), equalMeta.hashCode());
        assertNotEquals(meta, new Meta(new MetaKey("Style"), new MetaValue("Support")));
        assertNotEquals(meta, new Meta(new MetaKey("Role"), new MetaValue("Mid")));
        assertNotEquals(meta, null);
        assertNotEquals(meta, "Role:Support");
    }
}
