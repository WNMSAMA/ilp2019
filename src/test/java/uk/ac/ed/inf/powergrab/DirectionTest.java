package uk.ac.ed.inf.powergrab;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DirectionTest extends TestCase{
    public DirectionTest(String testName) {
        super( testName );
    }
    public static Test suite() {
        return new TestSuite(DirectionTest.class);
    }
    final Position p0 = new Position(55.944425, -3.188396);

    public void testDirN() {
        assertTrue(Position.nextDirection(p0,new Position(55.944725000000005,-3.188396)) == Direction.N);
    }
    public void testDirE() {
        assertTrue(Position.nextDirection(p0,new Position(55.944425,-3.188096)) == Direction.E);
    }
    public void testDirW() {
        assertTrue(Position.nextDirection(p0,new Position(55.944425,-3.188696)) == Direction.W);
    }
    public void testDirS() {
        assertTrue(Position.nextDirection(p0,new Position(55.944125,-3.188396)) == Direction.S);
    }
    public void testDirNW() {
        assertTrue(Position.nextDirection(p0,new Position(55.94463713203436,-3.188608132034356)) == Direction.NW);
    }
    public void testDirNE() {
        assertTrue(Position.nextDirection(p0,new Position(55.94463713203436,-3.1881838679656442)) == Direction.NE);
    }
    public void testDirSW() {
        assertTrue(Position.nextDirection(p0,new Position(55.944212867965646,-3.188608132034356)) == Direction.SW);
    }
    public void testDirSE() {
        assertTrue(Position.nextDirection(p0,new Position(55.944212867965646,-3.1881838679656442)) == Direction.SE);
    }
    public void testDirNNE() {
        assertTrue(Position.nextDirection(p0,new Position(55.94470216385976,-3.1882811949702905)) == Direction.NNE);
    }
    public void testDirENE() {
        assertTrue(Position.nextDirection(p0,new Position(55.94453980502971,-3.1881188361402466)) == Direction.ENE);
    }
    public void testDirESE() {
        assertTrue(Position.nextDirection(p0,new Position(55.94431019497029,-3.1881188361402466)) == Direction.ESE);
    }
    public void testDirSSE() {
        assertTrue(Position.nextDirection(p0,new Position(55.944147836140246,-3.1882811949702905)) == Direction.SSE);
    }
    public void testDirSSW() {
        assertTrue(Position.nextDirection(p0,new Position(55.944147836140246,-3.1885108050297095)) == Direction.SSW);
    }
    public void testDirWSW() {
        assertTrue(Position.nextDirection(p0,new Position(55.94431019497029,-3.1886731638597534)) == Direction.WSW);
    }
    public void testDirWNW() {
        assertTrue(Position.nextDirection(p0,new Position(55.94453980502971,-3.1886731638597534)) == Direction.WNW);
    }
    public void testDirNNW() {
        assertTrue(Position.nextDirection(p0,new Position(55.94470216385976,-3.1885108050297095)) == Direction.NNW);
    }
    public static void main(String[] args) {
        Position p0 = new Position(55.944425, -3.188396);

        System.out.println(p0.nextPosition(Direction.NW));
    }
}