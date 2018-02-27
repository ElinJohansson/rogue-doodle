import java.util.Random;

public class Map {

    private final int height = 30;
    private final int environmentWidth = 60;
    private final int statisticsWidth = 40;

    //Tvådimensionell int-array som representerar kartans olika siffror ie miljö;
    public int[][] environment;
    public int rock = 0;
    public int tile = 1;
    public int exit = 9;

    //Tvådimensionell int-array som representerar statistics area
    public int[][] statistics;

    //Position där man börjar gräva
    public Position startDigHere;

    //Random generator
    Random random = new Random();

    //Constructor
    public Map() {
        fillMapWithRock();
        findsAndDigsOutStartPosition();
        initializeStatisticsArea();
    }

    //Fyller kartan med sten, initierar kart-arrayen
    public void fillMapWithRock() {
        environment = new int[height][environmentWidth];
        for (int i = 0; i < environment.length; i++) {
            for (int j = 0; j < environment[i].length; j++) {
                environment[i][j] = rock;
            }
        }
    }

    //Initierar statistics-arrayen, fyller med 0:or där environmentMap går, och 1:or utanför
    public void initializeStatisticsArea() {
        statistics = new int[height][environmentWidth + statisticsWidth];
        //den delen där spelplanen ligger
        for (int i = 0; i < statistics.length; i++) {
            for (int j = 1; j < environment[i].length; j++) {
                statistics[i][j] = 0;
            }
            //den delen där statistics area ligger
            for (int j = environment[i].length; j < statistics[i].length; j++) {
                if (i == 0 || i == height - 1 || j == statistics[i].length-1) { //om räknaren är på ramen
                    statistics[i][j] = 2; //ramen
                } else {
                    statistics[i][j] = 1;
                }
            }
        }
    }

    //Randomiserar startposition på kartan, ej på kartans yttre gränser
    public void findsAndDigsOutStartPosition() {
        startDigHere = new Position();
        startDigHere.setX(random.nextInt(environmentWidth - 2) + 1);
        startDigHere.setY(random.nextInt(height - 2) + 1);
        environment[startDigHere.getY()][startDigHere.getX()] = tile;
    }

    public int getHeight() {
        return height;
    }

    public int getEnvironmentWidth() {
        return environmentWidth;
    }

    public int getStatisticsWidth() {
        return statisticsWidth;
    }
}
