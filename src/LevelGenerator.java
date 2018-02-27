import com.googlecode.lanterna.terminal.Terminal;

import java.util.*;

public class LevelGenerator {

    private static int levelOfDungeon = 0; //startnivå 1
    public Map map;

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
    public LevelGenerator(Terminal terminal) throws InterruptedException {
        this.terminal = terminal;

        map = new Map();
        stepLength = (int) (map.getHeight() * map.getEnvironmentWidth() * filledPercentage);
        digTunnels(map.startDigHere);
        setExit();
    }

    //Gräver ut tunnlar och börjar på en viss position
    public void digTunnels(Position position) throws InterruptedException {

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
            updateMap();
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
        if (newPositionX <= 0 || newPositionX >= map.getEnvironmentWidth() - 1 || newPositionY <= 0 || newPositionY >= map.getHeight() - 1) {
            return true;
        }
        return false;
    }

    //Slumpar fram en random position på spelplanen
    public Position randomPosition() {
        Position randomPosition = new Position();
        randomPosition.setX(random.nextInt(map.getEnvironmentWidth() - 2) + 1);
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
    //För att se hur dungeon grävs ut
    public void updateMap() throws InterruptedException {
        printEnvironment();
        printStatisticsArea();
        Thread.sleep(2);
    }

    public void printEnvironment() {
        terminal.applyForegroundColor(189, 60, 40);
        for (int i = 0; i < map.environment.length; i++) {
            for (int j = 0; j < map.environment[i].length; j++) {
                terminal.moveCursor(j, i);
                if (map.environment[i][j] == 0) { //rock
                    terminal.applyForegroundColor(189, 60, 40);
                    terminal.putCharacter('\u2588');
                } else if (map.environment[i][j] == 9) { //exit sign
                    terminal.applyForegroundColor(23, 104, 122);
                    terminal.putCharacter('E');
                } else { //utgrävd
                    terminal.applyForegroundColor(189, 60, 40);
                    terminal.putCharacter('\u00B7');
                }
            }
        }
    }

    public void printStatisticsArea() {
        //Löper igenom statistics-arrayen från y = 0, till y = map height; och från x = map width, till x = statistics map width
        for (int i = 0; i < map.getHeight(); i++) {
            for (int j = map.getEnvironmentWidth(); j < map.getEnvironmentWidth()+map.getStatisticsWidth(); j++) {
                terminal.moveCursor(j, i);
                if (map.statistics[i][j] == 1) { //utanför environment-map
                    terminal.applyForegroundColor(255, 255, 255);
                    terminal.putCharacter(' ');
                } else if (map.statistics[i][j] == 2) { //ramen
                    terminal.applyForegroundColor(189, 60, 40);
                    terminal.putCharacter('\u2588');
                }
            }
        }
    }

    public static void setLevelOfDungeon(int levelIncrease) {
        levelOfDungeon = levelOfDungeon+levelIncrease;
    }

    public static int getLevelOfDungeon() {
        return levelOfDungeon;
    }

}


