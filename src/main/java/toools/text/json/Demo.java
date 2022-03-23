package toools.text.json;

public class Demo {
	public static void main(String[] args) {

		JSONMap s = new JSONMap();
		s.add("luc", "hogie");
		s.add("nad", "hogie");
		s.add("elisa", new JSONArray("hogie", "dfkdj"));
		JSONArray a = new JSONArray("luc", s);
		System.out.println(a);
	}
}
