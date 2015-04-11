package edu.miracosta.cs220.project09;

import java.util.Arrays;

class Transform {
    // static final Float pi = new Float((short)3141, (short)-3);
    // static final Float negPI = new Float((short)-3141, (short)-3);
    // static final Float doublePI = new Float((short)6283, (short)-3);
    // static final Float halfPI = new Float((short)1571, (short)-3);

    // static final Float term0coeffecient = new Float((short)1273, (short)-3);
    // static final Float term1coeffecient = new Float((short)4053, (short)-4);

    static final Float[] identity = {Float.one.copy(), Float.zero.copy(), Float.zero.copy(), Float.one.copy(), Float.zero.copy(), Float.zero.copy()};

    /** 
     * +-------------------------------+
     * |  new   =   old   *  transform |
     * |                    +--     --+|
     * |                    | a  b  0 ||
     * |[x y 1] = [x y 1] * | c  d  0 ||
     * |                    | tx ty 1 ||
     * |                    +--     --+|
     * +-------------------------------+
     */

    Float[] matrix; 
    Float a,b,c,d,tx,ty;

    Transform(Float[] matrix) {
        this.matrix = matrix; // [0=a, 1=b, 2=c, 3=d, 4=tx, 5=ty]
        a = matrix[0];

    }

    void translate(Float txNew, Float tyNew) {
        // newTX = a*tx + c*ty + oldTTX
        // Float aBYtx = a.copy();
        // aBYtx.mult(tx);
        // Float cBYty = c.copy();
        // cBYty.mult(ty);
        // tx.add(aBYtx);
        // tx.add(cBYty);

        // // newTY = b*tx + d*ty + ty 
        // Float bBYtx = b.copy();
        // bBYtx.mult(tx);
        // Float dBYty = d.copy();
        // dBYty.mult(ty);
        // ty.add(bBYtx);
        // ty.add(dBYty);

        tx.add(txNew);
        ty.add(tyNew);

        // destroy aBYtx, cBYty, bBYtx, dBYty
    }

    /**
      * #--------------------------#
      * |    rotate    *   this    |
      * | +--      --+   +--   --+ |
      * | | cos sin 0|   | a  b 0| |
      * | |-sin cos 0| * | c  d 0| |
      * | |   0   0 1|   |tx ty 1| |
      * | +--      --+   +--   --+ |
      * #--------------------------#
      * Computes [ quadratic approximations of sin and cos](http://lab.polygonal.de/?p=205)
      * => this *= rotate  
      */
    /* // way too much work 
    void rotate(Float theta) {
        Float alpha = theta.copy();
        if (alpha.lessThan(negPI)) {
            alpha.add(doublePI);
        } else if (alpha.greaterThan(pi)) {
            alpha.sub(doublePI);
        }

        // sine 
        Float sin = term0coeffecient.copy();
        sin.mult(alpha);
        Float sinTerm1 = term1coeffecient.copy();
        sinTerm1.mult(alpha);
        sinTerm1.mult(alpha);
        if (alpha.lessThan(Float.zero.copy())) {
            sin.add(sinTerm1);
        } else {
            sin.sub(sinTerm1);
        }

        // cos
        alpha.add(halfPI);
        if (alpha.greaterThan(pi)) {
            alpha.sub(doublePI);
        }

        Float cos = term0coeffecient.copy();
        cos.mult(alpha);
        Float cosTerm1 = term1coeffecient.copy();
        cosTerm1.mult(alpha);
        cosTerm1.mult(alpha);

        if (alpha.lessThan(Float.zero.copy())) {
            cos.add(cosTerm1);
        } else {
            cos.sub(cosTerm1);
        }

        // Needed values for rotation 
        Float cBYsin = c.copy();
        cBYsin.mult(sin);
        Float dBYsin = d.copy();
        dBYsin.mult(sin);

        sin.neg();

        Float aBYsin = a.copy();
        aBYsin.mult(sin);
        sin.mult(b); // sin == bBYsin

        // [0=a, 1=b, 2=c, 3=d, 4=tx, 5=ty]
        // Applying the rotation 
        int index = 0;
        while (index < 4) {
            matrix[index].mult(cos);
        }

        // a
        a.add(cBYsin);
        // b
        b.add(dBYsin);
        // c
        c.add(aBYsin);
        // d
        d.add(sin); // sin == bBYsin

        // destroy alpha, sin, sinTerm1, cosTerm1, cos, cBYsin, dBYsin, aBYsin
    }
    */

    void scale(Float sx, Float sy) {
        a.mult(sx);
        b.mult(sx);
        c.mult(sy);
        d.mult(sy);
    }

    void apply(Rect rect) {
        apply(rect.getOrigin().getX(), rect.getOrigin().getY());
        apply(rect.getSize());
    }

    void apply(Float x, Float y) {
        Float cBYy = y.copy();
        cBYy.mult(c);
        Float bBYx = x.copy();
        bBYx.mult(b);

        y.mult(d);
        y.add(bBYx);
        y.add(ty);

        x.mult(a);
        x.add(cBYy);
        x.add(tx);

        // destroy cBYy, bBYx
    }

    void apply(Size size) {
        Float cBYy = size.getHeight().copy();
        cBYy.mult(c);
        Float bBYx = size.getWidth().copy();
        bBYx.mult(b);
        
        size.getHeight().mult(d);
        size.getHeight().add(bBYx);

        size.getWidth().mult(a);
        size.getWidth().add(cBYy);
    }

    public String toString() {
        return Arrays.toString(matrix);
    }
}
