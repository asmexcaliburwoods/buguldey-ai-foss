package taygalove_shepherd.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GoogleCsvDocumentImpl implements GoogleCsvDocument {

	private Set<String> columnNames=new HashSet<String>();
	private List<GoogleCsvRowImpl> rows=new LinkedList<GoogleCsvRowImpl>();

	@Override
	public void appendRow(GoogleCsvRow e) {
		GoogleCsvRowImpl row=(GoogleCsvRowImpl) e;
		rows.add(row);
	}

	@Override
	public GoogleCsvRow createRow() {
		return new GoogleCsvRowImpl(this);
	}

	@Override
	public String exportAsString() {
		StringBuilder sb=new StringBuilder();
		order=new ArrayList<String>(columnNames.size());
		appendColumnNamesRowTo(sb);
		for(GoogleCsvRowImpl row:rows){
			row.writeln(sb);
		}
		order=null;
		return sb.toString();
	}

	private List<String> order;
	
	private void appendColumnNamesRowTo(StringBuilder sb) {
		for(String colName:columnNames){
			order.add(colName);
		}
		Collections.sort(order);
		for(String colName:order){
			if(sb.length()>0)sb.append(",");
			sb.append(colName);
		}
		sb.append("\r\n");
	}

	void addColName(String colName) {
		columnNames.add(colName);
	}

	List<String> getOrder() {
		return order;
	}
}
