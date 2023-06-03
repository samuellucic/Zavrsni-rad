package hr.fer.zavrad.ga.insertions.localsearches;

import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.Group;
import hr.fer.zavrad.ga.GroupObject;
import hr.fer.zavrad.ga.insertions.IInsert;

public class LocalSearch3Insert implements IInsert {

	@Override
	public void insert(Chromosome chromosome, List<GroupObject> items, double capacity) {
		List<Group> groups = chromosome.getGroups();
		boolean replaced = true;
		
		while (replaced) {
			replaced = false;
			
			for (int i = 0; i < groups.size() - 1; i++) {
				Group g1 = groups.get(i);
				
				for (int j = i + 1; j < groups.size(); j++) {
					Group g2 = groups.get(j);
					
					groupIter:
					for (GroupObject go1 : g1.getGroup()) {
						double binSize1 = g1.getTotalSize();
						
						for (GroupObject go2: g2.getGroup()) {
							double newBinSize1 = binSize1 - go1.getSize() + go2.getSize();
							double newBinSize2 = g2.getTotalSize() - go2.getSize() + go1.getSize();
							
							if (newBinSize1 > binSize1 && newBinSize1 <= capacity && newBinSize2 <= capacity) {
								g1.getGroup().remove(go1);
								g1.getGroup().add(go2);
								g2.getGroup().remove(go2);
								g2.getGroup().add(go1);
								
								replaced = true;
								break groupIter;
							}
						}
					}
				}
			}
		}
	}

}
