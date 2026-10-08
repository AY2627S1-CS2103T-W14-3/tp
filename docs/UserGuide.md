---
layout: page
title: User Guide
---

GameMates is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, GameMates can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-W14-3/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for GameMates.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar gamemates.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com` : Adds a contact named `John Doe` to GameMates.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a friend: `add`

Adds a friend to GameMates.

Format: `add n/NAME [p/PHONE_NUMBER] [e/EMAIL] [t/TAG]…`

* Only the name is required. Omitted phone and email details are shown as `Not provided`.
* Supplied phone and email values must satisfy the validation rules. Empty `p/` or `e/` values are rejected.
* Leading and trailing spaces are removed. Names must be unique, ignoring case and repeated spaces:
  `John Doe` and `john   doe` identify the same friend.
* Different friends may share a phone number or email address.

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A friend can have any number of tags, including zero.
</div>

Examples:
* `add n/John Doe`
* `add n/John Doe p/98765432 e/johnd@example.com`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com p/1234567 t/criminal`

Names are trimmed before validation, must not be blank and may contain at most 100 characters.
Only ASCII letters, digits and spaces are accepted. Names are compared case-insensitively,
so `john doe` and `John Doe` are the same friend.

Tag names must be non-empty and contain only ASCII letters and digits (no spaces or symbols).

Phone numbers accept formats such as `91234567`, `+6591234567`, `+65 9123-4567` and `(123) 456-7890`.

* Use 3–15 ASCII digits without `+`, or 2–15 with `+`, including the country code.
* Put `+` at the start, immediately before a digit or `(`. The first digit after `+` must be `1`–`9`.
* Digit groups may have any length. Parentheses must enclose digits only and cannot be empty or nested.
* Use at most one hyphen or space between groups. Separators may differ or be omitted.
  Leading, trailing and consecutive separators are rejected.
* Dots, letters, non-ASCII digits and extensions are rejected.
* Command input is trimmed. Saved numbers must already match these rules.
* Formatting is preserved. `+6591234567` and `+65 9123-4567` are different phone values.

<div markdown="span" class="alert alert-warning">
Validation checks format only, not country codes, national number lengths or whether a number is active.
These rules also apply when editing friends and loading saved data.
</div>

Email addresses are trimmed before validation and must use ASCII `local-part@domain` syntax:

* The entire address may contain at most 254 characters, with at most 64 before `@`.
* The local part accepts letters, digits and the punctuation in ``!#$%&'*+/=?^_`{|}~-``.
  Dots may separate non-empty parts, so leading, trailing and consecutive dots are rejected.
* Each domain label (a part between dots) must contain 1–63 letters, digits or hyphens and must start
  and end with a letter or digit. Single-label domains such as `user@localhost` are accepted.
* Quoted local parts, address literals such as `user@[127.0.0.1]`, internal whitespace and non-ASCII
  characters are not supported. Validation checks format only, not whether a mailbox exists.

<div markdown="span" class="alert alert-info">
These rules use a practical subset of [RFC 5322's dot-atom syntax](https://www.rfc-editor.org/rfc/rfc5322#section-3.2.3)
and [RFC 5321's SMTP length limits](https://www.rfc-editor.org/rfc/rfc5321#section-4.5.3.1).
The 254-character address limit leaves room for the enclosing `<` and `>` in the 256-octet SMTP path limit.
</div>

### Listing all friends: `list`

Shows a list of all friends in GameMates.

Format: `list`

### Editing a friend: `edit`

Edits an existing friend in GameMates.

Format: `edit FRIEND_INDEX [n/NAME] [p/[PHONE]] [e/[EMAIL]] [t/TAG]…​`

* Edits the friend at the specified `FRIEND_INDEX`. The index refers to the index number shown in the displayed friend list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Supplied values are trimmed and validated using the same rules as `add`.
* Omitted fields remain unchanged. Enter `p/` or `e/` without a value to remove that contact detail.
* A name cannot be removed. The new name must be unique among other friends, ignoring case and repeated spaces.
* Games remain unchanged when editing friend details.
* Success feedback starts with `Edited NAME` and lists only changed details. Removed contact details are reported
  as `removed phone number` or `removed email`.
* An index is required; editing your own profile without an index is not currently supported.
* When editing tags, all of the friend's existing tags are removed; adding tags is not cumulative.
* To remove all of a friend's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st friend to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd friend to be `Betsy Crower` and clears all existing tags.
* `edit 2 p/ e/` removes the 2nd friend's phone number and email.

Errors:

* Missing or invalid index: `Invalid friend index. Enter a positive integer.`
* Empty displayed list: `No friends are currently displayed. Use list to show all friends.`
* Index outside the displayed list: `No friend exists at index INDEX. Choose an index from 1 to MAX_LIST_INDEX.`
* No fields provided: `No details were passed to the command. Use a parameter (p/PHONE_NUMBER, e/EMAIL, n/NAME, etc.) to edit specific details.`
* Duplicate name: `This friend already exists in GameMates.`

### Locating friends by name: `find`

Finds friends whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Only full words match; for example, `Han` does not match `Hans`.
* Friends matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a friend: `delete`

Deletes the specified friend from GameMates.

Format: `delete FRIEND_INDEX`

* Deletes the friend at the specified `FRIEND_INDEX`.
* The index refers to the index number shown in the displayed friend list.
* The index **must be a positive integer** 1, 2, 3, …​
* Leading and trailing spaces are ignored. The index must have a value from 1 to 2147483647.
* A successful deletion displays `Successfully deleted FRIEND_NAME.`

Examples:
* `list` followed by `delete 2` deletes the 2nd friend in GameMates.
* `find Betsy` followed by `delete 1` deletes the 1st friend in the results of the `find` command.

Errors:

* Missing index: `Please provide an index.`
* Invalid index (such as `0`, `-1`, or `abc`), extra arguments, or a value above 2147483647:
  `Index must be a positive integer.`
* Index outside the displayed list, including an empty list: `The friend index provided is invalid.`

### Clearing all entries: `clear`

Clears all entries from GameMates.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

GameMates automatically saves data after every command. You do not need to save manually.

### Editing the data file

GameMates data is saved automatically as a JSON file `[JAR file location]/data/gamemates.json`. The friend list is stored under the `friends` key. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, GameMates starts with an empty friend list at the next run. The invalid file remains on disk until you run a command (GameMates saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause GameMates to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous GameMates home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com t/friend t/colleague`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit FRIEND_INDEX [n/NAME] [p/[PHONE_NUMBER]] [e/[EMAIL]] [t/TAG]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List** | `list`
**Help** | `help`
