# Shogi Projekt Dokumentáció

**Szerző:** Domokos Erik Zsolt  
**Projekt:** NHF Shogi - Teljes körű shogi játék Java-ban

## Áttekintés

Ez a projekt egy teljes funkcionalitású shogi (japán sakk) játék implementációja Java nyelven, Swing GUI-val, JSON mentés/betöltés funkciókkal és AI ellenfél támogatással.

## Projekt Struktúra

```
NHF_Shogi/
├── src/
│   └── shogi/
│       ├── Main.java              # Belépési pont
│       ├── ShogiGUI.java          # Swing GUI (588 sor)
│       ├── Player.java            # Játékos osztály
│       ├── AIPlayer.java          # AI ellenfél
│       └── model/
│           ├── Board.java         # 9×9 tábla logika
│           ├── Position.java      # Pozíció (immutable)
│           ├── Piece.java         # Absztrakt bábu osztály
│           ├── King.java          # Király bábu
│           ├── Rook.java          # Bástya
│           ├── Bishop.java        # Futó
│           ├── GoldGeneral.java   # Arany tábornok
│           ├── SilverGeneral.java # Ezüst tábornok
│           ├── Knight.java        # Lovag
│           ├── Lance.java         # Lándzsa
│           ├── Pawn.java          # Gyalog
│           ├── ShogiGame.java     # Játéklogika (444 sor)
│           ├── SaveManager.java   # JSON mentés/betöltés
│           └── GameState.java     # Szerializációs POJO
├── test/
│   └── shogi/
│       └── model/
│           ├── PieceTest.java     # 17 bábu teszt
│           ├── BoardTest.java     # 14 tábla teszt
│           └── SaveManagerTest.java # 10 mentés teszt
├── lib/
│   ├── gson-2.10.1.jar           # JSON szerializáció
│   └── junit-platform-console-standalone-1.10.1.jar
└── bin/                           # Lefordított .class fájlok
```

## Funkciók

### Implementált Funkciók ✅

1. **Teljes Shogi Szabályrendszer**
   - 8 különböző bábu típus helyes mozgásokkal
   - Promóció (összes bábu kivéve King és Gold General)
   - Drop mechnika (leütött bábuk visszahelyezése)
   - Sakk és matt detekció

2. **Játékmódok**
   - Játékos vs Játékos (helyi 2 játékos)
   - Játékos vs AI (véletlenszerű, de legális lépések)

3. **Grafikus Felület (Swing)**
   - 9×9-es tábla megjelenítés
   - Kéz panelek (2 db) leütött bábuknak
   - Menu bar: Új játék, Mentés, Betöltés, Kilépés
   - Kijelölés vizualizáció
   - Legális lépések kiemelés

4. **Perzisztencia**
   - JSON alapú mentés/betöltés (Gson)
   - Teljes játékállapot mentése (tábla + kezek + aktuális játékos)

5. **Tesztelés**
   - 41 JUnit teszt (100% sikeres)
   - Bábu mozgások tesztelése (17 teszt)
   - Tábla műveletek (14 teszt)
   - Mentés/betöltés (10 teszt)

### Kizárt Funkciók ❌

- AI vs AI játékmód (specifikáció szerint nem kötelező)

## Technológiai Stack

| Komponens | Technológia | Verzió |
|-----------|-------------|--------|
| Nyelv | Java | 21 |
| GUI | Swing | Built-in |
| JSON | Gson | 2.10.1 |
| Tesztelés | JUnit 5 | 1.10.1 |
| IDE | VS Code | - |

## Kód Minőség

### Dokumentációs Standardok

Minden osztály és publikus metódus teljes JavaDoc dokumentációval rendelkezik:

- **@author tag**: Minden osztályban
- **Osztály szintű JavaDoc**: Részletes leírás, célkitűzés
- **Metódus JavaDoc**: @param, @return, @throws tagekkel
- **Mező kommentek**: Minden mezőre
- **Inline kommentek**: Komplex logikai blokkoknál

### Kód Stílus

- **Elnevezési konvenciók**: CamelCase (osztályok), camelCase (metódusok/változók)
- **Konzisztens formázás**: 4 szóköz indentálás
- **Értelmes változónevek**: `targetPiece` helyett `p`, `direction` helyett `d`
- **Magyarázó kommentek**: Shogi-specifikus szabályok dokumentálása

