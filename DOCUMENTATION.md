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

## Játékszabályok Röviden

### Bábuk és Mozgásaik

| Bábu | Normál Mozgás | Promótált Forma |
|------|---------------|-----------------|
| **King (K)** | 1 mező 8 irányba | Nem promótálható |
| **Rook (R)** | Végtelen egyenes (4 irány) | +4 átlós (1 mező) |
| **Bishop (B)** | Végtelen átlós (4 irány) | +4 egyenes (1 mező) |
| **Gold General (G)** | 6 irány (1 mező) | Nem promótálható |
| **Silver General (S)** | 5 irány (1 mező) | → Gold mozgás |
| **Knight (N)** | L-alak (csak előre) | → Gold mozgás |
| **Lance (L)** | Végtelen előre | → Gold mozgás |
| **Pawn (P)** | 1 előre | → Gold mozgás (Tokin) |

### Speciális Szabályok

1. **Promóció**: Bábu a promóciós zónába (ellenfél területe) lépve/belépve promótálhat
2. **Drop**: Leütött bábu visszahelyezhető bármely üres mezőre (kivételek: gyalog korlátozások)
3. **Győzelem**: Ellenfél királyának mattja

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
