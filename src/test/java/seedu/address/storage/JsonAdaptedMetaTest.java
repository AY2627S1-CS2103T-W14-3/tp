package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;

/**
 * Tests metadata validation at the JSON storage boundary.
 */
public class JsonAdaptedMetaTest {

    @Test
    public void toModelType_nullOrBlankFields_throwsIllegalValueException() {
        for (String field : new String[] {null, "", "   "}) {
            JsonAdaptedMeta invalidKey = new JsonAdaptedMeta(field, "Support");
            JsonAdaptedMeta invalidValue = new JsonAdaptedMeta("Role", field);
            assertThrows(IllegalValueException.class, MetaKey.MESSAGE_CONSTRAINTS, invalidKey::toModelType);
            assertThrows(IllegalValueException.class, MetaValue.MESSAGE_CONSTRAINTS, invalidValue::toModelType);
        }
    }

    @Test
    public void toModelType_lengthBoundaries_checkedAfterTrimming() throws Exception {
        String key = "k".repeat(MetaKey.MAX_LENGTH);
        String value = "v".repeat(MetaValue.MAX_LENGTH);
        Meta expected = new Meta(new MetaKey(key), new MetaValue(value));
        JsonAdaptedMeta padded = new JsonAdaptedMeta("  " + key + "  ", "  " + value + "  ");
        assertEquals(expected, padded.toModelType());
        String json = JsonUtil.toJsonString(padded);
        assertEquals(expected, JsonUtil.fromJsonString(json, JsonAdaptedMeta.class).toModelType());
        JsonAdaptedMeta longKey = new JsonAdaptedMeta(key + "k", value);
        JsonAdaptedMeta longValue = new JsonAdaptedMeta(key, value + "v");
        assertThrows(IllegalValueException.class, MetaKey.MESSAGE_CONSTRAINTS, longKey::toModelType);
        assertThrows(IllegalValueException.class, MetaValue.MESSAGE_CONSTRAINTS, longValue::toModelType);
    }

    @Test
    public void toModelType_controlsOrColonInKey_throwsIllegalValueException() throws Exception {
        for (String invalid : new String[] {"Role:Type", "Role\nType", "Role\tType", "Role\u0000Type"}) {
            JsonAdaptedMeta adapted = new JsonAdaptedMeta(invalid, "Support");
            String json = JsonUtil.toJsonString(adapted);
            JsonAdaptedMeta restored = JsonUtil.fromJsonString(json, JsonAdaptedMeta.class);
            assertThrows(IllegalValueException.class, MetaKey.MESSAGE_CONSTRAINTS, restored::toModelType);
        }
        JsonAdaptedMeta invalidValue = new JsonAdaptedMeta("Role", "Support\nMid");
        assertThrows(IllegalValueException.class, MetaValue.MESSAGE_CONSTRAINTS, invalidValue::toModelType);
    }
}
