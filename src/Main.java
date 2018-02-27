public class Main {
    public static void main(String[] args) throws InterruptedException {

        Game game = new Game();

        game.newGame();
        game.updateMap();

        while (true) {
            while (!game.gameOver()) {
                game.movePlayer();
                if(!game.playerIsOnExit()){
                    game.moveMonsters();
                    game.updateMap();
                } else if(game.playerIsOnExit()){
                    game.newGame();
                    game.updateMap();
                }
            }
        }
    }
}
