import java.util.Random;

public class Map {

    private final int height = 30;
    private final int width = 60;

    //Tvådimensionell int-array som representerar kartans olika siffror ie miljö;
    public int[][] environment;
    public int rock = 0;
    public int tile = 1;
    public int exit = 9;

    public Position startDigHere;

    //Random generator
    Random random = new Random();

    //Constructor
    public Map(){
        fillMapWithRock();
        findsAndDigsOutStartPosition();
    }

    //Fyller kartan med sten, initierar kart-arrayen
    public void fillMapWithRock() {
        environment = new int[height][width];
        for (int i = 0; i < environment.length; i++) {
            for (int j = 0; j < environment[i].length; j++) {
                environment[i][j] = rock;
            }
        }
    }

    //Randomiserar startposition på kartan, ej på kartans yttre gränser
    public void findsAndDigsOutStartPosition(){
        startDigHere = new Position();
        startDigHere.setX(random.nextInt(width-2)+1);
        startDigHere.setY(random.nextInt(height-2)+1);
        environment[startDigHere.getY()][startDigHere.getX()]=tile;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
