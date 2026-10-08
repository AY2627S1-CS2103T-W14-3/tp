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
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;

public class JsonAdaptedFriendTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

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
                new JsonAdaptedFriend(INVALID_NAME, VALID_PHONE, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, friend::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(null, VALID_PHONE, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, friend::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedFriend friend =
                new JsonAdaptedFriend(VALID_NAME, INVALID_PHONE, VALID_EMAIL, VALID_TAGS);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, friend::toModelType);
    }

    @Test
    public void toModelType_nullPhone_returnsAbsentDetail() throws Exception {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, null, VALID_EMAIL, VALID_TAGS);
        assertEquals(Phone.empty(), friend.toModelType().getPhone());
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedFriend friend =
                new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, INVALID_EMAIL, VALID_TAGS);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, friend::toModelType);
    }

    @Test
    public void toModelType_nullEmail_returnsAbsentDetail() throws Exception {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, null, VALID_TAGS);
        assertEquals(Email.empty(), friend.toModelType().getEmail());
    }

    @Test
    public void toModelType_emptyContactDetails_roundTrips() throws Exception {
        JsonAdaptedFriend friend = new JsonAdaptedFriend(VALID_NAME, "", "", VALID_TAGS);
        assertEquals(Phone.empty(), friend.toModelType().getPhone());
        assertEquals(Email.empty(), friend.toModelType().getEmail());
        assertEquals(friend.toModelType(), new JsonAdaptedFriend(friend.toModelType()).toModelType());
    }

    @Test
    public void toModelType_whitespaceContactDetails_throwsIllegalValueException() {
        JsonAdaptedFriend blankPhone = new JsonAdaptedFriend(VALID_NAME, " ", VALID_EMAIL, VALID_TAGS);
        assertThrows(IllegalValueException.class, Phone.MESSAGE_CONSTRAINTS, blankPhone::toModelType);
        JsonAdaptedFriend blankEmail = new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, " ", VALID_TAGS);
        assertThrows(IllegalValueException.class, Email.MESSAGE_CONSTRAINTS, blankEmail::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedFriend friend =
                new JsonAdaptedFriend(VALID_NAME, VALID_PHONE, VALID_EMAIL, invalidTags);
        assertThrows(IllegalValueException.class, friend::toModelType);
    }

}
