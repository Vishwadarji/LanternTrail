# Final project requirement mapping

Student: Vishwa Darji · Project: Lantern Trail · Course: ISYS 34603

Original, school-appropriate path puzzle. Win by finishing three maps; lose by exhausting three hearts. Score and explicit endings are implemented. This is not an identical copy of the in-class apps.

An A requires 8+ challenges plus working behavior and stability. The following 15 challenges are implemented in source; device verification is recorded separately in TESTING.md. Implementation alone does not guarantee a grade.

| Canvas # | Challenge | Demonstration |
| --- | --- | --- |
| 1 | Restart without closing | Play again, then start another journey |
| 2 | Rotation handling | onSaveInstanceState stores the full run and player; preferences preserve progress |
| 3 | Second screen with passed data | ResultsActivity receives player, score, ending, best, practice mode and completed maps |
| 4 | Array/database display responds to interaction | TrailGame.maps provides three maps; winning a map advances the board |
| 5 | User-entered text affects app | Player name changes journey text, results and the shared report |
| 6 | Easter egg or cheat | firefly name unlocks the clearly labeled route guide |
| 7 | App bar action | Native Toolbar Help action opens rules |
| 8 | Implicit intent | ACTION_SEND shares the actual result through a chooser |
| 12 | Custom View | TrailBoard overrides onDraw and onTouchEvent |
| 13 | Picture from shapes and colors | Tiles, rocks, lanterns, exit, marker and path use Canvas shapes |
| 14 | Animation | Repeating lantern glow |
| 15 | Second animation using AnimatorSet | Expanding ripple is sequenced with its fade |
| 16 | Touch-responsive animation | A board tap starts the ripple at that tile |
| 17 | App icon | Vector lantern icon in the manifest |
| 18 | Dynamic text | Score, hearts, map, collected count and instructions update |

Not claimed: #9 camera, #10 localization, #11 full TalkBack equivalence. The custom board is not fully playable with TalkBack.

## Canvas submission

Each student submits individually. Push the completed project to a repository and submit the exact branch URL only after all intended work is finished. Do not commit changes after submitting that link. Live stability and rotation must be checked before final submission.
