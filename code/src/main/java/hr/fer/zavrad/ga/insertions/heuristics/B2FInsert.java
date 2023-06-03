package hr.fer.zavrad.ga.insertions.heuristics;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.Group;
import hr.fer.zavrad.ga.GroupObject;
import hr.fer.zavrad.ga.insertions.IInsert;

public class B2FInsert implements IInsert {

	@Override
	public void insert(Chromosome chromosome, List<GroupObject> items, double capacity) {
		int binIndex = 0;
		while (items.size() > 0) {
			if (binIndex == chromosome.getGroups().size()) {
				chromosome.getGroups().add(new Group(new ArrayList<>()));
			}
			
			GroupObject go = items.get(0);
			Group g = chromosome.getGroups().get(binIndex);
			
			if (go.getSize() + g.getTotalSize() <= capacity) {
				g.getGroup().add(items.remove(0));
			} else {
				GroupObject smallest = g.getGroup().get(0);
				int smallestIndex = 0;
				
				for (int i = 1; i < g.getGroup().size(); i++) {
					if (smallest.getSize() > g.getGroup().get(i).getSize()) {
						smallestIndex = i;
						smallest = g.getGroup().get(i);
					}
				}
				
				loop:
				for (int i = 0; i < items.size() - 1; i++) {
					for (int j = i + 1; j < items.size(); j++) {
						double newSize = items.get(i).getSize() + items.get(j).getSize() + g.getTotalSize() - smallest.getSize();
						if (newSize <= capacity && newSize > g.getTotalSize()) {
							items.add(g.getGroup().remove(smallestIndex));
							g.getGroup().add(items.remove(j));
							g.getGroup().add(items.remove(i));
							break loop;
						}
					}
				}
				binIndex++;
			}
		}
	}
}
