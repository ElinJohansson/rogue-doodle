public class Main {
    public static void main(String[] args) throws InterruptedException {

        Game game = new Game();

        game.newGame();


            while (!game.gameOver()) {
                game.movePlayer();
                if (!game.playerIsOnExit()) {
                    game.gameTurn();
                } else if (game.playerIsOnExit()) {
                    game.newGame();
                }
            }

    }
}
