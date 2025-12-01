@echo off
REM Doxygen dokumentáció generálása
REM Shogi projekt
REM Szerző: Domokos Erik Zsolt

echo ============================================
echo   Shogi - Doxygen Dokumentáció Generálás
echo ============================================
echo.

REM Ellenőrizzük, hogy Doxygen telepítve van-e
where doxygen >nul 2>nul
if %errorlevel% neq 0 (
    echo [HIBA] Doxygen nem található a PATH-ban!
    echo.
    echo Telepítés:
    echo   choco install doxygen.install graphviz
    echo.
    echo Vagy letöltés:
    echo   https://www.doxygen.nl/download.html
    echo.
    pause
    exit /b 1
)

echo [OK] Doxygen megtalálva
echo.

REM Ellenőrizzük a GraphViz-t (opcionális)
where dot >nul 2>nul
if %errorlevel% equ 0 (
    echo [OK] GraphViz megtalálva - diagramok engedélyezve
) else (
    echo [FIGYELEM] GraphViz nem található - diagramok kikapcsolva
    echo   Telepítés: choco install graphviz
)
echo.

REM Generálás
echo [INFO] Dokumentáció generálása...
echo.
doxygen Doxyfile

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo   SIKER! Dokumentáció elkészült
    echo ============================================
    echo.
    echo Kimenet: docs\html\index.html
    echo.
    
    REM Megnyitás böngészőben
    set /p open="Megnyitja a dokumentációt? (I/N): "
    if /i "%open%"=="I" (
        start docs\html\index.html
    )
) else (
    echo.
    echo ============================================
    echo   HIBA! Generálás sikertelen
    echo ============================================
    echo.
    echo Ellenőrizze:
    echo   1. Doxyfile létezik-e
    echo   2. src/ mappa létezik-e
    echo   3. JavaDoc kommentek helyesek-e
    echo.
    pause
    exit /b 1
)

echo.
pause
