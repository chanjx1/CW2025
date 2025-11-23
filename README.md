# COMP2042 Tetris Game Maintenance and Extension

## Github
https://github.com/chanxj1/CW2025

## Compilation Instructions
1. Ensure Java JDK (version 21 or later) and Maven are installed.
2. Clone the repository.
3. Run `mvn clean compile` to clear any cached files and compile successfully.
4. Start the game with `mvn javafx:run`

## Implemented and Working Properly
1. Complete Package Restructuring (Model-View-Controller)
2. Centered and Fixed Game Layout (FXML improvements)
3. Removed Massive Duplication in Brick Classes
4. Cleaned up GUI Code (Styling & Focus Handling)
5. Centralised Key Handling
6. Controller-View Separation Improved (Clear-row logic moved)

## Implemented but Not Working Properly
* None
All intended refactoring features were tested and are functioning correctly

## Features Not Implemented
The following optional extension features were not added (yet):
- Levels / increasing game speed
- Ghost piece
- Hard drop
- Bag-7 randomizer
- High score saving
- Power-ups or adaptive difficulty

## New Java Classes
#### AbstractBrick.java
- Base class for all tetromino bricks
- Stores rotations
- Provides defensive deep-copy
- Removes boilerplate from all bricks

## Modified Java Classes
#### GuiController.java
- Added `handleKeyPressed` method for centralised key input
- Added `showScoreBonus` to separate visuals from logic
- Removed clear-row logic from `moveDown`
- Added unified block styling via `styleBlock`
- Updated overlay initialisation
- Simplified FXML integrations
#### GameController.java
- Added `handleClearRow` to move game logic out of the GUI
- Updated `onDownEvent` to return `DownData` after processing
- Improved MVC separation
- Maintains score, merging, and piece creation
#### gameLayout.fxml
- Redesigned layout using `StackPane`
- Ensures the game board is centered
- Introduced overlay panes for clean rendering
- Removed unused imports and unnecessary nodes
#### TetrisBoard.java (formerly SimpleBoard)
- Renamed for clarity
- Updated references to match model package
- Continues to handle all board operations cleanly
#### MatrixUtils.java (formerly MatrixOperations)
- Renamed for clearer intent
- Central place for deep-copy operations
#### All Brick Classes (IBrick, TBrick, etc.)
- Now extend `AbstractBrick`
- Contain only rotation data
- Significant reduction in duplicate code

## Unexpected Problems
#### Duplicate compiled classes caused runtime errors
Resolved by using `mvn clean` to remove stale `.class` files.