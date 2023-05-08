package hr.fer.zavrad.ga;

import java.util.Objects;

public class GroupObject {
	private int index;
	private double size;

	public GroupObject(int index, double size) {
		this.index = index;
		this.size = size;
	}

	public int getIndex() {
		return index;
	}

	public double getSize() {
		return size;
	}

	@Override
	public int hashCode() {
		return Objects.hash(index);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof GroupObject))
			return false;
		GroupObject other = (GroupObject) obj;
		return index == other.index;
	}

	@Override
	public String toString() {
		return "Index:" + index + "; Size: " + size;
	}
}
