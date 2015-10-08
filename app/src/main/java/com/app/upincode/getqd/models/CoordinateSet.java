package com.app.upincode.getqd.models;


public class CoordinateSet {
    public double x;
    public double y;

    public CoordinateSet() {
    }

    public CoordinateSet(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the point's x and y coordinates
     */
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(double x, double y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinateSet coordinateSet = (CoordinateSet) o;

        if (x != coordinateSet.x) return false;
        if (y != coordinateSet.y) return false;

        return true;
    }
}
