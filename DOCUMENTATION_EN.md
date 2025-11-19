# Shogi Project Documentation

**Author:** Domokos Erik Zsolt  
**Project:** NHF Shogi - Complete Shogi Game in Java

## Overview

This project is a full-featured implementation of Shogi (Japanese Chess) in Java, featuring a Swing GUI, JSON save/load functionality, and AI opponent support.

## Project Structure

```
NHF_Shogi/
├── src/
│   └── shogi/
│       ├── Main.java              # Entry point
│       ├── ShogiGUI.java          # Swing GUI (626 lines)
│       ├── Player.java            # Player class
│       ├── AIPlayer.java          # AI opponent
│       └── model/
│           ├── Board.java         # 9×9 board logic
│           ├── Position.java      # Position (immutable)
│           ├── Piece.java         # Abstract piece class
│           ├── King.java          # King piece
│           ├── Rook.java          # Rook piece
│           ├── Bishop.java        # Bishop piece
│           ├── GoldGeneral.java   # Gold General
│           ├── SilverGeneral.java # Silver General
│           ├── Knight.java        # Knight piece
│           ├── Lance.java         # Lance piece
│           ├── Pawn.java          # Pawn piece
│           ├── ShogiGame.java     # Game logic (480 lines)
│           ├── SaveManager.java   # JSON save/load
│           └── GameState.java     # Serialization POJO
├── test/
│   └── shogi/
│       └── model/
│           ├── PieceTest.java     # 17 piece tests
│           ├── BoardTest.java     # 14 board tests
│           └── SaveManagerTest.java # 10 save/load tests
├── lib/
│   ├── gson-2.10.1.jar           # JSON serialization
│   └── junit-platform-console-standalone-1.10.1.jar
└── bin/                           # Compiled .class files
```

## Features

### Implemented Features ✅

1. **Complete Shogi Rule Set**
   - 8 different piece types with correct movements
   - Promotion system (all pieces except King and Gold General)
   - Drop mechanic (placing captured pieces)
   - Check and checkmate detection

2. **Game Modes**
   - Player vs Player (local 2-player)
   - Player vs AI (random but legal moves)

3. **Graphical Interface (Swing)**
   - 9×9 board display
   - Hand panels (2) for captured pieces
   - Menu bar: New Game, Save, Load, Exit
   - Selection visualization
   - Legal move highlighting

4. **Persistence**
   - JSON-based save/load (Gson)
   - Complete game state saved (board + hands + current player)

5. **Testing**
   - 41 JUnit tests (100% passing)
   - Piece movement tests (17 tests)
   - Board operations (14 tests)
   - Save/load (10 tests)

### Excluded Features ❌

- AI vs AI game mode (not required by specification)

## Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 21 |
| GUI | Swing | Built-in |
| JSON | Gson | 2.10.1 |
| Testing | JUnit 5 | 1.10.1 |
| IDE | VS Code | - |

## Code Quality

### Documentation Standards

Every class and public method has complete JavaDoc documentation:

- **@author tag**: In every class
- **Class-level JavaDoc**: Detailed description, purpose
- **Method JavaDoc**: @param, @return, @throws tags
- **Field comments**: For every field
- **Inline comments**: For complex logic blocks

### Code Style

- **Naming conventions**: CamelCase (classes), camelCase (methods/variables)
- **Consistent formatting**: 4-space indentation
- **Meaningful variable names**: `targetPiece` instead of `p`, `direction` instead of `d`
- **Explanatory comments**: Shogi-specific rules documented

### SonarLint Compliance

The project generates minimal SonarLint warnings:
- Complexity warnings: Acceptable for complex methods (e.g., AI)
- POJO serialization: Intentionally public fields (Gson requirement)

## Detailed Architecture

### Model-View Separation

The project follows the **Model-View** architecture:

**Model Layer (`shogi.model` package):**
- `Board` - Board state management (9×9 grid)
- `Position` - Immutable coordinate representation
- `Piece` - Abstract piece class (8 concrete descendants)
- `ShogiGame` - Game logic, rules, checkmate detection
- `SaveManager` - Persistence layer (JSON)
- `GameState` - Serialization data structure

