import java.awt.event.*;
import java.util.Arrays;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Scanner;
import java.util.ArrayList;

public class Original extends JPanel implements ActionListener,MouseListener,MouseMotionListener,KeyListener
{
    int size;
    boolean click1 = true;
    boolean click2 = true;
    boolean click3 = false;
    int count;
    Node[][] arr;
    int pressedX;
    int pressedY;
    Timer tm = new Timer(100,this);
    int startX;
    int endX;
    int startY;
    int endY;
    String str = "";
    boolean condition = false;
    boolean found = false;
    ArrayList<int[]> points;
    int[] cords;
    final int BOXSIZE = 20;
    public Original(int size)
    {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(true);
        this.size = size;
        count = 1;
        cords = new int[2];
        points = new ArrayList<int[]>();
        arr = new Node[size/BOXSIZE][size/BOXSIZE];
        for(int r = 0; r < arr.length; r++)
        {
            for (int c = 0; c < arr[0].length; c++)
            {
                arr[r][c] = new Node(r*BOXSIZE + BOXSIZE/2,c*BOXSIZE+BOXSIZE/2);
            }
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        requestFocus(true);
        drawBoard(g);
        tm.start();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (condition)
            findEnd();
        else if (str.equals("Found"))
            locatePath();
        repaint();
    }

    public void mouseClicked(MouseEvent e)
    {
        pressedX = e.getX()/BOXSIZE;
        pressedY = e.getY()/BOXSIZE;
        if (!(pressedX >= 0 && pressedY >= 0 && pressedX < arr.length && pressedY < arr[0].length)) {
            return;
        }
        if (click1)
        {
            startY = pressedY;
            startX = pressedX;
            arr[pressedX][pressedY].setColor(Color.MAGENTA);
            click1 = false;
        }
        else if (click2)
        {
            endY = pressedY;
            endX = pressedX;
            arr[pressedX][pressedY].setColor(Color.RED);
            click2 = false;
            click3 = true;
            tm = new Timer(5,this);
        }
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}

    public void mouseDragged(MouseEvent e)
    {
        if (click3)
        {
            pressedX = e.getX()/BOXSIZE;
            pressedY = e.getY()/BOXSIZE;

            if (pressedX >= 0 && pressedX < arr.length && pressedY >= 0 && pressedY < arr[0].length) {
                if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK)
                {
                    arr[pressedX][pressedY].setColor(Color.BLACK);
                }
                else
                {
                    arr[pressedX][pressedY].setColor(Color.WHITE);
                }
            }
        }
    }
    public void mouseMoved(MouseEvent e){}

    public void keyPressed(KeyEvent e)
    {
        if (!click1 && !click2 && click3 && e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            condition = true;
            click3 = false;
            points.add(new int[]{startX,startY});
            arr[startX][startY].setGCost(0);
        }
    }
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}

    public void locatePath()
    {
        arr[cords[0]][cords[1]].setColor(Color.CYAN);
        int currX = cords[0];
        int currY = cords[1];
        cords[0] = arr[currX][currY].getAttachedArrX();
        cords[1] = arr[currX][currY].getAttachedArrY();
        if (currX == startX && currY == startY)
        {
            str = "Done";
            tm.stop();
        }
    }

    public void findEnd()
    {
        if (points.size() != 0)
        {
            int index = 0;
            for (int i = 1; i < points.size(); i++)
            {
                int xTemp01 = points.get(i)[0];
                int yTemp01 = points.get(i)[1];
                int xTemp02 = points.get(index)[0];
                int yTemp02 = points.get(index)[1];
                if (arr[xTemp01][yTemp01].getFCost() <= arr[xTemp02][yTemp02].getFCost())
                {
                    index = i;
                }
            }
            int x1 = points.get(index)[0];
            int y1 = points.get(index)[1];
            points.remove(index);
            arr[x1][y1].setColor(Color.MAGENTA);

            int minG = Integer.MAX_VALUE;
            int[][] nums = {{1,0},
                            {-1,0},
                            {0,1},
                            {0,-1},
                            /*{1,1},
                            {1,-1},
                            {-1,1},
                            {-1,-1}*/};
            for(int i = 0; i < nums.length; i++)
            {
                int currX = x1 + nums[i][0];
                int currY = y1 + nums[i][1];
                boolean decisionX = true;
                boolean decisionY = true;

                if (nums[i][0] < 0)
                    decisionX = currX > -1;
                else if (nums[i][0] > 0)
                    decisionX = currX < arr.length;
                if (nums[i][1] < 0)
                    decisionY = currY > -1;
                else if (nums[i][1] > 0)
                    decisionY = currY < arr[0].length;
                if (decisionX && decisionY && arr[currX][currY].getColor() == Color.MAGENTA)
                {
                    minG = Math.min(minG, arr[currX][currY].getGCost());
                }
            }
            if (minG == Integer.MAX_VALUE)
                minG = -10;
            arr[x1][y1].setGCost(minG + 10);
            if (x1 == endX && y1 == endY)
            {
                condition = false;
                str = "Found";
                cords[0] = x1;
                cords[1] = y1;
                tm.stop();
                tm = new Timer(100,this);
                tm.start();
                return;
            }

            for(int i = 0; i < nums.length; i++)
            {
                int currX = x1 + nums[i][0];
                int currY = y1 + nums[i][1];
                boolean decisionX = true;
                boolean decisionY = true;
                if (nums[i][0] < 0)
                    decisionX = currX > -1;
                else if (nums[i][0] > 0)
                    decisionX = currX < arr.length;
                if (nums[i][1] < 0)
                    decisionY = currY > -1;
                else if (nums[i][1] > 0)
                    decisionY = currY < arr[0].length;

                if (decisionX && decisionY && arr[currX][currY].getColor() != Color.BLACK && arr[currX][currY].getColor() != Color.MAGENTA)
                {
                    arr[currX][currY].setGHCost(arr[x1][y1].getGCost()+10,currX,currY,endX,endY,x1,y1);
                    if (arr[currX][currY].getColor() != Color.YELLOW)
                    {
                        arr[currX][currY].setColor(Color.YELLOW);
                        points.add(new int[]{currX,currY});
                    }
                }
            }
        }
    }
    public void drawBoard(Graphics g)
    {
        for (int r = 0; r < arr.length; r++)
        {
            for (int c = 0; c < arr[0].length; c++)
            {
                g.setColor(arr[r][c].getColor());
                fillSquare(arr[r][c].getPosX(), arr[r][c].getPosY(),BOXSIZE,g);
            }
        }
        g.setColor(Color.BLACK);
        for (int i = 0; i < size/BOXSIZE; i++)
        {
            g.drawLine(i*BOXSIZE,0,i*BOXSIZE,size);
            g.drawLine(0,i*BOXSIZE,size,i*BOXSIZE);
        }
    }
    public void fillSquare(int x, int y, int side, Graphics g)
    {
        g.fillRect(x-side/2, y-side/2, side, side);
    }
    public void drawWords(int x, int y, String str, Graphics g, int size)
    {
        int length = str.length();
        g.drawString(str, (int)(x-(1.0*(length+1)*size/4)), (int)(y+1.0*size/2));

    }
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter size (Must be multiple of 10): ");
        int size = scan.nextInt();
        Original t = new Original(size);
        JFrame jf = new JFrame();
        jf.setTitle("Original");
        jf.setSize(size+12,size+35);
        t.setBackground(Color.WHITE);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(t);
    }
}