package hr.fer.zavrad.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import hr.fer.zavrad.data.Data;
import hr.fer.zavrad.ga.mutations.IMutate;

public class GeneticAlgorithm {
	private static IFunction COST_FUNCTION = (groups, data, k) -> {
		double cost = 0;
		for (Group g : groups) {
			cost += Math.pow((double)g.getTotalSize() / data.capacity(), k);
		}
		return cost / data.n();
	};
	private static final int POP_SIZE = 49;
	private static final int NUM_CROSS = 12;
	private static final int NUM_MUTATE = 4;
	private static final int NUM_INVERSE = 4;
	private static final int K = 4;
	
	private Data data;
	private IMutate mutationAlgorithm;
	
	public GeneticAlgorithm(Data data, IMutate mutationAlgorithm) {
		this.data = data;
		this.mutationAlgorithm = mutationAlgorithm;
	}
	
	public String algorithm() {
		Random random = new Random();
		List<GroupObject> dataList = new ArrayList<>(data.n());
		for (int i = 0; i < data.n(); i++) {
			dataList.add(new GroupObject(i, data.items()[i]));
		}
		
		Chromosome[] population = createPopulation(POP_SIZE, dataList, random);
		for (int i = 0; i < 1; i++)
//			System.out.println(population[i]);
//			System.out.println();
			
		evaluatePopulation(population, COST_FUNCTION);
		int generation = 0;
		boolean breakLoop = false;
		for ( ; generation < 100_000; generation++) {
			Arrays.sort(population);
			
			for (Chromosome c : population) {
				if (c.getGroups().size() <= data.solution()) {
					System.out.println(c.getFitness());
					breakLoop = true;
					break;
				}
			}
			if (breakLoop) {
				break;
			}
			
//			if (generation % 1000 == 0) {
//				System.out.println("Generation: " + generation + "\nBin num: " + population[population.length - 1].getGroups().size());
//			}
			
//			crossParents(population, NUM_CROSS, random);
			int numCross = NUM_CROSS;
			for (int i = 0; i < numCross; i++) {
				int first = random.nextInt(numCross); 
				int second = random.nextInt(numCross); 
				while (first == second) {
					second = random.nextInt(numCross); 
				}
				
				Chromosome firstParent = population[population.length - 1 - first];
				Chromosome secondParent = population[population.length - 1 - second];
				
				int firstCross1 = random.nextInt((firstParent.getGroups().size() - 1)); 
				int firstCross2 = random.nextInt(firstParent.getGroups().size()); 
				while (firstCross1 >= firstCross2) {
					firstCross2 = random.nextInt(firstParent.getGroups().size()); 
				}
				
				int secondCross1 = random.nextInt((secondParent.getGroups().size() - 1)); 
				int secondCross2 = random.nextInt(secondParent.getGroups().size()); 
				while (secondCross1 >= secondCross2) {
					secondCross2 = random.nextInt(secondParent.getGroups().size()); 
				}
				
				Chromosome child1 = cross(firstParent, secondParent, firstCross1, firstCross2, secondCross1);
				Chromosome child2 = cross(secondParent, firstParent, secondCross1, secondCross2, firstCross1);
				
				mutate(child1, random);
				mutate(child2, random);
				
				population[2 * i] = child1;
				population[2 * i + 1] = child2;
			}
			
			evaluatePopulation(population, COST_FUNCTION);
//			System.out.println(population[population.length - 1]);
		}
//		for (int i = 0; i < population.length; i++) {
//			System.out.println(population[i].getGroups().size());
//		}
		return "Generation: " + generation + "\nBin num: " + population[population.length - 1].getGroups().size();
//		for (Group g : population[population.length - 1].getGroups()) {
//			System.out.println(g);
//		}
	}