**View Layer (`shogi` package):**
- `ShogiGUI` - Swing-based graphical interface
  - `BoardPanel` - Board rendering and interaction
  - `HandPanel` - Captured pieces display
- `Main` - Application entry point

**Controller Logic:**
- `ShogiGUI` communicates directly with `ShogiGame`
- Event handling: Mouse listeners on BoardPanel
- AI moves: `AIPlayer.makeMove(game)` calls

### Data Flow

```
User Input (click)
    ↓
BoardPanel MouseListener
    ↓
ShogiGUI.handleBoardClick()
    ↓
ShogiGame.makeMove() / dropPiece()
    ↓
Board.movePiece() (state update)
    ↓
ShogiGUI.repaint() (visual refresh)
    ↓
AI move (if AI mode)
    ↓
BoardPanel.paintComponent() (redraw)
```

### Class Diagrams (Main Relationships)

```
ShogiGame
    ├─ has-a → Board
    ├─ has-many → Piece (blackHand, whiteHand)
    └─ manages → currentPlayer (Color)

Board
    └─ contains → Piece[][] (9×9 grid)

Piece (abstract)
    ├─ King
    ├─ Rook
    ├─ Bishop
    ├─ GoldGeneral
    ├─ SilverGeneral
    ├─ Knight
    ├─ Lance
    └─ Pawn

ShogiGUI
    ├─ has-a → ShogiGame
    ├─ has-a → Player (player2)
    ├─ contains → BoardPanel
    └─ contains → HandPanel (×2)

AIPlayer extends Player
    └─ uses → ShogiGame.getAllPossibleMoves()
```

## Complete Game Rules

### 1. Basics

**Board:** 9 rows × 9 columns (81 squares)  
**Players:** 2 (BLACK starts, WHITE responds)  
**Goal:** Checkmate opponent's King

### 2. Detailed Piece Movements

#### King (玉将 - Gyokushō)
- **Movement:** 1 square in any direction (8 directions: ↑↓←→↖↗↙↘)
- **Promotion:** CANNOT promote
- **Special:** If checkmated, game ends
- **Symbol:** K (black), k (white)

#### Rook (飛車 - Hisha)
- **Normal:** Unlimited squares in straight lines (↑↓←→), until obstacle
- **Promoted (Dragon King - 竜王):** Rook movement + 1 square diagonally (↖↗↙↘)
- **Symbol:** R/r (normal), +R/+r (promoted)

#### Bishop (角行 - Kakugyō)
- **Normal:** Unlimited squares diagonally (↖↗↙↘), until obstacle
- **Promoted (Dragon Horse - 竜馬):** Bishop movement + 1 square straight (↑↓←→)
- **Symbol:** B/b (normal), +B/+b (promoted)

#### Gold General (金将 - Kinshō)
- **Movement:** 1 square in 6 directions (↑↓←→↖↗), but NOT diagonally backward
- **Promotion:** CANNOT promote
- **Symbol:** G/g

#### Silver General (銀将 - Ginshō)
- **Normal:** 1 square in 5 directions (↑↖↗↙↘), but NOT sideways or backward
- **Promoted:** Gold General movement (6 directions)
- **Symbol:** S/s (normal), +S/+s (promoted)

#### Knight (桂馬 - Keima)
- **Normal:** L-shape: 2 forward + 1 sideways (only 2 targets!)
  - BLACK: (row-2, col-1) and (row-2, col+1)
  - WHITE: (row+2, col-1) and (row+2, col+1)
- **Special:** Jumps over other pieces (only piece that can)
- **Promoted:** Gold General movement
- **Symbol:** N/n (normal), +N/+n (promoted)

#### Lance (香車 - Kyōsha)
- **Normal:** Unlimited forward (only 1 direction!), until obstacle
- **Promoted:** Gold General movement
- **Symbol:** L/l (normal), +L/+l (promoted)

#### Pawn (歩兵 - Fuhyō)
- **Normal:** 1 square forward (only empty squares!)
  - **IMPORTANT:** Shogi pawn does NOT capture forward (unlike chess)
  - Can only move forward, cannot capture
