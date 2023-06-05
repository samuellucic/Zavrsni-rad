package hr.fer.zavrad.ga.insertions.heuristics;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.Group;
import hr.fer.zavrad.ga.GroupObject;
import hr.fer.zavrad.ga.insertions.IInsert;

public class FFDInsert implements IInsert {

	@Override
	public void insert(Chromosome chromosome, List<GroupObject> items, double capacity) {
		items.sort(GroupObject.SIZE_COMPARATOR.reversed());
		
		for (GroupObject go : items) {
			boolean added = false;
			for (Group g : chromosome.getGroups()) {
				if (Double.compare(go.getSize() + g.getTotalSize(), capacity) <= 0) {
					g.getGroup().add(go);
					added = true;
					break;
				}
			}
			if (!added) {
				List<GroupObject> gList = new ArrayList<>();
				gList.add(go);
				chromosome.getGroups().add(new Group(gList));
			}
		}
	}
}
