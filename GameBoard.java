import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameBoard extends JPanel {

        private Tile[] Tiles;  //array to draw the grid
        //background color
        private static final Color Background = new Color(0xbbada0);
        //font
        private static final String Fontname = "Arial";
        private static final int side = 64;  //tile
        private static final int margin = 16;  //tile
        boolean Win = false;
        boolean Lose = false;
        int Score = 0;

        //KeyListners
        public GameBoard() {
            setPreferredSize(new Dimension(350, 410));
            setFocusable(true);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        resetGame();
                    }
                    if (!canMove()) {
                        Lose = true;
                    }

                    if (!Win && !Lose) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_A:
                                left();
                                break;
                            case KeyEvent.VK_D:
                                right();
                                break;
                            case KeyEvent.VK_S:
                                down();
                                break;
                            case KeyEvent.VK_W:
                                up();
                                break;
                        }
                    }

                    if (!Win && !Lose) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                left();
                                break;
                            case KeyEvent.VK_RIGHT:
                                right();
                                break;
                            case KeyEvent.VK_DOWN:
                                down();
                                break;
                            case KeyEvent.VK_UP:
                                up();
                                break;
                        }
                    }


                    if (!Win && !canMove()) {
                        Lose = true;
                    }

                    repaint();
                }
            });
            resetGame();
        }

        //resets game
        public void resetGame() {
            Score = 0;
            Win = false;
            Lose = false;
            Tiles = new Tile[4 * 4];
            for (int i = 0; i < Tiles.length; i++) {
                Tiles[i] = new Tile();
            }
            addTile();
            addTile();
        }

        //algorithms of game
        //use getline, moveline, merge, set to move everything to left
        public void left() {
            boolean needAddTile = false;
            for (int i = 0; i < 4; i++) {
                Tile[] line = getLine(i);
                Tile[] merged = mergeLine(moveLine(line));  //merges tiles
                setLine(i, merged);
                if (!needAddTile && !compare(line, merged)) {
                    needAddTile = true;
                }
            }

            if (needAddTile) {
                addTile();
            }
        }

        //rotate 180* after roatting left gives right
        public void right() {
            Tiles = rotate(180);
            left();
            Tiles = rotate(180);
        }


        public void up() {
            Tiles = rotate(270);
            left();
            Tiles = rotate(90);
        }

        public void down() {
            Tiles = rotate(90);
            left();
            Tiles = rotate(270);
        }

        //returns the position of tile
        private Tile tileAt(int x, int y) {
            return Tiles[x + y * 4];
        }
        //finnds a random empty tile in the grid and initialises a new tile on it
        private void addTile() {
            List<Tile> list = availableSpace();
            if (!availableSpace().isEmpty()) {
                int index = (int) (Math.random() * list.size()) % list.size();
                Tile emptyTime = list.get(index);
                emptyTime.value = Math.random() < 1 ? 2 : 4;
            }
        }
        //returns a list of empty spots
        private List<Tile> availableSpace() {
            final List<Tile> list = new ArrayList<Tile>(16);
            for (Tile t : Tiles) {
                if (t.isEmpty()) {
                    list.add(t);
                }
            }
            return list;
        }
        //checks if the gris is full
        private boolean isFull() {
            return availableSpace().size() == 0;
        }
        //checks if any tile is blocking any move
        boolean canMove() {
            if (!isFull()) {
                return true;
            }
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    Tile t = tileAt(x, y);
                    if ((x < 3 && t.value == tileAt(x + 1, y).value)
                            || ((y < 3) && t.value == tileAt(x, y + 1).value)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean compare(Tile[] line1, Tile[] line2) {
            if (line1 == line2) {
                return true;
            } else if (line1.length != line2.length) {
                return false;
            }

            for (int i = 0; i < line1.length; i++) {
                if (line1[i].value != line2[i].value) {
                    return false;
                }
            }
            return true;
        }
        //returns an array of tile which is roated according to the degree it is given
        //this is used to roate the board, used for making left,right,up,down move
        private Tile[] rotate(int angle) {
            Tile[] newTiles = new Tile[4 * 4];
            int offsetX = 3, offsetY = 3;
            if (angle == 90) {
                offsetY = 0;
            } else if (angle == 270) {
                offsetX = 0;
            }

            double rad = Math.toRadians(angle);
            int cos = (int) Math.cos(rad);
            int sin = (int) Math.sin(rad);
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    int newX = (x * cos) - (y * sin) + offsetX;
                    int newY = (x * sin) + (y * cos) + offsetY;
                    newTiles[(newX) + (newY) * 4] = tileAt(x, y);
                }
            }
            return newTiles;
        }

        //move the lines according the direction played
        private Tile[] moveLine(Tile[] oldLine) {
            LinkedList<Tile> l = new LinkedList<Tile>();
            for (int i = 0; i < 4; i++) {
                if (!oldLine[i].isEmpty())
                    l.addLast(oldLine[i]);
            }
            if (l.size() == 0) {
                return oldLine;
            } else {
                Tile[] newLine = new Tile[4];
                ensureSize(l, 4);
                for (int i = 0; i < 4; i++) {
                    newLine[i] = l.removeFirst();
                }
                return newLine;
            }
        }

        //merges the lines accordingly
        private Tile[] mergeLine(Tile[] oldLine) {
            LinkedList<Tile> list = new LinkedList<Tile>();
            for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
                int num = oldLine[i].value;
                if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
                    num *= 2;  //doubles the oldline
                    Score += num;
                    int ourTarget = 2048;
                    if (num == ourTarget) {
                        Win = true;
                    }
                    i++;
                }
                list.add(new Tile(num));
            }
            if (list.size() == 0) {
                return oldLine;
            } else {
                ensureSize(list, 4);
                return list.toArray(new Tile[4]);
            }
        }

        //checks if the size is correct when the grid shifts, adds 0's i.e tiles if not due to merginng
        private static void ensureSize(java.util.List<Tile> l, int s) {
            while (l.size() != s) {
                l.add(new Tile());
            }
        }

        //returns a line as array
        private Tile[] getLine(int index) {
            Tile[] result = new Tile[4];
            for (int i = 0; i < 4; i++) {
                result[i] = tileAt(i, index);
            }
            return result;
        }

        private void setLine(int index, Tile[] re) {
            System.arraycopy(re, 0, Tiles, index * 4, 4);
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(Background);
            g.fillRect(0, 0, this.getSize().width, this.getSize().height);
            for (int y = 0; y < 4; y++) {
                for (int x = 0; x < 4; x++) {
                    drawTile(g, Tiles[x + y * 4], x, y);
                }
            }
        }

        private void drawTile(Graphics g2, Tile tile, int x, int y) {
            Graphics2D g = ((Graphics2D) g2);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            int value = tile.value;
            int xOffset = offsetCoors(x);
            int yOffset = offsetCoors(y);
            g.setColor(tile.getBackground());
            g.fillRoundRect(xOffset, yOffset, side, side, 14, 14);
            g.setColor(tile.getForeground());
            final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
            final Font font = new Font(Fontname, Font.BOLD, size);
            g.setFont(font);

            String s = String.valueOf(value);
            final FontMetrics fm = getFontMetrics(font);

            final int w = fm.stringWidth(s);
            final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

            if (value != 0)
                g.drawString(s, xOffset + (side - w) / 2, yOffset + side - (side - h) / 2 - 2);

            //String Win & Lose
            if (Win || Lose) {
                g.setColor(new Color(255, 255, 255, 30));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(78, 139, 202));
                g.setFont(new Font(Fontname, Font.BOLD, 48));
                if (Win) {
                    g.drawString("You won!", 68, 150);
                }
                if (Lose) {
                    g.drawString("Game over!", 50, 130);
                    g.drawString("You lose!", 64, 200);
                }
                if (Win || Lose) {
                    g.setFont(new Font(Fontname, Font.PLAIN, 16));
                    g.setColor(new Color(128, 128, 128, 128));
                    g.drawString("Press ESC to play again", 80, getHeight() - 40);
                }
            }
            g.setFont(new Font(Fontname, Font.PLAIN, 18));
            g.drawString("SCORE: " + Score, 200, 365);

        }

        private static int offsetCoors(int arg) {
            return arg * (margin + side) + margin;
        }

        static class Tile {
            int value;

            public Tile() {
                this(0);
            }

            public Tile(int num) {
                value = num;
            }

            public boolean isEmpty() {
                return value == 0;
            }

            public Color getForeground() {
                return value < 16 ? new Color(0x776e65) : new Color(0xf9f6f2);
            }

            //Background game
            public Color getBackground() {
                switch (value) {
                    case 2:
                        return new Color(0xeee4da);
                    case 4:
                        return new Color(0xede0c8);
                    case 8:
                        return new Color(0xf2b179);
                    case 16:
                        return new Color(0xf59563);
                    case 32:
                        return new Color(0xf67c5f);
                    case 64:
                        return new Color(0xf65e3b);
                    case 128:
                        return new Color(0xedcf72);
                    case 256:
                        return new Color(0xedcc61);
                    case 512:
                        return new Color(0xedc850);
                    case 1024:
                        return new Color(0xedc53f);
                    case 2048:
                        return new Color(0xedc22e);
                }
                return new Color(0xcdc1b4);
            }
        }

        //Window of the game (JFrame) //
        public static void main(String[] args) {
            JFrame game = new JFrame();
            game.setTitle("2048-Tetris");
            game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            game.setSize(350, 420);
            game.setResizable(false);

            game.add(new GameBoard());

            game.setLocationRelativeTo(null);
            game.setVisible(true);
        }
    }