- **Promoted (Tokin - と金):** Gold General movement
- **Symbol:** P/p (normal), +P/+p (promoted)

### 3. Promotion Rules

**Promotion Zone:**
- BLACK: rows 0-2 (enemy territory)
- WHITE: rows 6-8 (enemy territory)

**When can promote:**
1. **Entering zone:** Piece moves into promotion zone
2. **Moving within zone:** Piece already in zone and moves within it
3. **Leaving zone:** Piece leaves zone (rare)

**Mandatory promotion cases:**
- Pawn on last rank (cannot move further)
- Knight on last 2 ranks (cannot legally move)
- Lance on last rank (cannot move further)

**Promotion reversal:**
- Captured pieces UNPROMOTE (revert to basic form)
- After DROP, always in normal state

### 4. Drop Mechanic

**Rule:** Captured piece can be placed on any empty square

**Restrictions:**
1. **Pawn special rules:**
   - Cannot be dropped on a file with own pawn already
   - Cannot be dropped on last rank (cannot move)
   - Cannot be dropped to give immediate checkmate
2. **Knight:** Cannot be dropped on last 2 ranks
3. **Lance:** Cannot be dropped on last rank
4. **Promoted piece:** After drop, in basic form (unpromoted)

### 5. Check and Checkmate

**Check:** King is under attack
- No mandatory announcement (unlike chess)
- Player CANNOT make move that leaves own King in check

**Checkmate:** King is in check AND no legal moves
- Opponent wins
- Game over

**Stalemate:** Does NOT exist in Shogi! If no legal moves (but not in check), that's also a loss.

### 6. Victory Conditions

1. **Checkmate:** Checkmate opponent's King
2. **Resignation:** Opponent resigns (GUI: "Cancel" in dialog)
3. **Illegal move:** Opponent makes invalid move (impossible in our implementation)

## AI Implementation Details

### Algorithm: Random Legal Move Selection

The `AIPlayer` class implements a simple but correct AI:

```java
public class AIPlayer extends Player {
    public void makeMove(ShogiGame game) {
        // 1. COLLECT ALL POSSIBLE MOVES
        List<Move> allMoves = getAllPossibleMoves(game);
        
        // 2. RANDOM SELECTION
        Move chosen = allMoves.get(random.nextInt(allMoves.size()));
        
        // 3. EXECUTION
        if (chosen.isDrop) {
            game.dropPiece(chosen.pieceType, chosen.toRow, chosen.toCol);
        } else {
            game.makeMove(chosen.from, chosen.to, false); // false = no promotion
        }
    }
}
```

### Move Generation

**getAllPossibleMoves() logic:**

1. **Moves for pieces on board:**
   ```java
   for (Piece piece : myPieces) {
       List<Position> legalMoves = piece.getLegalMoves(board);
       for (Position target : legalMoves) {
           if (!wouldBeInCheckAfter(piece.position, target)) {
               allMoves.add(new Move(piece.position, target));
           }
       }
   }
   ```

2. **Drop possibilities:**
   ```java
   for (Piece capturedPiece : myHand) {
       for (int row = 0; row < 9; row++) {
           for (int col = 0; col < 9; col++) {
               if (isValidDropPosition(capturedPiece, row, col)) {
                   allMoves.add(new DropMove(capturedPiece.type, row, col));
               }
           }
       }
   }
   ```

3. **Check validation for every move:**
   - Simulate state after each potential move
   - Check if own King remains in check
   - If yes, remove the move

### AI Strengths and Weaknesses

**✅ Strengths:**
- Always chooses legal moves
- Never leaves own King in check
- Fast decision-making (< 100ms)
- Deterministically correct

**❌ Weaknesses:**
- No strategic planning
- Does not evaluate positions
- Does not look ahead at opponent moves
- Treats pawn and rook equally
- Does not recognize checkmate opportunities

### Future Development Possibilities

