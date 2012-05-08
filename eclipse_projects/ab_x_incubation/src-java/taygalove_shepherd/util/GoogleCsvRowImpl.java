package taygalove_shepherd.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class GoogleCsvRowImpl implements GoogleCsvRow {

	private final GoogleCsvDocumentImpl doc;
	private Map<String, String> row=new HashMap<String, String>();

	public GoogleCsvRowImpl(GoogleCsvDocumentImpl googleCsvDocumentImpl) {
		this.doc=googleCsvDocumentImpl;
	}

	@Override
	public void setColumn(String colName, String value) {
		row.put(colName, value);
		doc.addColName(colName);
	}

	public void writeln(StringBuilder sb) {
		List<String> order=doc.getOrder();
		boolean first=true;
		for(String colName:order){
			if(!first)sb.append(",");
			String v=StringUtil.makeEmpty(row.get(colName));
			sb.append(v.length()==0?v:"\""+quoteQuotes(v)+"\"");
			first=false;
		}
		sb.append("\r\n");
	}
	//GTD check taygalove_shepherd.addressbook.ab.datamodel.AddressBookImpl#encodeAsGoogleCsv() for validity of `first` и `i`.

	private String quoteQuotes(String v) {
		StringTokenizer st=new StringTokenizer(v, "\"", true);
		StringBuilder sb=new StringBuilder();
		while(st.hasMoreTokens()){
			String t=st.nextToken();
			if(t.equals("\""))t="\"\"";
			sb.append(t);
		}
		return sb.toString();
	}
}
