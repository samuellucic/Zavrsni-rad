package hr.fer.zavrad.ga.insertions.localsearches;

import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.Group;
import hr.fer.zavrad.ga.GroupObject;
import hr.fer.zavrad.ga.insertions.IInsert;

public class LocalSearch1Insert implements IInsert {

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
	}
}
