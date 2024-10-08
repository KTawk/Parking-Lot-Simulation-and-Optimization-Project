import java.io.File;
import java.util.Scanner;

/**
 * @author Mehrdad Sabetzadeh, University of Ottawa
 */
public class ParkingLot {
	/**
	 * The delimiter that separates values
	 */
	private static final String SEPARATOR = ",";

	/**
	 * The delimiter that separates the parking lot design section from the parked
	 * car data section
	 */
	private static final String SECTIONER = "###";

	/**
	 * Instance variable for storing the number of rows in a parking lot
	 */
	private int numRows;

	/**
	 * Instance variable for storing the number of spaces per row in a parking lot
	 */
	private int numSpotsPerRow;

	/**
	 * Instance variable (two-dimensional array) for storing the lot design
	 */
	private CarType[][] lotDesign;

	/**
	 * Instance variable (two-dimensional array) for storing occupancy information
	 * for the spots in the lot
	 */
	private Car[][] occupancy;

	/**
	 * Constructs a parking lot by loading a file
	 * 
	 * @param strFilename is the name of the file
	 */
	public ParkingLot(String strFilename) throws Exception {

		if (strFilename == null) {
			System.out.println("File name cannot be null.");
			return;
		}

		// determine numRows and numSpotsPerRow; you can do so by
		// writing your own code or alternatively completing the
		// private calculateLotDimensions(...) that I have provided
		calculateLotDimensions(strFilename);

		// instantiate the lotDesign and occupancy variables!
		// WRITE YOUR CODE HERE!

		this.lotDesign = new CarType[this.numRows][this.numSpotsPerRow];
		this.occupancy = new Car[this.numRows][this.numSpotsPerRow];

		// populate lotDesign and occupancy; you can do so by
		// writing your own code or alternatively completing the
		// private populateFromFile(...) that I have provided
		populateFromFile(strFilename);
	}

	/**
	 * Parks a car (c) at a give location (i, j) within the parking lot.
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @param c is the car to be parked
	 */
	public void park(int i, int j, Car c) {
		this.occupancy[i][j] = c;
	}

	/**
	 * Removes the car parked at a given location (i, j) in the parking lot
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return the car removed; the method returns null when either i or j are out
	 *         of range, or when there is no car parked at (i, j)
	 */
	public Car remove(int i, int j) {
		// review the implementation of this method
		// I created an instance of type car and I sent the parameters: Type and
		// PlateNum as parameters of the class
		if (!(i < this.numRows && j < this.numSpotsPerRow) || ((occupancy[i][j] == null)))
			return null;
		else {
			occupancy[i][j] = null;
			return new Car(occupancy[i][j].getType(), occupancy[i][j].getPlateNum());
		}
	}

	/**
	 * Checks whether a car (which has a certain type) is allowed to park at
	 * location (i, j)
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return true if car c can park at (i, j) and false otherwise
	 */
	public boolean canParkAt(int i, int j, Car c) {
		if (i < this.numRows && j < this.numSpotsPerRow) {
			if ((occupancy[i][j] == null)
					&& (canBeParked((Util.getLabelByCarType(lotDesign[i][j])), (Util.getLabelByCarType(c.getType())))))
				return true;
		}
		return false;

	}

	/**
	 * @return the total capacity of the parking lot excluding spots that cannot be
	 *         used for parking (i.e., excluding spots that point to CarType.NA)
	 */
	public int getTotalCapacity() {
		int count = 0;
		for (int i = 0; i < lotDesign.length; i++) {
			for (int j = 0; j < lotDesign[i].length; j++) {
				if (!(Util.getLabelByCarType(lotDesign[i][j]).equalsIgnoreCase("N")))
					count++;
			}
		}
		return count;
	}

	/**
	 * @return the total occupancy of the parking lot (i.e., the total number of
	 *         cars parked in the lot)
	 */
	public int getTotalOccupancy() {
		int count = 0;
		for (int i = 0; i < occupancy.length; i++) {
			for (int j = 0; j < occupancy[i].length; j++) {
				if (occupancy[i][j] != null)
					count++;
			}
		}
		return count;
	}

	private void calculateLotDimensions(String strFilename) throws Exception {

		Scanner scanner = new Scanner(new File(strFilename));
		String wholeString = "";

		while (scanner.hasNext()) {
			String str = scanner.nextLine();

			if (str.equals(this.SECTIONER))
				break;

			if (!str.trim().isEmpty())
				wholeString += str + "\n";
		}

		scanner.close();

		this.numRows = wholeString.split("\n").length;
		this.numSpotsPerRow = wholeString.split("\n")[0].split(this.SEPARATOR).length;
	}

