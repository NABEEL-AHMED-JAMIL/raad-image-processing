package com.barco.raad.api;

import com.barco.raad.dto.ResponseDTO;
import com.barco.raad.comparison.ImageComparison;
import com.barco.raad.model.ImageCompare;
import com.barco.raad.model.ImageComparisonResult;
import com.barco.raad.model.ImageComparisonState;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@Api(tags = {"RAAD-Image-Processing"})
//https://www.pyimagesearch.com/2014/09/15/python-compare-two-images/
//https://www.pyimagesearch.com/2017/06/19/image-difference-with-opencv-and-python/
public class RaadImageProcessingApi {

    public Logger logger = LoggerFactory.getLogger(RaadImageProcessingApi.class);

    public static String PNG = ".png";
    private ResponseDTO response;
    private Map<String, Object> extData;


    //http://mundrisoft.com/tech-bytes/compare-images-using-java/
    @RequestMapping(path = "/imageReaderV1",  method = RequestMethod.POST)
    public ResponseEntity<?> imageReaderV1(ImageCompare imageCompare) {
        try {
            response = new ResponseDTO();
            // base image
            BufferedImage bImage = ImageIO.read(imageCompare.getOldImage().getInputStream());
            // compare image
            BufferedImage cImage = ImageIO.read(imageCompare.getNewImage().getInputStream());
            // height & width
            int height = bImage.getHeight();
            int width = bImage.getWidth();
            // process image
            BufferedImage rImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    try {
                        int pixelC = cImage.getRGB(x, y);
                        int pixelB = bImage.getRGB(x, y);
                        if (pixelB == pixelC ) {
                            rImage.setRGB(x, y,  bImage.getRGB(x, y));
                        } else {
                            int a= 0xff |  bImage.getRGB(x, y)>>24 ,
                                    r= 0xff &  bImage.getRGB(x, y)>>16 ,
                                    g= 0x00 &  bImage.getRGB(x, y)>>8,
                                    b= 0x00 &  bImage.getRGB(x, y);

                            int modifiedRGB=a<<24|r<<16|g<<8|b;
                            rImage.setRGB(x,y,modifiedRGB);
                        }
                    } catch (Exception e) {
                        // handled hieght or width mismatch
                        rImage.setRGB(x, y, 0x80ff0000);
                    }
                }
            }
            // convert buffered image to image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String imgType = imageCompare.getOldImage().getOriginalFilename()
                .substring(imageCompare.getOldImage().getOriginalFilename().indexOf("."));
            if (imgType.equalsIgnoreCase(PNG)) {
                ImageIO.write(rImage, "png", baos);
            } else {
                ImageIO.write(rImage, "jpg", baos);
            }
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            baos.flush();
            response.setData(imageInByte);
            response.setMessage("Image Process Result.");
            return ResponseEntity.ok().body(response);
        } catch (Exception ex) {
            response.setMessage("Process Fail.");
            response.setText("Some error occurs.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(path = "/imageReaderV2",  method = RequestMethod.POST)
    public ResponseEntity<?> imageReaderV2(ImageCompare imageCompare) {
        try {
            response = new ResponseDTO();
            extData = new HashMap<>();
            BufferedImage expectedImage = ImageIO.read(imageCompare.getOldImage().getInputStream());
            BufferedImage actualImage = ImageIO.read(imageCompare.getNewImage().getInputStream());
            ImageComparison imageComparison = new ImageComparison(expectedImage, actualImage);
            if(imageCompare.getThreshold() != null) {
                imageComparison.setThreshold(imageCompare.getThreshold());
            }
            if(imageCompare.getRectangleLineWidth() != null) {
                imageComparison.setRectangleLineWidth(imageCompare.getRectangleLineWidth());
            }
            if(imageCompare.getFillDifferenceRectangles() != null
                    && imageCompare.getPercentOpacityDifferenceRectangles() != null) {
                imageComparison.setDifferenceRectangleFilling(imageCompare.getFillDifferenceRectangles(),
                        imageCompare.getPercentOpacityDifferenceRectangles());
            }
            if(imageCompare.getFillExcludedRectangles() != null && imageCompare.getPercentOpacityExcludedRectangles() != null) {
                imageComparison.setExcludedRectangleFilling(imageCompare.getFillExcludedRectangles(),
                        imageCompare.getPercentOpacityExcludedRectangles());
            }
            if(imageCompare.getDrawExcludedRectangles() != null) {
                imageComparison.setDrawExcludedRectangles(imageCompare.getDrawExcludedRectangles());
            }
            if(imageCompare.getMaximalRectangleCount() != null) {
                imageComparison.setMaximalRectangleCount(imageCompare.getMaximalRectangleCount());
            }
            if(imageCompare.getMinimalRectangleSize() != null) {
                imageComparison.setMinimalRectangleSize(imageCompare.getMinimalRectangleSize());
            }
            if(imageCompare.getPixelToleranceLevel() != null) {
                imageComparison.setPixelToleranceLevel(imageCompare.getPixelToleranceLevel());
            }
            if(imageCompare.getAllowingPercentOfDifferentPixels() != null) {
                imageComparison.setAllowingPercentOfDifferentPixels(imageCompare.getAllowingPercentOfDifferentPixels());
            }
            //After configuring the ImageComparison object, can be executed compare() method:
            ImageComparisonResult imageComparisonResult = imageComparison.compareImages();
            // result
            extData.put("Status", imageComparisonResult.getImageComparisonState());
            if(imageComparisonResult.getRectangles() != null) {
                extData.put("total_rect", imageComparisonResult.getRectangles().size());
                extData.put("rect_size", imageComparisonResult.getRectangles()
                    .stream().map(rectangle -> {
                        return rectangle.size();
                    }).collect(Collectors.toList()));
            }
            System.out.println(extData);
            //And Result Image
            BufferedImage resultImage = imageComparisonResult.getResult();
            // convert buffered image to image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String imgType = imageCompare.getOldImage().getOriginalFilename()
                    .substring(imageCompare.getOldImage().getOriginalFilename().indexOf("."));
            if (imgType.equalsIgnoreCase(PNG)) {
                ImageIO.write(resultImage, "png", baos);
            } else {
                ImageIO.write(resultImage, "jpg", baos);
            }
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            baos.flush();
            response.setData(imageInByte);
            response.setMessage("Image Process Result.");
            response.setExt(extData);
            return ResponseEntity.ok().body(response);

        } catch (Exception ex) {
            response.setMessage("Process Fail.");
            response.setText("Some error occurs.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
