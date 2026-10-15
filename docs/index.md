---
layout: page
title: GameMates
---

[![CI Status](https://github.com/AY2627S1-CS2103T-W14-3/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2103T-W14-3/tp/actions)
[![codecov](https://codecov.io/gh/AY2627S1-CS2103T-W14-3/tp/graph/badge.svg?token=S4O2ICMM1L)](https://codecov.io/gh/AY2627S1-CS2103T-W14-3/tp)

## Keep your gaming friends in one place

GameMates is a desktop app for gamers who want to remember who they play with and the usernames their friends use
across games. Manage friends with typed commands and check their details in a visual panel alongside the friend list.
Your friends and game entries are saved locally for your next session.

![GameMates showing a friend and their games beside the friend list](images/Ui.png)

### What you can do

* Add friends with just a name, optional phone/email details and tags; edit or delete them later.
* Find friends by name or list friends who play a particular game.
* View a friend's games and in-game usernames, or show only one matching game with `gamelist`.
* Keep your stored profile separate from the friend list and back up your local data file.

For example, `list g/Valorant` lists Valorant players. Then `gamelist 1 g/Valorant` shows the first displayed
player's Valorant card without changing the filtered friend list.

The current build can view existing game entries. Own-profile editing, adding/deleting individual games and
metadata entry are pending integration; see the [User Guide's current limits](UserGuide.html#your-profile-and-game-entries).

### Get started

* [Download GameMates](https://github.com/AY2627S1-CS2103T-W14-3/tp/releases) and follow the
  [Quick start](UserGuide.html#quick-start). Java 25 or later is required.
* Use the [User Guide](UserGuide.html) for command formats, examples, validation and backup steps.
* Read the [Developer Guide](DeveloperGuide.html) for the design, implementation and manual test instructions.
* Meet the [team](AboutUs.html), browse the [source code](https://github.com/AY2627S1-CS2103T-W14-3/tp), or
  [report a problem](https://github.com/AY2627S1-CS2103T-W14-3/tp/issues).

### Acknowledgements

GameMates builds on AddressBook-Level3 from the [SE-EDU initiative](https://se-education.org/).
Libraries include [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson),
[Vavr](https://vavr.io/) and [JUnit 5](https://junit.org/junit5/).
