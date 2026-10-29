package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tokenizes arguments string of the form: {@code preamble <prefix>value <prefix>value ...}<br>
 *     e.g. {@code some preamble text t/ 11.00 t/12.00 k/ m/ July}  where prefixes are {@code t/ k/ m/}.<br>
 * 1. An argument's value can be an empty string e.g. the value of {@code k/} in the above example.<br>
 * 2. Leading and trailing whitespaces of an argument value will be discarded.<br>
 * 3. An argument may be repeated and all its values will be accumulated e.g. the value of {@code t/}
 *    in the above example.<br>
 * 4. An argument value may be enclosed in double quotes e.g. {@code t/"11.00 k/"}, where {@code k/} is literal.<br>
 */
public class ArgumentTokenizer {

    public static final String MESSAGE_UNCLOSED_QUOTE = "Missing closing double quote. To end a quoted value "
            + "with \\, add a space before the closing quote.";
    public static final String MESSAGE_TEXT_AFTER_QUOTE = "Text after a closing double quote is not allowed. "
            + "Wrap the whole value in double quotes, and add a space before the next prefix.";
    public static final String MESSAGE_BARE_QUOTE = "Invalid double quote. Wrap the whole value in double quotes, "
            + "or write the double quote as \\\".";

    private static final Pattern UNESCAPED_QUOTE = Pattern.compile("(?<!\\\\)\"");

    /**
     * Tokenizes an arguments string and returns an {@code ArgumentMultimap} object that maps prefixes to their
     * respective argument values. Only the given prefixes will be recognized in the arguments string.
     * A value whose first character after trimming is {@code "} is a quoted value, and prefixes inside it are
     * literal text. Any other double quote must be written as {@code \"}. Other backslashes are kept as is.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to tokenize the arguments string with
     * @return           ArgumentMultimap object that maps prefixes to their arguments
     * @throws ParseException if a quoted value is unterminated or has text after its closing quote,
     *     or an unquoted value has an unescaped double quote.
     */
    public static ArgumentMultimap tokenize(String argsString, Prefix... prefixes) throws ParseException {
        List<PrefixPosition> positions = findAllPrefixPositions(argsString, prefixes);
        positions.sort((first, second) -> first.getStartPosition() - second.getStartPosition());

        ArgumentMultimap arguments = new ArgumentMultimap();
        arguments.put(new Prefix(""), argsString.substring(0, startOfPrefixOrEnd(argsString, positions, 0)).trim());
        int index = 0;
        while (index < positions.size()) {
            ArgumentValue argument = extractArgumentValue(argsString, positions, index);
            arguments.put(positions.get(index).getPrefix(), argument.value());
            index = argument.nextIndex();
        }
        return arguments;
    }

    /**
     * Returns the trimmed value of the prefix at {@code index}, and the index of the next prefix outside that value.
     */
    private static ArgumentValue extractArgumentValue(String argsString, List<PrefixPosition> positions, int index)
            throws ParseException {
        int valueStart = positions.get(index).getValueStartPosition();
        String value = argsString.substring(valueStart, startOfPrefixOrEnd(argsString, positions, index + 1)).trim();
        if (!value.startsWith("\"")) {
            return new ArgumentValue(unescapeQuotes(value), index + 1);
        }
        return extractQuotedValue(argsString, positions, argsString.indexOf('"', valueStart));
    }

    /**
     * Returns the trimmed quoted value opening at {@code quoteStart}, and the index of the first prefix after it.
     *
     * @throws ParseException if the closing quote is missing or followed by text other than whitespace.
     */
    private static ArgumentValue extractQuotedValue(String argsString, List<PrefixPosition> positions,
            int quoteStart) throws ParseException {
        QuotedValue quoted = readQuotedValue(argsString, quoteStart);
        int nextIndex = indexOfFirstPrefixFrom(positions, quoted.endPosition());
        String trailingText = argsString.substring(quoted.endPosition(),
                startOfPrefixOrEnd(argsString, positions, nextIndex));
        if (!trailingText.trim().isEmpty()) {
            throw new ParseException(MESSAGE_TEXT_AFTER_QUOTE);
        }
        return new ArgumentValue(quoted.value().trim(), nextIndex);
    }

