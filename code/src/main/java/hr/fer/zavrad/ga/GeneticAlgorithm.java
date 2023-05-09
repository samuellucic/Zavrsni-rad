package hr.fer.zavrad.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import hr.fer.zavrad.data.Data;
import hr.fer.zavrad.ga.insertions.IInsert;

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
	private static final int K = 2;
	private static final double MUTATION_PROBABILITY = 0.02;
	private static final double INVERSE_PROBABILITY = 0.5;
	
	private Data data;
	private IInsert insertionAlgorithm;
	
	public GeneticAlgorithm(Data data, IInsert insertionAlgorithm) {
		Objects.nonNull(insertionAlgorithm);
		this.data = data;
		this.insertionAlgorithm = insertionAlgorithm;
	}
	
	public void setInsertionAlgorithm(IInsert insertionAlgorithm) {
		this.insertionAlgorithm = insertionAlgorithm;
	}
	
	public String algorithm() {
		Random random = new Random();
		List<GroupObject> dataList = new ArrayList<>(data.n());
		for (int i = 0; i < data.n(); i++) {
			dataList.add(new GroupObject(i, data.items()[i]));
		}
		
		Chromosome[] population = createPopulation(POP_SIZE, dataList, random);
		evaluatePopulation(population, COST_FUNCTION);
		
		int generation = 0;
		boolean breakLoop = false;
		for ( ; generation < 100_000; generation++) {
			// Best samples are at the end of the array
			Arrays.sort(population);
			
			for (Chromosome c : population) {
				if (c.getGroups().size() <= data.solution()) {
					//System.out.println(c.getFitness());
					breakLoop = true;
					break;
				}
			}
			if (breakLoop) {
				break;
			}
			
			if (generation % 1000 == 0) {
				System.out.println("Name: " + data.name() + " Generation: " + generation + "\nBin num: " + population[population.length - 1].getGroups().size());
			}
			
			for (int i = 0; i < NUM_CROSS; i++) {
				// Choosing parents from the best NUM_CROSS number of samples
				int first = random.nextInt(NUM_CROSS); 
				int second = random.nextInt(NUM_CROSS); 
				while (first == second) {
					second = random.nextInt(NUM_CROSS); 
				}
				
				Chromosome firstParent = population[population.length - 1 - first];
				Chromosome secondParent = population[population.length - 1 - second];
				
				// TODO
				// PITATI JE LI OVO NAJBOLJI NAČIN NASUMIČNOG ODABIRANJA RODITELJA	
				// ODNOSI SE NA SVE SILNE WHILE PETLJE SVAKI PRIMJER JE ISTI PA JE NEBITNO
				Chromosome child1 = cross(firstParent, secondParent, random);
				Chromosome child2 = cross(secondParent, firstParent, random);

				// TODO
				// PITATI JEL BOLJE RADITI MUTACIJU I INVERZ OVDJE
				// ILI KAKO PISE U ALGORITMU
				// PO MOJIM TESTIRANJIMA BOLJE IDE KADA SE DIJETE DIREKTNO MUTIRA
				 mutate(child1, random);
				 mutate(child2, random);
				
//				 inverse(child1, random);
//				 inverse(child2, random);
				
				population[2 * i] = child1;
				population[2 * i + 1] = child2;
				
				// TODO
				// PITATI GDJE JE BOLJE RADITI EVALUACIJU
				// OVDJE ILI NAKON SVIH OPERATORA KRIŽANJA
				// ZBOG MIJEŠANJA NOVIH I STARIH
//				evaluatePopulation(population, COST_FUNCTION);
				// Arrays.sort(population);
			}
			
//			for (int i = 0; i < NUM_MUTATE; i++) {
//				mutate(population[random.nextInt(POP_SIZE)], random);
//			}
//			
//			for (int i = 0; i < NUM_INVERSE; i++) {
//				inverse(population[random.nextInt(POP_SIZE)], random);
//			}
			
			evaluatePopulation(population, COST_FUNCTION);
//			System.out.println(population[population.length - 1]);
		}

		return "Generation: " + generation + "\nBin num: " + population[population.length - 1].getGroups().size();
	}

	private void mutate(Chromosome child, Random random) {
		if (child.getGroups().size() < 2) {
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
		
		Iterator<Group> it = child.getGroups().iterator();
		while (it.hasNext()) {
			Group g = it.next();
			if (random.nextDouble() <= MUTATION_PROBABILITY) {				
				it.remove();
				reinserted.addAll(g.getGroup());
			}
		}
		
		insertionAlgorithm.insert(child, reinserted, data.capacity());
	}

//	private void inverse(Chromosome child, Random random) {
//		int binSize = child.getGroups().size();
//		
//		int first = random.nextInt(binSize); 
//		int second = random.nextInt(binSize); 
//		while (first == second) {
//			second = random.nextInt(binSize); 
//		}
//
//		Collections.swap(child.getGroups(), first, second);
//	}

	private Chromosome cross(Chromosome firstParent, Chromosome secondParent, Random random) {
		Chromosome child = Chromosome.copyChromosome(secondParent);
		List<GroupObject> redundant = new ArrayList<>();
		List<GroupObject> reinserted = new ArrayList<>();
		
		int firstCross1 = random.nextInt((firstParent.getGroups().size() - 1)); 
		int firstCross2 = random.nextInt(firstParent.getGroups().size()); 
		while (firstCross1 >= firstCross2) {
			firstCross2 = random.nextInt(firstParent.getGroups().size()); 
		}
		int secondCross1 = random.nextInt((secondParent.getGroups().size() - 1)); 

		// New bins offset
		int j = 0;
		for (int i = firstCross1; i <= firstCross2; i++, j++) {
			child.getGroups().add(secondCross1 + j, new Group(new ArrayList<>(firstParent.getGroups().get(i).getGroup())));
			redundant.addAll(firstParent.getGroups().get(i).getGroup());
		}

		for (int i = 0; i < secondCross1; i++) {
			if (child.getGroups().get(i).getGroup().stream().anyMatch(go -> redundant.contains(go))) {
				reinserted.addAll(child.getGroups().get(i).getGroup());
				child.getGroups().get(i).getGroup().clear();
			}
		}
		for (int i = secondCross1 + j; i < child.getGroups().size(); i++) {
			if (child.getGroups().get(i).getGroup().stream().anyMatch(go -> redundant.contains(go))) {
				reinserted.addAll(child.getGroups().get(i).getGroup());
				child.getGroups().get(i).getGroup().clear();
			}
		}
		reinserted.removeAll(redundant);

		for (int i = child.getGroups().size() - 1; i >= 0; i--) {
			if (child.getGroups().get(i).getGroup().isEmpty()) {				
				child.getGroups().remove(i);
			}
		}

		insertionAlgorithm.insert(child, reinserted, data.capacity());
		
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
