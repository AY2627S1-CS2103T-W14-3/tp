package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.GameMates;
import seedu.address.model.ReadOnlyGameMates;
import seedu.address.model.friend.Friend;

/**
 * An Immutable GameMates that is serializable to JSON format.
 */
@JsonRootName(value = "gamemates")
class JsonSerializableGameMates {

    public static final String MESSAGE_DUPLICATE_FRIEND = "Friends list contains duplicate friend(s).";

    private final List<JsonAdaptedFriend> friends = new ArrayList<>();
    private final JsonAdaptedFriend myself;

    /**
     * Constructs a {@code JsonSerializableGameMates} with the given friends and optional user profile.
     */
    @JsonCreator
    public JsonSerializableGameMates(@JsonProperty("friends") List<JsonAdaptedFriend> friends,
            @JsonProperty("myself") JsonAdaptedFriend myself) {
        this.friends.addAll(friends);
        this.myself = myself;
    }

    /**
     * Converts a given {@code ReadOnlyGameMates} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableGameMates}.
     */
    public JsonSerializableGameMates(ReadOnlyGameMates source) {
        myself = new JsonAdaptedFriend(source.getMyself());
        friends.addAll(source.getFriendList().stream().map(JsonAdaptedFriend::new).collect(Collectors.toList()));
    }

    /**
     * Converts this GameMates into the model's {@code GameMates} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public GameMates toModelType() throws IllegalValueException {
        GameMates gameMates = new GameMates();
        if (myself != null) {
            gameMates.setMyself(myself.toModelType());
        }
        for (JsonAdaptedFriend jsonAdaptedFriend : friends) {
            Friend friend = jsonAdaptedFriend.toModelType();
            if (gameMates.hasFriend(friend)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_FRIEND);
            }
            gameMates.addFriend(friend);
        }
        return gameMates;
    }

}
