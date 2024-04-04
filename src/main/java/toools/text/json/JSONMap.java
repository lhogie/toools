package toools.text.json;

import java.util.ArrayList;
import java.util.List;

import toools.collection.Pair;
import toools.text.TextUtilities;

public class JSONMap extends JSONElement {

	List<Pair> l = new ArrayList<>();

	public void add(Object k, Object v) {
		if (k == null)
			throw new NullPointerException();

		if (v == null)
			throw new NullPointerException();

		l.add(new Pair(k, v));
	}

	@Override
	public String toString(int tab, boolean alwaysQuote) {
		String s = "{";

		for (int i = 0; i < l.size(); ++i) {
			Pair p = l.get(i);
			s += "\n" + TextUtilities.repeat(tabText, tab + 1) + quoteIfNecessary(p.a.toString(), alwaysQuote) + ": ";

			if (p.b instanceof JSONElement) {
				s += ((JSONElement) p.b).toString(tab + 1, alwaysQuote);
			} else {
				s += quoteIfNecessary(p.b.toString(), alwaysQuote);
			}

			if (i < l.size() - 1)
				s += ",";
		}

		s += "\n" + TextUtilities.repeat(tabText, tab) + "}";
		return s;
	}

}