	private void populateFromFile(String strFilename) throws Exception {

		Scanner scanner = new Scanner(new File(strFilename));

		String[] spotRows = new String[this.numRows];
		int index = 0;

		while (scanner.hasNext()) {
			String str = scanner.nextLine();

			if (str.equals(this.SECTIONER))
				break;

			if (!str.trim().isEmpty())
				spotRows[index++] = str;
		}

		for (int i = 0; i < spotRows.length; i++) {
			String[] carTypes = spotRows[i].split(this.SEPARATOR);
			for (int j = 0; j < carTypes.length; j++)
				this.lotDesign[i][j] = Util.getCarTypeByLabel(carTypes[j].trim());
		}

		String cars = "";
		while (scanner.hasNext()) {
			String str = scanner.nextLine();
			if (!str.trim().isEmpty())
				cars += str + "\n";
		}

		String[][] carsArray = new String[cars.split("\n").length][cars.split("\n")[0].split(this.SEPARATOR).length];

		for (int i = 0; i < carsArray.length; i++)
			for (int j = 0; j < carsArray[0].length; j++)
				carsArray[i][j] = cars.split("\n")[i].split(this.SEPARATOR)[j].trim();

		for (var car : carsArray) {
			Car c = new Car(Util.getCarTypeByLabel(car[2]), car[3]);
			int i = Integer.parseInt(car[0]);
			int j = Integer.parseInt(car[1]);

			if (canParkAt(i, j, c))
				park(i, j, c);
			else
				System.out.println("Car " + Util.getLabelByCarType(c.getType()) + "(" + c.getPlateNum()
						+ ") cannot be parked at (" + i + "," + j + ")");

		}
		scanner.close();
	}

	private boolean canBeParked(String parkType, String carType) {

		if (parkType.equalsIgnoreCase(Util.getLabelByCarType(CarType.NA)))
			return false;

		if (parkType.equalsIgnoreCase(carType))
			return true;

		if (parkType.equalsIgnoreCase("L"))
			return true;
		if (parkType.equalsIgnoreCase("R") && (!(carType.equalsIgnoreCase("L"))))
			return true;
		if (parkType.equalsIgnoreCase("S") && (carType.equalsIgnoreCase("S") || carType.equalsIgnoreCase("E")))
			return true;

		return false;
	}

	/**
	 * Produce string representation of the parking lot
	 * 
	 * @return String containing the parking lot information
	 */
	public String toString() {
		// NOTE: The implementation of this method is complete. You do NOT need to
		// change it for the assignment.
		StringBuffer buffer = new StringBuffer();
		buffer.append("==== Lot Design ====").append(System.lineSeparator());

		for (int i = 0; i < lotDesign.length; i++) {
			for (int j = 0; j < lotDesign[0].length; j++) {
				buffer.append((lotDesign[i][j] != null) ? Util.getLabelByCarType(lotDesign[i][j])
						: Util.getLabelByCarType(CarType.NA));
				if (j < numSpotsPerRow - 1) {
					buffer.append(", ");
				}
			}
			buffer.append(System.lineSeparator());
		}

		buffer.append(System.lineSeparator()).append("==== Parking Occupancy ====").append(System.lineSeparator());

		for (int i = 0; i < occupancy.length; i++) {
			for (int j = 0; j < occupancy[0].length; j++) {
				buffer.append(
						"(" + i + ", " + j + "): " + ((occupancy[i][j] != null) ? occupancy[i][j] : "Unoccupied"));
				buffer.append(System.lineSeparator());
			}

		}
		return buffer.toString();
	}

	/**
	 * <b>main</b> of the application. The method first reads from the standard
	 * input the name of the file to process. Next, it creates an instance of
	 * ParkingLot. Finally, it prints to the standard output information about the
	 * instance of the ParkingLot just created.
	 * 
	 * @param args command lines parameters (not used in the body of the method)
	 * @throws Exception
	 */

	public static void main(String args[]) throws Exception {

		StudentInfo.display();

		System.out.print("Please enter the name of the file to process: ");

		Scanner scanner = new Scanner(System.in);

		String strFilename = scanner.nextLine();

		ParkingLot lot = new ParkingLot(strFilename);

		System.out.println("Total number of parkable spots (capacity): " + lot.getTotalCapacity());

		System.out.println("Number of cars currently parked in the lot: " + lot.getTotalOccupancy());

		System.out.print(lot);

	}
}