package hr.fer.zavrad;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import hr.fer.zavrad.data.Data;
import hr.fer.zavrad.data.DataLoader;
import hr.fer.zavrad.ga.GeneticAlgorithm;

public class Main {
	
	public static void main(String[] args) {
		DataLoader dl = new DataLoader(Paths.get(args[0]));
		Data data;
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4);//);1);//
		List<Future<?>> tasks = new ArrayList<>();
		
		while ((data = dl.getDataset()) != null) {
			Task t = new Task(data);//new Task(new Data("", 100.0, 12, 4,new double[] {50,3,48,53,53,4,3,41,23,20,52,49}));//
			tasks.add(pool.submit(t));
		}
		
		for (Future<?> t : tasks) {
			while (true) {
				try {
					t.get();
					break;
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		pool.close();
	}
	
	private static class Task implements Runnable {
		private Data data;
		
		public Task(Data data) {
			this.data = data;
		}

		@Override
		public void run() {
			try {
				GeneticAlgorithm ga = new GeneticAlgorithm(data, false);
				
				StringBuilder sb = new StringBuilder();
				sb.append(data.name()).append(" ").append(data.solution()).append("\n");
				sb.append(ga.algorithm()).append("\n");
				ga.setHybridized(true);
				sb.append(ga.algorithm()).append("\n");
				
				try (BufferedWriter bw = Files.newBufferedWriter(Paths.get("./results_" + data.name() + ".txt"))) {
					bw.write(sb.toString());
				}
				System.out.println(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
