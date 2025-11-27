# COMP2042 Tetris Game Maintenance and Extension

## Github
https://github.com/chanjx1/CW2025

## Compilation Instructions
1. Ensure Java JDK (version 21 or later) and Maven are installed.
2. Clone the repository.
3. Run `mvn clean compile`
4. Start the game with `mvn javafx:run`

## Refactoring Summary (What Was Improved)
This project underwent extensive refactoring and cleanup to improve readability, maintainability, and program structure. All Refactoring work is fully functional and tested.
#### 1. Architectural Cleanup (MVC & SRP)
- **Decoupled View Logic:** The monolithic `GuiController` was significantly reduced by extracting distinct responsibilities into helper classes:
    - **`BoardRenderer`**: Handles all grid initialization, ghost piece projection, and block rendering.
    - **`BrickStyler`**: Manages color definitions and visual styling of shapes.
- **Decoupled Model:** Replaced the legacy `java.awt.Point` dependency with a custom, lightweight `GamePoint` record. This ensures the Model layer is pure Java and not tied to AWT UI libraries.
- **Package Organisation:** Split project into clear `model`, `view`, `controller`, and `model.bricks` packages.
- **Renaming:** Renamed confusing classes (`SimpleBoard` -> `TetrisBoard`, `MatrixOperations` -> `MatrixUtils`) to match their purpose.

#### 2. Supporting Single Responsibility
- **Scoring Logic:** Scoring calculations were extracted from `MatrixUtils` into a dedicated `ScoringRules` utility class.
- **Audio Management:** Created `SoundManager` to handle all audio resource loading and playback, removing this burden from the Controller.
- **Physics Encapsulation:** Dropping mechanics and line-clear detection were moved from the Controller directly into `TetrisBoard`, encapsulating the game state more effectively.
- **State Management:** Replaced boolean flags (`isPause`, `isGameOver`) with a `GameState` enum to make state transitions clearer and less error-prone.
- **Code Duplication:** Created `AbstractBrick` to remove duplicated code across all 7 Tetromino classes.

#### 3. Design Patterns Used
##### Facade Pattern
- **`SoundManager`**: Provides a simple interface for the game to trigger effects (e.g., `playLevelUp()`), hiding the complexity of JavaFX `AudioClip` resource management.
##### Strategy Pattern
- **`BrickGenerator`**: Defines a strategy interface for brick creation, with `RandomBrickGenerator` as the concrete implementation.
##### Template Method Pattern
- **`AbstractBrick`**: Defines the skeleton of a brick's behavior (rotation handling), while subclasses (`TBrick`, `ZBrick`) only define the specific data structures.
##### Observer Pattern
- **JavaFX Properties**: The `Score` model uses properties (`scoreProperty`, `levelProperty`) to allow the GUI to reactively update without the Model needing direct knowledge of the View.

#### 4. Meaningful JUnit Tests
Added tests under `src/test/java/com/comp2042/model/`:
- **ScoreTest.java** - verifies score increment and reset.
- **MatrixUtilsTest.java** - verifies row-clearing logic and quadratic bonus scoring.
- **TetrisBoardTest.java** - validates correct spawn position & absence of immediate collision.
  All tests pass using JUnit 5 and Maven Surefire Plugin.

#### 5. Bug Fixes
- **Critical Logic Fix**: Fixed a major bug in `MatrixUtils` where X and Y coordinates were transposed, which previously caused collision detection errors.
- **UI Glitch**: Resolved an issue where "Level Up" and "Score Bonus" notifications overlapped; they now stack dynamically.
- **Spawn Position**: Fixed bricks spawning from the middle of the board; they now spawn correctly from the hidden top rows.
- **Pause Behavior**: Fixed pause logic so the game actually freezes the timeline and resumes correctly.
- **Layout**: Fixed broken board centering in FXML and tidied styling via CSS.

## Implemented and Working Properly
1. **Complete MVC Refactoring**: Strict separation of concerns achieved by extracting rendering logic to `BoardRenderer` and audio logic to `SoundManager`.
2. **Dynamic Leveling System**: Implemented adaptive difficulty; game speed increases every 10 lines cleared.
3. **Audio System**: Integrated sound effects for Line Clears, Level Ups, and Game Over states.
4. **Advanced Gameplay Mechanics**:
    - **Ghost Piece**: Visual projection showing where the active block will land.
    - **Hard Drop**: Pressing `SPACE` instantly drops and locks the piece.
    - **Hold Mechanic**: Pressing `SHIFT` or `C` swaps the active piece with a held piece.
