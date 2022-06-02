import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.ArrayList;

public class Tester extends JPanel implements ActionListener,MouseListener,MouseMotionListener,KeyListener
{
    int size;
    boolean click1 = true;
    boolean click2 = true;
    boolean click3 = true;
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
    ArrayList<int[]> path;
    Toolkit t = Toolkit.getDefaultToolkit();

    final int BOXSIZE = 20;
    public Tester()
    {
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(true);
        this.size = 400;
        path = new ArrayList<int[]>();
        count = 1;

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

    //Set up Board
    public void mouseClicked(MouseEvent e)
    {
        //Place start Node
        if (click1)
        {
            pressedX = e.getX();
            pressedY = e.getY();
            startX = -2;
            startY = -2;
            for(int r = 0; r < arr.length; r++)
            {
                for (int c = 0; c < arr[0].length; c++)
                {
                    if ((arr[r][c].getPosX()-BOXSIZE/2 < pressedX && pressedX < arr[r][c].getPosX()+BOXSIZE/2)
                            && (arr[r][c].getPosY()-BOXSIZE/2 < pressedY && pressedY < arr[r][c].getPosY()+BOXSIZE/2))
                    {
                        found = true;
                        startY = c;
                        startX = r;
                        arr[r][c].setMode("point");
                    }
                }
            }
            if (found)
            {
                found = false;
                click1 = false;
                points.add(new int[]{startX,startY});
            }
        }

        //Place End Node
        else if (click2)
        {
            pressedX = e.getX();
            pressedY = e.getY();

            endX = -1;
            endY = -1;
            for(int r = 0; r < arr.length; r++)
            {
                for (int c = 0; c < arr[0].length; c++)
                {
                    if ((arr[r][c].getPosX()-BOXSIZE/2 < pressedX && pressedX < arr[r][c].getPosX()+BOXSIZE/2)
                            && (arr[r][c].getPosY()-BOXSIZE/2 < pressedY && pressedY < arr[r][c].getPosY()+BOXSIZE/2))
                    {
                        found = true;
                        endY = c;
                        endX = r;
                        arr[r][c].setMode("point");
                    }
                }
            }
            if (found)
            {
                found = false;
                click2 = false;
                tm = new Timer(5,this);
            }
        }
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}

    public void mouseDragged(MouseEvent e)
    {
        //Add walls
        if (!click1 && !click2 && click3)
        {
            pressedX = e.getX();
            pressedY = e.getY();

            for(int r = 0; r < arr.length; r++)
            {
                for (int c = 0; c < arr[0].length; c++)
                {
                    if ((arr[r][c].getPosX()-BOXSIZE/2 < pressedX && pressedX < arr[r][c].getPosX()+BOXSIZE/2)
                            && (arr[r][c].getPosY()-BOXSIZE/2  < pressedY && pressedY < arr[r][c].getPosY()+BOXSIZE/2))
                    {
                        if (!((r == startX && c == startY) || (r == endX && c == endY)))
                        {
                            if (e.getModifiers() == MouseEvent.BUTTON1_MASK)
                                arr[r][c].setMode("water");
                            else
                                arr[r][c].setMode("grass");
                        }
                    }
                }
            }
        }
    }
    public void mouseMoved(MouseEvent e){}

    public void keyPressed(KeyEvent e)
    {
        //Start code
        if (!click1 && !click2 && click3 && e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            condition = true;
            click3 = false;
        }
    }
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}

    public void locatePath()
    {
        int num01 = path.get(path.size()-1)[0];
        int num02 = path.get(path.size()-1)[1];
        int[] temp = {arr[num01][num02].getAttachedArrX(),arr[num01][num02].getAttachedArrY()};
        path.add(temp);

        //Find horizontal/Vertical path
        if (num01 == startX && num02 == startY)
        {
            str = "Done";
            tm.stop();
            path.remove(path.size()-1);
            int len1 = path.size()-1;
            int len2 = path.size()-2;
            if (path.get(len1)[0] - path.get(len2)[0] < 0)
                arr[num01][num02].setMode("pointRight");
            else if (path.get(len1)[0] - path.get(len2)[0] > 0)
                arr[num01][num02].setMode("pointLeft");
            else if (path.get(len1)[1] - path.get(len2)[1] > 0)
                arr[num01][num02].setMode("pointUp");
            else
                arr[num01][num02].setMode("pointDown");
            return;
        }
        else if (path.size() == 2)
        {
            if (path.get(1)[0] - path.get(0)[0] < 0)
                arr[path.get(0)[0]][path.get(0)[1]].setMode("pointLeft");
            else if (path.get(1)[0] - path.get(0)[0] > 0)
                arr[path.get(0)[0]][path.get(0)[1]].setMode("pointRight");
            else if (path.get(1)[1] - path.get(0)[1] > 0)
                arr[path.get(0)[0]][path.get(0)[1]].setMode("pointDown");
            else
                arr[path.get(0)[0]][path.get(0)[1]].setMode("pointUp");
        }
        if (path.size() > 1)
        {
            int len1 = path.size()-1;
            int len2 = path.size()-2;
            if (path.get(len1)[0] - path.get(len2)[0] != 0)
            {
                arr[path.get(len1)[0]][path.get(len1)[1]].setMode("pathHorizontal");
            }
            if (path.get(len1)[1] - path.get(len2)[1] != 0)
            {
                arr[path.get(len1)[0]][path.get(len1)[1]].setMode("pathVertical");
            }
        }

        //Find corners of path
        if (path.size() > 2)
        {
            String str = "path";
            int len1 = path.size()-1;
            int len2 = path.size()-2;
            int len3 = path.size()-3;

            if (Math.abs(path.get(len1)[0] - path.get(len3)[0]) == 1)
            {
                if (path.get(len1)[0] - path.get(len2)[0] > 0 && path.get(len2)[1] - path.get(len3)[1] > 0 ||
                        path.get(len3)[0] - path.get(len2)[0] > 0 && path.get(len2)[1] - path.get(len1)[1] > 0)
                    str += "DownRight";
                else if (path.get(len2)[0] - path.get(len3)[0] > 0 && path.get(len1)[1] - path.get(len2)[1] > 0 ||
                        path.get(len2)[0] - path.get(len1)[0] > 0 && path.get(len3)[1] - path.get(len2)[1] > 0)
                    str += "UpLeft";
                else if (path.get(len3)[0] - path.get(len2)[0] > 0 && path.get(len1)[1] - path.get(len2)[1] > 0 ||
                        path.get(len1)[0] - path.get(len2)[0] > 0 && path.get(len3)[1] - path.get(len2)[1] > 0 )
                    str += "UpRight";
                else
                    str += "DownLeft";

                arr[path.get(len2)[0]][path.get(len2)[1]].setMode(str);
            }
        }
    }