	private void mutate(Chromosome child, Random random) {
		int binSize = child.getGroups().size();
		if (binSize < 2) {
			return;
		}
		List<GroupObject> reinserted = new ArrayList<>();
		
		Group smallest = child.getGroups().get(0);
		for (Group g : child.getGroups()) {
			if (g.getTotalSize() < smallest.getTotalSize()) {
				smallest = g;
			}
		}
		
		child.getGroups().remove(smallest);
		reinserted.addAll(smallest.getGroup());
		
		for (int i = 0; i < NUM_MUTATE - 1; i++) {
			Group g = child.getGroups().get(random.nextInt(child.getGroups().size()));
			
			child.getGroups().remove(g);
			reinserted.addAll(g.getGroup());
		}
		
		Collections.shuffle(reinserted);
		// TODO
		// identičan kod kao u createPopulation riješi taj problem...
		List<GroupObject> gList = new ArrayList<>();
		double sum = 0;
		
		for (int j = 0; j < reinserted.size(); j++) {
			double num = reinserted.get(j).getSize();
			if (num + sum > data.capacity()) {
				child.getGroups().add(new Group(gList));
				gList = new ArrayList<>();
				sum = 0;
			}
			gList.add(reinserted.get(j));
			sum += num;
		}
		child.getGroups().add(new Group(gList));
	}

//	private void crossParents(Chromosome[] population, Random random) {
//		for (int i = 0; i < NUM_CROSS; i++) {
//			int first = random.nextInt(NUM_CROSS); 
//			int second = random.nextInt(NUM_CROSS); 
//			while (first == second) {
//				second = random.nextInt(NUM_CROSS); 
//			}
//			
//			Chromosome firstParent = population[population.length - 1 - first];
//			Chromosome secondParent = population[population.length - 1 - second];
//			
//			int firstCross1 = random.nextInt((firstParent.getGroups().size() - 1)); 
//			int firstCross2 = random.nextInt(firstParent.getGroups().size()); 
//			while (firstCross1 >= firstCross2) {
//				firstCross2 = random.nextInt(firstParent.getGroups().size()); 
//			}
//			
//			int secondCross1 = random.nextInt((secondParent.getGroups().size() - 1)); 
//			int secondCross2 = random.nextInt(secondParent.getGroups().size()); 
//			while (secondCross1 >= secondCross2) {
//				secondCross2 = random.nextInt(secondParent.getGroups().size()); 
//			}
//			
//			Chromosome child1 = cross(firstParent, secondParent, firstCross1, firstCross2, secondCross1);
//			Chromosome child2 = cross(secondParent, firstParent, secondCross1, secondCross2, firstCross1);
//			
//			//mutate(child1, NUM_MUTATE, random);
//			//mutate(child2, NUM_MUTATE, random);
//			population[2 * i] = child1;
//			population[2 * i + 1] = child2;
//		}
//	}

	private Chromosome cross(Chromosome firstParent, Chromosome secondParent,
							  int firstCross1, int firstCross2, int secondCross1) {
		Chromosome child = Chromosome.copyChromosome(secondParent);
		
		List<GroupObject> redundant = new ArrayList<>();
		int j = 0;
		for (int i = firstCross1; i <= firstCross2; i++, j++) {
			child.getGroups().add(secondCross1 + j , firstParent.getGroups().get(i));
			redundant.addAll(firstParent.getGroups().get(i).getGroup());
		}
		
		for (int i = 0; i < secondCross1; i++) {
			child.getGroups().get(i).getGroup().removeAll(redundant);
		}
		for (int i = secondCross1 + j; i < child.getGroups().size(); i++) {
			child.getGroups().get(i).getGroup().removeAll(redundant);
		}
		for (int i = child.getGroups().size() - 1; i >= 0; i--) {
			if (child.getGroups().get(i).getGroup().isEmpty()) {				
				child.getGroups().remove(i);
			}
		}
		
		return child;
	}

	private void evaluatePopulation(Chromosome[] population, IFunction costFunction) {
		for (int i = 0; i < population.length; i++) {
			evaluateIndividual(population[i], costFunction);
		}
	}

	private void evaluateIndividual(Chromosome chromosome, IFunction costFunction) {
		chromosome.setFitness(costFunction.evaluate(chromosome.getGroups(), data, K));
	}

	private Chromosome[] createPopulation(int populationSize, List<GroupObject> dataList, Random random) {		
		Chromosome[] population = new Chromosome[populationSize];
		
		for (int i = 0; i < population.length; i++) {
			List<Group> group = new ArrayList<>();

			if (random != null) {
				Collections.shuffle(dataList, random);

				List<GroupObject> gList = new ArrayList<>();
				double sum = 0;
				
				for (int j = 0; j < dataList.size(); j++) {
					double num = dataList.get(j).getSize();
					if (num + sum > data.capacity()) {
						group.add(new Group(gList));
						gList = new ArrayList<>();
						sum = 0;
					}
					gList.add(dataList.get(j));
					sum += num;
				}
				group.add(new Group(gList));

			} else {
				
			}
			population[i] = new Chromosome(group);
		}
		
		return population;
	}
	

}
