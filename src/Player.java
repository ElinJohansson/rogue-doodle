public class Player {
    public Position position;

    public Player(Position startPosition){
        this.position = startPosition;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        this.position.setX(x);
        this.position.setY(y);
    }
}
