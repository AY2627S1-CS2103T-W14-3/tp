package seedu.address.storage;

import static seedu.address.commons.util.StringUtil.isNullOrEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.friend.Email;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;
import seedu.address.model.friend.Remark;
import seedu.address.model.game.Game;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Friend}.
 */
class JsonAdaptedFriend {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Friend's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String remark;
    private final List<JsonAdaptedGame> games = new ArrayList<>();
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedFriend} with the given friend details.
     */
    @JsonCreator
    public JsonAdaptedFriend(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("games") List<JsonAdaptedGame> games,
            @JsonProperty("remark") String remark,
            @JsonProperty("tags") List<JsonAdaptedTag> tags) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.remark = remark;
        if (games != null) {
            this.games.addAll(games);
        }
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Constructs a friend without a remark, for existing callers and legacy data.
     */
    public JsonAdaptedFriend(String name, String phone, String email, List<JsonAdaptedGame> games,
            List<JsonAdaptedTag> tags) {
        this(name, phone, email, games, "", tags);
    }

    /**
     * Converts a given {@code Friend} into this class for Jackson use.
     */
    public JsonAdaptedFriend(Friend source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        remark = source.getRemark().value;
        games.addAll(source.getGames().stream().map(JsonAdaptedGame::new).toList());
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .toList());
    }

    /**
     * Converts this Jackson-friendly adapted friend object into the model's {@code Friend} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted friend.
     */
    public Friend toModelType() throws IllegalValueException {
        final Set<Game> modelGames = new HashSet<>();
        for (JsonAdaptedGame game : games) {
            modelGames.add(game.toModelType());
        }
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

        if (!isNullOrEmpty(phone) && !Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = isNullOrEmpty(phone) ? Phone.EMPTY : new Phone(phone);

        if (!isNullOrEmpty(email) && !Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = isNullOrEmpty(email) ? Email.EMPTY : new Email(email);

        final Set<Tag> modelTags = new HashSet<>(friendTags);
        final Remark modelRemark = remark == null ? Remark.EMPTY : new Remark(remark);
        return new Friend(modelName, modelPhone, modelEmail, modelGames, modelRemark, modelTags);
    }

}
