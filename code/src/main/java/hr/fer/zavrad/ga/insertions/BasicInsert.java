package hr.fer.zavrad.ga.insertions;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.Group;
import hr.fer.zavrad.ga.GroupObject;

public class BasicInsert implements IInsert {

	@Override
	public void insert(Chromosome chromosome, List<GroupObject> items, double capacity) {
		for (GroupObject go : items) {
			boolean added = false;
			for (Group g : chromosome.getGroups()) {
				if (go.getSize() + g.getTotalSize() <= capacity) {
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
