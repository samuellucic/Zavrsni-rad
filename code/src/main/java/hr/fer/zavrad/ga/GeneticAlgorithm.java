package hr.fer.zavrad.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import hr.fer.zavrad.data.Data;
import hr.fer.zavrad.ga.insertions.IInsert;
import hr.fer.zavrad.ga.insertions.heuristics.B2FInsert;
import hr.fer.zavrad.ga.insertions.heuristics.BFDInsert;
import hr.fer.zavrad.ga.insertions.heuristics.BasicInsert;
import hr.fer.zavrad.ga.insertions.heuristics.FFDInsert;
import hr.fer.zavrad.ga.insertions.localsearches.LocalSearch2Insert;
import hr.fer.zavrad.ga.insertions.localsearches.LocalSearch3Insert;
import hr.fer.zavrad.ga.insertions.localsearches.LocalSearch4Insert;
import hr.fer.zavrad.ga.insertions.localsearches.LocalSearchInsert;

public class GeneticAlgorithm {
	private static IFunction COST_FUNCTION = (groups, data, k) -> {
		double cost = 0;
		for (Group g : groups) {
			cost += Math.pow((double)g.getTotalSize() / data.capacity(), k);
		}
		return cost / (double)groups.size();
	};
	
	private static final int POP_SIZE = 50;
	private static final int NUM_CROSS = 24;
	private static final int K = 2;
	private static final int TOURNAMENT_NUM = 2;
	private static final double MUTATION_PROBABILITY = 0.2;

	private static IInsert basicInsert = new BasicInsert();
	private static IInsert[] heuristics = {
			new FFDInsert(),
			new BFDInsert(),
			new B2FInsert()};
	private static IInsert[] localSearches = {
			new LocalSearchInsert(),
			new LocalSearch2Insert(),
			new LocalSearch3Insert(),
			new LocalSearch4Insert()};
	
	
	private Data data;
	private boolean hybridized;
	
	public GeneticAlgorithm(Data data, boolean hybridized) {
		this.data = data;
		this.hybridized = hybridized;
	}
	
	public void setHybridized(boolean hybridized) {
		this.hybridized = hybridized;
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
		
		loop:
		for ( ; generation < 100_000; generation++) {
			// Best samples are at the end of the array
			Arrays.sort(population);
			
			for (Chromosome c : population) {
				if (c.getGroups().size() <= data.solution()) {
					//System.out.println(c.getFitness());
					break loop;
				}
			}
			// TODO
			// OVO SLUZI DA ZNAM DA PROGRAM ZAPRAVO RADI
			// OBRISATI KAD ZAVRSIM
//			if (generation % 1000 == 0) {
//				System.out.println("Name: " + data.name() + " Generation: " + generation + "\nBin num: " + population[population.length - 1].getGroups().size());
//			}
			
			Chromosome[] newPopulation = new Chromosome[NUM_CROSS * 2];
			for (int i = 0; i < NUM_CROSS; i++) {
				// Choosing parents from the best NUM_CROSS number of samples
				Chromosome firstParent = pickParent(population, random, TOURNAMENT_NUM);
				Chromosome secondParent = pickParent(population, random, TOURNAMENT_NUM);
				
				Chromosome child1 = cross(firstParent, secondParent, random);
				Chromosome child2 = cross(secondParent, firstParent, random);

				mutate(child1, random);
				mutate(child2, random);
				
				newPopulation[2 * i] = child1;
				newPopulation[2 * i + 1] = child2;
			}
			for (int i = 0; i < newPopulation.length; i++) {
				population[i] = newPopulation[i];
			}

			evaluatePopulation(population, COST_FUNCTION);
		}
		
		return "Generation: " + generation 
				+ "\nHybridized: " + hybridized
				+ "\nBin num: " + population[population.length - 1].getGroups().size()
				+ "\nOptimal: " + (population[population.length - 1].getGroups().size() == data.solution());
	}
	
	private Chromosome pickParent(Chromosome[] population, Random random, int tournamentNum) {
		List<Chromosome> tournamentPop = new ArrayList<>();
		
		while (tournamentPop.size() < tournamentNum) {
			Chromosome newParent = population[random.nextInt(population.length)];
			if (!tournamentPop.contains(newParent)) {
				tournamentPop.add(newParent);
			}
		}
		tournamentPop.sort(null);;
		
		return tournamentPop.get(tournamentNum - 1);
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
		
		if (hybridized) {
			getInsertAlgorithm(random, localSearches).insert(child, reinserted, data.capacity());
			getInsertAlgorithm(random, heuristics).insert(child, reinserted, data.capacity());
		} else {
			basicInsert.insert(child, reinserted, data.capacity());
		}
	}

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

		if (hybridized) {			
			getInsertAlgorithm(random, localSearches).insert(child, reinserted, data.capacity());
			getInsertAlgorithm(random, heuristics).insert(child, reinserted, data.capacity());
		} else {
			basicInsert.insert(child, reinserted, data.capacity());
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
			}
			
			population[i] = new Chromosome(group);
		}
		
		return population;
	}
	
	private IInsert getInsertAlgorithm(Random random, IInsert[] insertAlgorithms) {
		return insertAlgorithms[random.nextInt(insertAlgorithms.length)];
	}
	
}
