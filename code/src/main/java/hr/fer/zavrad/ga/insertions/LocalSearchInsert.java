package hr.fer.zavrad.ga.insertions;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.Group;
import hr.fer.zavrad.ga.GroupObject;

public class LocalSearchInsert implements IInsert {

	@Override
	public void insert(Chromosome chromosome, List<GroupObject> items, double capacity) {
		boolean inserted = true;
		while (inserted && !items.isEmpty()) {
			inserted = false;
			
			for (Group g : chromosome.getGroups()) {
				double binSize = g.getTotalSize();
				double maxBinSize = g.getTotalSize();
				if (binSize == capacity) continue;
				
				GroupObject replacement = null;
				GroupObject replaced = null;
				
				for (GroupObject goBin : g.getGroup()) {
					for (GroupObject goList : items) {
						double newBinSize = binSize - goBin.getSize() + goList.getSize();
						if (newBinSize > maxBinSize && newBinSize <= capacity) {
							maxBinSize = newBinSize;
							replacement = goList;
							replaced = goBin;
						}
					}
				}
				
				if (replacement != null) {
					items.remove(replacement);
					items.add(replaced);
					g.getGroup().add(replacement);
					g.getGroup().remove(replaced);
					inserted = true;
				}
			}
		}
		
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
