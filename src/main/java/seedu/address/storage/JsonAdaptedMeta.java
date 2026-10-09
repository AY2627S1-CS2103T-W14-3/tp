package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;

/**
 * Jackson-friendly version of a metadata pair.
 */
class JsonAdaptedMeta {

    private final String key;
    private final String value;

    /**
     * Constructs an adapted metadata pair from stored strings.
     */
    @JsonCreator
    public JsonAdaptedMeta(@JsonProperty("key") String key, @JsonProperty("value") String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Converts a model metadata pair for Jackson use.
     */
    public JsonAdaptedMeta(Meta source) {
        key = source.getKey().value;
        value = source.getValue().value;
    }

    /**
     * Converts this pair into its model representation, validating both fields.
     *
     * @throws IllegalValueException if either field is missing or invalid.
     */
    public Meta toModelType() throws IllegalValueException {
        if (!MetaKey.isValidMetaKey(key)) {
            throw new IllegalValueException(MetaKey.MESSAGE_CONSTRAINTS);
        }
        if (!MetaValue.isValidMetaValue(value)) {
            throw new IllegalValueException(MetaValue.MESSAGE_CONSTRAINTS);
        }
        return new Meta(new MetaKey(key), new MetaValue(value));
    }
}
