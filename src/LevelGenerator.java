import com.googlecode.lanterna.terminal.Terminal;

import java.util.*;

public class LevelGenerator {

    Map map;

    //Directions
    private final int[] left = {0, -1};
    private final int[] right = {0, 1};
    private final int[] up = {-1, 0};
    private final int[] down = {1, 0};
    private final int[][] directions = {up, down, left, right};

    //Percentage of map filled with tiles
    private final double filledPercentage = 0.4;
    //Total tunnel length
    private int stepLength;

    public Random random = new Random();

    //Terminal
    public Terminal terminal;

    //Constructor
    public LevelGenerator(Terminal terminal) {
        this.terminal = terminal;
        map = new Map();
        stepLength = (int) (map.getHeight() * map.getWidth() * filledPercentage);
        digTunnels(map.startDigHere);
        setExit();
    }

    //Gräver ut tunnlar och börjar på en viss position
    public void digTunnels(Position position) {

        //Där grävaren står just nu
        int currentY = position.getY();
        int currentX = position.getX();


        int[] lastDirection = {5, 5};

        //Upprepa tills x antal rutor grävts ut
        while (stepLength > 0) {
            //Randomisera en riktning
            int[] randomDirection = randomiseTunnelDirection(lastDirection);
            lastDirection = randomDirection;


            //Gå ett steg i den randomiserade riktningen
            int newPositionY = currentY + randomDirection[0];
            int newPositionX = currentX + randomDirection[1];


            //Om han går in i en vägg, slumpa ut ny position och kontrollera om den redan är utgrävd
            if (hitWall(newPositionY, newPositionX)) {
                digTunnels(getRandomAlreadyVisitedPosition());
            } else {
                //Gå ett steg i den randomiserade riktningen
                currentY = newPositionY;
                currentX = newPositionX;
                //Gräv ut på den nya platsen om den är fylld med sten, och isåfall minska steglängden
                if (map.environment[currentY][currentX] == map.rock) {
                    map.environment[currentY][currentX] = map.tile;
                    stepLength--;
                }
            }
        }
    }

    //Randomiserar tunnelriktningen
    public int[] randomiseTunnelDirection(int[] lastDirection) {
        if (random.nextInt(10) >= 3 && (lastDirection[0] != 5 && lastDirection[1] != 5)) {
            return lastDirection;
        } else {
            int[] newDirection;
            do {
                newDirection = directions[random.nextInt(4)];
            } while (newDirection[0] == -lastDirection[0] && newDirection[1] == -newDirection[1]);
            return newDirection;
        }
    }

    public int[] randomiseDirection() {
        int[] randomDirection = directions[random.nextInt(4)];
        return randomDirection;
    }

    //Metod som kontrollerar om current position med nya radnom direction krockar med en vägg
    public boolean hitWall(int newPositionY, int newPositionX) {
        if (newPositionX <= 0 || newPositionX >= map.getWidth() - 1 || newPositionY <= 0 || newPositionY >= map.getHeight() - 1) {
            return true;
        }
        return false;
    }

    //Slumpar fram en random position på spelplanen
    public Position randomPosition() {
        Position randomPosition = new Position();
        randomPosition.setX(random.nextInt(map.getWidth() - 2) + 1);
        randomPosition.setY(random.nextInt(map.getHeight() - 2) + 1);
        return randomPosition;
    }

    //Slumpar fram en utgrävd position på spelplanen
    public Position getRandomAlreadyVisitedPosition() {
        //Slumpa ut en ny position
        Position newPosition;
        do {
            newPosition = randomPosition();
        }
        while (map.environment[newPosition.getY()][newPosition.getX()] != map.tile); //Kollar om den redan är utgrävd
        return newPosition;
    }

    //Metod som slumpar fram ett exit, 9 = exit
    public void setExit() {
        Position emptyPosition = getRandomAlreadyVisitedPosition();
        map.environment[emptyPosition.getY()][emptyPosition.getX()] = map.exit;
    }

}


