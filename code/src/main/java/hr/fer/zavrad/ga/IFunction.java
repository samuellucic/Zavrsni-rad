package hr.fer.zavrad.ga;

import java.util.List;

import hr.fer.zavrad.data.Data;

public interface IFunction {
	double evaluate(List<Group> c, Data dataset, double k);
}