### SonarLint Megfelelés

A projekt minimális SonarLint figyelmeztetéseket generál:
- Complexity warnings: Elfogadható komplex metódusoknál (pl. AI)
- POJO serialization: Szándékosan publikus mezők (Gson követelmény)

## Használat

### Fordítás

```bash
javac -d bin -cp "lib/gson-2.10.1.jar" src/shogi/*.java src/shogi/model/*.java
```

### Futtatás

```bash
java -cp "bin;lib/gson-2.10.1.jar" shogi.Main
```

### Tesztek Futtatása

```bash
java -jar lib/junit-platform-console-standalone-1.10.1.jar --class-path "bin;lib/gson-2.10.1.jar" --scan-class-path
```

**Eredmény:** 41/41 teszt sikeres ✅

## Részletes Architektúra

### Model-View Szétválasztás

A projekt követi a **Model-View** architektúrát:

**Model Layer (`shogi.model` csomag):**
- `Board` - Tábla állapot kezelése (9×9 rács)
- `Position` - Immutable koordináta reprezentáció
- `Piece` - Absztrakt bábu osztály (8 konkrét leszármazott)
- `ShogiGame` - Játéklogika, szabályok, matt detekció
- `SaveManager` - Perzisztencia réteg (JSON)
- `GameState` - Szerializációs adatstruktúra

**View Layer (`shogi` csomag):**
- `ShogiGUI` - Swing-alapú grafikus felület
  - `BoardPanel` - Tábla rajzolás és interakció
  - `HandPanel` - Leütött bábuk megjelenítése
- `Main` - Alkalmazás belépési pont

**Controller Logic:**
- A `ShogiGUI` közvetlenül kommunikál a `ShogiGame`-mel
- Event handling: Mouse listeners a BoardPanel-en
- AI lépések: `AIPlayer.makeMove(game)` hívás

### Adatfolyam

```
User Input (kattintás)
    ↓
BoardPanel MouseListener
    ↓
ShogiGUI.handleBoardClick()
    ↓
ShogiGame.makeMove() / dropPiece()
    ↓
Board.movePiece() (állapot frissítés)
    ↓
ShogiGUI.repaint() (vizuális frissítés)
    ↓
AI lépés (ha AI mód)
    ↓
BoardPanel.paintComponent() (újrarajzolás)
```

### Osztály Diagramok (Főbb Kapcsolatok)

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

## Teljes Játékszabályok

### 1. Alapok

**Tábla:** 9 sor × 9 oszlop (81 mező)  
**Játékosok:** 2 fő (BLACK/fekete kezd, WHITE/fehér)  
**Cél:** Ellenfél királyának mattja

### 2. Bábuk Részletes Mozgásai

#### King (玉将 - Gyokushō)
- **Mozgás:** 1 mező bármely irányba (8 irány: ↑↓←→↖↗↙↘)
- **Promóció:** NEM lehet promótálni
- **Speciális:** Ha matt, a játék véget ér
- **Szimbólum:** K (fekete), k (fehér)

#### Rook (飛車 - Hisha)
- **Normál:** Végtelen mezőt egyenes vonalban (↑↓←→), akadályig
- **Promótált (Dragon King - 竜王):** Rook mozgás + 1 mező átlósan (↖↗↙↘)
- **Szimbólum:** R/r (normál), +R/+r (promótált)

#### Bishop (角行 - Kakugyō)
- **Normál:** Végtelen mezőt átlósan (↖↗↙↘), akadályig
- **Promótált (Dragon Horse - 竜馬):** Bishop mozgás + 1 mező egyenesen (↑↓←→)
- **Szimbólum:** B/b (normál), +B/+b (promótált)

#### Gold General (金将 - Kinshō)
- **Mozgás:** 1 mező 6 irányba (↑↓←→↖↗), de NEM átlósan hátra
- **Promóció:** NEM lehet promótálni
- **Szimbólum:** G/g

#### Silver General (銀将 - Ginshō)
- **Normál:** 1 mező 5 irányba (↑↖↗↙↘), de NEM oldalra vagy hátra
- **Promótált:** Gold General mozgása (6 irány)
- **Szimbólum:** S/s (normál), +S/+s (promótált)

