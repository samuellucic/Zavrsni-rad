package hr.fer.zavrad.ga.insertions.localsearches;

import java.util.ArrayList;
import java.util.List;

import hr.fer.zavrad.ga.Chromosome;
import hr.fer.zavrad.ga.Group;
import hr.fer.zavrad.ga.GroupObject;
import hr.fer.zavrad.ga.insertions.IInsert;

public class LocalSearch4Insert implements IInsert {

	private static final int BIN_MAX = 3;
	private static final int ITEMS_MAX = 2;
	
	@Override
	public void insert(Chromosome chromosome, List<GroupObject> items, double capacity) {
		List<Group> groups = chromosome.getGroups();
		for (Group g : groups) {
			if (g.getTotalSize() == capacity) continue;
			iterations(items, g, capacity);
		}
	}
	
	private void iterations(List<GroupObject> items, Group group, double capacity) {
		List<List<GroupObject>> lists = null;
		double newBinSize = 0;
		for (int i = 1; i <= BIN_MAX; i++) {
			for (int j = 1; j <= ITEMS_MAX; j++) {
				List<List<GroupObject>> newLists = change(items, group, new ArrayList<>(), new ArrayList<>(), 0, 0, capacity, i, j);				
				if (newLists == null) {
					continue;
				}
				double newSize = getNewBinSize(newLists.get(0), newLists.get(1), group.getTotalSize(), capacity);
				if (newSize > newBinSize) {
					lists = newLists;
					newBinSize = newSize;
				}
			}
		}
		
		if (lists == null) return;
		for (GroupObject go : lists.get(0)) {
			items.add(go);
			group.getGroup().remove(go);
		}
		for (GroupObject go : lists.get(1)) {
			items.remove(go);
			group.getGroup().add(go);
		}
	}

	private List<List<GroupObject>> change(
			List<GroupObject> items,
			Group group,
			List<GroupObject> binReplacements,
			List<GroupObject> itemsReplacements,
			int numBin,
			int numItems,
			double capacity,
			int binMax,
			int itemsMax) {
		if (binReplacements.size() == binMax && itemsReplacements.size() == itemsMax) {
			return List.of(binReplacements, itemsReplacements);
		}
		for (int i = numBin; i < group.getGroup().size(); i++) {
			for (int j = numItems; j < items.size(); j++) {
				List<GroupObject> binReplacementsNew = new ArrayList<>(binReplacements);
				List<GroupObject> itemsReplacementsNew = new ArrayList<>(itemsReplacements);
				binReplacementsNew.add(group.getGroup().get(i));
				itemsReplacementsNew.add(items.get(j));
				
				List<List<GroupObject>> lists = change(
						items,
						group,
						binReplacementsNew,
						itemsReplacementsNew,
						i + 1,
						j + 1,
						capacity,
						binMax,
						itemsMax);
				if (lists == null) continue;
				if (check(lists.get(0), lists.get(1), group.getTotalSize(), capacity)) {
					return lists;
				}
			}
		}
		
		return null;
	}
	
	private boolean check(List<GroupObject> binReplacements, List<GroupObject> itemReplacements, double binSize, double capacity) {
		double newBinSize = getNewBinSize(binReplacements, itemReplacements, binSize, capacity);
		return newBinSize > binSize && newBinSize <= capacity;
	}
	
	private double getNewBinSize(List<GroupObject> binReplacements, List<GroupObject> itemReplacements, double binSize, double capacity) {
		double newBinSize = binSize;
		for (GroupObject go : binReplacements) {
			newBinSize -= go.getSize();
		}
		for (GroupObject go : itemReplacements) {
			newBinSize += go.getSize();
		}
		
		return newBinSize;
	}
}
