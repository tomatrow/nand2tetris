package edu.miracosta.cs220.project09;

public class Float {
    private short mantissa;
    private short exponent;

    public Float(short mantissa, short exponent) {
        this.mantissa = mantissa;
        this.exponent = exponent;
    }

    public short getExponent() {
        return exponent;
    }

    public short getMantissa() {
        return mantissa;
    }

    public String toString() {
        return "(M:" + mantissa + ", E:" + exponent + ")";
    }

    public void add(Float f) {
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

    public void sub(Float f) {
        add(new Float((short)-f.getMantissa(), f.getExponent()));
    }

    public void mult(Float f) {
        this.exponent += f.getExponent();
        this.mantissa *= f.getMantissa();
        normalize();
    }

    public void div(Float f) {
        this.exponent -= f.getExponent();
        this.mantissa /= f.getMantissa();
        normalize();
    }

    public void normalize() {
        while (Math.abs(mantissa) > 9999) {
            mantissa /= 10;
            exponent++;
        }
    }

    public short toShort() {
        return (short)((short)this.mantissa * (short)Math.pow(10, this.exponent));
    }

    public Float copy() {
        return new Float(this.mantissa, this.exponent);
    }
}
