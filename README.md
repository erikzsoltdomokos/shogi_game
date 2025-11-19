# Shogi - Japanese Chess Game

A full-featured Shogi (Japanese Chess) implementation in Java with Swing GUI, JSON save/load functionality, and AI opponent.

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![License](https://img.shields.io/badge/license-Educational-blue.svg)
![Tests](https://img.shields.io/badge/tests-41%2F41-brightgreen.svg)

## Features

- ✅ Complete Shogi rule implementation (9×9 board, 8 piece types)
- ✅ Piece promotion system
- ✅ Drop mechanic (captured pieces)
- ✅ Check and checkmate detection
- ✅ Swing GUI with visual feedback
- ✅ Player vs Player mode
- ✅ Player vs AI mode (random legal moves)
- ✅ Save/Load game state (JSON with Gson)
- ✅ 41 JUnit tests (100% passing)
- ✅ Full JavaDoc documentation

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
├── test/                          # JUnit 5 tests
├── lib/                           # Dependencies
└── DOCUMENTATION.md               # Full documentation
```

## Code Statistics

- **Source code:** 2,690 lines (1,489 effective code)
- **Test code:** 795 lines (531 effective code)
- **Documentation:** ~42% of codebase (JavaDoc + comments)
- **Total:** 3,485 lines

## Quick Start

### Prerequisites

- Java JDK 21 or higher
- Git (for cloning)

### Compile

```bash
javac -d bin -cp "lib/gson-2.10.1.jar" src/shogi/*.java src/shogi/model/*.java
```

### Run

```bash
java -cp "bin;lib/gson-2.10.1.jar" shogi.Main
```

### Run Tests

```bash
java -jar lib/junit-platform-console-standalone-1.10.1.jar \
  --class-path "bin;lib/gson-2.10.1.jar" --scan-class-path
```

**Result:** 41/41 tests passing ✅

## Game Rules (Brief)

### Pieces and Movement

| Piece | Normal Movement | Promoted Form |
|-------|----------------|---------------|
| **King (K)** | 1 square in 8 directions | Cannot promote |
| **Rook (R)** | Unlimited straight (4 directions) | +4 diagonal (1 square) |
| **Bishop (B)** | Unlimited diagonal (4 directions) | +4 straight (1 square) |
| **Gold General (G)** | 6 directions (1 square) | Cannot promote |
| **Silver General (S)** | 5 directions (1 square) | → Gold movement |
| **Knight (N)** | L-shape (forward only) | → Gold movement |
| **Lance (L)** | Unlimited forward | → Gold movement |
| **Pawn (P)** | 1 forward | → Gold movement (Tokin) |

### Special Rules

1. **Promotion:** Pieces can promote when entering/moving within enemy territory (last 3 rows)
2. **Drop:** Captured pieces can be placed back on any empty square
3. **Victory:** Checkmate the opponent's King

## Technologies

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 21 |
| GUI | Swing | Built-in |
| JSON | Gson | 2.10.1 |
| Testing | JUnit 5 | 1.10.1 |

## Documentation

See [DOCUMENTATION.md](DOCUMENTATION.md) for:
- Detailed architecture
- Complete game rules
- AI implementation details
- Usage examples
- Development guide

## License

Educational project - University assignment (NHF - Nagy Házi Feladat)

## Author

**Domokos Erik Zsolt**

---

**Status:** ✅ Production Ready  
**Last Updated:** November 2025  
**Version:** 1.0
