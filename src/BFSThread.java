import java.awt.*;
import java.util.ArrayList;

public class BFSThread extends PathFindingThread implements Runnable {
    Node[][] board;
    int startC;
    int startR;
    int endR;
    int endC;
    int width;
    int height;
    ArrayList<int[]> points;
    public BFSThread(Node[][] board, int startR, int startC, int endR, int endC) {
        this.board = board;
        this.startC = startC;
        this.startR = startR;
        this.endC = endC;
        this.endR = endR;
        this.width = board[0].length;
        this.height = board.length;
    }

    public void run() {
        ArrayList<int[]> currBreath = new ArrayList<int[]>();
        currBreath.add(new int[]{startR,startC});

        while (currBreath.size() != 0) {
            for (int i = 0; i < currBreath.size(); i++) {
                int[] currPoint = currBreath.get(i);
                if (currPoint[0] == endR && currPoint[1] == endC) {
                    createPath(endR, endC);
                    return;
                }
            }

            int[][] vels = {{1,0},
                    {-1,0},
                    {0,1},
                    {0,-1},
                    /*{1,1},
                    {1,-1},
                    {-1,1},
                    {-1,-1}*/};
            for (int i = currBreath.size() - 1; i >= 0; i--) {
                int[] currCords = currBreath.get(i);
                for (int j = 0; j < vels.length; j++) {
                    boolean conditionC = true;
                    boolean conditionR = true;
                    synchronized (boardObj) {
                        conditionR = currCords[0] + vels[j][0] >= 0 && currCords[0] + vels[j][0] < board.length;
                        conditionC = currCords[1] + vels[j][1] >= 0 && currCords[1] + vels[j][1] < board[0].length;
                    }
                    if (conditionC && conditionR) {
                        synchronized (boardObj) {
                            Node n = board[currCords[0] + vels[j][0]][currCords[1] + vels[j][1]];
                            if (n.getColor() != Color.BLACK && n.getColor() != Color.MAGENTA) {
                                n.setColor(Color.MAGENTA);
                                n.connectsTo(currCords[1], currCords[0]);
                                currBreath.add(new int[]{currCords[0] + vels[j][0], currCords[1] + vels[j][1]});
                            }
                        }
                    }
                }
                currBreath.remove(i);
            }
            try {
                Thread.sleep(100);
            }catch (InterruptedException e) {}
        }
    }

    public void createPath(int r, int c)  {

        while (true) {
            synchronized (boardObj) {
                board[r][c].setColor(Color.CYAN);
            }
            if (r == startR && c == startC)
                return;
            int temp = r;
            synchronized (boardObj) {
                r = board[r][c].getAttachedArrY();
            }
            synchronized (boardObj) {
                c = board[temp][c].getAttachedArrX();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {}
        }
    }
}
