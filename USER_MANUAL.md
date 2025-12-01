# Shogi - Felhasználói Kézikönyv

## Tartalomjegyzék
1. [Játék Indítása](#játék-indítása)
2. [Játékmód Választás](#játékmód-választás)
3. [Játékszabályok](#játékszabályok)
4. [Bábuk Mozgatása](#bábuk-mozgatása)
5. [Drop Mechnika](#drop-mechnika)
6. [Promóció](#promóció)
7. [Játék Mentése és Betöltése](#játék-mentése-és-betöltése)
8. [Hibakeresés](#hibakeresés)

---

## Játék Indítása

### Windows-on:
1. Nyissa meg a parancsort (CMD) vagy PowerShellt
2. Navigáljon a projekt mappájába:
   ```cmd
   cd C:\Users\...\NHF_Shogi
   ```
3. Futtassa a játékot:
   ```cmd
   java -cp "bin;lib/*" shogi.Main
   ```

### Linux/Mac-en:
```bash
cd ~/NHF_Shogi
java -cp "bin:lib/*" shogi.Main
```

### Alternatíva - VS Code:
1. Nyissa meg a projektet VS Code-ban
2. Nyissa meg a `Main.java` fájlt
3. Kattintson a `Run` gombra (vagy nyomja meg az F5-öt)

### Alternatíva - Eclipse:
1. Indítsa el az Eclipse-t (Eclipse IDE for Java Developers)
2. Importálja a projektet:
   - File → Import → Existing Projects into Workspace
   - Root Directory: válassza ki a `NHF_Shogi` mappát → Finish
3. Állítsa be a függőségeket (ha vannak JAR-ok a `lib/` mappában):
   - Project → Properties → Java Build Path → Libraries
   - Add JARs… → válassza a projekt `lib/` mappájában lévő JAR-okat → Apply
   - (Alternatíva: Add External JARs… ha a JAR-ok nem a projektben vannak)
4. Ellenőrizze az output mappát:
   - Project → Properties → Java Build Path → Source → Default output folder: `bin`
5. Futtatás:
   - Nyissa meg a `src/shogi/Main.java` fájlt
   - Jobb klikk → Run As → Java Application
   - (Vagy Run → Run Configurations… → Java Application → Main class: `shogi.Main` → Run)

---

## Játékmód Választás

A program indulásakor egy párbeszédablak jelenik meg két lehetőséggel:

### 1. **Játékos vs Játékos**
- Két játékos egymás ellen, ugyanazon a számítógépen
- Felváltva lépnek (fekete kezd)
- Nincs időkorlát

### 2. **Játékos vs AI**
- Ön játszik feketével, az AI fehérrel
- Az AI automatikusan lép (véletlenszerű, de legális lépések)
- Az AI válasza azonnal megjelenik

---

## Játékszabályok

### A Tábla
- **9×9-es rács** (81 mező)
- Koordináták: **1-9** (sorok, balról), **a-i** (oszlopok, alul)
- Mindkét játékos 20 bábuval kezd

### Cél
**Matt adás:** Az ellenfél királyát olyan helyzetbe hozni, ahonnan nem tud elmenekülni.

### Bábuk és Mozgásaik

| Bábu | Szimbólum | Mozgás | Promóció |
|------|-----------|--------|----------|
| **Király (King)** | 王/玉 | 1 mező bármely irányba | ❌ Nem promótálható |
| **Bástya (Rook)** | 飛 | Tetszőleges távolság vízszintesen/függőlegesen | ✅ → 龍 (Dragon King) |
| **Futó (Bishop)** | 角 | Tetszőleges távolság átlósan | ✅ → 馬 (Dragon Horse) |
| **Arany (Gold General)** | 金 | 1 mező (előre, oldalra, átlósan előre, hátra) | ❌ Nem promótálható |
| **Ezüst (Silver General)** | 銀 | 1 mező (előre, átlósan) | ✅ → +銀 (Promoted Silver) |
| **Lovag (Knight)** | 桂 | L-alakú ugrás (2 előre + 1 oldal) | ✅ → +桂 (Promoted Knight) |
| **Lándzsa (Lance)** | 香 | Tetszőleges távolság előre | ✅ → +香 (Promoted Lance) |
| **Gyalog (Pawn)** | 歩 | 1 mező előre | ✅ → と (Tokin/Promoted Pawn) |

**Fontos:** A promótált bábuk aranytábornok-szerűen mozognak (kivéve a sárkánykirály és sárkányló, akik megtartják eredeti mozgásukat + plusz 1 mező bármely irányba).

---

## Bábuk Mozgatása

### 1. Bábu Kiválasztása
- **Kattintson** egy saját bábu ra (az Ön színe)
- A kiválasztott mező **sárga kerettel** jelenik meg
- A **lehetséges lépések kék négyzetekkel** kiemelve

### 2. Lépés Végrehajtása
- **Kattintson** egy kék négyzetre a lépés végrehajtásához
- Ha ütni tud, az ellenfél bábuja kerül a kezébe

### 3. Kiválasztás Törlése
- **Kattintson újra** ugyanarra a bábura a kiválasztás törléséhez
- Vagy válasszon ki egy másik saját bábut

### Vizuális Jelzések
- **Sárga keret:** Kiválasztott bábu
- **Kék négyzetek:** Legális lépések
- **Piros dupla keret:** Király sakkban van

---

## Drop Mechnika

Amikor leüt egy ellenfél bábut, az a **kezébe** kerül. Később bármikor visszahelyezheti a táblára.

### Drop Végrehajtása

1. **Kattintson** a kéz panelen (bal vagy jobb oldal) egy bábura
2. A kiválasztott bábu **kiemelve** jelenik meg
3. **Kattintson** a táblán egy **üres mezőre**
4. A bábu visszakerül a táblára

### Drop Szabályok
- ❌ Csak üres mezőre lehet droppolni
- ❌ Nem lehet két gyalog ugyanabban az oszlopban
- ❌ Nem lehet olyan mezőre droppolni, ahonnan a bábu nem tudna lépni (pl. gyalog az utolsó sorba)
- ❌ Gyaloggal nem lehet matt-ot adni (Pawn Drop Mate tiltás)
- ❌ Nem lehet úgy droppolni, hogy a saját király sakkban marad

### Példa
```
Fehér Rook-ot leütöttél → Fekete kézben
Fekete Rook drop e5-re → Támadja a fehér királyt
```

---

## Promóció

Amikor egy bábu belép az **ellenfél promóciós zónájába** (a táblától számított utolsó 3 sor), promótálhat.

### Promóciós Zóna
- **Fekete:** 1-3. sorok (fehér fél oldala)
- **Fehér:** 7-9. sorok (fekete fél oldala)

### Promóció Folyamata
1. Lépjen egy bábut a promóciós zónába
2. Egy **párbeszédablak** jelenik meg: "Promótálni szeretné a bábut?"
3. Válassza az **Igen** vagy **Nem** opciót

**Automatikus promóció:** Ha a bábu már nem tudna lépni (pl. gyalog az utolsó sorban), kötelező a promóció.

### Promótált Bábuk Jelölése
- Promótált bábu: `+` előtaggal jelenik meg (pl. `+銀` promótált ezüst)

---

## Játék Mentése és Betöltése

### Mentés
1. Kattintson a menüben: **Fájl → Mentés**
2. Válasszon egy helyet és adjon nevet (pl. `jatek1.json`)
3. A teljes játékállapot mentve (tábla, kezek, aktuális játékos)

### Betöltés
1. Kattintson a menüben: **Fájl → Betöltés**
2. Válassza ki a mentett JSON fájlt
3. A játék folytatódik a mentett állástól

### Új Játék
- **Fájl → Új játék:** Újrakezdés alapállásban

### Kilépés
- **Fájl → Kilépés:** Program bezárása
- Vagy egyszerűen zárja be az ablakot (X gomb)

---

## Hibakeresés

### Program nem indul
**Probléma:** "Could not find or load main class shogi.Main"

**Megoldás:**
```bash
# Fordítsa újra a projektet:
javac -d bin -cp "lib/*" src/shogi/*.java src/shogi/model/*.java

# Futtassa újra:
java -cp "bin;lib/*" shogi.Main
```

### Bábu nem mozog
- ✅ Ellenőrizze, hogy a **saját** bábuját választotta ki (ne az ellenfélét)
- ✅ Próbáljon másik bábut kiválasztani
- ✅ Ellenőrizze, hogy nem sakk-ban van-e (csak olyan lépés lehetséges, ami megszünteti a sakkot)

### AI nem lép
**Probléma:** Az AI nem reagál automatikusan

**Megoldás:**
- Várjon néhány másodpercet (az AI számol)
- Ha továbbra sem lép, újraindítás szükséges

### Mentés/betöltés hiba
**Probléma:** "IOException" vagy "FileNotFoundException"

**Megoldás:**
- Ellenőrizze, hogy van-e `saves/` mappa a projekt könyvtárában
- Hozza létre manuálisan: `mkdir saves`

---

## Tippek és Stratégiák

### Kezdőknek
1. **Védje a királyát:** Mindig ügyeljen, hogy ne kerüljön sakkba
2. **Használja a drop-ot:** A leütött bábuk hatalmas fegyvert jelentenek
3. **Promótáljon bátran:** A promótált bábuk sokkal erősebbek
4. **Arany tábornok értékes:** Ne áldozza fel könnyelműen

### Haladóknak
1. **Impasse szabály:** Ha mindkét király átjut az ellenfél oldalára, pontszámítás következik (Rook/Bishop=5, többi=1, 31+ pont = győzelem)
2. **Pawn Drop Mate tiltás:** Ne próbáljon gyaloggal matt-ot adni drop után
3. **Kettős támadás:** Használjon drop-ot két bábu egyidejű fenyegetésére

---

## További Információk

- **Teljes Dokumentáció:** `DOCUMENTATION.md`
- **Kód Dokumentáció:** Generálás: `doxygen` (lásd alább)
- **GitHub:** [erikzsoltdomokos/shogi_game](https://github.com/erikzsoltdomokos/shogi_game)

### Doxygen Dokumentáció Generálás
```bash
# Telepítés (Windows, Chocolatey):
choco install doxygen.install graphviz

# Generálás:
doxygen

# Megnyitás:
start docs/html/index.html
```

---

**Jó játékot!** 🎮 将棋を楽しんでください！

**Szerző:** Domokos Erik Zsolt  
**Verzió:** 1.0  
**Utolsó frissítés:** 2025.12.01
