import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
 * Written by Nicholas Cercos
 */
public class Utils {

	/**
	 * Loads any sprite within the game's resource directory.
	 *
	 * @param path The path starting from the res folder.
	 * @return An image, if it exists, otherwise null.
	 */
	public static BufferedImage loadSprite(String path) {
		try {
			return ImageIO.read(Utils.class.getResourceAsStream("/" + path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if a string is a number.
	 *
	 * @param s The string in question.
	 * @return True if the given string is an integer.
	 */
	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Prompts a player for a response.
	 *
	 * @param message The content of the prompt.
	 * @return An integer received by the user.
	 */
	public static int getPlayerInput(String message) {
		String input = promptPlayerInput(message);

		if (input == null) {
			System.exit(0);
			return 0;
		}

		while(!isInt(input) || Integer.parseInt(input) < 1)
			input = promptPlayerInput(message);
		return Integer.parseInt(input);
	}

	private static String promptPlayerInput(String message) {
		return JOptionPane.showInputDialog(message);
	}
}
