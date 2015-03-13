package com.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.entity.Configuration;
import com.entity.Rule;


public class Parse {
    //�����ļ�
	private static String input = "src/testExample.input";
	
	
	private static BufferedReader BR = null;
	
	private static Set<Rule> rules = new HashSet<Rule>();
	private static Set<Configuration> configurations = new HashSet<Configuration>();
	private static Set<String> symbols = new HashSet<String>();
	
	public static Set<String> getSymbols() {
		return symbols;
	}

	public static void setSymbols(Set<String> symbols) {
		Parse.symbols = symbols;
	}

	public static Set<Rule> getRules() {
		return rules;
	}

	public static void setRules(Set<Rule> rules) {
		Parse.rules = rules;
	}

	public static Set<Configuration> getConfigurations() {
		return configurations;
	}

	public static void setConfigurations(Set<Configuration> configurations) {
		Parse.configurations = configurations;
	}

	
	public static void init(){
		init(input);
	}
	
	/**
	 * �������ļ�
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
	 * �Ӵ򿪵�BufferedReader�ж�ȡ���򣬲�������rule����rules
	 * @return ��ȡ�����й���
	 */
	public static void readRules(){
		
		try {
			if(null == BR){
				throw new Exception("����readRules����ǰ,���ȵ���init����");
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
	 * ��rule�ַ���������rule,�������е�configuration����configurations
	 * @param line rule�ַ���
	 * @return Rule
	 */
	private static Rule parseRlue(String line) {
        //p1(a1,a4) p2(a2,a3) -> p'1(a2,a3)
		Rule rule = new Rule();
		
		String[] confugrationStrs = line.split("->");
		
		String premiseStr = confugrationStrs[0];
		String configurationStr = confugrationStrs[1];
		
		Set<Configuration> premise = parsePremise(premiseStr);
		Configuration configuration = pareConfiguration(configurationStr);
		
	
		
		rule.setConfiguration(configuration);
		rule.setPremise(premise);
		
		return rule;
	}
	
	/**
	 * ��Configuration�ַ���������Configuration
	 * @param line Configuration�ַ���
	 * @return Configuration
	 */
	private static Configuration pareConfiguration(String configurationStr) {
		// p'1(a2,a3)
		Configuration c = null;
		
		//ȥ��configuration�ַ����еĿո�
		configurationStr = configurationStr.trim();
		Logger.debug("configurationStr:" + configurationStr);
		if (!configurationStr.equals("")){
			c = new Configuration();
			String[] tempStrs = configurationStr.split("\\)");
			
			// tempStrs[0]:p'1(a2,a3
			String[] temp = tempStrs[0].split("\\(");
			//temp[0]:p'1( ; temp[1]:a2,a3
			String state = temp[0];
			String[] word = null;
			if( temp.length == 1){
				//p()
				word = new String[1];
				word[0] = "";
			}else{

				if(temp[1].trim().equalsIgnoreCase("#")){
					word = new String[]{"#"};
				}else{
//					word = ((tempStrs[0] + ",x").split("\\(")[1]).split(",");
					if(!temp[1].contains("x")){
						word = (temp[1] + ",x").split(",");
					}else{
						word = temp[1].split(",");
					}
				}
				for(int i = 0; i < word.length - 1;i++){
					symbols.add(word[i]);
				}
			}
			
			c.setState(state);
			c.setWord(word);
			Logger.debug("" + c);
		}else{
			c = null;
		}
		//����configurations��
		if(c == null){
			if(!configurations.contains(null))
				configurations.add(c);
		}else{
			configurations.add(c);
		}
		
		return c;
	}
	
	/**
	 * ��premise�ַ���������premise,���premise�ַ���Ϊ��,��premise��СΪ0
	 * @param line premise�ַ���
	 * @return premise
	 */
	private static Set<Configuration> parsePremise(String premiseStr) {
		// p1(a1,a4) & p2(a2,a3)
		Set<Configuration> premise = new HashSet<Configuration>();
		for(String temp : premiseStr.split("&")){
			Configuration c = pareConfiguration(temp);
			if ( null != c){
				premise.add(c);
		    }
		}
		return premise;
	}

}