#### Knight (桂馬 - Keima)
- **Normál:** L-alakban 2 előre + 1 oldalra (csak 2 célpont!)
  - BLACK: (row-2, col-1) és (row-2, col+1)
  - WHITE: (row+2, col-1) és (row+2, col+1)
- **Speciális:** Átugrik más bábukat (egyetlen bábu, ami tud)
- **Promótált:** Gold General mozgása
- **Szimbólum:** N/n (normál), +N/+n (promótált)

#### Lance (香車 - Kyōsha)
- **Normál:** Végtelen előre (csak 1 irány!), akadályig
- **Promótált:** Gold General mozgása
- **Szimbólum:** L/l (normál), +L/+l (promótált)

#### Pawn (歩兵 - Fuhyō)
- **Normál:** 1 mező előre (csak üres mezőre!)
  - **FONTOS:** Shogi gyalog NEM üt frontálisan (sakktól eltérően)
  - Csak előreléphet, nem üthet
- **Promótált (Tokin - と金):** Gold General mozgása
- **Szimbólum:** P/p (normál), +P/+p (promótált)

### 3. Promóció Szabályok

**Promóciós Zóna:**
- BLACK: 0-2. sorok (ellenfél területe)
- WHITE: 6-8. sorok (ellenfél területe)

**Mikor promótálhat:**
1. **Zónába lépéskor:** Bábu a promóciós zónába lép
2. **Zónán belül mozgáskor:** Bábu már a zónában van és ott mozog
3. **Zónából kilépéskor:** Bábu a zónából kilép (ritka)

**Kötelező promóció esetei:**
- Gyalog az utolsó soron (nem tud továbblépni)
- Lovag az utolsó 2 soron (nem tud legálisan lépni)
- Lándzsa az utolsó soron (nem tud továbblépni)

**Promóció visszafordítása:**
- Leütött bábu UNPROMOTE-olódik (alapformába tér vissza)
- DROP után mindig normál állapotban kerül vissza

### 4. Drop Mechnika

**Szabály:** Leütött bábu visszahelyezhető bármely üres mezőre

**Korlátozások:**
1. **Gyalog speciális szabályok:**
   - Nem helyezhető olyan oszlopba, ahol már van saját gyalog
   - Nem helyezhető az utolsó sorba (nem tudna lépni)
   - Nem helyezhető úgy, hogy azonnal mattot adjon
2. **Lovag:** Nem helyezhető az utolsó 2 sorba
3. **Lándzsa:** Nem helyezhető az utolsó sorba
4. **Promótált bábu:** Drop után alapformában van (unpromoted)

### 5. Sakk és Matt

**Sakk:** Király támadás alatt van
- Nincs kötelező bejelentés (mint shakkban)
- Játékos NEM léphet olyan lépést, ami saját királyát sakkba hozza

**Matt:** Király sakkban van ÉS nincs legális lépés
- Ellenfél győz
- Játék vége

**Patt:** Nincs a shogiban! Ha nincs legális lépés (de nem sakk), az is vereség.

### 6. Győzelem Feltételei

1. **Matt:** Ellenfél királyát matt pozícióba hozod
2. **Feladás:** Ellenfél feladja (GUI: "Mégse" a dialógusban)
3. **Illegális lépés:** Ellenfél érvénytelen lépést tesz (implementációnkban nem lehetséges)

## AI Implementáció Részletesen

### Algoritmus: Random Legal Move Selection

Az `AIPlayer` osztály egy egyszerű, de helyes AI-t implementál:

```java
public class AIPlayer extends Player {
    public void makeMove(ShogiGame game) {
        // 1. ÖSSZES LEHETSÉGES LÉPÉS GYŰJTÉSE
        List<Move> allMoves = getAllPossibleMoves(game);
        
        // 2. VÉLETLENSZERŰ VÁLASZTÁS
        Move chosen = allMoves.get(random.nextInt(allMoves.size()));
        
        // 3. VÉGREHAJTÁS
        if (chosen.isDrop) {
            game.dropPiece(chosen.pieceType, chosen.toRow, chosen.toCol);
        } else {
            game.makeMove(chosen.from, chosen.to, false); // false = no promotion
        }
    }
}
```

### Lépésgenerálás