1. **Minimax algorithm:**
   ```
   function minimax(depth, isMaximizing):
       if depth == 0 or gameOver:
           return evaluatePosition()
       
       if isMaximizing:
           maxEval = -∞
           for move in allMoves:
               eval = minimax(depth-1, false)
               maxEval = max(maxEval, eval)
           return maxEval
       else:
           minEval = +∞
           for move in allMoves:
               eval = minimax(depth-1, true)
               minEval = min(minEval, eval)
           return minEval
   ```

2. **Position evaluation:**
   - Piece values: Pawn=1, Lance=3, Knight=3, Silver=5, Gold=6, Bishop=7, Rook=8
   - Positional bonuses: Center squares more valuable
   - King safety evaluation
   - Hand value: Captured pieces count

3. **Alpha-beta pruning:** Minimax optimization

4. **Opening book:** Pre-defined good openings

5. **Endgame tables:** Checkmate position database

## Usage Examples

### 1. Basic Gameplay

**Starting a new game:**
```bash
# Compile (if not already done)
javac -d bin -cp "lib/gson-2.10.1.jar" src/shogi/*.java src/shogi/model/*.java

# Run
java -cp "bin;lib/gson-2.10.1.jar" shogi.Main
```

**Game mode selection:**
1. A dialog appears: "Choose game mode"
2. Select:
   - **"Player vs Player"** - two human players
   - **"Player vs AI"** - you are BLACK, you start
3. Click "OK"

**Gameplay:**
1. **Moving pieces:**
   - Click a piece → it gets selected (green border)
   - Legal moves marked with blue squares
   - Click target → piece moves

2. **Drop move (placing captured piece):**
   - Click a captured piece in right panel (hand)
   - Piece type gets selected
   - Click an empty square on board
   - Piece appears on board

3. **Promotion:**
   - If piece enters promotion zone, dialog appears
   - Choose "Promote" or "Don't Promote"

4. **AI move:**
   - In Player vs AI mode, AI moves automatically
   - Wait 1 second for visualization

### 2. Save and Load

**Saving game:**
```
Menu bar → Save → Choose location and filename (e.g., "myGame.json")
```

**Example save file structure:**
```json
{
  "currentPlayer": "WHITE",
  "board": [
    [null, null, {"type": "King", "color": "WHITE", "row": 0, "col": 4, "promoted": false}, ...],
    ...
  ],
  "blackHand": [
    {"type": "Pawn", "color": "BLACK", "row": -1, "col": -1, "promoted": false}
  ],
  "whiteHand": []
}
```

**Loading game:**
```
Menu bar → Load → Select saved JSON file
```

### 3. Programmatic Usage (API)

**Game initialization:**
```java
// Create new game
ShogiGame game = new ShogiGame();

// Create players
Player player1 = new Player("Player 1", Piece.Color.BLACK);
AIPlayer player2 = new AIPlayer("AI", Piece.Color.WHITE);
```

**Making a move:**
```java
// Move piece
Position from = new Position(6, 4); // Pawn position
Position to = new Position(5, 4);   // One forward
boolean success = game.makeMove(from, to, false);

if (success) {
    System.out.println("Move successful!");
} else {
    System.out.println("Illegal move!");
}
```

**Drop move:**
```java
// Place captured piece
boolean dropped = game.dropPiece("Pawn", 4, 3);
```

**Querying game state:**
```java
// Current player
Piece.Color current = game.getCurrentPlayer();

// Checkmate check
if (game.isCheckmate(Piece.Color.BLACK)) {
    System.out.println("CHECKMATE! White wins!");
}

// Check detection
if (game.isInCheck(Piece.Color.WHITE)) {
    System.out.println("White King is in check!");
}
```

**Save/load from code:**
```java
// Save
try {
    SaveManager.save(game, "saves/game1.json");
    System.out.println("Game saved!");
} catch (IOException e) {
    System.err.println("Save error: " + e.getMessage());
}

// Load
try {
    ShogiGame loadedGame = SaveManager.load("saves/game1.json");
    System.out.println("Game loaded!");
} catch (IOException e) {
    System.err.println("Load error: " + e.getMessage());
}
```

### 4. Running Tests

**All tests:**
```bash
java -jar lib/junit-platform-console-standalone-1.10.1.jar \
  --class-path "bin;lib/gson-2.10.1.jar" \
  --scan-class-path
```

