import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Bitmap {
	private int index;
	public byte[] array;
	public int offset;
	public int height;
	public int width;
	public int bytesPerPixel;
	public ArrayList[][] arrayColors;
	public int centerX;
	public int centerY;
	public float r;

	public final int FIRST_OFFSET_BYTE = 10;
	public final int FIRST_WIDTH_BYTE = 18;
	public final int FIRST_HEIGHT_BYTE = 22;
	public final int FIRST_BITS_PER_PIXEL_BYTE = 28;

	public final int NUMBER_OF_OFFSET_BYTES = 4;
	public final int NUMBER_OF_WIDTH_BYTES = 4;
	public final int NUMBER_OF_HEIGHT_BYTES = 4;
	public final int NUMBER_OF_BITS_PER_PIXEL_BYTES = 2;

	public Bitmap(String filePath) {
		try {
			this.array = Files.readAllBytes(new File(filePath).toPath());
			this.offset = this.calculateValueByBytes(FIRST_OFFSET_BYTE, NUMBER_OF_OFFSET_BYTES);
			this.height = this.calculateValueByBytes(FIRST_HEIGHT_BYTE, NUMBER_OF_WIDTH_BYTES);
			this.width = this.calculateValueByBytes(FIRST_WIDTH_BYTE, NUMBER_OF_HEIGHT_BYTES);
			this.bytesPerPixel = this.calculateValueByBytes(FIRST_BITS_PER_PIXEL_BYTE, NUMBER_OF_BITS_PER_PIXEL_BYTES) / 8;
			this.r = this.centerX = this.centerY = this.width / 2;
			System.out.println("offset: " + this.offset);
			System.out.println("height: " + this.height);
			System.out.println("width: " + this.width);
			System.out.println("bytes per pixel: " + this.bytesPerPixel);
			this.getColorsArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void changeColor(byte[] beforeColor, byte[] afterColor) {
		index = 0;
		for (byte b : this.array) {
			if (index >= offset && index <= offset + bytesPerPixel * height * width) {
				if (index % 3 == 0 && (index + 2) < array.length && array[index] == beforeColor[0]
						&& array[index + 1] == beforeColor[1] && array[index + 2] == beforeColor[2]) {
					this.array[index] = afterColor[0];
					this.array[index + 1] = afterColor[1];
					this.array[index + 2] = beforeColor[2];
				}
			}
			index++;
		}
		this.getColorsArray();
	}

	public void getColorsArray() {
		this.arrayColors = new ArrayList[this.height][this.width];
		index = 0;
		ArrayList<Integer> elements = new ArrayList<>();

		System.out.println(this.arrayColors.length);

		for (index = this.offset; index < this.array.length; index++) {
			if (index >= this.offset && index < (this.offset + this.bytesPerPixel * this.height * this.width)) {
				if (((index - this.offset) % this.bytesPerPixel == 0) && (elements.size() == this.bytesPerPixel)) {
					int x = (index - this.offset) / this.bytesPerPixel / this.height;
					int y = (index - this.offset) / this.bytesPerPixel % this.width;
					if (y == 0) {
						if (x < this.arrayColors.length && x > 0) {
							this.arrayColors[x - 1][width - 1] = elements;
						}
					} else {
						if (x < this.arrayColors.length) {
							this.arrayColors[x][y - 1] = elements;
						}
					}
					elements = new ArrayList<>();
					elements.add((int) this.array[index]);
				} else {
					elements.add((int) this.array[index]);
				}
			}
		}

	}

	public void drawCircle() throws FileNotFoundException, UnsupportedEncodingException {
		int[][] a = new int[this.height][this.width];
		ArrayList<Integer> blackColor = new ArrayList<>();
		blackColor.add(0);
		blackColor.add(0);
		blackColor.add(0);
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				double square = Math.pow((i - centerX), 2) + Math.pow((j - centerY), 2);
				if (square <= Math.pow(r, 2)) {
					a[i][j] = 1;
					a[i][width - 1 - j] = 1;
					this.arrayColors[i][j] = blackColor;
					this.arrayColors[i][width - 1 - j] = blackColor;
					break;
				}
			}
		}

		index = 0;
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				ArrayList<Integer> test1 = this.arrayColors[i][j];
				if (test1 != null) {
					for (Integer integer : test1) {
						this.array[index + this.offset] = (byte) integer.intValue();
						index++;
					}
				}

			}
		}
	}

	public void saveToFile(String filePath) {
		Path path = Paths.get(filePath);
		try {
			Files.write(path, this.array);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int calculateValueByBytes(int firstOffset, int limit) {
		int value = 0;
		for (int i = 0; i < limit; i++) {
			if (this.array[i + firstOffset] >= 0) {
				value += this.array[i + firstOffset] * Math.pow(256, i);
			} else {
				value += (256 + this.array[i + firstOffset]) * Math.pow(256, i);
			}
		}
		return value;
	}
}
