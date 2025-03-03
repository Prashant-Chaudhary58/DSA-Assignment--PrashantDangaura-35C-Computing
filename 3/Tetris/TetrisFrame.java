package Tetris;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

class Cell {
    private final int x;
    private int y;
    private final Color color;

    public Cell(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public void setY(int y) {
        this.y = y;
    }
}

class Block {
    private int[][] shape;
    private final Color color;
    private int x;
    private int y;

    public Block(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
        this.x = 4; // Start in the middle
        this.y = 0; // Start at the top
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public void moveDown() {
        y++;
    }

    public void moveUp() {
        y--;
    }

    public void rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotatedShape = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotatedShape[j][rows - 1 - i] = shape[i][j];
            }
        }
        shape = rotatedShape;
    }

    public List<Cell> getCells() {
        List<Cell> cells = new ArrayList<>();
        int rows = shape.length;
        int cols = shape[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (shape[row][col] == 1) {
                    cells.add(new Cell(x + col, y + row, color));
                }
            }
        }
        return cells;
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

class TetrisGame {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 20;

    private final Queue<Block> blockQueue;
    private final Stack<Cell> gameBoard;
    private Block currentBlock;
    private boolean gameOver;
    private int score;

    public TetrisGame() {
        blockQueue = new LinkedList<>();
        gameBoard = new Stack<>();
        enqueueNewBlock();
        currentBlock = blockQueue.poll();
        enqueueNewBlock();
        gameOver = false;
        score = 0;
    }

    private void enqueueNewBlock() {
        Random random = new Random();
        int[][] shape;
        Color color;
        int type = random.nextInt(3);
        shape = switch (type) {
            case 0 -> new int[][]{{1, 1, 1, 1}};
            case 1 -> new int[][]{{1, 1}, {1, 1}};
            case 2 -> new int[][]{{0, 1, 0}, {1, 1, 1}};
            default -> new int[][]{{1}};
        };
        color = switch (type) {
            case 0 -> Color.CYAN;
            case 1 -> Color.YELLOW;
            case 2 -> Color.MAGENTA;
            default -> Color.WHITE;
        };
        blockQueue.add(new Block(shape, color));
    }

    public void moveLeft() {
        currentBlock.moveLeft();
        if (collision()) {
            currentBlock.moveRight();
        }
    }

    public void moveRight() {
        currentBlock.moveRight();
        if (collision()) {
            currentBlock.moveLeft();
        }
    }

    public void rotate() {
        int[][] original = currentBlock.getShape();
        currentBlock.rotate();
        if (collision()) {
            currentBlock = new Block(original, currentBlock.getColor());
        }
    }

    public void moveDown() {
        currentBlock.moveDown();
        if (collision()) {
            currentBlock.moveUp();
            placeBlock();
        }
    }

    private boolean collision() {
        for (Cell cell : currentBlock.getCells()) {
            int x = cell.getX();
            int y = cell.getY();
            if (x < 0 || x >= WIDTH || y >= HEIGHT) return true;
            for (Cell placedCell : gameBoard) {
                if (placedCell.getX() == x && placedCell.getY() == y) {
                    return true;
                }
            }
        }
        return false;
    }

    private void placeBlock() {
        for (Cell cell : currentBlock.getCells()) {
            gameBoard.push(new Cell(cell.getX(), cell.getY(), cell.getColor()));
        }
        checkCompletedRows();
        currentBlock = blockQueue.poll();
        enqueueNewBlock();
        if (collision()) {
            gameOver = true;
        }
    }

    private void checkCompletedRows() {
        for (int y = HEIGHT - 1; y >= 0; y--) {
            int count = 0;
            for (Cell cell : gameBoard) {
                if (cell.getY() == y) count++;
            }
            if (count == WIDTH) {
                removeRow(y);
                score += 100;
                y++; // Recheck the same row index after removal
            }
        }
    }

    private void removeRow(int targetY) {
        Iterator<Cell> iterator = gameBoard.iterator();
        while (iterator.hasNext()) {
            Cell cell = iterator.next();
            if (cell.getY() == targetY) {
                iterator.remove();
            }
        }
        for (Cell cell : gameBoard) {
            if (cell.getY() < targetY) {
                cell.setY(cell.getY() + 1);
            }
        }
    }

    public Stack<Cell> getGameBoard() {
        return gameBoard;
    }

    public Block getCurrentBlock() {
        return currentBlock;
    }

    public Block getNextBlock() {
        return blockQueue.peek();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getScore() {
        return score;
    }
}

class GameBoardPanel extends JPanel {
    private static final int CELL_SIZE = 30;
    private final TetrisGame game;

    public GameBoardPanel(TetrisGame game) {
        this.game = game;
        setPreferredSize(new Dimension(TetrisGame.WIDTH * CELL_SIZE, TetrisGame.HEIGHT * CELL_SIZE));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Cell cell : game.getGameBoard()) {
            drawCell(g, cell);
        }
        Block current = game.getCurrentBlock();
        if (current != null) {
            for (Cell cell : current.getCells()) {
                drawCell(g, cell);
            }
        }
        if (game.isGameOver()) {
            g.setColor(Color.RED);
            g.drawString("GAME OVER", getWidth() / 2 - 30, getHeight() / 2);
        }
    }

    private void drawCell(Graphics g, Cell cell) {
        int x = cell.getX() * CELL_SIZE;
        int y = cell.getY() * CELL_SIZE;
        g.setColor(cell.getColor());
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
    }
}

class NextBlockPanel extends JPanel {
    private static final int CELL_SIZE = 30;
    private final TetrisGame game;

    public NextBlockPanel(TetrisGame game) {
        this.game = game;
        setPreferredSize(new Dimension(5 * CELL_SIZE, 5 * CELL_SIZE));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Block nextBlock = game.getNextBlock();
        if (nextBlock != null) {
            for (Cell cell : nextBlock.getCells()) {
                drawCell(g, cell);
            }
        }
    }

    private void drawCell(Graphics g, Cell cell) {
        int x = cell.getX() * CELL_SIZE;
        int y = cell.getY() * CELL_SIZE;
        g.setColor(cell.getColor());
        g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
    }
}