**Output:**
```
Test run finished after 169 ms
[        41 tests found           ]
[        41 tests successful      ]
[         0 tests failed          ]
```

**Individual test class:**
```bash
java -jar lib/junit-platform-console-standalone-1.10.1.jar \
  --class-path "bin;lib/gson-2.10.1.jar" \
  --select-class shogi.model.PieceTest
```

### 5. Debugging

**Enable debug mode:**
```java
// In ShogiGame.java (temporarily)
public boolean makeMove(Position from, Position to, boolean wantPromotion) {
    System.out.println("DEBUG: Moving from " + from + " to " + to);
    // ... rest of code
}
```

**Print board state:**
```java
// Simple console display
public void printBoard() {
    Board board = game.getBoard();
    for (int row = 0; row < 9; row++) {
        for (int col = 0; col < 9; col++) {
            Piece p = board.getPieceAt(row, col);
            System.out.print(p == null ? "." : p.getSymbol());
            System.out.print(" ");
        }
        System.out.println();
    }
}
```

## Developer Guide

### Development Environment Setup

**1. Required tools:**
- Java JDK 21: https://adoptium.net/
- VS Code: https://code.visualstudio.com/
- Git: https://git-scm.com/

**2. VS Code extensions:**
- Extension Pack for Java (Microsoft)
- Debugger for Java (Microsoft)
- Test Runner for Java (Microsoft)
- SonarLint (SonarSource) - code quality

**3. Clone project:**
```bash
git clone https://github.com/erikzsoltdomokos/shogi_game.git
cd shogi_game
```

**4. VS Code settings:**

`.vscode/settings.json`:
```json
{
    "java.project.sourcePaths": ["src", "test"],
    "java.project.outputPath": "bin",
    "java.project.referencedLibraries": [
        "lib/**/*.jar"
    ]
}
```

`.vscode/launch.json`:
```json
{
    "configurations": [
        {
            "type": "java",
            "name": "Launch Shogi",
            "request": "launch",
            "mainClass": "shogi.Main",
            "projectName": "NHF_Shogi",
            "classPaths": ["bin", "lib/gson-2.10.1.jar"]
        }
    ]
}
```

### Adding New Features

**Example: New Piece Type**

1. **Create new class:**
```java
// src/shogi/model/NewPiece.java
package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * New piece description.
 * 
 * @author Your Name
 */
public class NewPiece extends Piece {
    
    public NewPiece(Color color, Position pos) {
        super(color, pos);
    }
    
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        // Implement movement
        return moves;
    }
    
    @Override
    public String getSymbol() {
        return color == Color.BLACK ? "X" : "x";
    }
}
```

2. **Add tests:**
```java
// test/shogi/model/PieceTest.java
@Test
@DisplayName("NewPiece movement is correct")
void testNewPieceMovement() {
    NewPiece piece = new NewPiece(Piece.Color.BLACK, new Position(4, 4));
    board.setPieceAt(4, 4, piece);
    
    List<Position> moves = piece.getLegalMoves(board);
    
    // Assertions
    assertTrue(moves.contains(new Position(3, 4)));
}
```

3. **Update SaveManager:**
```java
// SaveManager.java in convertToGameState() method
if (piece instanceof NewPiece) {
    return new GameState.PieceData("NewPiece", ...);
}
```

### Code Review Checklist

Before committing, verify:

- [ ] All new methods have JavaDoc
- [ ] @author tag on new classes
- [ ] Variable names are meaningful (not `p`, `d`, etc.)
- [ ] Tests run and pass (41/41)
- [ ] Code compiles without errors
- [ ] SonarLint shows no critical issues
- [ ] No System.out.println in production code
- [ ] GUI changes tested manually

### Build and Release

**1. Version update:**
```java
// Main.java or ShogiGUI.java
public static final String VERSION = "1.1.0";
```

**2. Compile:**
```bash
javac -d bin -cp "lib/gson-2.10.1.jar" src/shogi/*.java src/shogi/model/*.java
```

