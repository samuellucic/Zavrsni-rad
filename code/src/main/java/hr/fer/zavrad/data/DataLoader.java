package hr.fer.zavrad.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataLoader {
	private Iterator<Path> dataPaths;
	private Path currentPath;
	private BufferedReader reader;
	private int datasetCount;
	private int datasetNum;

	public DataLoader() {
		try (Stream<Path> paths = Files.walk(Paths.get(Data.class.getClassLoader().getResource(".").toURI()), 1)) {
			dataPaths = paths.filter(path -> path.getFileName().toString().startsWith("binpack"))
							 .collect(Collectors.toList())
							 .iterator();
		} catch (IOException | URISyntaxException ignorable) {	
		}
	}

	public Data getDataset() {	
		try {
			if (reader == null) {
				if (dataPaths.hasNext()) {
					openFile();
				} else {
					return null;
				}
			}
			if (datasetNum == datasetCount) {
				closeFile();
				return getDataset();
			}
			
			String title = reader.readLine().trim();
			String[] info = reader.readLine().trim().split("\\s+");
			
			double capacity = Double.parseDouble(info[0]);
			int n = Integer.parseInt(info[1]);
			int solution = Integer.parseInt(info[2]);
			double[] items = new double[n];
			for (int i = 0; i < n; i++) {
				items[i] = Double.parseDouble(reader.readLine());
			}
			datasetNum++;
			return new Data(title, capacity, n, solution, items);
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void openFile() throws IOException {
		currentPath = dataPaths.next();
		reader = new BufferedReader(new InputStreamReader(Files.newInputStream(currentPath)));
		datasetCount = Integer.parseInt(reader.readLine());
		datasetNum = 0;
	}
	
	private void closeFile() throws IOException {
		reader.close();
		reader = null;
	}
}