**getAllPossibleMoves() logika:**

1. **Táblán lévő bábuk lépései:**
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

2. **Drop lehetőségek:**
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

3. **Sakk ellenőrzés minden lépésre:**
   - Minden potenciális lépés után szimuláljuk az állapotot
   - Ellenőrizzük, hogy saját király sakkban marad-e
   - Ha igen, töröljük a lépést

### AI Erősségek és Gyengeségek

**✅ Erősségek:**
- Mindig legális lépést választ
- Nem hagyja sakkban a saját királyát
- Gyors döntéshozatal (< 100ms)
- Determinisztikusan helyes

**❌ Gyengeségek:**
- Nincs stratégiai tervezés
- Nem értékeli a pozíciókat
- Nem látja előre az ellenfél lépéseit
- Egyformán kezeli a gyalogot és a bárót
- Nem ismeri fel a mattlehetőségeket

### Jövőbeli Fejlesztési Lehetőségek

1. **Minimax algoritmus:**
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

2. **Pozíció értékelés:**
   - Bábu értékek: Pawn=1, Lance=3, Knight=3, Silver=5, Gold=6, Bishop=7, Rook=8
   - Pozíciós bónuszok: Középső mezők értékesebbek
   - Királybiztonság értékelése
   - Kéz értéke: Fogott bábuk számítanak

3. **Alfa-béta vágás:** Minimax optimalizálás

4. **Nyitáskönyv:** Előre definiált jó nyitások

5. **Endgame táblák:** Matt pozíciók adatbázisa

## Használati Példák

### 1. Alapvető Játékmenet

**Új játék indítása:**
```bash
# Fordítás (ha még nem volt)
javac -d bin -cp "lib/gson-2.10.1.jar" src/shogi/*.java src/shogi/model/*.java

# Indítás
java -cp "bin;lib/gson-2.10.1.jar" shogi.Main
```

**Játékmód választás:**
1. Megjelenik egy dialógus: "Válassz játékmódot"
2. Válaszd ki:
   - **"Játékos vs Játékos"** - két emberi játékos
   - **"Játékos vs AI"** - te vagy a BLACK (fekete), kezdesz
3. Kattints "OK"

**Játék folyamata:**
1. **Bábu mozgatás:**
   - Kattints egy bábura → kijelölődik (zöld keret)
   - Legális lépések kék négyzetekkel jelölve
   - Kattints célpontra → bábu mozog

2. **Drop lépés (leütött bábu visszahelyezés):**
   - Kattints egy fogott bábura a jobb oldali panelen (kéz)
   - Bábu típusa kijelölődik
   - Kattints egy üres mezőre a táblán
   - Bábu megjelenik a táblán

3. **Promóció:**
   - Ha bábu promóciós zónába lép, dialógus jelenik meg
   - Válaszd "Promótál" vagy "Nem promótál"

4. **AI lépés:**
   - Játékos vs AI módban az AI automatikusan lép
   - Várj 1 másodpercet a vizualizációra

### 2. Mentés és Betöltés

**Játék mentése:**
```
Menu bar → Mentés → Válassz helyet és fájlnevet (pl. "myGame.json")
```

**Példa mentési fájl struktúra:**
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

**Játék betöltése:**
```
Menu bar → Betöltés → Válaszd ki a mentett JSON fájlt
```

### 3. Programozott Használat (API)

**Játék inicializálás:**
```java
// Új játék létrehozása
ShogiGame game = new ShogiGame();

// Játékos létrehozása
Player player1 = new Player("Játékos 1", Piece.Color.BLACK);
AIPlayer player2 = new AIPlayer("AI", Piece.Color.WHITE);
```

**Lépés végrehajtása:**
```java
// Bábu mozgatása
Position from = new Position(6, 4); // Gyalog pozíció
Position to = new Position(5, 4);   // Egy előre
boolean success = game.makeMove(from, to, false);

if (success) {
    System.out.println("Lépés sikeres!");
} else {
    System.out.println("Illegális lépés!");
}
```

**Drop lépés:**
```java
// Fogott bábu visszahelyezése
boolean dropped = game.dropPiece("Pawn", 4, 3);
```

