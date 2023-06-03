package hr.fer.zavrad.ga.insertions.heuristics;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.Group;
import hr.fer.zavrad.ga.GroupObject;
import hr.fer.zavrad.ga.insertions.IInsert;

public class BFDInsert implements IInsert {

	@Override
	public void insert(Chromosome chromosome, List<GroupObject> items, double capacity) {
		items.sort(GroupObject.SIZE_COMPARATOR.reversed());
		
		for (GroupObject go : items) {
			int groupIndex = -1;
			double maxSize = -1;
			
			int i = 0;
			for (Group g : chromosome.getGroups()) {
				double newMax = go.getSize() + g.getTotalSize();
				if (newMax <= capacity && newMax > maxSize) {
					groupIndex = i;
					maxSize = newMax;
				}
				i++;
			}
			
			if (groupIndex > -1) {
				chromosome.getGroups().get(groupIndex).getGroup().add(go);
			} else {
				List<GroupObject> gList = new ArrayList<>();
				gList.add(go);
				chromosome.getGroups().add(new Group(gList));
			}
		}
	}
}
