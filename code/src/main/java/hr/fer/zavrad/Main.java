package hr.fer.zavrad;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.zavrad.data.Data;
import hr.fer.zavrad.data.DataLoader;
import hr.fer.zavrad.ga.GeneticAlgorithm;
import hr.fer.zavrad.ga.insertions.BasicInsert;
import hr.fer.zavrad.ga.insertions.HeuristicInsert;

public class Main {

	public static void main(String[] args) {
		DataLoader dl = new DataLoader();
		Data data;
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 4);//);1);//
		while ((data = dl.getDataset()) != null) {
			Task t = new Task(data);//new Task(new Data("", 100.0, 12, 4,new double[] {50,3,48,53,53,4,3,41,23,20,52,49}));//
			pool.submit(t);
		}
		pool.close();
	}
	
	static class Task implements Runnable {
		private Data data;
		
		public Task(Data data) {
			this.data = data;
		}

		@Override
		public void run() {
			try {
				
				GeneticAlgorithm ga = new GeneticAlgorithm(data, new BasicInsert());
				
				StringBuilder sb = new StringBuilder();
				sb.append(data.name()).append(" ").append(data.solution()).append("\n");
				sb.append(ga.algorithm()).append("\n");
				
				//ga.setInsertionAlgorithm(new LocalSearchInsert());
				ga.setInsertionAlgorithm(new HeuristicInsert());
				sb.append(ga.algorithm()).append("\n");
				
				System.out.println(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
