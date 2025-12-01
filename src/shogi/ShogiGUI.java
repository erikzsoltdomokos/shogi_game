package shogi;

import shogi.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Shogi játék grafikus felülete Swing-gel.
 * 
 * Fő komponensek:
 * - BoardPanel: 9×9-es játéktábla megjelenítése és kattintás kezelés
 * - HandPanel (×2): Leütött bábuk megjelenítése játékosonként
 * - Menu bar: Új játék, mentés, betöltés, kilépés
 * 
 * Támogatott játékmódok:
 * - Játékos vs Játékos (helyi 2 játékos)
 * - Játékos vs AI (random, de legális lépéseket választ)
 * 
 * @author Domokos Erik Zsolt
 */
public class ShogiGUI extends JFrame {
    
    /** Alapértelmezett cella méret pixelben */
    private static final int DEFAULT_CELL_SIZE = 60;
    
    /** Alapértelmezett tábla méret pixelben (9×60) */
    private static final int DEFAULT_BOARD_SIZE = 9 * DEFAULT_CELL_SIZE;
    
    /** Kéz panel szélessége pixelben */
    private static final int HAND_PANEL_WIDTH = 200;
    
    /** Aktuális cella méret (dinamikusan változik az ablak méretével) */
    private int currentCellSize = DEFAULT_CELL_SIZE;
    
    /** Aktuális tábla méret (dinamikusan változik az ablak méretével) */
    private int currentBoardSize = DEFAULT_BOARD_SIZE;
    
    /** Tábla rajzolás eltolása X irányban (középre igazításhoz) */
    private int boardOffsetX = 0;
    
    /** Tábla rajzolás eltolása Y irányban (középre igazításhoz) */
    private int boardOffsetY = 0;
    
    /** A játék logikai motorja */
    private ShogiGame game;
    
    /** Első játékos (BLACK) */
    private Player player1;
    
    /** Második játékos (WHITE) */
    private Player player2;
    
    /** Aktuális játékmód */
    private GameMode gameMode;
    
    /** Tábla rajzoló/kezelő komponens */
    private BoardPanel boardPanel;
    
    /** Fekete játékos kéz panele */
    private HandPanel blackHandPanel;
    
    /** Fehér játékos kéz panele */
    private HandPanel whiteHandPanel;
    
    /** Aktuálisan kiválasztott pozíció a táblán (lehet null) */
    private Position selectedPosition;
    
    /** Lehetséges lépések a kiválasztott bábuval */
    private List<Position> validMoves;
    
    /** True, ha drop módban vagyunk (kézből bábu visszahelyezés) */
    private boolean selectingDropPiece;
    
    /** Drop módban kiválasztott bábu típusa */
    private String dropPieceType;
    
    /**
     * Játékmódok.
     */
    public enum GameMode {
        /** Két játékos egymás ellen helyben */
        PLAYER_VS_PLAYER,
        
        /** Egy játékos az AI ellen */
        PLAYER_VS_AI
    }
    
