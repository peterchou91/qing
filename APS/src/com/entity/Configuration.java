package com.entity;

public class Configuration {
	//p'1(a2,a3)
	private String[] word = null;
	private String state = null;
	
	public Configuration(){
		
	}
	
	public Configuration(String state,String[] word){
		this.setState(state);
		this.setWord(word);
	}
	
	public Configuration(String state,String word){
		this(state,(word+",x").split(","));
	}
	
	

	public String[] getWord() {
		return word;
	}
	public void setWord(String[] word) {
		this.word = word;
	}
	
	/**
	 * 
	 * @param word "a1,a2"
	 */
	public void setWord(String word){
		this.word = (word).split(",");
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		//去掉state中的空格
		this.state = state.trim();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(state).append("(");
		for ( int i = 0; i < word.length; i++ ){
			if(!word[i].equals("")){
				sb.append(word[i]);
				if ( i < word.length - 1){
					sb.append(",");
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}

}
