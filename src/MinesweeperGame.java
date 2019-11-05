import com.javarush.engine.cell.*;

import java.util.ArrayList;

public class MinesweeperGame extends Game{
    private int countMinesOnField;
    private final static int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private static final String MINE = "\uD83D\uDCA3"; //значек мины
    private static final String FLAG = "\uD83D\uDEA9"; //значек флага
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    public void initialize(){
        setScreenSize(SIDE, SIDE);
        createGame();
    }


    private void createGame(){
        for (int i = 0; i < gameField.length; i++){
            for(int j = 0; j < gameField[i].length; j++){
                gameField[j][i] = new GameObject(i, j, false);
                setCellColor(j, i, Color.ORANGE);
                if (getRandomNumber(10) < 1){
                    gameField[j][i].isMine = true;
                    countMinesOnField++;
                }
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
        for (int i = 0; i < SIDE; i++){
            for (int j = 0; j < SIDE; j++){
                setCellValue(j, i, "");
            }
        }
    }


    //находим соседей текущей €чейки
    private ArrayList<GameObject> getNeighbors(GameObject obj){
        ArrayList<GameObject> neighbors = new ArrayList<>();
        int x = obj.x;
        int y = obj.y;

        for (int i = obj.y - 1; i <= obj.y + 1; i++){
            for (int j = obj.x - 1; j <= obj.x + 1; j++){
                if ((j != obj.y || i != obj.x) && (j >= 0 && i >= 0 && j < SIDE && i <SIDE)){
                    neighbors.add(gameField[i][j]);
                }
            }
        }
        return neighbors;
    }


    //определ€ем заминировынных соседей
    private void countMineNeighbors(){
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {
                if (!gameField[y][x].isMine) {
                    ArrayList<GameObject> list = getNeighbors(gameField[y][x]);

                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isMine) {
                            gameField[y][x].countMineNeighbors++;
                        }
                    }
                }
            }
        }

    }


    //реагируем на левый клик мышкой
    public void onMouseLeftClick(int x, int y){

        if (isGameStopped) {
            restart();
        }else openTile(x, y);
    }


    public void onMouseRightClick(int x, int y){

        markTile(x, y);
    }

    //окрашиваем €чейку в зеленый цвет при открытии. если там мина - рисуем значек мина
    //если нет - количество соседей-мин.

    private void openTile(int x, int y) {
        //если мина, то открываем €чейку, ставим знак мина и красим в красный цвет

        if (gameField[y][x].isOpen ||gameField[y][x].isFlag || isGameStopped) return;

        if (gameField[y][x].isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
            gameField[y][x].isOpen = true;
        }
        //если не мина и значение 0, то открываем, красим в зеленый цвет, вместо 0 ничего не ставим,
        //провер€ем всех соседей по той же схеме (проходим циклом по сосед€м €чейки и дл€
        // каждой делаем ту же операцию (включаем рекурсию)
        else if (!gameField[y][x].isMine && gameField[y][x].countMineNeighbors == 0) {

            gameField[y][x].isOpen = true;
            countClosedTiles--;
            score += 5;
            setCellColor(x, y, Color.GREEN);
            setCellValue(x, y, "");
            ArrayList<GameObject> list = getNeighbors(gameField[y][x]);
            for (GameObject ob : list) {
                if (!ob.isOpen) {
                    setCellColor(x, y, Color.GREEN);
                    countClosedTiles--;
                    score += 5;
                    openTile(ob.x, ob.y);
                    ob.isOpen = true;
                            /*countClosedTiles--;
                            score += 5;*/
                }
            }

        } else if (!gameField[y][x].isMine && gameField[y][x].countMineNeighbors != 0) {
            setCellColor(x, y, Color.GREEN);
            setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            gameField[y][x].isOpen = true;
            countClosedTiles--;
            score += 5;
        }
        setScore(score);

        if(countClosedTiles == countMinesOnField){
            win();
        }
    }


    private void markTile ( int x, int y){
        if (gameField[y][x].isOpen) return;
        if (isGameStopped) return;
        if (countFlags > 0) {
            if (!gameField[y][x].isOpen & !gameField[y][x].isFlag) {
                gameField[y][x].isFlag = true;
                countFlags--;
                countClosedTiles--;
                setCellValue(x, y, FLAG);
                setCellColor(x, y, Color.YELLOW);

            } else if (!gameField[y][x].isOpen & gameField[y][x].isFlag) {
                gameField[y][x].isFlag = false;
                countFlags++;
                countClosedTiles++;
                setCellValue(x, y, "");
                setCellColor(x, y, Color.ORANGE);
            }
        }
    }


    private void gameOver () {
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "Game over!", Color.RED, 50);
    }


    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.GOLD, "YOU ARE WINNER!", Color.DARKBLUE, 50);
    }


    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        createGame();
        setScore(0);
    }

}