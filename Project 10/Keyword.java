
public enum Keyword {
	CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR, STATIC, FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS;
	
	public static Keyword toKeyword(String name) {
		for(Keyword keyword : values()) {
			if(keyword.name().toLowerCase().equals(name)) {
				return keyword;
			}
		}
		
		return null;
	}
}
