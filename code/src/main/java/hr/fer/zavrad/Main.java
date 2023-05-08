package hr.fer.zavrad;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.fer.zavrad.data.Data;
import hr.fer.zavrad.data.DataLoader;
import hr.fer.zavrad.ga.GeneticAlgorithm;

public class Main {

	public static void main(String[] args) {
		DataLoader dl = new DataLoader();
		Data data;
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		while ((data = dl.getDataset()) != null) {
			Task t = new Task(data);
			pool.submit(t);
		}
	}
	
	static class Task implements Runnable {
		private Data data;
		
		public Task(Data data) {
			this.data = data;
		}

		@Override
		public void run() {
			GeneticAlgorithm ga = new GeneticAlgorithm(data, null);
			System.out.println(data.name() + " " + data.solution() + "\n" +  ga.algorithm());
		}
		
	}
}
