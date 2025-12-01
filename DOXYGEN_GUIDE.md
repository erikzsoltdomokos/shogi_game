# Doxygen Dokumentáció Generálás - Útmutató

## Előfeltételek

### Doxygen Telepítése

#### Windows (Chocolatey):
```powershell
choco install doxygen.install graphviz
```

#### Windows (Manuális):
1. Töltse le: https://www.doxygen.nl/download.html
2. Telepítse a `doxygen-X.X.X-setup.exe` fájlt
3. (Opcionális) GraphViz: https://graphviz.org/download/

#### Linux (Ubuntu/Debian):
```bash
sudo apt-get install doxygen graphviz
```

#### macOS (Homebrew):
```bash
brew install doxygen graphviz
```

---

## Dokumentáció Generálása

### 1. Alapvető Generálás

```bash
# Navigáljon a projekt gyökérkönyvtárába
cd C:\Users\...\NHF_Shogi

# Futtassa a Doxygent
doxygen Doxyfile
```

**Kimenet:** `docs/html/index.html` fájl létrejön.

### 2. Dokumentáció Megnyitása

#### Windows:
```cmd
start docs\html\index.html
```

#### Linux/Mac:
```bash
xdg-open docs/html/index.html   # Linux
open docs/html/index.html        # macOS
```

---

## Doxyfile Konfiguráció

A `Doxyfile` tartalmazza az összes beállítást. Főbb opciók:

### Projekt Beállítások
```ini
PROJECT_NAME           = "Shogi - 将棋"
PROJECT_NUMBER         = 1.0
PROJECT_BRIEF          = "Teljes funkcionalitású Shogi implementáció"
OUTPUT_DIRECTORY       = docs
OUTPUT_LANGUAGE        = Hungarian
```

### Java Optimalizáció
```ini
OPTIMIZE_OUTPUT_JAVA   = YES
JAVADOC_AUTOBRIEF      = YES
EXTRACT_ALL            = YES
EXTRACT_PRIVATE        = YES
```

### Bemenet Beállítások
```ini
INPUT                  = src \
                         README.md
RECURSIVE              = YES
EXCLUDE_PATTERNS       = */test/* \
                         */bin/* \
                         */lib/*
```

### HTML Kimenet
```ini
GENERATE_HTML          = YES
HTML_OUTPUT            = html
GENERATE_TREEVIEW      = YES
SEARCHENGINE           = YES
```

### Osztálydiagramok (GraphViz szükséges)
```ini
HAVE_DOT               = YES
CLASS_GRAPH            = YES
COLLABORATION_GRAPH    = YES
UML_LOOK               = YES
```

---

## Generált Dokumentáció Tartalma

### Fő Oldalak

1. **Main Page (README.md)**
   - Projekt áttekintés
   - Funkciók listája
   - Telepítési útmutató

2. **Classes**
   - Összes osztály listája
   - Öröklési hierarchia
   - Osztálydiagramok

3. **Files**
   - Forrásfájlok listája
   - Fájl tartalma böngészhető

4. **Namespaces**
   - Java csomagok (`shogi`, `shogi.model`)

### Osztály Oldalak

Minden osztályhoz:
- **Brief Description:** Rövid leírás (első JavaDoc sor)
- **Detailed Description:** Részletes leírás
- **Public Methods:** Publikus metódusok
- **Private Members:** Privát mezők és metódusok
- **Inheritance Diagram:** Öröklési diagram (ha van)
- **Collaboration Diagram:** Együttműködési diagram

---

## Testreszabás

### 1. Nyelvválasztás Magyar → Angol

Szerkessze a `Doxyfile`-t:
```ini
OUTPUT_LANGUAGE        = English
```

### 2. LaTeX PDF Generálás (Opcionális)

```ini
GENERATE_LATEX         = YES
```

Majd:
```bash
cd docs/latex
make
# Eredmény: refman.pdf
```

### 3. GraphViz Kikapcsolása

