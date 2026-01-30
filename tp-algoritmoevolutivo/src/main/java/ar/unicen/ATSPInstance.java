package ar.unicen;

public class ATSPInstance {
    private final String name;
    private final int dimension;
    private final int[][] cost;

    public ATSPInstance(String name, int dimension, int[][] cost) {
        this.name = name;
        this.dimension = dimension;
        this.cost = cost;
    }

    public String getName() { return name; }
    public int getDimension() { return dimension; }
    public int[][] getCost() { return cost; }
}