**3. Create JAR (optional):**
```bash
# Manifest file
echo "Main-Class: shogi.Main" > manifest.txt
echo "Class-Path: lib/gson-2.10.1.jar" >> manifest.txt

# JAR build
jar cfm Shogi.jar manifest.txt -C bin .

# Run
java -jar Shogi.jar
```

**4. Git commit:**
```bash
git add .
git commit -m "feat: Add new feature"
git push origin main
```

**Commit message conventions:**
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation
- `refactor:` - Code refactoring
- `test:` - Add tests

### Performance Optimization

**1. Speed up move generation:**
```java
// Cache frequently used moves
private Map<Position, List<Position>> moveCache = new HashMap<>();

public List<Position> getLegalMoves(Board board) {
    if (moveCache.containsKey(position)) {
        return moveCache.get(position);
    }
    List<Position> moves = calculateMoves(board);
    moveCache.put(position, moves);
    return moves;
}
```

**2. GUI redraw optimization:**
```java
// Only repaint changed area
public void repaintCell(int row, int col) {
    int x = col * CELL_SIZE;
    int y = row * CELL_SIZE;
    repaint(x, y, CELL_SIZE, CELL_SIZE);
}
```

**3. AI thinking time limit:**
```java
// Timeout for AI
ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Move> future = executor.submit(() -> aiPlayer.findBestMove(game));

try {
    Move move = future.get(5, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    future.cancel(true);
    // Fallback: random move
}
```

### Debugging Tips

**1. Board state visualization:**
```java
public void debugPrintBoard() {
    System.out.println("  0 1 2 3 4 5 6 7 8");
    for (int r = 0; r < 9; r++) {
        System.out.print(r + " ");
        for (int c = 0; c < 9; c++) {
            Piece p = board.getPieceAt(r, c);
            System.out.print(p == null ? "." : p.getSymbol());
            System.out.print(" ");
        }
        System.out.println();
    }
}
```

**2. Breakpoint locations:**
- `ShogiGame.makeMove()` - move execution
- `Piece.getLegalMoves()` - move generation
- `ShogiGUI.handleBoardClick()` - UI interaction
- `AIPlayer.makeMove()` - AI decision-making

**3. Using logging framework:**
```java
import java.util.logging.*;

private static final Logger LOGGER = Logger.getLogger(ShogiGame.class.getName());

LOGGER.info("Game started");
LOGGER.warning("Illegal move attempt: " + from + " → " + to);
LOGGER.severe("Critical error during save: " + e.getMessage());
```

## Installation Guide

### Prerequisites

- Java JDK 21 or newer
- VS Code (optional, but recommended)
- Git (for version control)

### Steps

1. **Clone:**
   ```bash
   git clone https://github.com/erikzsoltdomokos/shogi_game.git
   cd shogi_game
   ```

2. **Verify dependencies:**
   - `lib/gson-2.10.1.jar` ✅
   - `lib/junit-platform-console-standalone-1.10.1.jar` ✅

3. **Compile:**
   ```bash
   javac -d bin -cp "lib/gson-2.10.1.jar" src/shogi/*.java src/shogi/model/*.java
   ```

4. **Run:**
   ```bash
   java -cp "bin;lib/gson-2.10.1.jar" shogi.Main
   ```

## Known Limitations

1. **AI Level**: Random selection, no strategic evaluation
2. **Network play**: Not supported (local play only)
3. **Time limit**: Not implemented
4. **Move history**: Not stored (current state only)

## Project History

1. **SaveManager + JSON** - Persistence implementation
2. **JUnit Test Suite** - 41 test coverage
3. **Debug Cleanup** - Removed all System.out
4. **Player/AIPlayer** - Player model and AI
5. **Full Swing GUI** - 626-line complete GUI
6. **Main Separation** - Clean architecture
7. **Code Documentation** - Complete JavaDoc, @author tags, code standardization ✅

## License

Educational project - NHF (Large Homework Assignment)

## Contact

**Developer:** Domokos Erik Zsolt  
**Email:** erikzsolt.domokos@gmail.com  
**Project type:** University large homework assignment

---

**Last updated:** November 19, 2025  
**Version:** 1.0 - Production Ready ✅
