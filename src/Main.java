public class Main {
    public static void main(String[] args) throws InterruptedException {

        Game game = new Game();

        //To do

        while (true) {
            game.newGame();
            game.updateMap();
            while (!game.gameOver()) {
                game.movePlayer();
                if(game.isOnExit()){
                    break;
                }
                game.moveMonsters();
                game.updateMap();
            }
        }
    }
}
