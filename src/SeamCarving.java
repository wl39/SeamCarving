
import static java.awt.Color.red;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.Math;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import javax.imageio.ImageIO;


/**
 * The seamCarving programme implements an application that simply read
 * image file and return the seam-carved image.
 *
 * @author 160005106, 170005030
 * @since 12.03.2018
 * */
public class SeamCarving {

    public Color[][] rgb;

    private int count = 0;
    private int X;
    private int Y;
    private double[][] energyCalculation;
    private double[][] valueTable;
    private int[][] paths;
    private int[][] seamHistory;
    private int[][] multipleVerticalSeams;
    private int[][] multipleHorizontalSeams;
    /**
     * Constructor
     * Firstly read the user image file and make Color 2D array for each pixel and
     *  make double 2D array for energy calculation.
     *
     * @param file (required) file name.
     * */
    public SeamCarving (String file) throws IOException {

        BufferedImage image = ImageIO.read(new File(file));
        X = image.getWidth();
        Y = image.getHeight();
        seamHistory = new int[Y][X];
        System.out.println(X);
        rgb = new Color[Y][X];

        //Initializing actual value of rgb from image (in this case Test.jpb)
        for(int width = 0; width < X; width++) {
            for(int height = 0; height < Y; height++) {
                rgb[height][width] = new Color(image.getRGB(width, height));
            }
        }




        int width ;
        int height;

        int redEnergyX;
        int greenEnergyX;
        int blueEnergyX;

        int redEnergyY;
        int greenEnergyY;
        int blueEnergyY;

        //Using for corner cases.
        int tempX1;
        int tempX2;
        int tempY1;
        int tempY2;

        double energy;


        energyCalculation = new double[Y][X];

        for(width = 0; width < X; width++) {
            for(height = 0; height < Y; height++) {

                tempX1 = width;
                tempX2 = width;
                tempY1 = height;
                tempY2 = height;

                if (width  == 0) {
                    tempX2 = X; //If width is zero, then tempX2 has to change to last index.
                }
                if (width == X -1) {
                    tempX1 = -1; //If width is last index, then tempX1 has to change to the first index.
                }

                if (height == 0) {
                    tempY2 = Y;
                }
                if (height == Y -1) {
                    tempY1 = -1; //If
                }

                //get each red, green and blue value for calculating energy
                //Calculation way:  √(Δx2(x, y) + Δy2(x, y))
                redEnergyX = (int) Math.pow(rgb[height][tempX1 + 1].getRed() - rgb[height][tempX2 - 1].getRed(), 2);
                greenEnergyX = (int) Math.pow(rgb[height][tempX1 + 1].getGreen() - rgb[height][tempX2 - 1].getGreen(), 2);
                blueEnergyX = (int) Math.pow(rgb[height][tempX1 + 1].getBlue( )- rgb[height][tempX2 - 1].getBlue(), 2);

                redEnergyY = (int) Math.pow(rgb[tempY1 + 1][width].getRed() - rgb[tempY2 - 1][width].getRed(), 2);
                greenEnergyY = (int) Math.pow(rgb[tempY1 + 1][width].getGreen() - rgb[tempY2 - 1][width].getGreen(), 2);
                blueEnergyY = (int) Math.pow(rgb[tempY1 + 1][width].getBlue( )- rgb[tempY2 - 1][width].getBlue(), 2);

                energyCalculation[height][width] = Math.sqrt(redEnergyX + greenEnergyX + blueEnergyX +redEnergyY + greenEnergyY + blueEnergyY);


            }
        }
    }




