package edu.miracosta.cs220.project09;

class Float {
    static final Float zero = new Float((short)0, (short)0);
    static final Float one = new Float((short)1, (short)0);

    short mantissa;
    short exponent;

    Float(short mantissa, short exponent) {
        this.mantissa = mantissa;
        this.exponent = exponent;
    }

    short getExponent() {
        return exponent;
    }
 
    short getMantissa() {
        return mantissa;
    }

    public String toString() {
        return "(M:" + mantissa + ", E:" + exponent + ")";
    }

    void add(Float f) {
        short otherExp = f.getExponent();
        short otherMan = f.getMantissa();
        short exDiff = (short)(this.exponent - otherExp);

        // no point in adding if they're too far apart
        if (Math.abs(exDiff) > 4) {
            if (exDiff > 0) { 
                this.mantissa = otherMan;
                this.exponent = otherExp;
            } 
            return;
        }

        if (exDiff > 0) { // this is greater than that 
            while (otherExp < exponent) {
                otherExp++;
                otherMan /= 10;
            }
            this.mantissa += otherMan;
        } else if (exDiff < 0){ // that is greater than this 
            while (exponent < otherExp) {
                exponent++;
                mantissa /= 10;
            }
            this.mantissa += otherMan;
            normalize();
        } else { // they are equal
            this.mantissa += otherMan;
        }
    }

    void sub(Float f) {
        Float neg = f.copy();
        neg.neg();
        add(neg);
        // destroy neg 
    }

    void mult(Float f) {
        this.exponent += f.getExponent();
        this.mantissa *= f.getMantissa();
        normaenlize();
    }

    void div(Float f) {
        this.exponent -= f.getExponent();
        this.mantissa /= f.getMantissa();
        normalize();
    }

    void normalize() {
        while (Math.abs(mantissa) > 9999) {
            mantissa /= 10;
            exponent++;
        }
    }

    void neg() {
        this.mantissa *= (short)-1;
    }

    // boolean lessThan(Float f) {
    //     short otherExp = f.getExponent();
    //     short otherMan = f.getMantissa();
    //     short thisMan;
    //     short exDiff = (short)(this.exponent - otherExp);

    //     if (exDiff > 0) { // this is greater than that 
    //         while (otherExp < exponent) {
    //             otherExp++;
    //             otherMan /= 10;
    //         }

    //         return mantissa < otherMan;
    //     } else if (exDiff < 0){ // that is greater than this 
    //         thisMan = mantissa;
    //         while (exDiff < 0) {
    //             exDiff++;
    //             thisMan /= 10;
    //         }

    //         return thisMan < otherMan;
    //     } else { // they are equal
    //         return mantissa < otherMan;
    //     }
    // }

    // boolean greaterThan(Float f) {
    //     return !lessThan(f) && !equals(f);
    // }

    boolean equals(Float f) {
        boolean sameMantissa = mantissa == f.getMantissa();
        boolean sameExponent = exponent == f.getExponent();

        return sameMantissa && sameExponent || sameMantissa && isZero();
    }

    boolean isZero() {
        return mantissa == 0;
    }

    short toShort() {
        return (short)((short)this.mantissa * (short)Math.pow(10, this.exponent));
    }

    Float copy() {
        return new Float(this.mantissa, this.exponent);
    }
}
