
public class CapacityOptimizer {
	private static final int NUM_RUNS = 10;

	private static final double THRESHOLD = 5.0d;

	public static int getOptimalNumberOfSpots(int hourlyRate) {

		if (hourlyRate < 0)
			throw new IllegalArgumentException("HourlyRate should be a non negative integer !");

		var lotSize = 1;

		while (true) {
			System.out.println();
			System.out.println("\n==== Setting lot capacity to: " + lotSize + "====");
			var totalIncommingLength = 0;

			for (int i = 0; i < NUM_RUNS; i++) {
				var simulator = new Simulator(new ParkingLot(lotSize), hourlyRate, 24 * 3600);
				var now = System.currentTimeMillis();
				simulator.simulate();
				System.out.println("Simulation run " + (i + 1) + " (" + (System.currentTimeMillis() - now)
						+ "ms); Queue length at the end of simulation run: "
						+ simulator.getIncomingQueueSize());
				totalIncommingLength += simulator.getIncomingQueueSize();
			}

			if (((1.0 * totalIncommingLength) / NUM_RUNS) <= THRESHOLD)
				break;

			lotSize++;
		}

		return lotSize;
	}

	public static void main(String args[]) {
		StudentInfo.display();

		long mainStart = System.currentTimeMillis();

		if (args.length < 1) {
			System.out.println("Usage: java CapacityOptimizer <hourly rate of arrival>");
			System.out.println("Example: java CapacityOptimizer 11");
			return;
		}

		if (!args[0].matches("\\d+")) {
			System.out.println("The hourly rate of arrival should be a positive integer!");
			return;
		}

		int hourlyRate = Integer.parseInt(args[0]);

		int lotSize = getOptimalNumberOfSpots(hourlyRate);

		System.out.println();
		System.out.println("SIMULATION IS COMPLETE!");
		System.out.println("The smallest number of parking spots required: " + lotSize);

		long mainEnd = System.currentTimeMillis();

		System.out.println("Total execution time: " + ((mainEnd - mainStart) / 1000f) + " seconds");

	}
}