package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.game.Username;

/**
 * Jackson-friendly version of {@link Username}, stored as a JSON string.
 */
class JsonAdaptedUsername {
    private final String value;

    /**
     * Constructs an adapter from its stored value.
     */
    @JsonCreator
    public JsonAdaptedUsername(String value) {
        this.value = value;
    }

    /**
     * Converts a model value for Jackson use.
     */
    public JsonAdaptedUsername(Username source) {
        value = source.value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Converts the stored value to the model, validating its constraints.
     *
     * @throws IllegalValueException if the stored value is missing or invalid.
     */
    public Username toModelType() throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(String.format(JsonAdaptedGame.MISSING_FIELD_MESSAGE_FORMAT,
                    Username.class.getSimpleName()));
        }
        if (!Username.isValidUsername(value)) {
            throw new IllegalValueException(Username.MESSAGE_CONSTRAINTS);
        }
        return new Username(value);
    }
}
