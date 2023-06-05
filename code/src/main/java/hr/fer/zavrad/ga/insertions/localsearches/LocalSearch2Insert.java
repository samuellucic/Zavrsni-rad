package hr.fer.zavrad.ga.insertions.localsearches;

import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.Group;
import hr.fer.zavrad.ga.GroupObject;
import hr.fer.zavrad.ga.insertions.IInsert;

public class LocalSearch2Insert implements IInsert {

	@Override
	public void insert(Chromosome chromosome, List<GroupObject> items, double capacity) {
		boolean inserted = true;
		while (inserted && !items.isEmpty()) {
			inserted = false;
			
			for (Group g : chromosome.getGroups()) {
				double binSize = g.getTotalSize();
				double maxBinSize = binSize;
				if (Double.compare(binSize, capacity) == 0) continue;
				
				GroupObject replacement1 = null;
				GroupObject replacement2 = null;
				GroupObject replaced = null;
				
				for (GroupObject goBin : g.getGroup()) {
					for (int i = 0; i < items.size() - 1; i++) {
						GroupObject goList1 = items.get(i);
						for (int j = i + 1; j < items.size(); j++) {
							GroupObject goList2 = items.get(j);
							
							double newBinSize = binSize - goBin.getSize() + goList1.getSize() + goList2.getSize();
							if (Double.compare(newBinSize, maxBinSize) > 0 && Double.compare(newBinSize, capacity) <= 0) {
								maxBinSize = newBinSize;
								replacement1 = goList1;
								replacement2 = goList2;
								replaced = goBin;
							}
						}
					}
				}
				
				if (replacement1 != null) {
					items.remove(replacement1);
					items.remove(replacement2);
					items.add(replaced);
					g.getGroup().add(replacement1);
					g.getGroup().add(replacement2);
					g.getGroup().remove(replaced);
					inserted = true;
				}
			}
		}
	}

}
