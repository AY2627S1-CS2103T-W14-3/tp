package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.storage.JsonAdaptedFriend.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalFriends.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.friend.Email;
import seedu.address.model.friend.Friend;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

public class JsonAdaptedFriendTest {
    private static final String INVALID_NAME = "a".repeat(Name.MAX_LENGTH + 1);
    private static final String INVALID_PHONE = "+659123456a";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final List<JsonAdaptedGame> VALID_GAMES = BENSON.getGames().stream()
            .map(JsonAdaptedGame::new)
            .collect(Collectors.toList());
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_games_roundTrips() throws Exception {
        Friend original = new FriendBuilder(BENSON).withGames(
                new GameBuilder().withGameName("Valorant").withUsername("benson").build(),
                new GameBuilder().withGameName("Minecraft").withUsername("benson2").build()).build();
        String json = JsonUtil.toJsonString(new JsonAdaptedFriend(original));
        assertEquals(original, JsonUtil.fromJsonString(json, JsonAdaptedFriend.class).toModelType());
    }

    @Test
    public void toModelType_missingGames_returnsEmptyGames() throws Exception {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                null, VALID_TAGS);
        assertEquals(BENSON, friend.toModelType());
    }

    @Test
    public void toModelType_invalidGames_throwsIllegalValueException() {
        for (JsonAdaptedGame game : List.of(new JsonAdaptedGame(" ", "player"),
                new JsonAdaptedGame("Valorant", " "), new JsonAdaptedGame(null, "player"),
                new JsonAdaptedGame("Valorant", null))) {
            JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                    List.of(game), VALID_TAGS);
            assertThrows(IllegalValueException.class, friend::toModelType);
        }
    }

    @Test
    public void toModelType_validFriendDetails_returnsFriend() throws Exception {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(BENSON);
        assertEquals(BENSON, friend.toModelType());
    }

    @Test
    public void json_roundTripWithoutAddress_returnsFriend() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedFriend(BENSON));
        assertFalse(json.contains("\"address\""));
        assertEquals(BENSON, JsonUtil.fromJsonString(json, JsonAdaptedFriend.class).toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedFriend friend =
                new JsonAdaptedFriend(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_GAMES, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, friend::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(null, VALID_PHONE, VALID_EMAIL, VALID_GAMES, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, friend::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedFriend friend =
                new JsonAdaptedFriend(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_GAMES, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, friend::toModelType);
    }

    @Test
    public void toModelType_plainPhone_returnsFriend() throws Exception {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, "91234567", VALID_EMAIL, VALID_GAMES,
                VALID_TAGS);
        assertEquals("91234567", friend.toModelType().getPhone().value);
    }

    @Test
    public void toModelType_phoneWithSurroundingWhitespace_throwsIllegalValueException() {
        // stored data is not trimmed; only user input is
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, " 91234567", VALID_EMAIL, VALID_GAMES,
                VALID_TAGS);
        assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, friend::toModelType);
    }

    @Test
    public void toModelType_nullPhone_returnsAbsentDetail() throws Exception {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, null, VALID_EMAIL, VALID_GAMES, VALID_TAGS);
        assertEquals(Phone.EMPTY, friend.toModelType().getPhone());
    }

    @Test
    public void toModelType_tooLongEmail_throwsIllegalValueException() {
        String tooLongEmail = "a".repeat(65) + "@example.com";
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, tooLongEmail, VALID_GAMES,
                VALID_TAGS);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, friend::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedFriend friend =
                new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_GAMES, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, friend::toModelType);
    }

    @Test
    public void toModelType_nullEmail_returnsAbsentDetail() throws Exception {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, null, VALID_GAMES, VALID_TAGS);
        assertEquals(Email.EMPTY, friend.toModelType().getEmail());
    }

    @Test
    public void toModelType_emptyContactDetails_roundTrips() throws Exception {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, "", "", VALID_GAMES, VALID_TAGS);
        assertEquals(Phone.EMPTY, friend.toModelType().getPhone());
        assertEquals(Email.EMPTY, friend.toModelType().getEmail());
        assertEquals(friend.toModelType(), new JsonAdaptedFriend(friend.toModelType()).toModelType());
    }

    @Test
    public void toModelType_whitespaceContactDetails_throwsIllegalValueException() {
        JsonAdaptedFriend blankPhone = new JsonAdaptedFriend(VALID_NAME, " ", VALID_EMAIL, VALID_GAMES, VALID_TAGS);
        assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, blankPhone::toModelType);
        JsonAdaptedFriend blankEmail = new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, " ", VALID_GAMES, VALID_TAGS);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, blankEmail::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedFriend friend =
                new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_GAMES, invalidTags);
        assertThrows(IllegalValueException.class, friend::toModelType);
    }

}