    /**
     * Létrehozza és megjelenít egy új Shogi GUI ablakot.
     * Játékmód választás → GUI felépítés → ablak megjelenítés.
     */
    public ShogiGUI() {
        super("Shogi - 将棋");
        
        // Játékmód választása dialógussal
        selectGameMode();
        
        // Játék inicializálása alapállásban
        game = new ShogiGame();
        selectedPosition = null;
        validMoves = List.of();
        selectingDropPiece = false;
        
        setupGUI();
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // ===================================================================
    //                    JÁTÉKMÓD VÁLASZTÁS
    // ===================================================================
    
    /**
     * Játékmód választó dialógus.
     * Játékos vs Játékos vagy Játékos vs AI.
     */
    private void selectGameMode() {
        String[] options = {"Játékos vs Játékos", "Játékos vs AI", "Mégse"};
        int choice = JOptionPane.showOptionDialog(
            null,
            "Válassz játékmódot:",
            "Shogi - Új játék",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == 0) {
            gameMode = GameMode.PLAYER_VS_PLAYER;
            player1 = new Player("Fekete játékos", Piece.Color.BLACK);
            player2 = new Player("Fehér játékos", Piece.Color.WHITE);
        } else if (choice == 1) {
            gameMode = GameMode.PLAYER_VS_AI;
            player1 = new Player("Te", Piece.Color.BLACK);
            player2 = new AIPlayer("AI", Piece.Color.WHITE);
        } else {
            System.exit(0);
        }
    }
    
    /**
     * GUI elemek felépítése.
     */
    private void setupGUI() {
        setLayout(new BorderLayout());
        
        // Menüsor
        createMenuBar();
        
        // Központi panel: tábla
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);
        
        // Jobb oldali panel: fehér játékos keze
        whiteHandPanel = new HandPanel(Piece.Color.WHITE);
        JScrollPane whiteScrollPane = new JScrollPane(whiteHandPanel);
        whiteScrollPane.setPreferredSize(new Dimension(HAND_PANEL_WIDTH, DEFAULT_BOARD_SIZE));
        whiteScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        whiteScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(whiteScrollPane, BorderLayout.EAST);
        
        // Bal oldali panel: fekete játékos keze
        blackHandPanel = new HandPanel(Piece.Color.BLACK);
        JScrollPane blackScrollPane = new JScrollPane(blackHandPanel);
        blackScrollPane.setPreferredSize(new Dimension(HAND_PANEL_WIDTH, DEFAULT_BOARD_SIZE));
        blackScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        blackScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(blackScrollPane, BorderLayout.WEST);
    }
    
    /**
     * Tábla skálázásának frissítése az ablak mérete alapján.
     */
    private void updateBoardScale() {
        if (boardPanel == null) return;
        
        int panelWidth = boardPanel.getWidth();
        int panelHeight = boardPanel.getHeight();
        
        // Kisebb oldal határozza meg a tábla méretét
        int minSize = Math.min(panelWidth, panelHeight);
        
        // Új cellaméret és táblaméret számítása
        currentCellSize = minSize / 9;
        currentBoardSize = currentCellSize * 9;
        
        // Középre igazítás offset számítása
        boardOffsetX = (panelWidth - currentBoardSize) / 2;
        boardOffsetY = (panelHeight - currentBoardSize) / 2;
        
        boardPanel.repaint();
    }
    
    /**
     * Menüsor létrehozása.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Fájl");
        
        JMenuItem newGameItem = new JMenuItem("Új játék");
        newGameItem.addActionListener(e -> newGame());
        fileMenu.add(newGameItem);
        
        fileMenu.addSeparator();
        
        JMenuItem saveItem = new JMenuItem("Mentés");
        saveItem.addActionListener(e -> saveGame());
        fileMenu.add(saveItem);
        
        JMenuItem loadItem = new JMenuItem("Betöltés");
        loadItem.addActionListener(e -> loadGame());
        fileMenu.add(loadItem);
        
        fileMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Kilépés");
        exitItem.addActionListener(e -> handleExit());
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
    /**
     * Tábla panel - rajzolja a 9×9-es táblát és a figurákat.
     */
    private class BoardPanel extends JPanel {
        
