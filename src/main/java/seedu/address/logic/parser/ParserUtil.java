package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
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

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_MISSING_INDEX = "Please provide an index.";
    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        requireNonNull(oneBasedIndex);
        String trimmedIndex = oneBasedIndex.trim();
        if (trimmedIndex.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     * The trimmed value must contain only alphanumeric characters and spaces, and be at most
     * {@value Name#MAX_LENGTH} characters long.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     * The trimmed value must match {@link Phone#VALIDATION_REGEX}, for example {@code (123) 456-7890}.
     * Accepted country-code prefixes, parentheses and separators are preserved; extensions are rejected.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     * The trimmed value must be an ASCII {@code local-part@domain} address of at most 254 characters, as
     * described in the User Guide. Only the format is checked, not whether the mailbox exists.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String gameName} into a {@code GameName}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code gameName} is invalid.
     */
    public static GameName parseGameName(String gameName) throws ParseException {
        requireNonNull(gameName);
        String trimmedGameName = gameName.strip();
        if (!GameName.isValidGameName(trimmedGameName)) {
            throw new ParseException(GameName.MESSAGE_CONSTRAINTS);
        }
        return new GameName(trimmedGameName);
    }

    /**
     * Parses a {@code String username} into a {@code Username}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code username} is invalid.
     */
    public static Username parseUsername(String username) throws ParseException {
        requireNonNull(username);
        String trimmedUsername = username.strip();
        if (!Username.isValidUsername(trimmedUsername)) {
            throw new ParseException(Username.MESSAGE_CONSTRAINTS);
        }
        return new Username(trimmedUsername);
    }

    /**
     * Parses a metadata key, stripping leading and trailing whitespace.
     *
     * @throws ParseException if the key is invalid.
     */
    public static MetaKey parseMetaKey(String key) throws ParseException {
        requireNonNull(key);
        String trimmedKey = key.strip();
        if (!MetaKey.isValidMetaKey(trimmedKey)) {
            throw new ParseException(MetaKey.MESSAGE_CONSTRAINTS);
        }
        return new MetaKey(trimmedKey);
    }

    /**
     * Parses a metadata value, stripping leading and trailing whitespace and preserving colons.
     *
     * @throws ParseException if the value is invalid.
     */
    public static MetaValue parseMetaValue(String value) throws ParseException {
        requireNonNull(value);
        String trimmedValue = value.strip();
        if (!MetaValue.isValidMetaValue(trimmedValue)) {
            throw new ParseException(MetaValue.MESSAGE_CONSTRAINTS);
        }
        return new MetaValue(trimmedValue);
    }

    /**
     * Parses a metadata pair at the first colon, trimming the key and value independently.
     *
     * @throws ParseException if the separator, key or value is invalid.
     */
    public static Meta parseMeta(String meta) throws ParseException {
        requireNonNull(meta);
        String[] parts = StringUtil.splitAtFirstColon(meta);
        if (parts.length != 2) {
            throw new ParseException(Meta.MESSAGE_CONSTRAINTS);
        }
        return new Meta(parseMetaKey(parts[0]), parseMetaValue(parts[1]));
    }

    /**
     * Parses metadata flags for one game entry, rejecting repeated keys even when values match.
     *
     * @throws ParseException if a pair is invalid or a key occurs more than once.
     */
    public static Set<Meta> parseMetas(Collection<String> metas) throws ParseException {
        requireNonNull(metas);
        Set<Meta> result = new LinkedHashSet<>();
        Set<MetaKey> keys = new HashSet<>();
        for (String text : metas) {
            Meta meta = parseMeta(text);
            if (!keys.add(meta.getKey())) {
                throw new ParseException(Meta.MESSAGE_DUPLICATE_KEY);
            }
            result.add(meta);
        }
        return result;
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
