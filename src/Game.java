import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.terminal.Terminal;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class Game {

    public LevelGenerator levelGenerator;
    public Player player;

    //Monstervariabler
    public Monster[] monsters;
    private int monstersAtGameStart = 4;

    public Terminal terminal;

    //Constructor
    public Game() {
        terminal = TerminalFacade.createTerminal(System.in, System.out, Charset.forName("UTF8"));
        terminalSettings();
    }

    public void newGame() {
        levelGenerator = new LevelGenerator(terminal);
        //Lägg till spelare
        player = new Player(levelGenerator.map.startDigHere);
        //Lägg till monster
        monsters = new Monster[monstersAtGameStart];
        for (int i = 0; i < monsters.length; i++) {
            monsters[i] = new Monster(levelGenerator.getRandomAlreadyVisitedPosition());
        }
    }

    private void terminalSettings() {
        terminal.enterPrivateMode();
        terminal.applyBackgroundColor(189, 132, 40);
        terminal.setCursorVisible(false);
    }

    public void updateMap() {
        printEnvironment();
        printPlayer();
        printMonster();
    }

    public void printEnvironment() {
        terminal.applyForegroundColor(189, 60, 40);
        for (int i = 0; i < levelGenerator.map.environment.length; i++) {
            for (int j = 0; j < levelGenerator.map.environment[i].length; j++) {
                terminal.moveCursor(j, i);
                if (levelGenerator.map.environment[i][j] == 0) {
                    terminal.applyForegroundColor(189, 60, 40);
                    terminal.putCharacter('\u2588');
                } else if (levelGenerator.map.environment[i][j] == 9) {
                    terminal.applyForegroundColor(23, 104, 122);
                    terminal.putCharacter('E');
                } else {
                    terminal.applyForegroundColor(189, 60, 40);
                    terminal.putCharacter('\u00B7');
                }
            }
        }
    }

    public void printPlayer() {
        terminal.moveCursor(player.getPosition().getX(), player.getPosition().getY());
        terminal.applyForegroundColor(23, 104, 122);
        terminal.putCharacter('\u263B');
    }

    public void printMonster() {
        for (int i = 0; i < monsters.length; i++) {
            terminal.moveCursor(monsters[i].getPosition().getX(), monsters[i].getPosition().getY());
            terminal.applyForegroundColor(51, 27, 171);
            terminal.putCharacter('\u046A');
        }
    }

    //Metod som flyttar spelaren
    public void movePlayer() throws InterruptedException {
        Key key;
        do {
            Thread.sleep(5);
            key = terminal.readInput();
        }
        while (key == null);

        int x = player.getPosition().getX();
        int y = player.getPosition().getY();

        Position newPosition = new Position();

        //Vilken knapp som trycks ner
        switch (key.getKind()) {
            case ArrowDown:
                newPosition.setY(y + 1);
                newPosition.setX(x);
                if (!hitEnvironment(newPosition) && !hitWall(newPosition)) {
                    player.setPosition(newPosition.getX(), newPosition.getY());
                    break;
                } else {
                    break;
                }
            case ArrowUp:
                newPosition.setY(y - 1);
                newPosition.setX(x);
                if (!hitEnvironment(newPosition) && !hitWall(newPosition)) {
                    player.setPosition(newPosition.getX(), newPosition.getY());
                    break;
                } else {
                    break;
                }
            case ArrowLeft:
                newPosition.setX(x - 1);
                newPosition.setY(y);
                if (!hitEnvironment(newPosition) && !hitWall(newPosition)) {
                    player.setPosition(newPosition.getX(), newPosition.getY());
                    break;
                } else {
                    break;
                }
            case ArrowRight:
                newPosition.setX(x + 1);
                newPosition.setY(y);
                if (!hitEnvironment(newPosition) && !hitWall(newPosition)) {
                    player.setPosition(newPosition.getX(), newPosition.getY());
                    break;
                } else {
                    break;
                }
        }
    }

    //Monster ska röra sig mot spelaren
    public void moveMonsters() {
        int iterator = 0;

        for (Monster mon : monsters) {
            //Hämta monstrets position
            int x = monsters[iterator].getPosition().getX();
            int y = monsters[iterator].getPosition().getY();
            //Lagra monstrets gamla position
            int oldX = x;
            int oldY = y;

            if (monsters[iterator].getPosition().getX() > player.getPosition().getX()) {
                monsters[iterator].getPosition().setX(--x);
            }
            if (monsters[iterator].getPosition().getX() < player.getPosition().getX()) {
                monsters[iterator].getPosition().setX(++x);
            }
            if (monsters[iterator].getPosition().getY() > player.getPosition().getY()) {
                monsters[iterator].getPosition().setY(--y);
            }
            if (monsters[iterator].getPosition().getY() < player.getPosition().getY()) {
                monsters[iterator].getPosition().setY(++y);
            }
            while (hitWall(monsters[iterator].getPosition()) || hitEnvironment(monsters[iterator].getPosition()) || isMonsterOnMonster()) {
                int[] monsterDirection = levelGenerator.randomiseDirection();
                monsters[iterator].getPosition().setY(oldY + monsterDirection[0]);
                monsters[iterator].getPosition().setX(oldX + monsterDirection[1]);
            }
            iterator++;
        }
    }

    //Kontrollerar om någon går in i sten/miljön
    public boolean hitEnvironment(Position position) {
        if (levelGenerator.map.environment[position.getY()][position.getX()] == 0) {
            return true;
        }
        return false;
    }

    //Metod som kontrollerar om current position krockar med yttre väggar
    public boolean hitWall(Position position) {
        if (position.getX() <= 0 || position.getX() >= levelGenerator.map.getWidth() - 1 || position.getY() <= 0
                || position.getY() >= levelGenerator.map.getHeight() - 1) {
            return true;
        }
        return false;
    }

    public boolean isMonsterOnMonster() {
        Set<String> monsterPositionString = new HashSet<>();
        for (int i = 0; i < monsters.length; i++) {
            String s = "";
            s = "x:" + monsters[i].getPosition().getX() + "y:" + monsters[i].getPosition().getY();
            if (monsterPositionString.contains(s)) {
                return true;
            }
            monsterPositionString.add(s);
        }
        return false;
    }

    //Metod som kontrollerar om spelaren står på exit
    public boolean playerIsOnExit() {
        if (levelGenerator.map.environment[player.getPosition().getY()][player.getPosition().getX()] == 9) { //Står på exit point
            return true;
        }
        return false;
    }

    //När monster och spelare är på samma position är spelet slut
    public boolean gameOver() {
        for (int i = 0; i < monsters.length; i++) {
            if (monsters[i].getPosition().getY() == player.getPosition().getY() &&
                    monsters[i].getPosition().getX() == player.getPosition().getX()) {
                printGameOver();
                return true;
            }
        }
        return false;
    }

    //Om game over ska game over skrivas ut på skärmen
    public void printGameOver() {
        String gameOver = "Game Over";
        int xPos = 75;
        int yPos = 5;
        for (int i = 0; i < gameOver.length(); i++) {
            terminal.moveCursor(xPos, yPos);
            terminal.putCharacter(gameOver.charAt(i));
            xPos = xPos + 1;
        }
    }

}