    /**
     * After removing vertical or horizontal seam,
     * The programme has to re-calculate the energy for finding energy.
     * This is because, the pixels are removed so, previous energy calculation value is wrong.
     * (Exactly same as above)
     *
     * @return new double array of energy calculation result
     * */
    private double[][] calculation() {

        int width ;
        int height;

        int redEnergyX;
        int greenEnergyX;
        int blueEnergyX;

        int redEnergyY;
        int greenEnergyY;
        int blueEnergyY;

        int tempX1;
        int tempX2;
        int tempY1;
        int tempY2;


        boolean negative = false;

        for(height = 0; height < Y; height++) {
            for(width = 0; width < X; width++) {

                if(width == -1) {
                    width = X;
                    negative = true;
                }
                tempX1 = width;
                tempX2 = width;
                tempY1 = height;
                tempY2 = height;

                if (width  == 0) {
                    tempX2 = X;
                }
                if (height == 0) {
                    tempY2 = Y;
                } else if (height == Y -1) {
                    tempY1 = -1;
                }

                if (width == X -1) {
                    tempX1 = -1;
                }

                redEnergyX = (int) Math.pow(rgb[height][tempX1 + 1].getRed() - rgb[height][tempX2 - 1].getRed(), 2);
                greenEnergyX = (int) Math.pow(rgb[height][tempX1 + 1].getGreen() - rgb[height][tempX2 - 1].getGreen(), 2);
                blueEnergyX = (int) Math.pow(rgb[height][tempX1 + 1].getBlue( )- rgb[height][tempX2 - 1].getBlue(), 2);

                redEnergyY = (int) Math.pow(rgb[tempY1 + 1][width].getRed() - rgb[tempY2 - 1][width].getRed(), 2);
                greenEnergyY = (int) Math.pow(rgb[tempY1 + 1][width].getGreen() - rgb[tempY2 - 1][width].getGreen(), 2);
                blueEnergyY = (int) Math.pow(rgb[tempY1 + 1][width].getBlue( )- rgb[tempY2 - 1][width].getBlue(), 2);

                energyCalculation[height][width] = Math.sqrt(redEnergyX + greenEnergyX + blueEnergyX +redEnergyY + greenEnergyY + blueEnergyY);

                if(width == X && negative) {
                    width = -1;
                }
            }
        }
        return energyCalculation;
    }

    /**
     * Making the value table (path or distance table) and the paths table by Dijkstra Algorithm.
     *
     *
     * @return int array which is index number
     * */
    private void dijkstraVertical() {

        valueTable = new double[Y][X];

        for(int y = 0; y < Y; y++) {
            for (int x = 0; x < X; x++)
                valueTable[y][x] = energyCalculation[y][x];
        }

        //-1 left, 0 mid and 1 right
        paths = new int[Y][X];


        //for the first weight
        for (int x = 0; x < X; x++) {
            for (int k = x - 1; k <= x + 1; k++) {
                int path = x - k;
                if (x == 0) {
                    if(k == -1) {
                        continue;//Because k is negative
                    }

                    valueTable[1][k] = valueTable[0][x] + valueTable[1][k];
                    paths[1][k] = path;

                } else if (x == X - 1) {
                    if (k == X) {
                        break;
                    }
                    double tempValue = energyCalculation[0][x] + energyCalculation[1][k];
                    if (valueTable[1][k] > tempValue) {
                        valueTable[1][k] = tempValue;
                        paths[1][k] = path;
                    }

                } else {

                    double tempValue = energyCalculation[0][x] + energyCalculation[1][k];
                    if (tempValue < valueTable[1][k]) {
                        valueTable[1][k] = tempValue;
                        paths[1][k] = path;
                    }
                    if(k == x + 1) {
                        valueTable[1][k] = valueTable[0][x] + valueTable[1][k];
                    }
                }
            }
        }

        for(int y = 2; y < Y; y++) {
            for (int x = 0; x < X; x++) {
                for (int k = x - 1; k <= x + 1; k++) {
                    int path = x - k;
                    if (x == 0) {
                        if(k == -1) {
                            continue;//Because k is negative
                        }
                        valueTable[y][k] = valueTable[y - 1][x] + energyCalculation[y][k];
                        paths[y][k] = path;

                    } else if (x == X - 1) {
                        if (k == X) {
                            break;
                        }
                        double tempValue = valueTable[y - 1][x] + energyCalculation[y][k];
                        if (valueTable[y][k] > tempValue) {
                            valueTable[y][k] = tempValue;
                            paths[y][k] = path;
                        }

                    } else {

                        double tempValue = valueTable[y - 1][x] + energyCalculation[y][k];
                        if (tempValue < valueTable[y][k]) {
                            valueTable[y][k] = tempValue;
                            paths[y][k] = path;
                        }
                        if(k == x + 1) {
                            valueTable[y][k] = valueTable[y - 1][x] + valueTable[y][k];
                            paths[y][k] = path;
                        }
                    }
                }
            }
        }
    }

    /**
     * In this method the programme will find the shortest path from the bottom of picture
     * and depending on that path saving the index of vertical seam.
     *
     * @return integer array which is the array of the index numbers of vertical seam
     * */
    private int[] findVerticalSeam() {
        dijkstraVertical();
        int[] verticalSeam = new int[Y];
        int indexMin = 0;
        int y = Y -1;

        double minimum = valueTable[Y - 1][0];
        for(int x = 0; x < X; x++) {
            if(valueTable[Y - 1][x] < minimum) {
                minimum = valueTable[Y - 1][x];
                indexMin = x;
            }
        }

        while (y >= 0) {
            switch (paths[y][indexMin]) {
                case -1:
                    verticalSeam[y] = indexMin;

                    y--;
                    indexMin--;
                    break;
                case 0:
                    verticalSeam[y] = indexMin;

                    y--;
                    break;
                case 1:
                    verticalSeam[y] = indexMin;

                    y--;
                    indexMin++;
                    break;
            }
        }
        for(int i = 0; i < verticalSeam.length; i++) {
            seamHistory[i][count] = verticalSeam[i];
        }
        count++;
        return verticalSeam;
    }

