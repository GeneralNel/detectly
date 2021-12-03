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
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class OCR {

    private ITesseract tesseract;

    public OCR() {
        this.tesseract =  new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        tesseract.setDatapath(tessDataFolder.getAbsolutePath());
    }

    public String getContentsFromFile(File file) throws IOException, TesseractException {
        boolean isPDFFile = checkIfPdf(file.getName());
        if (isPDFFile)
            return getContentsFromPDF(file);
        else
            return getContentsFromImage(file);
    }

    public String getContentsFromPDF(File file) throws TesseractException, IOException {
        PDDocument document = PDDocument.load(file);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
        File inputPdf = new File("src/main/resources/preprocessedFiles/"+"imageFromPdf.jpg");
        ImageIO.write(bufferedImage, "jpg", inputPdf);
        String contentsAfterOCR = "";
        try {
            contentsAfterOCR = extractText(file);
            System.out.println(contentsAfterOCR);
        } catch (IOException e){
            System.out.println("Exception " + e.getMessage());
        }
        document.close();
        return contentsAfterOCR;
    }

    // original function
    public String getContentsFromImage(File file) throws TesseractException, IOException {
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
        nu.pattern.OpenCV.loadShared();

        // load source image
        String preprocessedPath = "src/main/resources/preprocessedFiles/" + file.getName();
        Imgcodecs imageCodecs = new Imgcodecs();
        Mat img  = imageCodecs.imread(file.getAbsolutePath());
        imageCodecs.imwrite(preprocessedPath, img);

        // make grayscale
        Mat imgGray = new Mat();
        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
        imageCodecs.imwrite(preprocessedPath, imgGray);

        // reduce blur
        Mat imgGaussianBlur = new Mat();
        Imgproc.GaussianBlur(imgGray,imgGaussianBlur,new Size(3, 3),0);
        imageCodecs.imwrite(preprocessedPath, imgGaussianBlur);

        // adaptive threshold
        Mat imgAdaptiveThreshold = new Mat();
        Imgproc.adaptiveThreshold(imgGaussianBlur, imgAdaptiveThreshold, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C ,Imgproc.THRESH_BINARY,99, 4);
        imageCodecs.imwrite(preprocessedPath, imgAdaptiveThreshold);

        return new File(preprocessedPath);
    }

    // converts the image to grayscale and to jpg format
    private File makeGrayscale(File input) throws IOException {
        BufferedImage image = ImageIO.read(input);
        int width = image.getWidth();
        int height = image.getHeight();

        for(int i=0; i<height; i++) {

            for(int j=0; j<width; j++) {

                Color c = new Color(image.getRGB(j, i));
                int red = (int)(c.getRed() * 0.299);
                int green = (int)(c.getGreen() * 0.587);
                int blue = (int)(c.getBlue() *0.114);
                Color newColor = new Color(red+green+blue,

                        red+green+blue,red+green+blue);

                image.setRGB(j,i,newColor.getRGB());
            }
        }
        File output = new File("src/main/resources/preprocessedFiles/"+"grayscaledOutput.jpg");
        ImageIO.write(image, "jpg", output);
        return output;
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

    private boolean checkIfPdf(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i >= 0) { extension = fileName.substring(i+1); }
        return extension.equals("pdf");
    }

}
