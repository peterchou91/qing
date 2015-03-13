package com.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.exceptions.IllegalWordException;

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
		this.setState(state);
		this.setWord(word);
	}
	
	/*如果字段是boolean 计算为(f?1:0);
	如果字段是byte,char,short,int则计算为 (int)f;
	如果字段是long 计算为 (int)(f^(f>>32));
	如果字段是float 计算为 Float.floatToLongBits(f);
	如果字段是一个引用对象，那么直接调用对象的hashCode方法，如果需要判空，可以加上如果为空就返回0;
	如果字段是一个数组则需要遍历所有元素，按上面几种方法计算；*/
	@Override
	public int hashCode(){
		int result = 3;
		int f1 = 0;
		int f2 = 0;
		if(null != state){
			f1 = state.hashCode();
		}
		if(null != word){
			f2 = Arrays.hashCode(word);
		}
		result = 31 * result + f1;
		result = 31 * result + f2;
		
		return result;
	}
	
	public boolean equals(Object object){
		if(object != null && object instanceof Configuration){
			Configuration c = (Configuration)object;
			if(this.getState().equals( c.getState()) && this.wordEquals(c.getWord())){
				return true;
			}
		}
		return false;
	}

	private boolean wordEquals(String[] word2) {
		if(word2 == null || this.getWord().length != word2.length){
			return false;
		}
		List<String> word2List =  Arrays.asList(word2);
		List<String> wordList =  Arrays.asList(this.getWord());
		HashSet<String> word2ListHs = new HashSet<String>(word2List);
		HashSet<String> wordListHs = new HashSet<String>(wordList);
		if(!word2ListHs.equals(wordListHs)){
			return false;
		}
		
		return true;
	}

	public String[] getWord() {
		return word;
	}
	public void setWord(String[] word) {
		if(word == null){
			try {
				throw new IllegalWordException("数组对象word不能为null");
			} catch (IllegalWordException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.word = word;
	}
	
	/**
	 * 
	 * @param word "a1,a2,x"
	 */
	public void setWord(String word){
		if(word.equals("")){
			this.word = new String[]{"x"};
		}else if(word.contains("#")){
			this.word = new String[]{"#"};
		}else if (!word.contains("x")){
			this.word = (word+",x").split(",");
		}else
			this.word = word.split(",");
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		//
		this.state = state.trim();
	}
	
	@Override
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
