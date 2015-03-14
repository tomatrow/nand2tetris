package edu.miracosta.cs220.project07and08;

public class Triple<X, Y, Z> { 
    public final X x; 
    public final Y y; 
    public final Z z; 
  
    public Triple(X x, Y y, Z z) { 
        this.x = x; 
        this.y = y; 
        this.z = z;
    } 

    public String toString() {
        return String.format("(%s, %s, %s)", x, y, z);
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        } else if (other == this) {
            return true;
        }
        
        Triple<X, Y, Z> otherTriple = (Triple<X, Y, Z>) other;
        boolean sameX = otherTriple.x.equals(this.x);
        boolean sameY = otherTriple.y.equals(this.y);
        boolean sameZ = otherTriple.z.equals(this.z);

        return sameX && sameY && sameZ;
    }

    public int hashCode() {
        return x.hashCode() ^ y.hashCode() ^ z.hashCode();
    }
} 