        public BoardPanel() {
            setPreferredSize(new Dimension(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE));
            setBackground(new Color(220, 179, 92));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleBoardClick(e.getX(), e.getY());
                }
            });
            
            // ComponentListener a dinamikus átméretezéshez
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    updateBoardScale();
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Eltoljuk a rajzolást, hogy középre kerüljön
            g2d.translate(boardOffsetX, boardOffsetY);
            
            drawBoard(g2d);
            drawPieces(g2d);
            drawValidMoves(g2d);
            drawSelectedSquare(g2d);
            drawCheckIndicator(g2d);
            drawCoordinates(g2d);
        }
        
        /**
         * Tábla rajzolása.
         */
        private void drawBoard(Graphics2D g) {
            // Vonalak
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            
            for (int i = 0; i <= 9; i++) {
                // Vízszintes vonalak
                g.drawLine(0, i * currentCellSize, currentBoardSize, i * currentCellSize);
                // Függőleges vonalak
                g.drawLine(i * currentCellSize, 0, i * currentCellSize, currentBoardSize);
            }
            
            // Koordináta címkék
            drawCoordinates(g);
        }
        
        /**
         * Koordináta címkék rajzolása (1-9 sorok, a-i oszlopok).
         */
        private void drawCoordinates(Graphics2D g) {
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.setColor(new Color(100, 100, 100));
            
            // Sorok (1-9) bal oldalon
            for (int row = 0; row < 9; row++) {
                String label = String.valueOf(row + 1);
                g.drawString(label, 5, row * currentCellSize + currentCellSize / 2 + 5);
            }
            
            // Oszlopok (a-i) alul
            for (int col = 0; col < 9; col++) {
                String label = String.valueOf((char)('a' + col));
                g.drawString(label, col * currentCellSize + currentCellSize / 2 - 3, currentBoardSize - 5);
            }
        }
        
        /**
         * Figurák rajzolása.
         */
        private void drawPieces(Graphics2D g) {
            Board board = game.getBoard();
            
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Piece piece = board.getPieceAt(row, col);
                    if (piece != null) {
                        drawPiece(g, piece, row, col);
                    }
                }
            }
        }
        
        /**
         * Egy figura rajzolása.
         */
        private void drawPiece(Graphics2D g, Piece piece, int row, int col) {
            int x = col * currentCellSize;
            int y = row * currentCellSize;
            
            // Figura háttér
            if (piece.getColor() == Piece.Color.BLACK) {
                g.setColor(new Color(50, 50, 50));
            } else {
                g.setColor(new Color(240, 240, 240));
            }
            
            int margin = (int)(currentCellSize * 0.13);
            g.fillRoundRect(x + margin, y + margin, 
                          currentCellSize - 2 * margin, currentCellSize - 2 * margin, 10, 10);
            
            // Keret
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x + margin, y + margin, 
                          currentCellSize - 2 * margin, currentCellSize - 2 * margin, 10, 10);
            
            // Figura szimbólum - font méret skálázva
            String symbol = piece.getSymbol();
            g.setColor(piece.getColor() == Piece.Color.BLACK ? Color.WHITE : Color.BLACK);
            int fontSize = (int)(currentCellSize * 0.47);
            g.setFont(new Font("Serif", Font.BOLD, fontSize));
            
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (currentCellSize - fm.stringWidth(symbol)) / 2;
            int textY = y + (currentCellSize + fm.getAscent() - fm.getDescent()) / 2;
            
            g.drawString(symbol, textX, textY);
        }
        
        /**
         * Lehetséges lépések megjelenítése átlátszó kék négyzetekkel.
         */
        private void drawValidMoves(Graphics2D g) {
            if (!validMoves.isEmpty()) {
                g.setColor(new Color(0, 150, 255, 100));
                for (Position pos : validMoves) {
                    int x = pos.getCol() * currentCellSize;
                    int y = pos.getRow() * currentCellSize;
                    g.fillRect(x, y, currentCellSize, currentCellSize);
                    
                    // Kék keret a jobban láthatósághoz
                    g.setColor(new Color(0, 100, 255, 150));
                    g.setStroke(new BasicStroke(2));
                    g.drawRect(x + 2, y + 2, currentCellSize - 4, currentCellSize - 4);
                    g.setColor(new Color(0, 150, 255, 100));
                }
            }
        }
        
        /**
         * Kiválasztott mező kiemelése.
         */
        private void drawSelectedSquare(Graphics2D g) {
            if (selectedPosition != null) {
                g.setColor(new Color(255, 255, 0, 100));
                int x = selectedPosition.getCol() * currentCellSize;
                int y = selectedPosition.getRow() * currentCellSize;
                g.fillRect(x, y, currentCellSize, currentCellSize);
                
                g.setColor(Color.YELLOW);
                g.setStroke(new BasicStroke(3));
                g.drawRect(x, y, currentCellSize, currentCellSize);
            }
        }
        
        /**
         * Sakk jelzés rajzolása - piros keret a sakkban lévő király körül.
         */
        private void drawCheckIndicator(Graphics2D g) {
            // Ellenőrizzük mindkét királyt
            drawCheckForKing(g, Piece.Color.BLACK);
            drawCheckForKing(g, Piece.Color.WHITE);
        }
        
        /**
         * Sakk jelzés egy adott színű királyra.
         */
        private void drawCheckForKing(Graphics2D g, Piece.Color color) {
            if (!game.isInCheck(color)) {
                return; // Nincs sakkban
            }
            
            // Megkeressük a királyt
            Board board = game.getBoard();
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Piece piece = board.getPieceAt(row, col);
                    if (piece instanceof King && piece.getColor() == color) {
                        // Piros vilógó keret
                        int x = col * currentCellSize;
                        int y = row * currentCellSize;
                        
                        g.setColor(new Color(255, 0, 0, 180));
                        g.setStroke(new BasicStroke(5));
                        g.drawRect(x + 2, y + 2, currentCellSize - 4, currentCellSize - 4);
                        
                        // Belső világosabb keret
                        g.setColor(new Color(255, 100, 100, 100));
                        g.setStroke(new BasicStroke(3));
                        g.drawRect(x + 5, y + 5, currentCellSize - 10, currentCellSize - 10);
                        
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * Kéz panel - mutatja a fogott figurákat.
     */
    private class HandPanel extends JPanel {
        
        private Piece.Color color;
        
        public HandPanel(Piece.Color color) {
            this.color = color;
            setBackground(new Color(200, 160, 80));
            setBorder(BorderFactory.createTitledBorder(
                color == Piece.Color.BLACK ? "Fekete keze" : "Fehér keze"));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleHandClick(color, e.getY());
                }
            });
        }
        
        @Override
        public Dimension getPreferredSize() {
            // Dinamikusan számoljuk a szükséges magasságot
            List<Piece> hand = (color == Piece.Color.BLACK) 
                ? game.getBlackHand() 
                : game.getWhiteHand();
            
            int height = Math.max(DEFAULT_BOARD_SIZE, 30 + hand.size() * 50 + 10);
            return new Dimension(HAND_PANEL_WIDTH, height);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            List<Piece> hand = (color == Piece.Color.BLACK) 
                ? game.getBlackHand() 
                : game.getWhiteHand();
            
            int y = 30;
            for (Piece piece : hand) {
                drawHandPiece(g2d, piece, y);
                y += 50;
            }
        }
        
        /**
         * Kézben lévő figura rajzolása.
         */
        private void drawHandPiece(Graphics2D g, Piece piece, int y) {
            int x = 10;
            int size = 40;
            
            // Háttér
            if (piece.getColor() == Piece.Color.BLACK) {
                g.setColor(new Color(50, 50, 50));
            } else {
                g.setColor(new Color(240, 240, 240));
            }
            g.fillRoundRect(x, y, size, size, 8, 8);
            
            // Keret
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(x, y, size, size, 8, 8);
            
            // Szimbólum
            String symbol = piece.getSymbol();
            g.setColor(piece.getColor() == Piece.Color.BLACK ? Color.WHITE : Color.BLACK);
            g.setFont(new Font("Serif", Font.BOLD, 20));
            
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (size - fm.stringWidth(symbol)) / 2;
            int textY = y + (size + fm.getAscent() - fm.getDescent()) / 2;
            
            g.drawString(symbol, textX, textY);
            
            // Típus neve
            g.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g.drawString(piece.getClass().getSimpleName(), x + size + 5, y + 25);
        }
    }
    
    /**
     * Táblára kattintás kezelése.
     */
    private void handleBoardClick(int x, int y) {
        // Offsetet levonva számoljuk a pozíciót
        int adjustedX = x - boardOffsetX;
        int adjustedY = y - boardOffsetY;
        
        int col = adjustedX / currentCellSize;
        int row = adjustedY / currentCellSize;
        
        if (row < 0 || row >= 9 || col < 0 || col >= 9) {
            return;
        }
        
        Position clickedPos = new Position(row, col);
        
        // Drop mód
        if (selectingDropPiece) {
            performDrop(clickedPos);
            return;
        }
        
        // Normál lépés
        if (selectedPosition == null) {
            // Figura kiválasztása
            Piece piece = game.getBoard().getPieceAt(row, col);
            if (piece != null && piece.getColor() == game.getCurrentPlayer()) {
                selectedPosition = clickedPos;
                validMoves = piece.getLegalMoves(game.getBoard());
                boardPanel.repaint();
            }
        } else {
            // Lépés végrehajtása
            if (selectedPosition.equals(clickedPos)) {
                // Ugyanarra kattintott - törlés
                selectedPosition = null;
                validMoves = List.of();
                boardPanel.repaint();
            } else {
                performMove(selectedPosition, clickedPos);
            }
        }
    }
    
    /**
     * Kézre kattintás kezelése.
     */
    private void handleHandClick(Piece.Color color, int y) {
        if (game.getCurrentPlayer() != color) {
            return; // Nem a te köröd
        }
        
        List<Piece> hand = (color == Piece.Color.BLACK) 
            ? game.getBlackHand() 
            : game.getWhiteHand();
        
        int index = (y - 30) / 50;
        if (index >= 0 && index < hand.size()) {
            Piece piece = hand.get(index);
            dropPieceType = piece.getClass().getSimpleName();
            selectingDropPiece = true;
            selectedPosition = null;
            
            JOptionPane.showMessageDialog(this, 
                "Kattints a táblára, ahová le szeretnéd helyezni: " + dropPieceType);
        }
    }
    
    /**
     * Lépés végrehajtása.
     */
    private void performMove(Position from, Position to) {
        boolean success = game.makeMove(from, to);
        
        selectedPosition = null;
        validMoves = List.of();
        boardPanel.repaint();
        blackHandPanel.repaint();
        whiteHandPanel.repaint();
        
        if (!success) {
            JOptionPane.showMessageDialog(this, "Szabálytalan lépés!");
            return;
        }
        
        checkGameEnd();
        
        // AI lép, ha szükséges
        if (gameMode == GameMode.PLAYER_VS_AI && game.getCurrentPlayer() == Piece.Color.WHITE) {
            Timer timer = new Timer(500, e -> {
                AIPlayer ai = (AIPlayer) player2;
                boolean aiSuccess = ai.makeMove(game);
                
                boardPanel.repaint();
                blackHandPanel.repaint();
                whiteHandPanel.repaint();
                
                if (!aiSuccess) {
                    // AI nem tud lépni - játék vége
                    JOptionPane.showMessageDialog(this, 
                        "Fekete nyert! Az AI nem tud többé lépni.",
                        "Játék vége",
                        JOptionPane.INFORMATION_MESSAGE);
                    newGame();
                    return;
                }
                
                checkGameEnd();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    /**
     * Drop művelet végrehajtása.
     */
    private void performDrop(Position to) {
        boolean success = game.dropPiece(dropPieceType, to);
        
        selectingDropPiece = false;
        dropPieceType = null;
        validMoves = List.of();
        
        boardPanel.repaint();
        blackHandPanel.repaint();
        whiteHandPanel.repaint();
        
        if (!success) {
            JOptionPane.showMessageDialog(this, "Nem helyezhető ide a figura!");
            return;
        }
        
        checkGameEnd();
        
        // AI lép
        if (gameMode == GameMode.PLAYER_VS_AI && game.getCurrentPlayer() == Piece.Color.WHITE) {
            Timer timer = new Timer(500, e -> {
                AIPlayer ai = (AIPlayer) player2;
                ai.makeMove(game);
                
                boardPanel.repaint();
                blackHandPanel.repaint();
                whiteHandPanel.repaint();
                
                checkGameEnd();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    /**
     * Játék végének ellenőrzése.
     */
    private void checkGameEnd() {
        // 1. Impasse (入玉) ellenőrzés
        ShogiGame.ImpasseResult impasse = game.checkImpasse();
        if (impasse.isImpasse) {
            String message;
            if (impasse.winner == null) {
                message = String.format(
                    "Döntetlen! (入玉 - Impasse)\n\n" +
                    "Mindkét király átjutott az ellenfél térfelére.\n" +
                    "Fekete pontok: %d\n" +
                    "Fehér pontok: %d\n\n" +
                    "Egyik fél sem érte el a 31 pontot a győzelemhez.",
                    impasse.blackPoints, impasse.whitePoints
                );
            } else {
                String winnerName = (impasse.winner == Piece.Color.BLACK) ? "Fekete" : "Fehér";
                message = String.format(
                    "%s nyert! (入玉 - Impasse)\n\n" +
                    "Mindkét király átjutott az ellenfél térfelére.\n" +
                    "Fekete pontok: %d\n" +
                    "Fehér pontok: %d\n\n" +
                    "%s elérte a 31+ pontot!",
                    winnerName, impasse.blackPoints, impasse.whitePoints, winnerName
                );
            }
            JOptionPane.showMessageDialog(this, message, "Játék vége - Impasse", 
                JOptionPane.INFORMATION_MESSAGE);
            newGame();
            return;
        }
        
        // 2. Ellenőrizzük, hogy valamelyik király ki lett-e ütve / sakkmatt
        if (game.isGameOver(Piece.Color.BLACK)) {
            JOptionPane.showMessageDialog(this, 
                "Fehér nyert! A fekete király ki lett ütve vagy sakkmattban van.",
                "Játék vége",
                JOptionPane.INFORMATION_MESSAGE);
            newGame();
        } else if (game.isGameOver(Piece.Color.WHITE)) {
            JOptionPane.showMessageDialog(this, 
                "Fekete nyert! A fehér király ki lett ütve vagy sakkmattban van.",
                "Játék vége",
                JOptionPane.INFORMATION_MESSAGE);
            newGame();
        }
    }
    
    /**
     * Új játék indítása.
     */
    private void newGame() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Új játékot indítasz. Biztosan?",
            "Új játék",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            selectGameMode();
            game = new ShogiGame();
            selectedPosition = null;
            selectingDropPiece = false;
            
            boardPanel.repaint();
            blackHandPanel.repaint();
            whiteHandPanel.repaint();
        }
    }
    
    /**
     * Játék mentése.
     */
    private void saveGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Játék mentése");
        fileChooser.setSelectedFile(new File("shogi_save.json"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                SaveManager.save(game, fileChooser.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Játék sikeresen elmentve!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Hiba a mentés során: " + ex.getMessage(),
                    "Hiba",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Játék betöltése.
     */
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Játék betöltése");
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                game = SaveManager.load(fileChooser.getSelectedFile().getAbsolutePath());
                selectedPosition = null;
                selectingDropPiece = false;
                
                boardPanel.repaint();
                blackHandPanel.repaint();
                whiteHandPanel.repaint();
                
                JOptionPane.showMessageDialog(this, "Játék sikeresen betöltve!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Hiba a betöltés során: " + ex.getMessage(),
                    "Hiba",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Kilépés kezelése (mentési ajánlattal).
     */
    private void handleExit() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Szeretnéd elmenteni a játékot kilépés előtt?",
            "Kilépés",
            JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            saveGame();
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // CANCEL esetén nem történik semmi
    }
}
