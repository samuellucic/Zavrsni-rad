package hr.fer.zavrad.ga;

import java.util.List;

public class Group {
	private List<GroupObject> group;

	public Group(List<GroupObject> group) {
		this.group = group;
	}

	public List<GroupObject> getGroup() {
		return group;
	}

	public double getTotalSize() {
		double sum = 0;
		for (GroupObject go : group) {
			sum += go.getSize();
		}
		return sum;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Total size: ").append(getTotalSize());
		for (GroupObject go : group) {
			sb.append("\n\tGroupObject: ").append(go.toString());
		}
		return sb.toString();
	}

}