5. **Code Optimization**:
    - Removed duplication across all Tetromino classes using `AbstractBrick`.
    - Removed legacy `java.awt` dependencies by implementing a lightweight `GamePoint` record.
6. **UI & Layout**:
    - Centered game board with corrected `StackPane` overlays.
    - Live HUD showing Score, Level, and Next/Hold pieces.
    - Notification system for "Level Up" and bonuses that stack dynamically without overlapping.
7. **Input & State Management**:
    - Centralised input handling for Keyboard (Arrows/WASD) and Game State (Running/Paused/GameOver).
    - Pause and Resume functionality via the `P` key.
8. **Critical Bug Fixes**:
    - Fixed `MatrixUtils` logic where X/Y coordinates were transposed.
    - Fixed piece spawning coordinates to correctly use hidden rows.

## Implemented but Not Working Properly
* None. All intended refactoring features were tested and are functioning correctly.

## Features Not Implemented
- Bag-7 randomizer
- High score saving

## New Java Classes Introduced
| Class         | Purpose                                                               |
|---------------|-----------------------------------------------------------------------|
| SoundManager  | Facade for loading resources and playing sound effects (.mp3/.wav).   |
| BoardRenderer | Handles JavaFX grid generation and updates rectangle positions.       |
| BrickStyler   | "Contains visual design logic (colors, strokes) for blocks."          |
| GamePoint     | An immutable record replacing java.awt.Point for coordinate tracking. |
| ScoringRules  | Pure logic class for calculating points based on lines cleared.       |
| AbstractBrick | Parent class reducing code duplication in Tetromino definitions.      |

## Modified Java Classes
#### GuiController.java
- **Refactoring**: Delegated all grid rendering to `BoardRenderer` and block styling to `BrickStyler` to satisfy Single Responsibility Principle.
- **Key Handling**: Centralised keyboard input events (Arrow keys, WASD, Pause).
- **UI Logic**: Implemented `showScoreBonus` with relative positioning to prevent overlapping notifications.
- **Cleanup**: Improved overlay creation and focus management.
#### GameController.java
- **MVC Separation**: Moved all game logic out of the GUI; now handles game rules, scoring, and state management.
- **Audio Integration**: Integrated `SoundManager` to trigger effects for Game Over, Level Up, and Line Clears.
- **Logic**: Centralised scoring logic (including soft/hard drop points) and handles dynamic Level Up progression.
#### TetrisBoard.java
- **Renaming**: Renamed from `SimpleBoard` to `TetrisBoard`.
- **Decoupling**: Replaced `java.awt.Point` with `GamePoint` to remove AWT dependencies from the Model.
- **Physics**: Removed scoring side-effects to ensure the class functions purely as a physics/collision engine.
- **State**: Manages board constants, hidden rows, and spawn coordinates.
#### MatrixUtils.java
- **Renaming**: Renamed from `MatrixOperations`.
- **Bug Fix**: Fixed a critical logic error in `intersect` and `merge` methods where X and Y coordinates were transposed.
- **Utility**: Provides helper methods for deep-copying matrices and row-clearing checks.
#### Score.java
- **Extension**: Added `level` and `lines` properties to support the dynamic difficulty system.
- **Binding**: Uses JavaFX properties to allow the UI to observe changes automatically.
#### gameLayout.fxml
- **Layout**: Rebuilt using `StackPane` overlay structure to support background, board, and ghost pieces.
- **Centering**: Fixed alignment issues to ensure the board is centered.
- **Cleanup**: Removed unused imports and redundant nodes.
#### Brick Classes (TBrick, LBrick, etc.)
- **Refactoring**: All bricks now extend `AbstractBrick`.
- **Cleanup**: Removed code duplication; classes now contain only their specific rotation data.

## Unexpected Problems
- **Duplicate compiled classes caused runtime errors**: Resolved by using `mvn clean` to remove stale `.class` files.
- **Notification Overlap**: Simultaneous "Level Up" and "Line Clear" events caused text to overlap. Resolved by implementing a relative Y-axis offset calculation in `GuiController`.
- **Audio Lag**: Initial audio playback caused slight frame drops. Resolved by pre-loading `AudioClip` resources in the `SoundManager` constructor.