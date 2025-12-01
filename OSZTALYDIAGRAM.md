# Shogi Projekt - Osztálydiagram

## Öröklési Hierarchia

```
java.lang.Object
│
├── shogi.AIPlayer.Move
│
├── shogi.model.Board
│
├── shogi.model.GameState
│   └── (használja: SaveManager)
│
├── shogi.model.GameState.PieceData
│
├── shogi.Main
│
├── shogi.model.Piece (abstract)
│   ├── shogi.model.Bishop
│   ├── shogi.model.GoldGeneral
│   ├── shogi.model.King
│   ├── shogi.model.Knight
│   ├── shogi.model.Lance
│   ├── shogi.model.Pawn
│   ├── shogi.model.Rook
│   └── shogi.model.SilverGeneral
│
├── shogi.Player
│   └── shogi.AIPlayer (extends Player)
│
├── shogi.model.Position
│
├── shogi.model.SaveManager
│
├── shogi.model.ShogiGame
│   └── shogi.model.ShogiGame.ImpasseResult
│
└── shogi.ShogiGUI (extends JFrame)
    ├── shogi.ShogiGUI.BoardPanel (extends JPanel)
    └── shogi.ShogiGUI.HandPanel (extends JPanel)
```

## Enum Osztályok

```
java.lang.Enum<E>
│
├── shogi.model.Piece.Color
│   ├── BLACK
│   └── WHITE
│
└── shogi.ShogiGUI.GameMode
    ├── PLAYER_VS_PLAYER
    └── PLAYER_VS_AI
```

## Fő Kapcsolatok (Aggregáció/Kompozíció)

```
ShogiGUI
│
├── has-a → ShogiGame (game)
├── has-a → Player (player1, player2)
├── has-a → BoardPanel (boardPanel)
├── has-a → HandPanel (blackHandPanel, whiteHandPanel)
├── has-a → Position (selectedPosition)
└── has-a → List<Position> (validMoves)

ShogiGame
│
├── has-a → Board (board)
├── has-a → List<Piece> (blackHand, whiteHand)
└── manages → Piece.Color (currentPlayer)

Board
│
└── has-a → Piece[][] (grid, 9×9 mátrix)

Piece (abstract)
│
├── has-a → Color (color)
├── has-a → Position (position)
└── has-a → boolean (isPromoted)

SaveManager
│
└── creates/loads → GameState
```

## Metódus Kapcsolatok (Főbb Hívások)

```
Main.main()
    └─> SwingUtilities.invokeLater()
        └─> new ShogiGUI()
            ├─> selectGameMode()
            ├─> new ShogiGame()
            └─> setupGUI()

ShogiGUI.handleBoardClick()
    ├─> game.makeMove()
    │   ├─> board.getPieceAt()
    │   ├─> piece.getLegalMoves()
    │   ├─> board.movePiece()
    │   └─> game.isInCheck()
    │
    └─> aiPlayer.makeMove()
        ├─> game.getAllLegalMoves()
        └─> game.dropPiece()

ShogiGame.isGameOver()
    ├─> findKing()
    ├─> isInCheckmate()
    │   ├─> isInCheck()
    │   └─> getAllLegalMoves()
    └─> checkImpasse()

SaveManager.saveGame()
    └─> Gson.toJson()
        └─> File.write()

SaveManager.loadGame()
    └─> Gson.fromJson()
        └─> File.read()
```

## Package Struktúra

```
shogi/
├── Main.java                 (Entry point)
├── ShogiGUI.java             (Swing GUI, 833 sor)
│   ├── BoardPanel            (Inner class)
│   └── HandPanel             (Inner class)
├── Player.java               (Base player class)
├── AIPlayer.java             (AI implementation)
│
└── model/
    ├── Board.java            (9×9 tábla)
    ├── Position.java         (Koordináta)
    ├── Piece.java            (Abstract bábu)
    │   ├── King.java
    │   ├── Rook.java
    │   ├── Bishop.java
    │   ├── GoldGeneral.java
    │   ├── SilverGeneral.java
    │   ├── Knight.java
    │   ├── Lance.java
    │   └── Pawn.java
    ├── ShogiGame.java        (Játéklogika)
    ├── SaveManager.java      (JSON I/O)
    └── GameState.java        (Serialization POJO)
```

## Részletes UML-szerű Leírás

### Piece (Abstract Class)
```
┌─────────────────────────────┐
│        <<abstract>>         │
│           Piece             │
├─────────────────────────────┤
│ - color: Color              │
│ - position: Position        │
│ - isPromoted: boolean       │
├─────────────────────────────┤
│ + Piece(color, position)    │
│ + getColor(): Color         │
│ + getPosition(): Position   │
│ + isPromoted(): boolean     │
│ + promote(): void           │
│ + canPromote(board): boolean│
│ + getLegalMoves(board):     │
│   List<Position>            │
│ + getSymbol(): String       │
└─────────────────────────────┘
            △
            │
    ┌───────┴───────┐
    │               │
┌───┴───┐       ┌───┴────┐
│ King  │  ...  │  Pawn  │
└───────┘       └────────┘
```

### ShogiGame (Main Logic)
```
┌──────────────────────────────┐
│         ShogiGame            │
├──────────────────────────────┤
│ - board: Board               │
│ - blackHand: List<Piece>     │
│ - whiteHand: List<Piece>     │
│ - currentPlayer: Color       │
├──────────────────────────────┤
│ + ShogiGame()                │
│ + makeMove(from, to):boolean │
│ + dropPiece(piece, to):      │
│   boolean                    │
│ + isInCheck(color): boolean  │
│ + isInCheckmate(color):      │
│   boolean                    │
│ + isGameOver(): boolean      │
│ + checkImpasse():            │
│   ImpasseResult              │
│ + switchPlayer(): void       │
└──────────────────────────────┘
```

### Board (9×9 Grid)
```
┌──────────────────────────────┐
│           Board              │
├──────────────────────────────┤
│ - grid: Piece[][]  (9×9)     │
├──────────────────────────────┤
│ + Board()                    │
│ + getPieceAt(pos): Piece     │
│ + setPieceAt(pos, piece):    │
│   void                       │
│ + movePiece(from, to): Piece │
│ + removePiece(pos): Piece    │
│ + setupInitialPosition():    │
│   void                       │
└──────────────────────────────┘
```

---

## Megjegyzések

Ez a dokumentum a JavaDoc által generált **"overview-tree.html"** kiegészítése.

**JavaDoc helye:**
- Fájl: `docs/javadoc/overview-tree.html`
- Nézet: Class Hierarchy (szöveges lista)

**Ez a vizuális diagram:**
- Fájl: `OSZTALYDIAGRAM.md` (ez a fájl)
- Formátum: ASCII art + leírás

Mindkettő együtt teljesíti az osztálydiagram követelményt a házi feladathoz.
