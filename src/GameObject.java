public class GameObject {
    public int x, y;
    public boolean isMine = false;
    public int countMineNeighbors;
    public boolean isOpen;
    public boolean isFlag;

    public GameObject(int x, int y, boolean isMine){
        this.x = x;
        this.y = y;
        this.isMine = isMine;
    }
}
