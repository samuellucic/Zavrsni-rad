package hr.fer.zavrad.ga;

import java.util.List;
import java.util.Objects;

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
	public int hashCode() {
		return Objects.hash(group);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Group))
			return false;
		Group other = (Group) obj;
		return Objects.equals(group, other.group);
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