    public void findEnd()
    {
        if (points.size() != 0)
        {
            //Find lowest FCost
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
            arr[x1][y1].setMode("flowers");


            //Find surrounding nodes
            int minG = Integer.MAX_VALUE;
            int[][] nums = {{1,0},
                    {-1,0},
                    {0,1},
                    {0,-1}};
            for(int i = 0; i < nums.length; i++)
            {
                int currX = x1 + nums[i][0];
                int currY = y1 + nums[i][1];
                boolean decisionX = true;
                boolean decisionY = true;

                if (nums[i][0] < 0 || nums[i][1] < 0)
                {
                    decisionX = currX > -1;
                    decisionY = currY > -1;
                }
                else
                {
                    decisionX = currX < arr.length;
                    decisionY = currY < arr[0].length;
                }
                if (decisionX && decisionY && arr[currX][currY].getMode().equals("flowers"))
                    minG = Math.min(minG, arr[currX][currY].getGCost());
            }
            arr[x1][y1].setGCost(minG + 10);
            points.remove(index);

            for(int i = 0; i < nums.length; i++)
            {
                int currX = x1 + nums[i][0];
                int currY = y1 + nums[i][1];
                boolean decisionX = true;
                boolean decisionY = true;
                if (nums[i][0] < 0 || nums[i][1] < 0)
                {
                    decisionX = currX > -1;
                    decisionY = currY > -1;
                }
                else
                {
                    decisionX = currX < arr.length;
                    decisionY = currY < arr[0].length;
                }
                //Determine if new node can be placed
                if (decisionX && decisionY && !arr[currX][currY].getMode().equals("water") && !arr[currX][currY].getMode().equals("flowers"))
                {
                    arr[currX][currY].setGHCost(arr[x1][y1].getGCost()+10,currX,currY,endX,endY,x1,y1);
                    if (arr[currX][currY].getColor() != Color.YELLOW)
                    {
                        arr[currX][currY].setColor(Color.YELLOW);
                        points.add(new int[]{currX,currY});
                    }
                    //Determine if node is end node
                    if (currX == endX && currY == endY)
                    {
                        condition = false;
                        str = "Found";
                        int[] temp = {currX, currY};
                        path.add(temp);
                        tm.stop();
                        tm = new Timer(100,this);
                        tm.start();
                        break;
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
                String str = arr[r][c].getMode();
                if (str.equals("water"))
                {
                    if (c-1 <= - 1 || !arr[r][c-1].getMode().equals("water"))
                        str += "Up";
                    if (c+1 >= arr[0].length || !arr[r][c+1].getMode().equals("water"))
                        str += "Down";
                    if (r-1 <= -1 || !arr[r-1][c].getMode().equals("water"))
                        str += "Left";
                    if (r+1 >= arr.length || !arr[r+1][c].getMode().equals("water"))
                        str += "Right";
                }

                if (str.equals("water"))
                {
                    if (r-1 > -1 && arr[r-1][c].getMode().indexOf("water") != -1 && c+1 < arr.length && arr[r][c+1].getMode().indexOf("water") != -1 &&
                            !arr[r-1][c+1].getMode().equals("water"))
                        str += "BL";
                    if (r+1 < arr.length && arr[r+1][c].getMode().indexOf("water") != -1 && c+1 < arr[0].length && arr[r][c+1].getMode().indexOf("water") != -1 &&
                            !arr[r+1][c+1].getMode().equals("water"))
                        str += "BR";
                    if (r+1 < arr.length && arr[r+1][c].getMode().indexOf("water") != -1 && c-1 > - 1 && arr[r][c-1].getMode().indexOf("water") != -1 &&
                            !arr[r+1][c-1].getMode().equals("water"))
                        str += "TR";
                    if (r-1 > -1 && arr[r-1][c].getMode().indexOf("water") != -1 && c-1 > - 1 && arr[r][c-1].getMode().indexOf("water") != -1 &&
                            !arr[r-1][c-1].getMode().equals("water"))
                        str += "TL";
                }

                Image picture = t.getImage(str+ ".jpg");
                g.drawImage(picture, arr[r][c].getPosX()-BOXSIZE/2,arr[r][c].getPosY()-BOXSIZE/2,BOXSIZE,BOXSIZE,this);

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
        Tester t = new Tester();
        JFrame jf = new JFrame();
        jf.setTitle("Tester");
        jf.setSize(400+12,400+35);
        t.setBackground(Color.WHITE);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(t);
    }
}