import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class main {

	public static void main(String[] args) throws IOException {		
		Bitmap bm = new Bitmap("src/bmp_24.bmp");
		byte[] beforeColor = {0, 0, -1};
		byte[] afterColor = {0, -1, -1};
		bm.changeColor(beforeColor, afterColor);
		bm.drawCircle();	
		bm.saveToFile("src/bmp_24_results.bmp");
	}
}
