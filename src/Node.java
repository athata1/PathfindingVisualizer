import java.awt.*;

public class Node
{
    private int posX;
    private int posY;
    private Color color;
    private int x;
    private int y;
    private int g;
    private int h;
    private String mode;
    public Node(int x, int y)
    {
        posX = x;
        posY = y;
        color = Color.WHITE;
        g = Integer.MAX_VALUE;
        mode = "grass";
    }
    public void setHCost(int currentX, int currentY, int endX, int endY) {
        this.h = Math.abs(endX-currentX)*10 + Math.abs(endY-currentY)*10;
    }
    public String getMode()
    {
        return mode;
    }
    public void setMode(String s)
    {
        mode = s;
    }
    public int getPosX()
    {
        return posX;
    }
    public int getPosY()
    {
        return posY;
    }
    public void setColor(Color c)
    {
        color = c;
    }
    public Color getColor()
    {
        return color;
    }
    public void connectsTo(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public int getAttachedArrX()
    {
        return x;
    }
    public int getAttachedArrY()
    {
        return y;
    }
    public int findDist(int posX, int posY, int x, int y)
    {
        return (int)(Math.sqrt(Math.pow(((posX-x)*10),2) + Math.pow((posY - y)*10,2))*10);
    }
    public void setGHCost(int g, int currentX, int currentY, int endX, int endY, int x, int y)
    {
        if (g < this.g)
        {
            this.g = g;
            h = Math.abs(endX-currentX)*10 + Math.abs(endY-currentY)*10;
            connectsTo(x,y);
        }
    }
    public void setGCost(int g)
    {
        this.g = g;
    }
    public int getFCost()
    {
        return g+h;
    }
    public int getGCost()
    {
        return g;
    }
    public int getHCost()
    {
        return h;
    }
}