Ha nincs telepítve GraphViz:
```ini
HAVE_DOT               = NO
```

### 4. Egyéni Logo Hozzáadása

```ini
PROJECT_LOGO           = logo.png
```

---

## Gyakori Problémák és Megoldások

### "doxygen: command not found"

**Probléma:** Doxygen nincs telepítve vagy nincs a PATH-ban.

**Megoldás:**
1. Telepítse a Doxygent (lásd fent)
2. Adja hozzá a PATH-hoz (Windows):
   ```
   C:\Program Files\doxygen\bin
   ```

### "warning: no matching class member found"

**Probléma:** JavaDoc és metódus szignatúra nem egyezik.

**Megoldás:** Ellenőrizze a `@param` és `@return` tageket.

### Osztálydiagramok nem jelennek meg

**Probléma:** GraphViz nincs telepítve.

**Megoldás:**
1. Telepítse a GraphViz-t
2. Állítsa be a `Doxyfile`-ban:
   ```ini
   HAVE_DOT = YES
   DOT_PATH = C:/Program Files/Graphviz/bin
   ```

### Üres oldal jelenik meg

**Probléma:** Nincs JavaDoc komment az osztályokban.

**Megoldás:** A projektben minden osztály és metódus már JavaDoc-kal dokumentálva van, ezért ez nem probléma.

---

## Automatizálás

### Batch Script (Windows)

Hozzon létre egy `generate_docs.bat` fájlt:
```batch
@echo off
echo Generating Doxygen documentation...
doxygen Doxyfile
if %errorlevel% equ 0 (
    echo Success! Opening documentation...
    start docs\html\index.html
) else (
    echo Error: Doxygen failed!
    pause
)
```

Futtatás:
```cmd
generate_docs.bat
```

### Bash Script (Linux/Mac)

Hozzon létre egy `generate_docs.sh` fájlt:
```bash
#!/bin/bash
echo "Generating Doxygen documentation..."
doxygen Doxyfile
if [ $? -eq 0 ]; then
    echo "Success! Opening documentation..."
    xdg-open docs/html/index.html  # Linux
    # open docs/html/index.html    # macOS
else
    echo "Error: Doxygen failed!"
    exit 1
fi
```

Futtatás:
```bash
chmod +x generate_docs.sh
./generate_docs.sh
```

---

## GitHub Pages Publikálás (Opcionális)

### 1. Generálás
```bash
doxygen Doxyfile
```

### 2. Létrehoz egy `.nojekyll` fájlt
```bash
touch docs/html/.nojekyll
```

### 3. Commit és Push
```bash
git add docs/html
git commit -m "Add Doxygen documentation"
git push
```

### 4. GitHub Settings
1. Repository → Settings → Pages
2. Source: Deploy from branch
3. Branch: main, Folder: `/docs/html`
4. Save

**Eredmény:** Dokumentáció elérhető: `https://erikzsoltdomokos.github.io/shogi_game/`

---

## Hasznos Linkek

- **Doxygen Manual:** https://www.doxygen.nl/manual/
- **JavaDoc Tags:** https://www.doxygen.nl/manual/docblocks.html#javaDocTags
- **GraphViz:** https://graphviz.org/
- **Doxygen GUI (Doxywizard):** Grafikus konfigurációs eszköz

---

## Összefoglalás

**1. Telepítés:**
```bash
choco install doxygen.install graphviz
```

**2. Generálás:**
```bash
doxygen
```

**3. Megnyitás:**
```bash
start docs\html\index.html
```

**4. Beadás előtt:**
- ✅ Generálja a dokumentációt
- ✅ Ellenőrizze a `docs/html/index.html` fájlt
- ✅ Tartalmazza a PDF-ben: "Dokumentáció: Doxygen által generálva"
- ✅ NE commitolja a `docs/` mappát (`.gitignore`-ban van)

---

**Szerző:** Domokos Erik Zsolt  
**Utolsó frissítés:** 2025.12.01
