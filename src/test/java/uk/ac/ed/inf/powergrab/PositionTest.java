package uk.ac.ed.inf.powergrab;
public class PositionTest {
    public static void main(String[] args) {
        Position p = new Position(55.946233,-3.192473);
        Position n = p.nextPosition(Direction.S);
        System.out.println(n.latitude);
        System.out.println(n.longitude);
        System.out.print(n.inPlayArea());
    }
}
