import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

public class PathFinding extends JPanel implements MouseListener, MouseMotionListener, KeyListener, ActionListener {

    Node[][] board;
    final int BOXSIZE = 20;
    String mode;
    int startR;
    int startC;
    int endR;
    int endC;
    int width;
    int height;
    PathFindingThread thread;
    Timer tm = new Timer(10,this);
    int pathfindingAlgo;
    public PathFinding(int size, int pathfindingAlgo) {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(true);
        this.pathfindingAlgo = pathfindingAlgo;
        width = size/BOXSIZE;
        height = size/BOXSIZE;
        board = new Node[size/BOXSIZE][size/BOXSIZE];
        for(int r = 0; r < height; r++)
        {
            for (int c = 0; c < width; c++)
            {
                board[r][c] = new Node(r*BOXSIZE + BOXSIZE/2,c*BOXSIZE+BOXSIZE/2);
            }
        }
        thread = null;
        mode = "start";
        startC = -1;
        startR = -1;
        endR = -1;
        endC = -1;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        requestFocus(true);
        drawBoard(g);
        tm.start();
    }
    public void drawBoard(Graphics g) {
        if (thread == null) {
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    g.setColor(board[r][c].getColor());
                    g.fillRect(c * BOXSIZE, r * BOXSIZE, BOXSIZE, BOXSIZE);
                }
            }
            for (int r = 0; r < height + 1; r++) {
                g.setColor(Color.BLACK);
                g.drawLine(0, r * BOXSIZE, height * BOXSIZE, r * BOXSIZE);
            }
            for (int c = 0; c < height + 1; c++) {
                g.setColor(Color.BLACK);
                g.drawLine(c * BOXSIZE, 0, c * BOXSIZE, width * BOXSIZE);
            }
        }
        else {
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    synchronized (thread.getObject()) {
                        g.setColor(board[r][c].getColor());
                    }
                    g.fillRect(c * BOXSIZE, r * BOXSIZE, BOXSIZE, BOXSIZE);
                }
            }
            for (int r = 0; r < height + 1; r++) {
                g.setColor(Color.BLACK);
                g.drawLine(0, r * BOXSIZE, height * BOXSIZE, r * BOXSIZE);
            }
            for (int c = 0; c < height + 1; c++) {
                g.setColor(Color.BLACK);
                g.drawLine(c * BOXSIZE, 0, c * BOXSIZE, width * BOXSIZE);
            }
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if (mode.equals("start")) {
            int r = e.getY()/BOXSIZE;
            int c = e.getX()/BOXSIZE;
            if (r >= 0 && c >= 0 && r < height && c < width) {
                if (startR != -1) {
                    board[startR][startC].setColor(Color.WHITE);
                }
                if (board[r][c].getColor() == Color.BLACK || board[r][c].getColor() == Color.RED)
                    return;
                startR = r;
                startC = c;
                board[r][c].setColor(Color.MAGENTA);
                repaint();
            }
        }
        else if (mode.equals("end")) {
            int r = e.getY()/BOXSIZE;
            int c = e.getX()/BOXSIZE;
            if (r >= 0 && c >= 0 && r < height && c < width) {
                if (endR != -1) {
                    board[endR][endC].setColor(Color.WHITE);
                }
                if (board[r][c].getColor() == Color.BLACK || board[r][c].getColor() == Color.MAGENTA)
                    return;
                endR = r;
                endC = c;
                board[r][c].setColor(Color.RED);
                repaint();
            }
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mode.equals("wall")) {
            int r = e.getY()/BOXSIZE;
            int c = e.getX()/BOXSIZE;
            if (r >= 0 && c >= 0 && r < height && c < width) {
                if (board[r][c].getColor() == Color.RED || board[r][c].getColor() == Color.MAGENTA)
                    return;
                if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
                    board[r][c].setColor(Color.BLACK);
                }
                else if (e.getModifiersEx() == InputEvent.BUTTON3_DOWN_MASK){
                    board[r][c].setColor(Color.WHITE);
                }
                repaint();
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println((int)e.getKeyChar());
        if (mode.equals("running")) {
            return;
        }
        System.out.println(e.getKeyChar());
        if (e.getKeyChar() == 'w') {
            mode = "wall";
        }
        if (e.getKeyChar() == 'e') {
            mode = "end";
        }
        if (e.getKeyChar() == 's') {
            mode = "start";
        }
        if (e.getKeyChar() == 10 && startR != -1 && endR != -1) {
            mode = "running";
            PathFindingThread thread = null;
            if (pathfindingAlgo == 0) {
                thread = new AStarThread(board, startR, startC, endR, endC);
            }
            if (pathfindingAlgo == 1) {
                thread = new DijkstraThread(board, startR, startC, endR, endC);
            }
            if (pathfindingAlgo == 2) {
                thread = new BFSThread(board, startR, startC, endR, endC);
            }
            assert thread != null;
            Thread th = new Thread(thread);
            th.start();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int size = 0;
        while(true) {
            try{
                size = Integer.parseInt(JOptionPane.showInputDialog("Enter size of board (Must be multiple of 10)"));
                break;
            }catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: Please type a positive integer");
            }
        }
        int pathfindingAlgo = 0;
        while (true) {
            pathfindingAlgo = JOptionPane.showOptionDialog(null,
                                                        "Click which pathfinding algorithm you want to use:",
                                                            "Pathfinding",
                                                                JOptionPane.DEFAULT_OPTION,
                                                                JOptionPane.INFORMATION_MESSAGE,
                                                                null,
                                                                new String[]{"AStar", "Dijkstra", "Breath First"},
                                                                "AStar");
            if (pathfindingAlgo != -1) {
                break;
            }
        }
        PathFinding t = new PathFinding(size, pathfindingAlgo);
        JFrame jf = new JFrame();
        jf.setTitle("Original");
        jf.setSize(size+12,size+35);
        t.setBackground(Color.WHITE);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(t);
    }
}