    /**
     * Same logic as Vertical one.
     * */
    private void dijkstraHorizontal() {
        int[] horizontalSeam = new int[X];
        valueTable = new double[Y][X];

        for(int y = 0; y < Y; y++) {
            for (int x = 0; x < X; x++)
                valueTable[y][x] = energyCalculation[y][x];
        }

        //-1 left, 0 mid and 1 right
        paths = new int[Y][X];



        for (int y = 0; y < Y; y++) {
            for (int k = y - 1; k <= y + 1; k++) {
                int path = y - k;
                if (y == 0) {
                    if(k == -1) {
                        continue;//Because k is negative
                    }
                    valueTable[k][1] = valueTable[y][0] + valueTable[k][1];
                    paths[k][1] = path;

                } else if (y == Y - 1) {
                    if (k == Y) {
                        break;
                    }
                    double tempValue = energyCalculation[y][0] + energyCalculation[k][1];
                    if (valueTable[k][1] > tempValue) {
                        valueTable[k][1] = tempValue;
                        paths[k][1] = path;
                    }

                } else {

                    double tempValue = energyCalculation[y][0] + energyCalculation[k][1];
                    if (tempValue < valueTable[k][1]) {
                        valueTable[k][1] = tempValue;
                        paths[k][1] = path;
                    }
                    if(k == 1 + 1) {
                        valueTable[y][k] = valueTable[y - 1][1] + valueTable[y][k];
                    }
                }
            }
        }


        for(int x = 2; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                for (int k = y - 1; k <= y + 1; k++) {
                    int path = y - k;
                    if (y == 0) {
                        if(k == -1) {
                            continue;//Because k is negative
                        }
                        valueTable[k][x] = valueTable[y][x - 1] + energyCalculation[k][x];
                        paths[k][x] = path;

                    } else if (y == Y - 1) {
                        if (k == Y) {
                            break;
                        }
                        double tempValue = valueTable[y][x - 1] + energyCalculation[k][x];
                        if (valueTable[k][x] > tempValue) {
                            valueTable[k][x] = tempValue;
                            paths[k][x] = path;
                        }

                    } else {

                        double tempValue = valueTable[y][x - 1] + energyCalculation[k][x];
                        if (tempValue < valueTable[k][x]) {
                            valueTable[k][x] = tempValue;
                            paths[k][x] = path;
                        }
                        if(k == y + 1) {
                            valueTable[k][x] = valueTable[y][x - 1] + valueTable[k][x];
                            paths[k][x] = path;
                        }
                    }
                }
            }
        }
    }

    /**
     * Same logic as Vertical one.
     * */
    private int[] findHorizontalSeam() {
        dijkstraHorizontal();
        int[] horizontalSeam = new int[X];
        int indexMin = 0;
        int x = X -1;

        double minimum = valueTable[0][X - 1];
        for(int y = 0; y < Y; y++) {
            if(valueTable[y][X - 1] < minimum) {
                minimum = valueTable[y][X - 1];
                indexMin = y;
            }
        }

        while (x >= 0) {
            switch (paths[indexMin][x]) {
                case -1:
                    horizontalSeam[x] = indexMin;

                    x--;
                    indexMin--;
                    break;
                case 0:
                    horizontalSeam[x] = indexMin;

                    x--;
                    break;
                case 1:
                    horizontalSeam[x] = indexMin;

                    x--;
                    indexMin++;
                    break;
            }
        }
        for(int i = 0; i < horizontalSeam.length; i++) {
            seamHistory[count][i] = horizontalSeam[i];
        }
        count++;
        return horizontalSeam;
    }

    /**
     * This method actually moves all pixels which is next to the vertical seam to the left.
     * Hence, rightmost pixels are same as the second last pixels.
     *
     * @param verticalSeam vertical seam for removing that seam
     * @return Color 2D array, which is image with a line removed.
     * */
    private Color[][] removeVerticalSeam(int[] verticalSeam) {
        int y = 0;
        for (int i : verticalSeam) {
            for (int k = i; k < X - 1; k++) {
                rgb[y][k] = rgb[y][k + 1];
            }
            y++;
        }
        X -= 1;
        return rgb;
    }

    /**
     * Same logic as vertical one.
     * @param horizontalSeam horizontal seam for removing that seam.
     * @return Color 2D array, which is image with a line removed.
     * */
    private Color[][] removeHorizontalSeam(int[] horizontalSeam) {

        int x = 0;
        for (int i  = 0; i < horizontalSeam.length; i++) {
            for (int k = horizontalSeam[i]; k < Y - 1; k++) {
                rgb[k][x] = rgb[k + 1][x];
            }
            x++;
        }
        Y -= 1;
        return rgb;
    }

    /**
     * [EXTENSION]
     * Changing seams to red lines.
     * These lines will overwriting int the original image.
     *
     * @return Color 2D array for making image, which shows the removed seams.
     * */
    public Color[][] showSeam(Color[][] seeSeam, boolean verOrHor) {
        if (verOrHor) {
            for (int i = 0; i < seamHistory.length; i++) {
                for (int j = 0; j < seamHistory[0].length; j++) {
                    seeSeam[i][seamHistory[i][j]] = red;
                }
            }
            return seeSeam;
        } else {
            for (int i = 0; i < seamHistory.length; i++) {
                for (int j = 0; j < seamHistory[0].length; j++) {
                    seeSeam[seamHistory[i][j]][j] = red;
                }
            }
            return seeSeam;
        }
    }

    /***
     * Getter of X
     * @return value of X (last index of width)
     */
    public int getX() {
        return X;
    }

    /**
     * Getter of Y
     * @return value of Y (last index of height)
     * */
    public int getY() {
        return Y;
    }

    /**
     * Load all methods to do seam carve more than one times.
     *
     * @param times the number of seams to remove.
     * @param verOrHor if verOrHor is false, the programme would execute vertical seam carving other wise,
     *                 would execute horizontal seam carving.
     * @throws ArrayIndexOutOfBoundsException if times is less than zero or bigger than last index it will
     *                                        handle in the FileChoose.java.
     * @return Seam carved image.
     * */
    public Color[][] seamCarving(int times, boolean verOrHor) throws  ArrayIndexOutOfBoundsException{
        if (times < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (verOrHor) {
            if(times >= X) {
                throw new ArrayIndexOutOfBoundsException();
            }
            multipleVerticalSeams = new int[times][X];

            Color[][] seeSeam = new Color[rgb.length][rgb[0].length];
            for (int x = 0; x < seeSeam[0].length; x++) {
                for (int y = 0; y < seeSeam.length; y++) {
                    seeSeam[y][x] = rgb[y][x];
                }
            }
            int[] verticalSeam = new int[rgb.length];
            for (int i = 0; i < times; i++) {
                verticalSeam = findVerticalSeam();
                removeVerticalSeam(verticalSeam);
                calculation();
                multipleVerticalSeams[i] = verticalSeam;
            }
            seeSeam = showSeam(seeSeam, verOrHor);
            return seeSeam;
        } else {
            if(times >= Y) {
                throw new ArrayIndexOutOfBoundsException();
            }
            multipleHorizontalSeams = new int[times][Y];
            Color[][] seeSeam = new Color[rgb.length][rgb[0].length];
            for (int x = 0; x < seeSeam[0].length; x++) {
                for (int y = 0; y < seeSeam.length; y++) {
                    seeSeam[y][x] = rgb[y][x];
                }
            }
            int[] horizontalSeam = new int[rgb[0].length];
            for (int i = 0; i < times; i++) {
                horizontalSeam = findHorizontalSeam();
                removeHorizontalSeam(horizontalSeam);
                calculation();
                multipleHorizontalSeams[i] = horizontalSeam;
            }
            seeSeam = showSeam(seeSeam, verOrHor);
            return seeSeam;
        }
    }

    /**
     * [Extension]
     * This method will add vertical seam (which is from the removal) in the original picture.
     *
     * @param verticalSeam the vertical seam to add in the original picture.
     * */
    private Color[][] addVerticalSeam(int[] verticalSeam) {
        Color[][] newRGB = new Color[Y][X + 1];

        for (int y = 0; y < Y; y++) {
            for (int x = 0; x <= verticalSeam[y]; x++) {
                newRGB[y][x] = rgb[y][x];
            }
        }

        for (int y = 0; y < Y; y++) {
            newRGB[y][verticalSeam[y] + 1] = rgb[y][verticalSeam[y]];
        }
        for (int y = 0; y < Y; y++) {
            for (int x = verticalSeam[y] + 1; x < X; x++) {
                newRGB[y][x + 1] = rgb[y][x];
            }
        }

        rgb = new Color[Y][X + 1];
        for (int y = 0; y < Y; y++) {
            for (int x = 0; x <= X; x++) {
                rgb[y][x] = newRGB[y][x];
            }
        }

        X++;
        return newRGB;
    }

    /**
     * [EXTENSION]
     * Same as vertical method
     *
     * @param horizontalSeam the horizontal seam to add in the original picture.
     * */
    private Color[][] addHorizontalSeam(int[] horizontalSeam) {
        Color[][] newRGB = new Color[Y + 1][X];

        for (int x = 0; x < X; x++) {
            for (int y = 0; y <= horizontalSeam[x]; y++) {
                newRGB[y][x] = rgb[y][x];
            }
        }

        for (int x = 0; x < X; x++) {
            newRGB[horizontalSeam[x] + 1][x] = rgb[horizontalSeam[x]][x];
        }
        for (int x = 0; x < X; x++) {
            for (int y = horizontalSeam[x] + 1; y < Y; y++) {
                newRGB[y + 1][x] = rgb[y][x];
            }
        }

        rgb = new Color[Y + 1][X];
        for (int x = 0; x < X; x++) {
            for (int y = 0; y <= Y; y++) {
                rgb[y][x] = newRGB[y][x];
            }
        }
        Y++;
        return newRGB;
    }

    /**
     * [EXTENSION]
     * This method has to use in different object.
     * Inserting seams which are the same seams that used for removal.
     *
     * @param times the number of seams to remove.
     * @param verOrHor if verOrHor is false, the programme would execute vertical seam carving other wise,
     *                 would execute horizontal seam carving.
     * @param multipleSeams Bunch of seams.
     * @throws ArrayIndexOutOfBoundsException if times is less than zero or bigger than last index it will
     *                                        handle in the FileChoose.java.
     * @return Seams are inserted into image.
     * */
    public Color[][] seamAdding(int times, boolean verOrHor, int[][] multipleSeams) throws  ArrayIndexOutOfBoundsException{

        if (times < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (verOrHor) {
            if (times >= X) {
                throw new ArrayIndexOutOfBoundsException();
            }
            for (int i = 0; i < times; i++) {
                addVerticalSeam(multipleSeams[i]);
            }
            return rgb;
        } else {
            if (times >= Y) {
                throw new ArrayIndexOutOfBoundsException();
            }
            for (int i = 0; i < times; i++) {
                addHorizontalSeam(multipleSeams[i]);
            }
            return rgb;
        }

    }

    /**
     * [EXTENSION]
     * Making output file, this method will make image with red lines.
     * @param anyRgb rgb for making image file.
     * @param name file name.
     * */
    public void makeFile(Color[][] anyRgb, String name) throws IOException {
        BufferedImage image = new BufferedImage(anyRgb[0].length, anyRgb.length, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < anyRgb[0].length; x++) {
            for (int y = 0; y < anyRgb.length; y++) {
                image.setRGB(x, y, anyRgb[y][x].getRGB());
            }
        }
        OutputStream out = new FileOutputStream(name + ".jpg");
        ImageIO.write(image, "jpg",out);
        out.close();
    }

    /**
     * Making output file, which is seam carved.
     * @param anyRgb rgb for making image file.
     * @param X the width of output file.
     * @param Y the height of output file.
     * @param name file name.
     * */
    public void makeFile(Color[][] anyRgb, int X, int Y, String name) throws IOException, ArrayIndexOutOfBoundsException{
        BufferedImage image = new BufferedImage(X, Y, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                image.setRGB(x, y, anyRgb[y][x].getRGB());
            }
        }
        OutputStream out = new FileOutputStream(name + ".jpg");
        ImageIO.write(image, "jpg",out);
        out.close();
    }

    public int[][] multipleSeams(boolean verOrHor) {
        if (verOrHor) {
            return multipleVerticalSeams;
        } else {
            return multipleHorizontalSeams;
        }
    }

    public static void main(String[] args) throws IOException{

        SeamCarving seamCarving = new SeamCarving("jpg.jpg");
        SeamCarving seamCarving2 = new SeamCarving("jpg.jpg");

        Color[][] seeSeam = seamCarving.seamCarving(50, true);
        Color[][] newRGB = seamCarving2.seamAdding(50, true, seamCarving.multipleVerticalSeams);
        seamCarving.makeFile(seeSeam, "Seam");
        seamCarving.makeFile(seamCarving.rgb, seamCarving.X, seamCarving.Y, "Removal");
        seamCarving.makeFile(newRGB, seamCarving2.X, seamCarving2.Y, "Insertion");
    }
}
