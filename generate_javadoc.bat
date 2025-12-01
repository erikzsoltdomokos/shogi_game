@echo off
REM JavaDoc dokumentáció generálása
REM Shogi projekt

echo ============================================
echo   Shogi - JavaDoc Dokumentáció Generálás
echo ============================================
echo.

REM Kimeneti mappa létrehozása
if not exist "docs\javadoc" mkdir docs\javadoc

echo [INFO] Dokumentáció generálása...
echo.

REM JavaDoc generálás
javadoc -d docs/javadoc ^
    -sourcepath src ^
    -subpackages shogi ^
    -charset UTF-8 ^
    -docencoding UTF-8 ^
    -windowtitle "Shogi - 将棋 Documentation" ^
    -doctitle "Shogi - Japanese Chess Game" ^
    -author ^
    -version ^
    -use ^
    -private ^
    -classpath "lib/*"

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo   SIKER! JavaDoc elkészült
    echo ============================================
    echo.
    echo Kimenet: docs\javadoc\index.html
    echo.
    
    set /p open="Megnyitja a dokumentációt? (I/N): "
    if /i "%open%"=="I" (
        start docs\javadoc\index.html
    )
) else (
    echo.
    echo ============================================
    echo   HIBA! Generálás sikertelen
    echo ============================================
    echo.
    pause
    exit /b 1
)

echo.
pause
