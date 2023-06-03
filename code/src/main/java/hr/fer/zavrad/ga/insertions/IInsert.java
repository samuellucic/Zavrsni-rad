package hr.fer.zavrad.ga.insertions;

import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.GroupObject;

public interface IInsert {
	void insert(Chromosome chromosome, List<GroupObject> items, double capacity);
}
