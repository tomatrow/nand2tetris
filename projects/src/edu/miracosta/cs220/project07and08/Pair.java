package edu.miracosta.cs220.project07and08;

public class Pair<X, Y> { 
    public final X x; 
    public final Y y; 
  
    public Pair(X x, Y y) { 
        this.x = x; 
        this.y = y; 
    } 
  
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        } else if (other == this) {
            return true;
        }
        
        Pair<X, Y> otherPair = (Pair<X, Y>) other;
        boolean sameX = otherPair.x.equals(this.x);
        boolean sameY = otherPair.y.equals(this.y);

        return sameX && sameY;
    }

    public int hashCode() {
        return x.hashCode() ^ y.hashCode();
    }
} 