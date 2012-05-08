package taygalove_shepherd.util;

public interface GoogleCsvDocument {
	GoogleCsvRow createRow();
	void appendRow(GoogleCsvRow e);
	String exportAsString();
}
