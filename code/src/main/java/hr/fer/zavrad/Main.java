package hr.fer.zavrad;

import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.zavrad.data.Data;
import hr.fer.zavrad.data.DataLoader;
import hr.fer.zavrad.ga.GeneticAlgorithm;

public class Main {
	
	public static void main(String[] args) {
		DataLoader dl = new DataLoader(Paths.get(args[0]));
		Data data;
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4);//);1);//
		while ((data = dl.getDataset()) != null) {
			Task t = new Task(data);//new Task(new Data("", 100.0, 12, 4,new double[] {50,3,48,53,53,4,3,41,23,20,52,49}));//
			pool.submit(t);
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
				
				System.out.println(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
