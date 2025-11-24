# COMP2042 Tetris Game Maintenance and Extension

## Github
https://github.com/chanjx1/CW2025

## Compilation Instructions
1. Ensure Java JDK (version 21 or later) and Maven are installed.
2. Clone the repository.
3. Run `mvn clean compile`
4. Start the game with `mvn javafx:run`

## Refactoring Summary (What Was Improved)
This project underwent extensive refactoring and cleanup to improve readability, maintainability, and program structure.
All Refactoring work is fully functional and tested.
#### Meaningful Package Organisation
- Split project into clear model, view, controller, and model.bricks packages.
- Renamed confusing classes (`SimpleBoard` -> `TetrisBoard`, `MatrixOperations` -> `MatrixUtils`) to match purpose.
#### Basic Maintenance & Code Cleanup
- Removed unused imports and dead code.
- Extracted constants (`BOARD_HEIGHT`, `BOARD_WIDTH`, `HIDDEN_ROWS`, `SPAWN_X`, `SPAWN_Y`) to eliminate magic numbers.
- Fixed layout bugs in `FXML` and tidied styling via CSS.
- Cleaned old FXML and unused UI nodes.
#### Supporting Single Responsibility
- GUI no longer handles logic such as clear-row score or merging.
- `GameController` now contains gameplay logic.
- `GuiController` handles UI rendering, keyboard input, and animations.
- Created `AbstractBrick` to remove duplicated code from all brick classes.
- Replaced boolean flags (`isPause`, `isGameOver`) with a small `GameState` enum to make state transitions clearer and less error-prone.
#### Design Patterns Used
##### Strategy Pattern
- `BrickGenerator` and `RandomBrickGenerator` implement interchangeable generation strategies.
##### Template / Abstract Base Pattern
`AbstractBrick` provides shared behaviour; child classes (TBrick, ZBrick…) only define rotation data.
##### Observer Pattern (via JavaFX properties)
Score uses `scoreProperty()` to allow reactive GUI updates.
#### Meaningful JUnit Tests
Added tests under `src/test/java/com/comp2042/model/`:
- **ScoreTest.java** - verifies score increment and reset.
- **MatrixUtilsTest.java** - verifies row-clearing logic and quadratic bonus scoring.
- **TetrisBoardTest.java** - validates correct spawn position & absence of immediate collision.
All tests pass using JUnit 5 and Maven Surefire Plugin.
#### Bug Fixes
- Fixed broken board centering in FXML.
- Fixed incorrect spawning position (bricks dropping from middle).
- Fixed GUI handling of clear-row events.
- Cleaned event-handling duplication.
- Fixed pause behaviour so the game actually freezes/resumes the falling Timeline.
- Ensured score is displayed correctly via a HUD label bound to the Score property.


## Implemented and Working Properly
1. Complete refactoring to MVC
2. Centered and fixed game board layout
3. Removed duplication across all tetromino classes
4. Unified block styling and rendering 
5. Centralised input handling (cleaner key events)
6. Improved controller/view separation 
7. Fixed piece spawning, score update, and clear-row event flow
8. Pause and resume via the P key with correct state handling
9. Live score display in the top-right HUD bound to the model Score

## Implemented but Not Working Properly
* None

All intended refactoring features were tested and are functioning correctly.

## Features Not Implemented
The following optional extension features were not added (yet):
- Levels / increasing game speed
- Ghost piece
- Hard drop
- Bag-7 randomizer
- High score saving
- Power-ups or adaptive difficulty

## New Java Classes Introduced
#### AbstractBrick.java
- Stores rotation states
- Provides deep-copy safety
- Removes boilerplate from all bricks

## Modified Java Classes
#### GuiController.java
- Centralised key handling
- Extracted styling logic (`styleBlock`)
- Added `showScoreBonus` for better MVC separation
- Cleaned overlay creation & focus management
#### GameController.java
- Added `handleClearRow`
- Moved logic out of GUI (true MVC separation)
- Handles score, merging, and new brick creation
#### gameLayout.fxml
- Rebuilt using `StackPane` overlay structure
- Centered the board
- Removed unused imports & redundant nodes
#### TetrisBoard.java
- Renamed from `SimpleBoard`
- Added board constants & spawn constants
- Manages board state, movement, collision, and new bricks
#### MatrixUtils.java
- Renamed from `MatrixOperations`
- Helper for deep-copy and row-clearing logic
#### Brick Classes (TBrick, LBrick, etc.)
- Now extend `AbstractBrick`
- Contain only rotation data

## Unexpected Problems
#### Duplicate compiled classes caused runtime errors
Resolved by using `mvn clean` to remove stale `.class` files.