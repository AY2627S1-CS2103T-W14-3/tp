package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.friend.Email;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Friend}.
 */
class JsonAdaptedFriend {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Friend's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedFriend} with the given friend details.
     */
    @JsonCreator
    public JsonAdaptedFriend(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Friend} into this class for Jackson use.
     */
    public JsonAdaptedFriend(Friend source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted friend object into the model's {@code Friend} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted friend.
     */
    public Friend toModelType() throws IllegalValueException {
        final List<Tag> friendTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            friendTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        final Set<Tag> modelTags = new HashSet<>(friendTags);
        return new Friend(modelName, modelPhone, modelEmail, modelTags);
    }

}