    /**
     * Reads the quoted value opening at {@code quoteStart}. Only a backslash right before a double quote
     * is removed, so {@code \\"} is read as {@code \"}.
     *
     * @throws ParseException if the closing quote is missing.
     */
    private static QuotedValue readQuotedValue(String argsString, int quoteStart) throws ParseException {
        StringBuilder value = new StringBuilder();
        int i = quoteStart + 1;
        while (i < argsString.length() && argsString.charAt(i) != '"') {
            if (argsString.startsWith("\\\"", i)) {
                i++; // Skip the backslash so the quote is kept as text.
            }
            value.append(argsString.charAt(i));
            i++;
        }
        if (i == argsString.length()) {
            throw new ParseException(MESSAGE_UNCLOSED_QUOTE);
        }
        return new QuotedValue(value.toString(), i + 1);
    }

    /**
     * Returns {@code value} with the backslash removed from each {@code \"}.
     *
     * @throws ParseException if {@code value} has a double quote without a backslash right before it.
     */
    private static String unescapeQuotes(String value) throws ParseException {
        if (UNESCAPED_QUOTE.matcher(value).find()) {
            throw new ParseException(MESSAGE_BARE_QUOTE);
        }
        return value.replace("\\\"", "\"");
    }

    /**
     * Returns the index of the first prefix starting at or after {@code position}.
     */
    private static int indexOfFirstPrefixFrom(List<PrefixPosition> positions, int position) {
        int index = 0;
        while (index < positions.size() && positions.get(index).getStartPosition() < position) {
            index++;
        }
        return index;
    }

    /**
     * Returns the start position of the prefix at {@code index}, or the end of {@code argsString} if there is none.
     */
    private static int startOfPrefixOrEnd(String argsString, List<PrefixPosition> positions, int index) {
        return index < positions.size() ? positions.get(index).getStartPosition() : argsString.length();
    }

    private record ArgumentValue(String value, int nextIndex) {
    }

    private record QuotedValue(String value, int endPosition) {
    }

    /**
     * Finds all zero-based prefix positions in the given arguments string.
     *
     * @param argsString Arguments string of the form: {@code preamble <prefix>value <prefix>value ...}
     * @param prefixes   Prefixes to find in the arguments string
     * @return           List of zero-based prefix positions in the given arguments string
     */
    private static List<PrefixPosition> findAllPrefixPositions(String argsString, Prefix... prefixes) {
        return Arrays.stream(prefixes)
                .flatMap(prefix -> findPrefixPositions(argsString, prefix).stream())
                .collect(Collectors.toList());
    }

    /**
     * @see #findAllPrefixPositions(String, Prefix...)
     */
    private static List<PrefixPosition> findPrefixPositions(String argsString, Prefix prefix) {
        List<PrefixPosition> positions = new ArrayList<>();

        int prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), 0);
        while (prefixPosition != -1) {
            PrefixPosition extendedPrefix = new PrefixPosition(prefix, prefixPosition);
            positions.add(extendedPrefix);
            prefixPosition = findPrefixPosition(argsString, prefix.getPrefix(), prefixPosition);
        }

        return positions;
    }

    /**
     * Returns the index of the first occurrence of {@code prefix} in
     * {@code argsString} starting from index {@code fromIndex}. An occurrence
     * is valid if there is a whitespace before {@code prefix}. Returns -1 if no
     * such occurrence can be found.
     *
     * E.g if {@code argsString} = "e/hip/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns -1 as there are no valid
     * occurrences of "p/" with whitespace before it. However, if
     * {@code argsString} = "e/hi p/900", {@code prefix} = "p/" and
     * {@code fromIndex} = 0, this method returns 5.
     */
    private static int findPrefixPosition(String argsString, String prefix, int fromIndex) {
        int prefixIndex = argsString.indexOf(" " + prefix, fromIndex);
        return prefixIndex == -1 ? -1
                : prefixIndex + 1; // +1 as offset for whitespace
    }

    /**
     * Represents a prefix's position in an arguments string.
     */
    private static class PrefixPosition {
        private int startPosition;
        private final Prefix prefix;

        PrefixPosition(Prefix prefix, int startPosition) {
            this.prefix = prefix;
            this.startPosition = startPosition;
        }

        int getStartPosition() {
            return startPosition;
        }

        int getValueStartPosition() {
            return startPosition + prefix.getPrefix().length();
        }

        Prefix getPrefix() {
            return prefix;
        }
    }

}
