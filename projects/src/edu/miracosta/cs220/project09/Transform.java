public class Transform {
    /** 
     * +-------------------------------+
     * |                    +--     --+|
     * |                    | a  b  0 ||
     * |[x y 1] = [x y 1] * | c  d  0 ||
     * |                    | tx ty 1 ||
     * |                    +--     --+|
     * +-------------------------------+
     */

    private Float[] matrix; 
    public Transform(Float[] matrix) {
        this.matrix = matrix; // [0=a, 1=b, 2=c, 3=d, 4=tx, 5=ty]
    }

    public void translate(Float tx, Float ty) {
        matrix[4].add(tx);
        matrix[5].add(ty);
    }

    public void rotate(Float theta) {

    }

    public void scale(Float sx, Float sy) {
        matrix[0].mult(sx);
        matrix[1].mult(sx);
        matrix[2].mult(sy);
        matrix[3].mult(sy);
    }

    public Point apply(Point point) {
        Point newPoint = new Point(new Float(0,0), new Float(0,0));
        
        Float aBYx = point.getX().copy();
        aBYx.mult(matrix[0]);
        Float bBYx = point.getX().copy();
        bBYx.mult(matrix[1])
        Float cBYy = point.getY().copy();
        cBYy.mult(matrix[2]);
        Float dBYy = point.getY().copy();
        dBYy.mult(matrix[3]);

        newPoint.getX().add(aBYx);
        newPoint.getX().add(cBYy);
        newPoint.getX().add(matrix[4]);

        newPoint.getY().add(bBYx);
        newPoint.getY().add(dBYy);
        newPoint.getY().add(matrix[5]);

        // Destroy: aBYx, bBYx, cBYy, dBYy

        return newPoint;
    }
}
