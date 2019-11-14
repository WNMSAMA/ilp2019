package uk.ac.ed.inf.powergrab;

public class Station {
    private final String id;
    private double coins;
    private double power;
    public enum Symbol{
        DANGER,LIGHTHOUSE,DEAD
    }
    private Symbol symbol;
    private final Position position;
    public Station(String id, double coins , double power,Symbol symbol,Position position) {
        this.id = id;
        this.coins = coins;
        this.power = power;
        this.symbol = symbol;
        this.position = position;
    }
    public double getCoins() {
        return coins;
    }
    public void setCoins(double coins) {
        this.coins = coins;
    }
    public double getPower() {
        return power;
    }
    public void setPower(double power) {
        this.power = power;
    }
    public Symbol getSymbol() {
        return symbol;
    }
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
    public Position getCorrdinate() {
        return this.position;
    }
    @Override
    public String toString() {
        return String.format("id = %s , coins = %s , power = %s , status = %s , position = %s",id,coins,power,symbol.toString(),position.toString());
    }

}