**Játék állapot lekérdezése:**
```java
// Aktuális játékos
Piece.Color current = game.getCurrentPlayer();

// Matt ellenőrzés
if (game.isCheckmate(Piece.Color.BLACK)) {
    System.out.println("MATT! Fehér győzött!");
}

// Sakk ellenőrzés
if (game.isInCheck(Piece.Color.WHITE)) {
    System.out.println("Fehér király sakkban van!");
}
```

**Mentés/betöltés programból:**
```java
// Mentés
try {
    SaveManager.save(game, "saves/game1.json");
    System.out.println("Játék elmentve!");
} catch (IOException e) {
    System.err.println("Mentési hiba: " + e.getMessage());
}

// Betöltés
try {
    ShogiGame loadedGame = SaveManager.load("saves/game1.json");
    System.out.println("Játék betöltve!");
} catch (IOException e) {
    System.err.println("Betöltési hiba: " + e.getMessage());
}
```

### 4. Tesztek Futtatása

**Összes teszt:**
```bash
java -jar lib/junit-platform-console-standalone-1.10.1.jar \
  --class-path "bin;lib/gson-2.10.1.jar" \
  --scan-class-path
```

**Kimenet:**
```
Test run finished after 169 ms
[        41 tests found           ]
[        41 tests successful      ]
[         0 tests failed          ]
```

**Egyedi teszt osztály:**
```bash
java -jar lib/junit-platform-console-standalone-1.10.1.jar \
  --class-path "bin;lib/gson-2.10.1.jar" \
  --select-class shogi.model.PieceTest
```

### 5. Hibakeresés

**Debug mód engedélyezése:**
```java
// ShogiGame.java-ban (átmenetileg)
public boolean makeMove(Position from, Position to, boolean wantPromotion) {
    System.out.println("DEBUG: Moving from " + from + " to " + to);
    // ... további kód
}
```

