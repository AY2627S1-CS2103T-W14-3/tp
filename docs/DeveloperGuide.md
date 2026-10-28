---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* [Vavr](https://vavr.io/) provides `Option`, `Try`, and checked-function adapters for parsing, edit descriptors, and storage.

Implementation sections describe this checkout; [specification departures](#mvp-integration-and-specification-departures)
record differences from the submitted draft. User stories include product goals; proposed capabilities are not yet implemented.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

`MainApp` initializes the model, storage, logic and UI, and saves preferences at shutdown.
Components expose interfaces implemented by their corresponding `*Manager` classes.

| Component | Responsibility | Main dependencies |
| --- | --- | --- |
| [UI](#ui-component) | Display data and execute user commands | Logic, model value objects |
| [Logic](#logic-component) | Parse, execute and save successful commands | Model, Storage |
| [Model](#model-component) | Store friends, profile, view scope and preferences | Commons |
| [Storage](#storage-component) | Convert and persist JSON | Model, Commons |
| [Commons](#common-classes) | Shared utilities and value types | Independent of the four components |

<img src="images/ComponentManagers.png" width="300" />

This sequence shows the component interactions for `delete 1`:

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

### UI component

**API:** [`Ui.java`](https://github.com/AY2627S1-CS2103T-W14-3/tp/blob/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

* `UiPart` loads FXML layouts from `src/main/resources/view`.
* `MainWindow` places `FriendListPanel` beside `FriendDetailsPanel` in a split pane.
* `FriendDetailsPanel` renders contact details, tags and sorted `GameDetailsCard` entries.
* The friend list observes model changes. Mouse selection calls a consumer owned by `MainWindow`.
* Commands select details through `CommandResult.friendToShow`, `showMyself` and `gameFilter`.
  See [Viewing games](#viewing-a-friends-games).
* A successful result without a friend payload shows `Logic.getMyself()`; a single-result `find` shows its friend.
  Parse/command failures update feedback without explicitly refreshing details.

### Logic component

**API:** [`Logic.java`](https://github.com/AY2627S1-CS2103T-W14-3/tp/blob/master/src/main/java/seedu/address/logic/Logic.java)

<img src="images/LogicClassDiagram.png" width="550" />

`LogicManager.execute(commandText)` follows this sequence:

1. `GameMatesParser` selects an `XYZCommandParser` implementing `Parser`.
2. The parser validates arguments and constructs a `Command`.
3. The command executes against `Model` and creates a `CommandResult`.
4. `LogicManager` saves `Model.getGameMates()` through `Storage`, then returns the result to the UI.

Viewing commands also save. Parse/execution failures skip saving. A save failure becomes `CommandException`;
the in-memory change is retained and the result is not returned to the UI.

![Interactions Inside the Logic Component for the delete command](images/DeleteSequenceDiagram.png)

<img src="images/ParserClasses.png" width="600" />

#### Optional values

Vavr `Option` distinguishes an omitted argument from a supplied empty value. Checked-function adapters
propagate validation errors as `ParseException` rather than treating invalid input as omission.

| Input | Add behavior | Edit behavior |
| --- | --- | --- |
| Omitted phone/email | `Phone.EMPTY` / `Email.EMPTY` | Preserve existing value |
| Empty `p/` or `e/` | Reject | Clear the value |
| Omitted `u/` | `gameadd` uses `Username.EMPTY` | `gameedit` preserves the username |
| Empty `u/` | `gameadd` rejects | `gameedit` clears the username |
| Single empty `t/` | Reject | Clear tags |

`EditFriendDescriptor` copies tag sets in its setters and exposes unmodifiable sets from its getters.

### Model component

**API:** [`Model.java`](https://github.com/AY2627S1-CS2103T-W14-3/tp/blob/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="625" />

* `GameMates` stores all friends in `UniqueFriendList` and the profile separately as `Friend myself`.
* `ModelManager` exposes a `SortedList` over a `FilteredList`. `FriendListScope` distinguishes active,
  archived and global search views, including empty results.
* `UniqueFriendList` partitions stored friends with pinned entries first after additions/replacements.
  Explicit list sorting applies a separate comparator to the displayed view.
* `UserPrefs` stores GUI settings and exposes them through `ReadOnlyUserPrefs`.

| Value | Contents and identity |
| --- | --- |
| `Friend` | Name, optional phone/email, tags, games, archive and pin status. Identity uses normalized names. |
| `Name` | Case-insensitive equality; repeated ASCII spaces collapse for comparison. |
| `Game` | Game name, optional username and copied metadata set. `isSameGame()` compares names; full equality includes all fields. |
| `GameName` | Complete-name equality after lower-case normalization with `Locale.ROOT`. |
| `Username` | Case preserved; `Username.EMPTY` represents omission. |

Add/edit commands prevent names shared between a friend and Myself. `gameadd` rejects duplicate game names,
but JSON loading currently permits same-name games with different usernames because sets use full equality.

### Storage component

**API:** [`Storage.java`](https://github.com/AY2627S1-CS2103T-W14-3/tp/blob/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

`StorageManager` delegates to `JsonGameMatesStorage` and `JsonUserPrefsStorage`.
The JSON adapters map `friends` and `myself` to friends, games, tags and metadata.
Paths are relative to the process working directory, rather than the JAR location:

| File | Written by | Persisted state |
| --- | --- | --- |
| `data/gamemates.json` | Every successfully executed command | Friends and profile, including games, metadata, archive and pin status |
| `preferences.json` | `MainApp.initPrefs()` and `stop()` | GUI settings; `MainWindow.handleExit()` records dimensions/position |

Selection, view scope, sorting and filters are transient. Unknown JSON fields are ignored and lost on the next save.

| Stored field | Missing/null/empty handling |
| --- | --- |
| `myself` | Use `Friend.DEFAULT_MYSELF` |
| `games`, `tags`, `metas` | Missing/null collections load as empty |
| `phone`, `email`, `username` | Null/empty values become the corresponding `EMPTY` value |
| `archived`, `pinned` | Missing values default to `false`; null `pinned` also becomes `false` |
| Game `name` | Required and validated |

Invalid non-empty contacts are rejected without trimming. Invalid games/metadata, duplicate metadata keys or
duplicate friend names reject the entire load. `Option` represents missing files; `Try` wraps read/conversion
failures in `DataLoadingException` with the original cause.

| Load outcome | Startup behavior |
| --- | --- |
| Missing data file | Sample friends and default profile |
| Invalid data file | Empty friends and default profile |
| Missing/unreadable preferences | Default preferences |
| Root JSON `null` | Known defect: an uncaught `NullPointerException` can prevent startup |

There is no automatic repair or backup. A successful command can overwrite invalid data with the fallback state.
Model mutation and saving are not one atomic transaction; shutdown does not save the friend data.

### Common classes

Shared utilities and value types are in `seedu.address.commons`.
Diagram sources are in `docs/diagrams`; regenerate matching `docs/images/*.png` after edits.
See the [PlantUML tutorial](https://se-education.org/guides/tutorials/plantUml.html) for the diagram workflow.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Friend creation, editing and deletion

| Command | Validation and update | Effect on friend view |
| --- | --- | --- |
| `add` | Requires `n/`; optional contacts default to `EMPTY`. Rejects duplicate names, including Myself. | Show all active friends |
| `edit` | Requires a displayed index or `myself`. Omitted fields are preserved; games remain unchanged. | Clear filters within active/archive views; retain search predicates and the profile's current view |
| `delete` | Requires a displayed index; permanently removes the friend and their games. | Preserve current predicate |
| `clear` | Replace the friends with an empty `GameMates`, copying the profile. | Retain scope; friend list becomes empty |

* `ParserUtil` trims and validates supplied values. `ArgumentMultimap` rejects duplicate singleton prefixes.
* `FriendValidator` checks normalized names across stored friends and the profile. Different contacts/tags do not
  make an equal name unique. See [Friend archiving](#friend-archiving) for archived-name conflicts.
* `EditFriendDescriptor` distinguishes omission from clearing. Supplied tags toggle individually; repeated tags
  apply once. A single empty `t/` clears all tags; mixing empty and non-empty tags is invalid.
* `DeleteCommand` uses the generic invalid-index error. `EditCommand` and `GameListCommand` distinguish an empty
  list from an out-of-range index and report the displayed range.

The [User Guide](UserGuide.html#adding-a-friend-add) defines contact syntax. Names accept 1–100 ASCII
alphanumeric characters/spaces; phones validate digit counts/grouping; emails use ASCII dot-atom/domain rules.
No fields are verified against online services.

### Myself profile

`GameMates.myself` stores a separate `Friend`, initially `Friend.DEFAULT_MYSELF`.
The case-insensitive `myself` selector works independently of the displayed friend list:

```text
edit FRIEND_INDEX|myself [n/NAME] [p/[PHONE_NUMBER]] [e/[EMAIL]] [t/TAG]…
gameadd FRIEND_INDEX|myself g/GAME_NAME [u/USER_NAME] [m/KEY:VALUE]…
gameedit FRIEND_INDEX|myself g/EXISTING_GAME [n/NEW_GAME_NAME] [u/USERNAME] [m/KEY:VALUE]…
gamedel FRIEND_INDEX|myself g/GAME_NAME
gamelist FRIEND_INDEX|myself [g/GAME_NAME]
```

1. `ParserUtil.parseIndexOrMyself()` maps the keyword to `Index.myself()` or parses a displayed numeric index.
2. `ModelManager.resolveFriend()` captures the selected friend in a `FriendTarget.Myself` or `ListedFriend` record.
3. Mutating commands validate a replacement, then call `Model.setFriend(target, updatedFriend)`.
   The target type routes the update to the profile or the captured listed friend.

`FriendValidator` checks profile-name conflicts against all stored friends, including hidden/archived entries.
Profile edits preserve the friend-list scope and predicate. After saving, `MainWindow` refreshes the profile
through `Logic.getMyself()`.

![Classes involved in resolving and updating a friend target](images/MyselfTargetClassDiagram.png)

This sequence shows `gameadd myself g/Valorant u/player` after parsing; helpers and subsequent saving are omitted:

![Successful game addition to the Myself profile](images/MyselfGameAddSequenceDiagram.png)

### Name and game filtering

| Command | Predicate and scope | Sort behavior |
| --- | --- | --- |
| `find KEYWORD...` | `NameContainsKeywordsPredicate`: any whole name word matches any keyword, ignoring case; searches both statuses | Retain current sort |
| `list [g/GAME_NAME]` | Active friends; `GameNameMatchesPredicate` matches a complete game name, ignoring case | Clear sort when `s/` is omitted |
| `list s/name` | Active friends, with an optional game filter | Name comparator |
| `list s/game` | Active friends, with an optional game filter | Alphabetically first game; name breaks ties |

Each command replaces the previous predicate. `ModelManager` sorts through `SortedList`; `o/desc` reverses the
selected comparator and `o/asc` is the default. Without explicit sorting, the stored order is used.
Ascending game order puts friends without games last; reversing the comparator currently reverses that placement too.

`find` accepts a non-empty keyword list, without prefixes or a keyword-length cap; `list` does not accept `n/`.
No matches is a successful empty result. Filtering/sorting does not include or modify the separate profile.

### Squad selection

`SquadCommandParser` parses `squad g/GAME n/NUMBER_OF_PEOPLE` using
`ArgumentTokenizer.tokenizeWithQuotedValues()`. Whole-value quotes preserve literal `g/` and `n/` text:
`squad g/"Version n/2" n/2`. It shares the quote/escape rules used by `gameedit`; malformed quotes, duplicate
prefixes, invalid game names and sizes outside 1–20 are rejected before execution.

`SquadCommand` finds matching games in the displayed friend list, randomly selects `size - 1` distinct friends,
and reports them in displayed-index order. Myself is always the first member, even without a matching profile game.
The command preserves stored data, scope, filters and sorting.

Regression tests cover prefix-containing names added by `gameadd`, either parameter order, escaped quotes/backslashes,
malformed input and preservation of the filtered view.

### Editing an existing game

`GameEditCommandParser` reuses the shared field validators and `tokenizeWithQuotedValues()`.
Quote complete values containing literal prefixes: `g/"PUBG n/Game"`. The tokenizer decodes `\"` and `\\`,
rejects malformed quotes/escapes or trailing text, and leaves unquoted prefix handling unchanged.

| Editable field | Omitted | Supplied |
| --- | --- | --- |
| `n/` | Preserve game name | Rename, rejecting a collision with another game |
| `u/` | Preserve username | Replace; empty `u/` becomes `Username.EMPTY` |
| `m/` | Preserve metadata (`null` in the descriptor) | Replace the entire set; a single empty `m/` clears it |

`EditGameDescriptor` copies supplied metadata. Shared parsing rejects duplicate normalized keys and splits each
pair at the first colon. At least one editable field is required.

Execution through `GameEditCommand`:

1. Resolve a displayed friend or Myself through `FriendTargetResolver`.
2. Require exactly one matching game; reject missing or ambiguous names from externally edited JSON.
3. Construct the replacement and check for name collisions before mutation.
4. Call `Friend.withGames()` and `Model.setFriend()` once, preserving contact details, tags, archive and pin status.
5. Return the updated friend payload, or use the profile refresh for Myself. Successful edits clear old card filters.

The friend-list predicate is retained; a rename can remove its friend from a game-filtered view.
Validation failures preserve data and details. Saving follows the existing `LogicManager` path without rollback
on disk-write failure. When adding friend fields, update the `withGames()` copy constructor too.

Tests cover routing, validation before mutation, displayed/profile/archived targets, metadata replacement,
case-only renames, UI refresh, pin status and JSON round trips.
See the [game edit sequence diagram source](diagrams/GameEditSequenceDiagram.puml).

### Viewing a friend's games

`gamelist FRIEND_INDEX|myself [g/GAME_NAME]` filters right-hand game cards without changing stored data or the
left-hand friend list.

1. `GameListCommandParser` requires a selector before at most one `g/`. It rejects unknown/repeated prefixes,
   validates the selector with `parseIndexOrMyself()` and validates game names using `GameName`.
2. `GameListCommand` checks numeric indices against the displayed list, then resolves the selected friend/profile.
   It matches complete names using `GameName.equals()`. No match and no games are successful results.
3. `CommandResult` carries the original friend, optional `gameFilter` and `showMyself` flag.
   A filter requires a friend; the fields participate in equality, hashing and diagnostic output.
4. `MainWindow.refreshFriendDetails()` passes the payload to `FriendDetailsPanel.setFriend()`.
   The panel filters the card stream and displays an empty label when needed; the stored game set is unchanged.

* Matching feedback uses the stored game spelling; no-match feedback uses the trimmed query.
* Unfiltered `gamelist`, mouse selection and single-result `find` show all games for the selected friend.
  The two-argument `setFriend()` clears a previous card filter.
* Parse/execution failures preserve the previous details. Default-profile selection shows `No details added`.
* `GameDetailsCard` shows the game name, username and wrapping metadata sorted by key ignoring case.

Parser/command tests cover validation, displayed indices and unchanged data. JavaFX tests cover filtered payloads,
empty states, filter clearing and preservation after invalid input.

![Command-to-panel flow for a filtered game list](images/GameListSequenceDiagram.png)

### MVP integration and specification departures

The submitted *GameMates – Feature Specification* in the
[shared project document](https://docs.google.com/document/d/1UI16NkQ8w8hXb-8Jez8UDpGYkRc8wbs42XZntx9n_sI/edit)
(CS2103T-W14-3, 20 September 2026, exported pages 3–26) differs from the implementation:

| Area (draft pages) | Implemented behavior |
| --- | --- |
| Own-profile editing (10–13) | Explicit `myself` selector required; profile and friend names must differ. |
| Games/metadata (16–23) | Add/delete/edit, optional usernames, metadata entry/replacement/clearing and card rendering are available. |
| Tags (11) | Supplied tags toggle rather than replace the set; empty `t/` clears it. |
| Names (5–6) | Up to 100 ASCII letters/digits/spaces, rather than 50 characters. Case/repeated spaces do not distinguish names; parentheses are invalid. |
| Phone/email (5–6) | Constrained phone grouping with 3–15 digits (2–15 after `+`); ASCII email syntax with a 254-character total and 64-character local-part limit. |
| Filtering (14–15) | Separate `find` and `list g/` commands replace filters. Feedback is `Listed all active friends.` or `N friend(s) listed!`. |
| GUI (15, 25) | Split layout keeps friends on the left and details/game cards on the right. |
| Errors | Use the command-specific wording in the User Guide. |
| Game uniqueness (16–19) | Commands reject duplicate names; JSON sets still permit same-name games with different usernames. |

Use the implemented syntax when reconciling the draft. Deletion confirmation and undo are outside product scope.
Known loader and text-validation limitations are recorded in [Storage](#storage-component) and
[Game metadata](#game-metadata).

### Game metadata

`Meta` pairs a `MetaKey` with a `MetaValue`. These immutable classes are in `seedu.address.model.game.meta`.
`Game` copies its metadata collection and exposes an unmodifiable set; its two-argument constructor uses no metadata.

#### Validation and equality

`StringUtil.isValidTextField()` strips surrounding whitespace, requires a letter/digit and rejects ISO controls.
It allows punctuation and format characters, so it is not a complete Unicode printability test.
Lengths use UTF-16 units (`String.length()`); supplementary characters count as two.
`GameName` and `Username` share this validation.

| Field | Length after trimming | Colons | Equality |
| --- | --- | --- | --- |
| `MetaKey` | 1–50 | Rejected | Case-insensitive with `Locale.ROOT` |
| `MetaValue` | 1–200 | Allowed | Case-sensitive |

Keys are unique within a game, even for repeated identical pairs; other games may reuse them.
`Game` enforces uniqueness at construction. `Meta.equals()` compares both fields; full `Game.equals()` includes
metadata, while `isSameGame()` compares only the game name.

#### Parsing

* `parseMeta()` splits at the first colon and delegates to `parseMetaKey()` / `parseMetaValue()`.
  For example, ` Role : Support:Mid ` becomes key `Role`, value `Support:Mid`.
* `parseMetas()` rejects invalid pairs and duplicate normalized keys. Pass all raw values, without deduplicating first.
* `gameadd` accepts repeatable `m/` pairs alongside singleton `g/` and `u/`; omission creates an empty set.
* `gameedit` uses the same pair validation for replacement; a single empty `m/` clears the set.
* Validation completes before either command changes a friend/profile.

#### Display

`GameDetailsCard` renders wrapping `Key: Value` labels below the username, sorted by key ignoring case.
Spelling/values are preserved; an empty metadata container is hidden and unmanaged.

#### Persistence and testing

`JsonAdaptedGame` stores a `metas` array of `JsonAdaptedMeta` objects:

```json
{
  "name": "Valorant",
  "username": "player",
  "metas": [{ "key": "Role", "value": "Support:Mid" }]
}
```

Missing/null `metas` loads as empty. Null entries, invalid fields and duplicate keys reject conversion.
Tests cover validation boundaries, key uniqueness, immutability, friend/profile updates, filter preservation,
metadata preservation during contact edits and JSON round trips.
`GameBuilder.addMeta()` appends; `withMetas()` replaces; `build()` rejects duplicate keys.

### Friend archiving

Archiving preserves friends and their complete game data in the same `UniqueFriendList`.
It does not back up files or provide undo.

#### Stored status and identity

* `Friend.withArchived()` copies the friend with a new boolean status. Equality/hash code include status;
  `isSameFriend()` still compares normalized names.
* `FriendValidator` reserves names across active/archived friends and Myself. Conflicts with an archived name
  suggest `archivelist` / `unarchive`; validation is independent of the displayed filter.
* An existing friend may retain their own name. Myself cannot be archived/restored.

#### Display scope and filtering

`ModelManager` stores `FriendListScope` independently of result contents. The logic exposes it to `MainWindow`
for the list heading; `FriendCard` shows an Archived badge.

| Scope | Entered by | Predicate |
| --- | --- | --- |
| `ACTIVE` | Startup, `list [g/GAME_NAME]`, adding a friend | Active status plus optional game match |
| `ARCHIVED` | `archivelist [g/GAME_NAME]` | Archived status plus optional game match |
| `SEARCH` | `find KEYWORD...` | Name keywords across both statuses |

Each command replaces the predicate. Game matching uses `GameNameMatchesPredicate`; only the archive parser
uses `parseOptionalGameFilter()`, while the active-list parser also handles sorting. `gamelist` preserves the view.
Empty unfiltered archives give archiving guidance; empty filtered archives report no matching friends.

#### Archive and restore execution

1. Parse one positive displayed index; reject `myself` and extra arguments.
2. Check scope, index and target status. `archive` rejects archive-only views; `unarchive` requires an archived target.
3. Replace the friend through `Model.setFriend()`, preserving other fields and the current predicate/scope.
4. The observable list removes entries that no longer match the status filter. Search results retain the friend.
5. `LogicManager` saves through the existing storage path.

Validation failures preserve state. `edit`, `gameadd`, `gameedit` and `gamedel` preserve archive status.
Friend edits clear active/archive filters but retain name searches; game edits retain all predicates.
`delete` permanently removes either status; `clear` removes both and preserves Myself.

#### Persistence and design considerations

`JsonAdaptedFriend` persists `archived`; missing status defaults to `false`.
Restart restores friend data but resets the view to unfiltered active friends.

One collection avoids transfers and cross-collection uniqueness checks. Filtering scans that collection;
separate active/archive collections could reduce status-specific scans, but would complicate global search/storage.
Regression tests cover scopes, displayed indices, validation, identity conflicts, archived edits and JSON round trips.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* Play multiple online or multiplayer games.
* Have many gaming friends across different games or platforms.
* Want to remember each friend's in-game usernames, preferred roles, servers, play styles, or other game-specific details.
* Prefer using a command-line interface over navigating menus and mouse interactions.
* Prefers a desktop application for managing gaming contacts.
* Is comfortable typing commands to manage information quickly.
* Still wants a simple visual interface to view their friend and game information clearly.

**Value proposition**: Manage gaming friends and their game-specific details faster than generic contacts apps, spreadsheets, or chat history searches.


### User stories

These are product requirements, including future work. Implemented availability is recorded in the integration table above.

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                          | I want to …​                                            | So that I can…​                                                   |
|----------|----------------------------------|--------------------------------------------------------|------------------------------------------------------------------|
| `* * *`  | new user                         | see usage instructions                                 | refer to instructions when I forget how to use the App           |
| `* * *`  | gamer                            | add a gaming friend                                    | remember the people I play games with                            |
| `* * *`  | gamer                            | delete a friend                                        | remove friends I no longer play with                             |
| `* * *`  | gamer                            | list all my friends                                    | see everyone I have saved at a glance                            |
| `* * *`  | gamer                            | edit a friend's details                                | keep their information up to date                                |
| `* * *`  | gamer                            | add games to a friend's entry                          | remember what games they play                                    |
| `* * *`  | gamer                            | remove games from a friend's entry                     | know which games they no longer play                             |
| `* * *`  | gamer                            | add a friend's in-game username for a game or platform | know what name to search for when inviting them                  |
| `* * *`  | gamer                            | edit a friend's in-game username                       | keep it up to date when they change their username               |
| `* * *`  | gamer                            | search for a friend by name                            | find the people I want without scrolling through the whole list  |
| `* * *`  | gamer                            | filter my friends by game or platform                  | quickly find friends relevant to what I am currently playing     |
| `* * *`  | gamer                            | add my own details                                     | see how I fit into the context of my friends                     |
| `* * *`  | gamer                            | edit my own details                                    | keep my profile accurate as my games and usernames change        |
| `* * *`  | gamer                            | have my data saved automatically                       | keep my friend list after closing the App                        |
| `* *`    | gamer                            | be warned when adding a friend who already exists      | avoid duplicate entries                                          |
| `* *`    | gamer                            | see which games my friends and I have in common        | decide what to play together                                     |
| `* *`    | gamer who plays many games       | see all the games a friend plays                       | know which games I can invite them to                            |
| `* *`    | gamer                            | record a friend's preferred role (e.g. support, tank)  | put together a balanced team                                     |
| `* *`    | gamer                            | record the server or region a friend plays on          | invite only friends who can join my lobby                        |
| `* *`    | gamer                            | record a friend's rank in a game                       | find friends at a similar skill level                            |
| `* *`    | gamer                            | add notes about a friend's play style                  | remember who is casual and who is competitive                    |
| `* *`    | gamer                            | tag friends (e.g. "duo", "casual")                     | group them in ways that suit me                                  |
| `* *`    | expert user                      | use shorter command aliases                            | manage my friends even faster                                    |
| `*`      | gamer with many friends          | sort friends by name                                   | locate a friend easily                                           |
| `*`      | gamer                            | mark friends as favourites                             | find my regular teammates first                                  |
| `*`      | gamer with friends abroad        | record a friend's time zone                            | know when they are likely to be online                           |
| `*`      | gamer                            | record when I last played with a friend                | reconnect with friends I have not played with in a while         |
| `*`      | gamer                            | export my friend list                                  | back up my data or move it to another computer                   |

### Use cases

(For all use cases below, the **System** is `GameMates` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add a friend**

**MSS**
1. User requests to add a friend, providing a name.
2. GameMates adds the friend to the list and shows a confirmation.

   Use case ends.

**Extensions**
* 1a. The name field is missing or empty.

  * 1a1. GameMates shows an error message.

    Use case ends.
* 1b. A friend with the same name already exists.

  * 1b1. GameMates shows `This friend already exists in GameMates.`

    Use case resumes at step 1.


**Use case: Delete a friend**

**MSS**
1. User requests to list friends.
2. GameMates shows a list of friends.
3. User requests to delete a specific friend in the list.
4. GameMates immediately deletes the friend and their game entries and shows `Successfully deleted NAME.`

   Use case ends.

**Extensions**
* 2a. The list is empty.

  Use case ends.
* 3a. The given index is invalid.

  * 3a1. GameMates shows an error message.

    Use case resumes at step 2.

**Use case: Edit a friend's in-game username**

Implemented by `gameedit FRIEND_INDEX g/GAME_NAME u/USERNAME`. The `myself` selector also supports profile games.

**MSS**
1. User requests to list friends.
2. GameMates shows a list of friends.
3. User selects a displayed friend and an existing game, providing the new username.
4. GameMates updates that game's username and shows the updated friend details and command feedback.

   Use case ends.

**Extensions**
* 2a. The list is empty.

  Use case ends.
* 3a. The given index is invalid.

  * 3a1. GameMates shows an error message.

    Use case resumes at step 2.
* 3b. The specified game does not exist for that friend.

  * 3b1. GameMates shows an error message.

    Use case resumes at step 2.

* 3c. The supplied username is invalid.

  * 3c1. GameMates shows a validation error without changing the game or current details view.

    Use case resumes at step 3.


**Use case: Search for a friend**

**MSS**
1. User requests to search for friends matching a keyword.
2. GameMates shows a list of friends matching the keyword.

   Use case ends.

**Extensions**
* 1a. No keyword is given.

  * 1a1. GameMates shows an error message.

    Use case ends.
* 2a. No friends match the keyword.

  Use case ends.


**Use case: Filter friends by game**

**MSS**
1. User requests to filter friends by a specific game.
2. GameMates shows a list of friends who play that game.

   Use case ends.

**Extensions**
* 2a. No friends play that game.

  Use case ends.

### Non-Functional Requirements

The following are targets, not measured guarantees. Startup/performance and large-data capacity need separate measurement.

#### Environment

1. Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
1. Should work offline on a single user's personal laptop, without requiring an internet connection or any installed software other than Java `25` or above.
1. Should work on screens with a resolution of `1280 x 720` or higher, with all panels and the command box visible without scrolling the window at its default size.

#### Data

1. All data should be stored locally in a human-editable JSON file. GameMates should not send any data over the network.
1. Successfully saved data should be restored on the next launch from the same working directory. Current data saves
   occur after successful commands; window preferences save on shutdown. Metadata persistence is implemented in the JSON adapters.
1. If the data file is missing, GameMates should start with the sample data without crashing. If the data file is corrupted, GameMates should start with an empty list without crashing.

#### Capacity

1. Should be able to hold up to 1000 friends, each with up to 100 game entries.

#### Performance

1. GameMates should be ready to accept commands within 3 seconds of starting the application on a _modern computer_ running Java `25`, with up to 1000 friends and 100 game entries per friend.
1. GameMates should display the results of each friend listing or filtering command within 2 seconds of submission on a _modern computer_ running Java `25`, with up to 1000 friends and 100 game entries per friend.

#### Usability

1. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to add, edit, delete, list and filter friends, and add, delete and list game entries, faster using commands than using the mouse.
1. A first-time user should be able to add a friend and add a game entry to that friend within 5 minutes of first launching GameMates, using only the in-app help and the sample data as a reference.
1. Every invalid command should produce an error message that states what was wrong and shows the correct usage or allowed input range, and should not modify any data.
1. Every command described in the User Guide should be usable through the command box alone, without needing the mouse.

#### Constraints

1. GameMates does not verify friend names, game names, usernames or metadata against any online gaming platform. All such data is whatever the user enters.

### Glossary

* **Modern computer**: A computer running a mainstream OS with 16 GB of RAM, a 512 GB SSD, and a mid-range modern processor such as an Intel Core i5 or AMD Ryzen 5.
* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Friend**: A person stored in GameMates, identified by a unique name and optionally associated with contact details and game entries.
* **User profile**: The current user's separately stored details, edited using `edit myself ...` and preserved by friend-list commands.
* **Game entry**: A record that a friend plays a game, containing a game name, in-game username and optional metadata.
* **Role**: A job or duty a friend plays as in a particular game, for example a friend can play as a `Sniper` or a `Healer`.
* **Game name**: The user-provided name used to identify a game within one friend’s game entries. Aliases such as “Val” and “Valorant” are treated as different game names.
* **In-game username/Handle**: The name/alias a friend uses within a particular game. It may not refer to their real name and can be different for different games.
* **Metadata**: Additional stored information for a game entry, such as a role, preferred weapon, server or play style; entered through `gameadd` using `m/KEY:VALUE`, replaced or cleared through `gameedit`, and displayed below the username in game cards.
* **Metadata key**: The label before the first colon in a metadata item. For example, `Role` in `Role:Sentinel`.
* **Metadata value**: The information after the first colon in a metadata item. For example, `Sentinel` in `Role:Sentinel`.
* **Friend index**: A positive number representing a friend’s position in the currently displayed friend list.
* **Currently displayed list**: The friend list currently shown after any active filters have been applied. Friend indices refer to this list.
* **Filter**: A condition used to narrow a displayed list, such as a name keyword or game name.
* **Name keyword**: A full word matched against a friend's name, ignoring case. A substring such as `Dav` does not match `David`.
* **Case-insensitive comparison**: A comparison that treats uppercase and lowercase letters as equivalent.
* **Trimmed value**: A value after its leading and trailing spaces have been removed.
* **Duplicate**: Two entries considered the same according to the project’s matching rules, and therefore not both allowed.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

### Prerequisites and test isolation

Use JDK 25 and a graphical desktop. Build with `./gradlew shadowJar`, then copy `build/libs/gamemates.jar` into
an empty disposable folder. Open a terminal in that folder and run `java -jar gamemates.jar`.
Never use a real user's data for deletion, corruption or write-failure tests. Use a new disposable folder for each
scenario labelled **fresh sample data**; do not run two app instances against the same data file.
Commands below are entered in the application's command box, not the terminal.

### Launch, help and window settings

Prerequisite: fresh sample data.

1. Launch from the disposable folder. Expected: six sample friends on the left; the right panel says
   `Your details` and `No details added`. A missing data file is not a crash.
2. Enter `help`. Expected: feedback `Opened help window.`, a Help window containing the User Guide URL and
   **Copy URL** button. Copy and open it in a browser. Close the Help window and press `F1` to reopen it.
3. Resize/move the main window, then enter `exit`. Relaunch from the same folder.
   Expected: saved size/position restored, all friends visible, default profile on the right.

### Friend creation, editing, identity and tags

Prerequisite: fresh sample data. These steps deliberately add a disposable friend.

| Step | Input/action | Expected result |
|------|--------------|-----------------|
| 1 | `add n/Joash Test t/duo` | Seven friends. Feedback starts `Added Joash Test.` and shows `Not provided` for phone and email. |
| 2 | `add n/joash   test t/other` | Duplicate-name error; still seven friends. A different tag does not bypass identity checks. |
| 3 | `find Joash`, then `edit 1 p/+65 9123-4567 e/joash@example.com t/duo t/casual` | Edits Joash rather than full-list friend 1. The search remains filtered to Joash. Games remain unchanged; `duo` is removed and `casual` added. |
| 4 | `find Joash`, then click the result | Right panel shows the formatted phone, email and only `casual`. |
| 5 | `edit 1 p/ e/ t/casual t/casual` | Phone/email removed; `casual` toggled off once. Feedback lists removed details. |
| 6 | `find Joash`, then `edit 1 t/duo t/casual`, then `find Joash`, then `edit 1 t/` | Both tags are added, then all are cleared. Omitted name/contact fields are preserved. |
| 7 | `find Joash`, then `gamelist 1` | `Joash Test has no games added yet.` and a `No games added` label. |

Test invalid input independently: `add`, `add n/`, `add n/Joash-Test`, `add n/Test p/`, `add n/Test p/12`,
`add n/Test p/9123.4567`, `add n/Test p/123;4`, `add n/Test e/name@`, `add n/Test e/a..b@example.com`,
`add n/Test n/Test`, and `edit 1 t/ t/duo`. Expected: validation or duplicate-prefix errors and no stored change.
Also try 100 versus 101 ASCII letters for a name, 15 versus 16 phone digits, and email local parts of 64 versus 65
letters followed by `@example.com`. Expected: the first in each pair is valid and the second rejected, using fresh
names or a disposable friend for each attempt. These are syntax checks, not online contact verification.

### Friend filters and displayed indices

Prerequisite: fresh sample data.

1. `find alex david`: expected Alex Yeoh and David Li, with `2 friend(s) listed!`.
2. `find Dav`: expected zero results; substring matching is not supported. `find david` returns David and opens
   his details automatically. `find` without keywords reports usage and preserves the current display.
3. `list g/minecraft`: expected Bernice Yu and Roy Balakrishnan, replacing the David filter.
4. `gamelist 1`: expected Bernice's Minecraft card, although Bernice was full-list index 2. Both Minecraft players
   remain on the left. `find Alex` now finds Alex from the full data, not from the two Minecraft results.
5. `list g/Val`: expected zero results. `list` restores all six friends and returns `Listed all active friends.`.
6. `list n/Alex g/Valorant`: expected invalid-format feedback; combined filtering is not supported.

### Game cards, empty states and display-filter clearing

Prerequisite: fresh sample data.

1. `gamelist 1`: expected Alex's Fortnite and Valorant cards, each showing a game name and username.
2. `gamelist 1 g/valorant`: expected `Showing Valorant for Alex Yeoh:` and only his Valorant card.
3. `gamelist 0`, then `gamelist 2147483648`, then `gamelist 7`: expected index errors; the existing Valorant
   card stays visible. Try `gamelist`, `gamelist 1 g/`, `gamelist 1 g/Valorant g/Valorant`,
   `gamelist 1 x/Valorant`, and `gamelist 1 extra`: expected the corresponding usage, empty-name or repeated-filter error.
4. `gamelist 1 g/Val`: expected `Alex Yeoh does not have Val.` and `No matching game: Val`. This is successful,
   not an exception. Alex's contact details remain visible, and the left list still has six friends.
5. `gamelist 1`: expected both cards again. Filter to Valorant again, then click Bernice: expected her Minecraft card,
   proving that selecting another friend clears the display filter. No stored game entry should be removed.
6. `gamelist 3 g/Stardew Valley`: expected Charlotte's Stardew Valley card. `find David`, then `gamelist 1`,
   selects David rather than Alex. `list g/Unknown`, then `gamelist 1`, reports an empty displayed list.
7. `gamelist myself`: expected `Your details` and all profile games. `gamelist myself g/Valorant` shows only
   your Valorant card if present. `find Unknown`, then `gamelist MYSELF`, still selects your profile with the empty
   friend list unchanged. `gamelist myself g/Unknown` reports no match. If no profile details exist, the panel
   continues showing `No details added`.

### Deletion and clearing

Prerequisite: fresh sample data. These actions are destructive within the disposable folder.

1. `list g/Minecraft`, then `delete 1`: expected Bernice is removed immediately, with
   `Successfully deleted Bernice Yu.`. No confirmation appears. Roy remains in the filtered list, and other friends
   reappear with `list`. Bernice's games are removed with her, while Roy's Minecraft entry remains.
2. `delete`, `delete 0`, `delete -1`, `delete x`, `delete 2147483648`, `delete 1 2` and `delete 999`:
   expected errors without deleting anyone. Missing index reports `Please provide an index.`; malformed values
   report `Index must be a positive integer between 1 and 2147483647 (inclusive).`; an out-of-range index reports `The friend index provided is invalid.`.
3. `clear`: expected immediate empty list and `All active and archived friends have been cleared!`. `delete 1` then reports the generic
   invalid-displayed-index error; `edit 1 n/Test` and `gamelist 1` report an empty displayed list. There is no undo.

### Saved profile and game round trip

Prerequisite: a disposable app folder. Close the app and create `data/gamemates.json` with the fixture below.
This seeds existing data for testing; it is not an own-profile edit command.

```json
{
  "friends": [{
    "name": "Fixture Friend", "phone": "", "email": "", "tags": ["duo"],
    "games": [{"name": "Valorant", "username": "friend123"}]
  }],
  "myself": {
    "name": "Joash", "phone": "91234567", "email": "joash@example.com", "tags": [],
    "games": [{"name": "Minecraft", "username": "joash123"}]
  }
}
```

1. Relaunch: expected Joash's profile and Minecraft username on the right. `gamelist 1` shows Fixture Friend's
   Valorant entry. `edit 1 p/98765432` preserves that entry and shows the unchanged profile after success.
2. `list g/Unknown`, then `edit MYSELF n/Joash Updated p/`: expected the renamed profile with no phone number and
   its Minecraft game preserved. The left friend list remains empty. `edit myself n/Fixture Friend` rejects the
   duplicate name, and `add n/joash updated` rejects a name matching the profile.
3. Try `edit n/Changed`: expected a missing-index error. Run `gameadd myself g/Test u/player m/Role:Support:Mid`, then
   `gamelist myself g/Test`: expected `Role: Support:Mid` in the addition feedback and below the username in the game card.
   Run `gamedel myself g/Test`: expected success and the original Minecraft entry preserved.
   To also test loading metadata from JSON, close the app, add
   `"metas": [{"key": "Role", "value": "Support:Mid"}]` to the fixture's Minecraft entry, relaunch and run `list`.
   The saved JSON must retain that pair; cards show the game name and `Username: Not provided` when the username is omitted.
   A missing `metas` field loads as empty; a repeated key (including case variants) rejects the load and uses the documented fallback.
4. Run `gameadd myself g/Test` without `u/`: expect success with `Username: Not provided` in feedback and the game card.
   `gameadd myself g/Other u/` must fail. Exit and relaunch; the Test game must retain its empty username.
5. `find Fixture`, then `gamelist 1 g/valorant`, then `exit`. Relaunch from the same folder.
   Expected: the edited friend phone, friend game and profile game survive, with Joash Updated's profile displayed
   and its phone still removed. Filters are reset.
6. `clear`, then `exit` and relaunch. Expected: no friends, but the same updated profile and Minecraft username.

### Missing, invalid and unwritable files

Prerequisite: independent disposable app folders and closed app instances before each file edit.

| Setup and action | Expected result |
|------------------|-----------------|
| Start in an empty folder without `data/gamemates.json`; enter `list`. | Six sample friends and a default profile; the successful command creates the data file. |
| Save the fixture above, close the app, copy the data file to a backup, then replace its contents with `{broken`. Relaunch without entering commands. | Empty friends and default profile; the malformed file stays on disk. No automatic backup or recovery prompt. Close the app and restore the backup to recover. |
| Repeat the invalid-file setup, then enter `list`. | `Listed all active friends.` with an empty list; the file is overwritten with the empty fallback data. |
| In a separate disposable folder, replace the data file with the literal `null`, then launch. | Known defect: startup can fail with an uncaught null-value error instead of the empty-data fallback. Restore a valid backup to continue. |
| In a copy of the valid fixture, use an invalid phone such as `12`, or duplicate the friend with an equal name. Relaunch. | The whole data load fails; empty friends and default profile. Restore the backup before continuing. |
| Remove the `myself` and `games` properties from a valid copy, then relaunch. | Default profile and a friend with no games. Empty/missing phone and email are accepted; invalid non-empty contacts are not. |
| Copy the same JAR to a second empty folder and launch there. | A separate sample dataset, not the first folder's saved friends. Restore a backup into the second folder to transfer data. |
| With the app running, make its data file unwritable for that user (for example `chmod a-w data/gamemates.json` on Linux/macOS). Enter `add n/Unsaved Test`. | A `Could not save data` error. The friend may already be visible in memory; this is not a rolled-back transaction. |
| Restore write access (`chmod u+w data/gamemates.json`), enter `list`, then exit and relaunch. | The in-memory change is saved and the new friend reloads. Test permissions as a normal user, not root. |

### Documentation preview

Use the repository's Jekyll workflow locally with Ruby 3.3 and Bundler:

```sh
cd docs
bundle install
JEKYLL_ENV=production bundle exec jekyll build --baseurl /tp --destination /tmp/gamemates-docs-site
```

Serve the generated directory under `/tp/` when checking links, or build with an empty base URL for a local root
preview. Inspect the User Guide, Developer Guide and home page in a browser: tables, command brackets, code blocks,
TOC anchors, screenshots, diagrams and mobile-width overflow. The existing `Build docs PR preview` workflow runs
on open PRs labelled `type.Docs`; this task does not publish a preview or change workflows.

Current screenshots must come from the application, not the specification mockups. `Ui.png` shows both panels,
`findAlexDavidResult.png` shows name filtering, and `gamelistFiltered.png` / `gamelistNoMatch.png` show card filtering.
Diagram sources live in `docs/diagrams`; regenerate their matching `docs/images/*.png` after changing a source.

### Manual squad selection checks

Use a disposable data folder. Run `clear`, `add n/Tester`, then `gameadd 1 g/Version n/2 u/player`.

1. Run `find Tester`, then `squad g/"Version n/2" n/2`. Expect Me and Tester, with username `player` and index 1.
2. Run `squad n/2 g/"Version n/2"`. Expect the same two members; the parameter order does not change selection.
3. Try an unclosed quoted value, `squad g/"Version n/2 n/2`, and duplicate size prefixes,
   `squad g/"Version n/2" n/2 n/2`. Expect errors with stored data and the search filter unchanged.
4. Run `squad g/"Version n/2" n/3`. Expect `Could not find a squad of 3 people.` without changing the view.

### Manual game edit checks

Use a disposable data directory. Add `add n/Tester`, `gameadd 1 g/Valorant u/tester` and
`gameadd 1 g/Minecraft u/builder`. Edit with `gameedit 1 g/Valorant n/VALORANT u/newPlayer m/Role:Healer`.
Expect both games and preserved friend fields. Try `gameedit 1 g/Valorant n/Minecraft` and repeated
normalized metadata keys; expect errors with unchanged data and details. Clear with `gameedit 1 g/Valorant m/`.
Add `gameadd myself g/Valorant u/own`, run `find Nobody`, then `gameedit myself g/Valorant u/newOwn`;
expect updated profile despite the empty list. Restart from the same directory and confirm all edits persist.

To check literal prefixes, run `list`, then add both `gameadd 1 g/PUBG u/plain` and
`gameadd 1 g/PUBG n/Game u/special`.
Run `gameedit 1 g/"PUBG n/Game" u/updated`: only `PUBG n/Game` should change; the plain `PUBG` entry must
retain username `plain`. Rename the quoted target using `n/"PUBG m/Edition"` and edit it again using
`g/"PUBG m/Edition"`. An unclosed quoted selector must report an error without changing either game.

Run `list`, `archive 1`, then `archivelist`. Edit with `gameedit 1 g/Valorant u/archivePlayer`;
expect the edited friend to remain archived, the heading to stay `Archived friends`, and other fields/games
to remain unchanged. Run `find Tester` and edit the same game again: expect archived status and the
`Search results` scope to remain. Restart and use `archivelist` to confirm the saved edit and archive status.

Run `unarchive 1`, `list`, then `pin 1`. Edit with `gameedit 1 g/Valorant m/Role:Tank`;
expect the friend to keep its pinned badge and list position, and the details card to show `Role: Tank`.
Archive the pinned friend, run `archivelist` and edit its username again. Restart and use `archivelist`;
expect the edited game, pinned badge and archived status to persist.
