package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_FRIEND;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.friend.Email;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.Phone;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "a".repeat(Name.MAX_LENGTH + 1);
    private static final String INVALID_PHONE = "+659123456a";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_GAME_NAME = "!!!";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_USERNAME = "!!!";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "(123) 456-7890";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_GAME_NAME = "Counter-Strike 2";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";
    private static final String VALID_USERNAME = "ShadowStrikerXx";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseGameFields_outerControlCharacters_rejected() {
        for (String input : new String[] {"\u0000Player", "Player\u0000", "\u001bPlayer", "Player\u001b"}) {
            assertThrows(ParseException.class, () -> ParserUtil.parseGameName(input));
            assertThrows(ParseException.class, () -> ParserUtil.parseUsername(input));
            assertThrows(ParseException.class, () -> ParserUtil.parseMeta(input + ":Support"));
            assertThrows(ParseException.class, () -> ParserUtil.parseMeta("Role:" + input));
        }
    }

    @Test
    public void parseGameFields_unicodeWhitespace_trimmedConsistently() throws Exception {
        String input = "\u2003Player\u2003";
        assertEquals(new GameName("Player"), ParserUtil.parseGameName(input));
        assertEquals(new Username("Player"), ParserUtil.parseUsername(input));
        assertEquals(new Meta(new MetaKey("Role"), new MetaValue("Player")),
                ParserUtil.parseMeta("\u2003Role\u2003:" + input));
    }

    @Test
    public void parseMetaKey_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMetaKey(null));
    }

    @Test
    public void parseMetaKey_invalid_throwsParseException() {
        for (String input : List.of("", " ", "!!!", "Role:Type", "k".repeat(51), "a\nb", "a\u0000b")) {
            assertThrows(ParseException.class, MetaKey.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseMetaKey(input));
        }
    }

    @Test
    public void parseMetaKey_valid_trimsAndPreservesCase() throws Exception {
        for (String input : List.of("A", "1", "Play style!", "角色", "k".repeat(MetaKey.MAX_LENGTH))) {
            assertEquals(input, ParserUtil.parseMetaKey("\u2003 " + input + " \u2003").value);
        }
    }

    @Test
    public void parseMetaValue_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMetaValue(null));
    }

    @Test
    public void parseMetaValue_invalid_throwsParseException() {
        for (String input : List.of("", " ", "!!!", ":", "v".repeat(201), "a\tb", "a\u0000b")) {
            assertThrows(ParseException.class, MetaValue.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseMetaValue(input));
        }
    }

    @Test
    public void parseMetaValue_valid_trimsAndPreservesColons() throws Exception {
        for (String input : List.of("A", "1", "Support:Mid", ":Support:", "角色", "v".repeat(MetaValue.MAX_LENGTH))) {
            assertEquals(input, ParserUtil.parseMetaValue("\u2003 " + input + " \u2003").value);
        }
    }

    @Test
    public void parseMeta_validPair_splitsAtFirstColon() throws Exception {
        Meta expected = new Meta(new MetaKey("Role"), new MetaValue("Support:Mid"));
        assertEquals(expected, ParserUtil.parseMeta("  Role  :  Support:Mid  "));
        assertEquals(expected, ParserUtil.parseMeta(expected.toString()));
    }

    @Test
    public void parseMeta_valueWithEdgeColons_preservesColons() throws Exception {
        for (String value : List.of(":Support", "Support:", ":Support:", "Support::Mid")) {
            assertEquals(new Meta(new MetaKey("Role"), new MetaValue(value)), ParserUtil.parseMeta("Role:" + value));
        }
    }

    @Test
    public void parseMeta_lengthBoundaries_validatesTrimmedFields() throws Exception {
        String key = "k".repeat(MetaKey.MAX_LENGTH);
        String value = "v".repeat(MetaValue.MAX_LENGTH);
        assertEquals(new Meta(new MetaKey(key), new MetaValue(value)),
                ParserUtil.parseMeta("  " + key + "  :  " + value + "  "));
        assertThrows(ParseException.class, MetaKey.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseMeta(key + "k:" + value));
        assertThrows(ParseException.class, MetaValue.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseMeta(key + ":" + value + "v"));
        for (String input : List.of("", "Role", ":Support", "Role:", ":", "::")) {
            assertThrows(ParseException.class, () -> ParserUtil.parseMeta(input));
        }
    }

    @Test
    public void parseMetas_nullElement_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMetas(Arrays.asList("Role:Support", null)));
    }

    @Test
    public void parseMetas_internalSpacesInKeys_remainDistinct() throws Exception {
        assertEquals(Set.of(ParserUtil.parseMeta("Play style:Casual"), ParserUtil.parseMeta("Play  style:Competitive")),
                ParserUtil.parseMetas(List.of("Play style:Casual", "Play  style:Competitive")));
    }

    @Test
    public void parseMeta_invalidPair_throwsParseException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMeta(null));
        assertThrows(ParseException.class, Meta.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseMeta("Role Support"));
        for (String key : List.of("", " ", "!!!", "a".repeat(51), "a\nb")) {
            assertThrows(ParseException.class, MetaKey.MESSAGE_CONSTRAINTS, ()
                    -> ParserUtil.parseMeta(key + ":Support"));
        }
        for (String value : List.of("", " ", ":", "a".repeat(201), "a\tb")) {
            assertThrows(ParseException.class, MetaValue.MESSAGE_CONSTRAINTS, ()
                    -> ParserUtil.parseMeta("Role:" + value));
        }
    }

    @Test
    public void parseMetas_uniqueKeys_success() throws Exception {
        assertEquals(Set.of(), ParserUtil.parseMetas(List.of()));
        assertEquals(Set.of(ParserUtil.parseMeta("Role:Support"), ParserUtil.parseMeta("Style:Support")),
                ParserUtil.parseMetas(List.of("Role:Support", "Style:Support")));
    }

    @Test
    public void parseMetas_repeatedFlags_throwsParseException() {
        Prefix metaPrefix = new Prefix("m/");
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" m/Role:Support m/role:Mid", metaPrefix);
        assertThrows(ParseException.class, Meta.MESSAGE_DUPLICATE_KEY, ()
                -> ParserUtil.parseMetas(arguments.getAllValues(metaPrefix)));
    }

    @Test
    public void parseMetas_duplicateKeys_throwsParseException() {
        for (String duplicate : List.of("Role:Support", "role:Mid", " ROLE : Mid ")) {
            assertThrows(ParseException.class, Meta.MESSAGE_DUPLICATE_KEY, ()
                    -> ParserUtil.parseMetas(List.of("Role:Support", duplicate)));
        }
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMetas(null));
        assertThrows(ParseException.class, () -> ParserUtil.parseMetas(List.of("Role:Support", "invalid")));
    }

    @Test
    public void parseIndex_missingInput_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_MISSING_INDEX, () -> ParserUtil.parseIndex(""));
        assertThrows(ParseException.class, ParserUtil.MESSAGE_MISSING_INDEX, () -> ParserUtil.parseIndex(WHITESPACE));
    }

    @Test
    public void parseIndex_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseIndex(null));
    }

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1L)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_FRIEND, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_FRIEND, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName(null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseName_lengthBoundaries_validatesAfterTrimming() throws Exception {
        assertEquals("a", ParserUtil.parseName(WHITESPACE + "a" + WHITESPACE).fullName);
        String maxLengthName = "a".repeat(Name.MAX_LENGTH);
        assertEquals(maxLengthName, ParserUtil.parseName(WHITESPACE + maxLengthName + WHITESPACE).fullName);
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseName("a".repeat(Name.MAX_LENGTH + 1)));
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName(WHITESPACE));
    }

    @Test
    public void parseName_punctuation_throwsParseException() {
        assertThrows(ParseException.class, Name.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseName("O'Connor-Jane"));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone(null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parsePhone_lengthBoundaries_validatesAfterTrimming() throws Exception {
        String phone = "1234567890";
        assertEquals(new Phone(phone), ParserUtil.parsePhone(WHITESPACE + phone + WHITESPACE));
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone("12"));
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone("1234567890123456"));
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone(WHITESPACE));
    }

    @Test
    public void parsePhone_supportedFormats_preservesFormatting() throws Exception {
        String[] phones = {"91234567", "+6591234567", "+65 9123-4567", "(123) 456-7890"};
        for (String phone : phones) {
            assertEquals(new Phone(phone), ParserUtil.parsePhone(WHITESPACE + phone + WHITESPACE));
        }
    }

    @Test
    public void parsePhone_unsupportedFormats_throwsParseException() {
        String[] invalidPhones = {"123.456.7890", "+65.9123.4567", "12", "+06591234567", "+65 (1234)-5678.;#",
            "+6591234567 ext 123", "+６５９１２３４５６７", "+", "+1"};
        for (String phone : invalidPhones) {
            assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone(phone));
        }
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail(null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseGameName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseGameName(null));
    }

    @Test
    public void parseGameName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseGameName(INVALID_GAME_NAME));
    }

    @Test
    public void parseGameName_validValueWithoutWhitespace_returnsGameName() throws Exception {
        GameName expectedGameName = new GameName(VALID_GAME_NAME);
        assertEquals(expectedGameName, ParserUtil.parseGameName(VALID_GAME_NAME));
    }

    @Test
    public void parseGameName_validValueWithWhitespace_returnsTrimmedGameName() throws Exception {
        String gameNameWithWhitespace = WHITESPACE + VALID_GAME_NAME + WHITESPACE;
        GameName expectedGameName = new GameName(VALID_GAME_NAME);
        assertEquals(expectedGameName, ParserUtil.parseGameName(gameNameWithWhitespace));
    }

    @Test
    public void parseUsername_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseUsername(null));
    }

    @Test
    public void parseUsername_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseUsername(INVALID_USERNAME));
    }

    @Test
    public void parseUsername_validValueWithoutWhitespace_returnsUsername() throws Exception {
        Username expectedUsername = new Username(VALID_USERNAME);
        assertEquals(expectedUsername, ParserUtil.parseUsername(VALID_USERNAME));
    }

    @Test
    public void parseUsername_validValueWithWhitespace_returnsTrimmedUsername() throws Exception {
        String usernameWithWhitespace = WHITESPACE + VALID_USERNAME + WHITESPACE;
        Username expectedUsername = new Username(VALID_USERNAME);
        assertEquals(expectedUsername, ParserUtil.parseUsername(usernameWithWhitespace));
    }

    @Test
    public void parseEmail_lengthBoundaries_validatesAfterTrimming() throws Exception {
        assertEquals(new Email("a@b"), ParserUtil.parseEmail(WHITESPACE + "a@b" + WHITESPACE));
        String maxLengthEmail = "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(63)
                + "." + "d".repeat(61);
        assertEquals(new Email(maxLengthEmail),
                ParserUtil.parseEmail(WHITESPACE + maxLengthEmail + WHITESPACE));
        assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, ()
                -> ParserUtil.parseEmail(maxLengthEmail + "d"));
        assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseEmail(WHITESPACE));
    }

    @Test
    public void parseEmail_malformedAddresses_throwsParseException() {
        String[] invalidEmails = {"@", "user@", "user@@example.com", "user..name@example.com",
            "user@-example.com", "user name@example.com"};
        for (String email : invalidEmails) {
            assertThrows(ParseException.class, Email.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseEmail(email));
        }
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTag_unsupportedFormats_throwsParseException() {
        String[] invalidTags = {"", WHITESPACE, "best friend", "best-friend", "caf\u00e9"};
        for (String tag : invalidTags) {
            assertThrows(ParseException.class, Tag.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseTag(tag));
        }
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