**Tábla állapot kiírása:**
```java
// Egyszerű konzolos megjelenítés
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

## Fejlesztői Útmutató

### Fejlesztői Környezet Beállítása

**1. Szükséges eszközök:**
- Java JDK 21: https://adoptium.net/
- VS Code: https://code.visualstudio.com/
- Git: https://git-scm.com/

**2. VS Code extension-ök:**
- Extension Pack for Java (Microsoft)
- Debugger for Java (Microsoft)
- Test Runner for Java (Microsoft)
- SonarLint (SonarSource) - kódminőség

**3. Projekt klónozása:**
```bash
git clone https://github.com/erikzsoltdomokos/shogi_game.git
cd shogi_game
```

**4. VS Code beállítások:**

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

### Új Funkció Hozzáadása

**Példa: Új Bábu Típus**

1. **Hozz létre új osztályt:**
```java
// src/shogi/model/NewPiece.java
package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Új bábu leírása.
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
        // Implementáld a mozgást
        return moves;
    }
    
    @Override
    public String getSymbol() {
        return color == Color.BLACK ? "X" : "x";
    }
}
```

2. **Adj hozzá tesztet:**
```java
// test/shogi/model/PieceTest.java
@Test
@DisplayName("NewPiece mozgása helyes")
void testNewPieceMovement() {
    NewPiece piece = new NewPiece(Piece.Color.BLACK, new Position(4, 4));
    board.setPieceAt(4, 4, piece);
    
    List<Position> moves = piece.getLegalMoves(board);
    
    // Assert-ek
    assertTrue(moves.contains(new Position(3, 4)));
}
```

3. **Frissítsd a SaveManager-t:**
```java
// SaveManager.java convertToGameState() metódusban
if (piece instanceof NewPiece) {
    return new GameState.PieceData("NewPiece", ...);
}
```

### Kód Review Checklist

Mielőtt commit-olsz, ellenőrizd:

- [ ] Minden új metódusnak van JavaDoc-ja
- [ ] @author tag az új osztályokon
- [ ] Változónevek értelmesek (nem `p`, `d`, stb.)
- [ ] Tesztek lefutnak és sikeresek (41/41)
- [ ] Kód fordítható hiba nélkül
- [ ] SonarLint nem jelez kritikus hibát
- [ ] Nincs System.out.println a production kódban
- [ ] GUI változtatások tesztelve manuálisan

### Build és Release

**1. Verzió frissítés:**
```java
// Main.java vagy ShogiGUI.java
public static final String VERSION = "1.1.0";
```

**2. Fordítás:**
```bash
javac -d bin -cp "lib/gson-2.10.1.jar" src/shogi/*.java src/shogi/model/*.java
```

**3. JAR készítés (opcionális):**
```bash
# Manifest fájl
echo "Main-Class: shogi.Main" > manifest.txt
echo "Class-Path: lib/gson-2.10.1.jar" >> manifest.txt

# JAR build
jar cfm Shogi.jar manifest.txt -C bin .

# Futtatás
java -jar Shogi.jar
```

**4. Git commit:**
```bash
git add .
git commit -m "feat: Új funkció hozzáadása"
git push origin main
```

**Commit üzenet konvenciók:**
- `feat:` - Új funkció
- `fix:` - Hibajavítás
- `docs:` - Dokumentáció
- `refactor:` - Kód refaktorálás
- `test:` - Teszt hozzáadása

### Teljesítmény Optimalizálás

**1. Lépésgenerálás gyorsítása:**
```java
// Cache-eld a gyakran használt lépéseket
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

**2. GUI újrarajzolás optimalizálás:**
```java
// Csak a megváltozott területet rajzold újra
public void repaintCell(int row, int col) {
    int x = col * CELL_SIZE;
    int y = row * CELL_SIZE;
    repaint(x, y, CELL_SIZE, CELL_SIZE);
}
```

**3. AI gondolkodási idő korlátozás:**
```java
// Timeout az AI számára
ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Move> future = executor.submit(() -> aiPlayer.findBestMove(game));

try {
    Move move = future.get(5, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    future.cancel(true);
    // Fallback: random lépés
}
```

### Debugging Tippek

**1. Tábla állapot vizualizáció:**
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

**2. Breakpoint helyek:**
- `ShogiGame.makeMove()` - lépés végrehajtás
- `Piece.getLegalMoves()` - mozgásgenerálás
- `ShogiGUI.handleBoardClick()` - UI interakció
- `AIPlayer.makeMove()` - AI döntéshozatal

**3. Logging framework használata:**
```java
import java.util.logging.*;

private static final Logger LOGGER = Logger.getLogger(ShogiGame.class.getName());

LOGGER.info("Játék indítva");
LOGGER.warning("Illegális lépés kísérlet: " + from + " → " + to);
LOGGER.severe("Kritikus hiba a mentésnél: " + e.getMessage());
```

## AI Implementáció

Az AI jelenleg **véletlenszerű legális lépés** stratégiát követ:

1. Összes lehetséges lépés generálása (tábla + drop)
2. Legális lépések szűrése (nem marad sakk)
3. Véletlenszerű választás a listából

**Jövőbeli fejlesztési lehetőség:** Minimax algoritmus alfa-béta vágással.

## Telepítési Útmutató

### Előfeltételek

- Java JDK 21 vagy újabb
- VS Code (opcionális, de ajánlott)
- Git (verziókezeléshez)

### Lépések

1. **Klónozás:**
   ```bash
   git clone <repository-url>
   cd NHF_Shogi
   ```

2. **Függőségek ellenőrzése:**
   - `lib/gson-2.10.1.jar` ✅
   - `lib/junit-platform-console-standalone-1.10.1.jar` ✅

3. **Fordítás:**
   ```bash
   javac -d bin -cp "lib/gson-2.10.1.jar" src/shogi/*.java src/shogi/model/*.java
   ```

4. **Futtatás:**
   ```bash
   java -cp "bin;lib/gson-2.10.1.jar" shogi.Main
   ```

## Ismert Korlátozások

1. **AI Szint**: Véletlenszerű választás, nincs stratégiai értékelés
2. **Hálózati játék**: Nem támogatott (csak helyi játék)
3. **Időkorlát**: Nincs implementálva
4. **Lépéstörténet**: Nem tárolva (csak aktuális állapot)

## Licensz

Oktatási projekt - NHF (Nagy Házi Feladat)

## Kapcsolat

**Fejlesztő:** Domokos Erik Zsolt  
**E-mail:** erikzsolt.domokos@gmail.com  
**Projekt típus:** Egyetemi nagy házi feladat

---

**Utolsó frissítés:** 2025.11.19
**Verzió:** 1.0 - Production Ready ✅
