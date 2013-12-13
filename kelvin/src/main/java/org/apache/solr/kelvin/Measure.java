package org.apache.solr.kelvin;

public class Measure {
	private String scorer;
	private String measureName;
	private double value;
	
	public Measure(String _scorer,String _measureName,double _value){
		scorer=_scorer;
		measureName=_measureName;
		value=_value;
	}
	
	@Override
	public String toString() {
		return "" + scorer + " -> " + measureName
				+ " = " + value;
	}
	public String getScorer() {
		return scorer;
	}
	public void setScorer(String scorer) {
		this.scorer = scorer;
	}
	public String getMeasureName() {
		return measureName;
	}
	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
