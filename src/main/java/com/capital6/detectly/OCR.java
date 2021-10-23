package com.capital6.detectly;

import com.recognition.software.jdeskew.ImageDeskew;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class OCR {

    private ITesseract tesseract;
    private Highgui Imgcodecs;

    public OCR() {
        this.tesseract =  new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        tesseract.setDatapath(tessDataFolder.getAbsolutePath());
    }

    public String getContentsFromFile(File file) throws IOException, TesseractException {
        if (isPDF(file))
            return getContentsFromPDF(file);
        else
            return getContentsFromImage(file);
    }

    // original function
    public String getContentsFromImage(File file) throws TesseractException {
        //file = preprocessImage(file);
        String contentsAfterOCR = "";
        try {
            contentsAfterOCR = tesseract.doOCR(file);
            System.out.println(contentsAfterOCR);
        } catch (TesseractException e){
            System.out.println("Exception " + e.getMessage());
        }
        return contentsAfterOCR;
    }

    private File preprocessImage(File file) {
        // create source
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String preprocessedPath = "src/main/resources/preprocessedFiles/" + file.getName();
        Mat img  = Imgcodecs.imread(file.getAbsolutePath());
        Imgcodecs.imwrite(preprocessedPath, img);

        // make grayscale
        Mat imgGray = new Mat();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite(preprocessedPath, imgGray);

        // reduce blur
        Mat imgGaussianBlur = new Mat();
        Imgproc.GaussianBlur(imgGray,imgGaussianBlur,new Size(3, 3),0);
        Imgcodecs.imwrite(preprocessedPath, imgGaussianBlur);

        // adaptive threshold
        Mat imgAdaptiveThreshold = new Mat();
        Imgproc.adaptiveThreshold(imgGaussianBlur, imgAdaptiveThreshold, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C ,Imgproc.THRESH_BINARY,99, 4);
        Imgcodecs.imwrite(preprocessedPath, imgAdaptiveThreshold);

        return new File(preprocessedPath);
    }

    public String getContentsFromPDF(File file) throws TesseractException {
        String contentsAfterOCR = "";
        try {
            // contentsAfterOCR = tesseract.doOCR(file);
            contentsAfterOCR = extractText(file);
            System.out.println(contentsAfterOCR);
        } catch (IOException e){
            System.out.println("Exception " + e.getMessage());
        }
        return contentsAfterOCR;
    }

    private BufferedImage correctSkewness(BufferedImage image) {
        final double MINIMUM_DESKEW_THRESHOLD = 0.05d;

        ImageDeskew mImage = new ImageDeskew(image);
        double imageSkewAngle = mImage.getSkewAngle(); // determine skew angle
        if ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD))) {
            image = ImageHelper.rotateImage(image, -imageSkewAngle); // deskew image
        }

        return image;
    }
    // Extract text
    private String extractText(File file) throws IOException {
        StringBuilder extractedText = new StringBuilder("");
        LinkedList<BufferedImage> bufferedImageList = new LinkedList<BufferedImage>();
        bufferedImageList = checkScannedPdf(file);

        if(!bufferedImageList.isEmpty()){
            for(BufferedImage image: bufferedImageList){
                BufferedImage deskewedImage = correctSkewness(image);
                String text = extractTextFromImage(deskewedImage);

                if(text != null ) {
                    extractedText.append(text);
                }
            }
        }

        return extractedText.toString();
    }


    // Extract text from pdf images
    private String extractTextFromImage(BufferedImage image) {
        BufferedImage grayImage = ImageHelper.convertImageToGrayscale(image);

        String ocrResults = null;
        try {
            ocrResults = tesseract.doOCR(grayImage).replaceAll("\\n{2,}", "\n");
        } catch (TesseractException e) {
            e.printStackTrace();
        }

        if (ocrResults == null || ocrResults.trim().length() == 0) {

            return null;
        }

        ocrResults = ocrResults.trim();
        // TODO remove the trash that doesn't seem to be words
        return ocrResults;
    }

    /*
     * @return LinkedList<BufferedImage>
     * */
    private LinkedList<BufferedImage> checkScannedPdf(File pdfFile ) throws IOException {
        int images = 0;
        int numberOfPages = 0;

        LinkedList<BufferedImage> bufferedImages = new LinkedList<>();


        PDDocument doc = PDDocument.load(pdfFile);

        PDPageTree list = doc.getPages();

        numberOfPages = doc.getNumberOfPages();

        for (PDPage page : list) {

            PDResources resource = page.getResources();

            for (COSName xObjectName : resource.getXObjectNames()) {

                PDXObject xObject = resource.getXObject(xObjectName);

                if (xObject instanceof PDImageXObject) {
                    PDImageXObject image = (PDImageXObject) xObject;

                    BufferedImage bufferedImage = image.getImage();
                    // Add bufferedImages to list
                    bufferedImages.add(bufferedImage);
                    images++;
                }

            }

        }

        doc.close();

        //  pdf pages if equal to the images === scanned pdf ===
        if (numberOfPages == images || images > numberOfPages) {

            return bufferedImages;
        } else {

            return new LinkedList<>();

        }

    }

    /**
     * Test if the data in the given byte array represents a PDF file.
     */
    public static boolean isPDF(File fileName) throws IOException {
        byte[] data = FileUtils.readFileToByteArray(fileName);
        if (data != null && data.length > 4 &&
                data[0] == 0x25 && // %
                data[1] == 0x50 && // P
                data[2] == 0x44 && // D
                data[3] == 0x46 && // F
                data[4] == 0x2D) { // -

            // version 1.3 file terminator
            if (data[5] == 0x31 && data[6] == 0x2E && data[7] == 0x33 &&
                    data[data.length - 7] == 0x25 && // %
                    data[data.length - 6] == 0x25 && // %
                    data[data.length - 5] == 0x45 && // E
                    data[data.length - 4] == 0x4F && // O
                    data[data.length - 3] == 0x46 && // F
                    data[data.length - 2] == 0x20 && // SPACE
                    data[data.length - 1] == 0x0A) { // EOL
                return true;
            }

            // version 1.3 file terminator
            if (data[5] == 0x31 && data[6] == 0x2E && data[7] == 0x34 &&
                    data[data.length - 6] == 0x25 && // %
                    data[data.length - 5] == 0x25 && // %
                    data[data.length - 4] == 0x45 && // E
                    data[data.length - 3] == 0x4F && // O
                    data[data.length - 2] == 0x46 && // F
                    data[data.length - 1] == 0x0A) { // EOL
                return true;
            }
        }
        return false;
    }
}
