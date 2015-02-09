package com.entity;
import java.util.ArrayList;
import java.util.List;

import com.util.Constants;
import com.util.EllegalFormException;
import com.util.Logger;
import com.util.RuleFormNotInEinException;


public class Rule {
	private int form = 0;
	
	private List<Configuration> premise = null;
	private Configuration configuration = null;
	public List<Configuration> getPremise() {
		return premise;
	}
	public void setPremise(List<Configuration> premise) {
		this.premise = premise;
	}
	public Configuration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public Rule(List<Configuration> premise,Configuration configuration){
		this.premise = premise;
		this.configuration = configuration;
	}
	public Rule(Configuration premise,Configuration configuration){
		ArrayList<Configuration> l = new ArrayList<Configuration>();
		l.add(premise);
		this.premise = l;
		this.configuration = configuration;
	}
	public Rule(){
		
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for ( Configuration c : premise ){
			sb.append(c).append(" ");
		}
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
		}
		
		sb.append("\n");
		int len1 = sb.length();
		for ( Configuration c : premise ){
			if(null != c){
				int len = 3;
				len += c.getState().length();
				for ( String symbol : c.getWord()){
					len += symbol.length();
				}
				++len;
				for(int i = 0 ; i < len ; i++){
					sb.append("-");
				}
			}
		}
		
		int len2 = sb.length();
		if (len1 == len2){
			sb = addUnderlineForConf(sb,configuration);
		}
		if(this.getForm() == Constants.ELIMINATION){
			sb.append("ELIMINATION");
		}else if (this.getForm() == Constants.INTRODUCTION){
			sb.append("INTRODUCTION");
		}else if (this.getForm() == Constants.NEUTRAL){
			sb.append("NEUTRAL");
		}else{
			sb.append(this.getForm());
		}
		
		sb.append("\n");
		
		sb.append(configuration);
		return sb.toString();
	}
	
	private StringBuilder addUnderlineForConf(StringBuilder sb,
			Configuration configuration2) {
		int len = 3;
		len += configuration.getState().length();
		for ( String symbol : configuration.getWord()){
			len += symbol.length();
		}
		++len;
		for(int i = 0 ; i < len ; i++){
			sb.append("-");
		}
		return sb;
	}
	public int getForm(){
		return this.form;
	}
	
	public void setForm(int form){
		try {
			if( form <0 || form > 3){
				throw new EllegalFormException("a rule can only be an introduction ,elimination or neutral");
			}else{
				this.form = form;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void toSmallStep() {
		if(this.getForm() == 0){
			int wordExInPre = 0;
			Configuration gama = null;
			for(Configuration c : premise){
				String[] word = c.getWord();
				Logger.debug("state:" + c.getState());
				Logger.debug("word[0]:" + word[0]);
				if(word[0].equals("x")){
					wordExInPre += 1;
				}else{
					gama = c;
				}
			}
			Logger.debug("wordExInPre=" + wordExInPre);
			Logger.debug("premise size:" + premise.size());
			int wordExInCon = this.configuration.getWord()[0].equals("x") ? 1:0;
			
			if(((wordExInPre == this.premise.size() || this.getPremise().size() == 0) && this.configuration.getWord().length == 2) || (this.premise.size() == 0 && this.configuration.getWord()[0].equals(""))){
				this.setForm(Constants.INTRODUCTION);
				// todo
				
			}else if ((wordExInPre == this.premise.size() - 1 && gama.getWord().length == 2 )&& this.configuration.getWord()[0].equals("x") ){
				this.setForm(Constants.ELIMINATION);
			}else if ((wordExInPre == this.premise.size() || premise.size() == 0)&& this.configuration.getWord()[0].equals("x")){
				this.setForm(Constants.NEUTRAL);
			}else{
				Logger.debug("这条规则不合法不能转到small step");
				Logger.debug(this.toString());
				Logger.debug("不合法规则结束");
			}
		}
		
	}
	
}
