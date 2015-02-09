package com.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.entity.Configuration;
import com.entity.Rule;


public class Parse {
    //输入文件
	private static String input = "src/input.in.bak";
	
	
	private static BufferedReader BR = null;
	
	private static ArrayList<Rule> rules = new ArrayList<Rule>();
	public static ArrayList<Rule> getRules() {
		return rules;
	}

	public static void setRules(ArrayList<Rule> rules) {
		Parse.rules = rules;
	}

	public static List<Configuration> getConfigurations() {
		return configurations;
	}

	public static void setConfigurations(List<Configuration> configurations) {
		Parse.configurations = configurations;
	}

	private static List<Configuration> configurations = new ArrayList<Configuration>();
	
	public static void init(){
		init(input);
	}
	
	/**
	 * 打开输入文件
	 * @param input
	 */
	public static void init(String input){
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(new File(input)));
			BR = br;
			readRules();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			close();
		}
	}
	
	private static void close() {
		try {
			BR.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 从打开的BufferedReader中读取规则，并将规则rule存入rules
	 * @return 读取的所有规则
	 */
	public static void readRules(){
		
		try {
			if(null == BR){
				throw new Exception("调用readRules方法前,请先调用init方法");
			}
			String line = null;
			while (null != ( line = BR.readLine() ) ) {
				Logger.debug(line);
				Rule rule = parseRlue(line);
				rules.add(rule);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			close();
		}
	}
	/**
	 * 将rule字符串解析成rule,并将其中的configuration存入configurations
	 * @param line rule字符串
	 * @return Rule
	 */
	private static Rule parseRlue(String line) {
        //p1(a1,a4) p2(a2,a3) -> p'1(a2,a3)
		Rule rule = new Rule();
		
		String[] confugrationStrs = line.split("->");
		
		String premiseStr = confugrationStrs[0];
		String configurationStr = confugrationStrs[1];
		
		List<Configuration> premise = parsePremise(premiseStr);
		Configuration configuration = pareConfiguration(configurationStr);
		
	
		
		rule.setConfiguration(configuration);
		rule.setPremise(premise);
		
		return rule;
	}
	
	/**
	 * 将Configuration字符串解析成Configuration
	 * @param line Configuration字符串
	 * @return Configuration
	 */
	private static Configuration pareConfiguration(String configurationStr) {
		// p'1(a2,a3)
		Configuration c = null;
		
		//去掉configuration字符串中的空格
		configurationStr = configurationStr.trim();
		Logger.debug("configurationStr:" + configurationStr);
		if (!configurationStr.equals("")){
			c = new Configuration();
			String[] tempStrs = configurationStr.split("\\)");
			
			// tempStrs[0]:p'1(a2,a3
//			tempStrs[0] += ",x";
			String state = tempStrs[0].split("\\(")[0];
			String[] word = null;
			if( tempStrs[0].split("\\(").length == 1){
				//p()
				word = new String[1];
				word[0] = "";
			}else{
				word = ((tempStrs[0] + ",x").split("\\(")[1]).split(",");
			}
			
			c.setState(state);
			c.setWord(word);
			Logger.debug("" + c);
		}else{
			c = null;
		}
		//加入configurations中
		if(c == null){
			if(!configurations.contains(null))
				configurations.add(c);
		}else{
			configurations.add(c);
		}
		
		return c;
	}
	
	/**
	 * 将premise字符串解析成premise
	 * @param line premise字符串
	 * @return premise
	 */
	private static List<Configuration> parsePremise(String premiseStr) {
		// p1(a1,a4) & p2(a2,a3)
		List<Configuration> premise = new ArrayList<Configuration>();
		for(String temp : premiseStr.split("&")){
			Configuration c = pareConfiguration(temp);
			if ( null != c){
				premise.add(c);
		    }
		}
		return premise;
	}

}
