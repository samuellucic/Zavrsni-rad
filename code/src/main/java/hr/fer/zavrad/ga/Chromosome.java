package hr.fer.zavrad.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chromosome implements Comparable<Chromosome> {
	private List<Group> groups;
	private double fitness;

	public Chromosome(List<Group> groups) {
		this.groups = groups;
		this.fitness = 0;
	}
	
	public List<Group> getGroups() {
		return groups;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}
	
	public static Chromosome copyChromosome(Chromosome o) {
		List<Group> groups = new ArrayList<>();
		for (Group g : o.groups) {
			groups.add(new Group(new ArrayList<>(g.getGroup())));
		}
		return new Chromosome(groups);
	}
	
	@Override
	public int compareTo(Chromosome o) {
		if (this.fitness < o.fitness) {
			return -1;
		}
		if (this.fitness > o.fitness) {
			return 1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(groups);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Chromosome)) return false;
		Chromosome other = (Chromosome) obj;
		return Objects.equals(groups, other.groups);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Fitness: ").append(fitness)
		  .append("\nNum of bins: ").append(groups.size())
		  .append("\nGroup: {");
		for (Group g : groups) {
			sb.append("\n").append(g);
		}
		return sb.append("\n}").toString();
	}
	
}
