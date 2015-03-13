package com;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import com.entity.Configuration;
import com.entity.Rule;
import com.exceptions.WordIntroRuleMapException;
import com.util.Constants;
import com.util.Logger;
import com.util.Parse;


public class Aps2Multi {
	
	private static Set<Rule> rules = new HashSet<Rule>();
	
	/**
	 * 转换成small step 时,新增加的rule
	 */
	private static Set<Rule> addedRules = null;
	
	/**
	 * 转换成small step 时,新增加的configuration
	 */
	private static Set<Configuration> addedConfigurations = new HashSet<Configuration>();
	
	private static Set<Configuration> configurations = null;
	
	private static Set<Rule> elimRules = new HashSet<Rule>();
	private static Set<Rule> introRules = new HashSet<Rule>();
	private static Set<Rule> neutralRules = new HashSet<Rule>();
	
	private static Set<Rule> addElimRules = new HashSet<Rule>();
	private static Set<Rule> addIntroRules = new HashSet<Rule>();
	private static Set<Rule> addNeutralRules = new HashSet<Rule>();
	
	private static Map<String,Set<Rule>> wordIntroRuleMap = new HashMap<String,Set<Rule>>();
	
	private static List<Rule> specialNeutralRule = new ArrayList<Rule>();
	
	private static Set<String> symbols = null;
	
	private static Set<Rule> multiRules = new HashSet<Rule>();
	
	private static Map<Configuration,List<Rule>> conRulesMap = new HashMap<Configuration,List<Rule>>();
	
	private static Set<Rule> complementationRules = new HashSet<Rule>();
	
//	private static List<Rule> ama = new ArrayList<Rule>();
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("start");
		Parse.init();
		rules = Parse.getRules();
		configurations = Parse.getConfigurations();
		symbols = Parse.getSymbols();
		Logger.debug("******* symbols:" + symbols.toString());
		
		System.out.println("----------rules size:" + rules.size() + "---------");
		HashSet<Rule> hsRules = new HashSet<Rule>(rules);
		System.out.println("---------- hsRules size:" + hsRules.size() + "---------");
		System.out.println("------------------------------");
		for(Configuration c : configurations){
			System.out.println(c);
		}
		
		Logger.debug(configurations.size()+"");
		for(Rule rule : rules){
			rule.toSmallStep();
			switch(rule.getForm()){
			case Constants.ELIMINATION:
				elimRules.add(rule);
				break;
			case Constants.INTRODUCTION:
				introRules.add(rule);
				String key = rule.getConfiguration().getWord()[0];
				if(wordIntroRuleMap.containsKey(key)){
					wordIntroRuleMap.get(key).add(rule);
				}else{
					Set<Rule> temp = new HashSet<Rule>();
					temp.add(rule);
					wordIntroRuleMap.put(rule.getConfiguration().getWord()[0], temp);
				}
				break;
			case Constants.NEUTRAL:
				neutralRules.add(rule);
				if(rule.getPremise().size() == 0){
					specialNeutralRule.add(rule);
				}
				break;
			}
		}
		Logger.debug("***********alternating pushdown system rules start*******************");
		Logger.debug("rules:" + rules.size() + ";introRules:" + introRules.size() + ";elimRules" + elimRules.size() + ";neutralRules" + neutralRules.size());
		for(Rule rule : rules){
			System.out.println(rule);
			System.out.println();
		}
		Logger.debug("***********alternating pushdown system rules end*******************");
		
//		Alternating pushdown system  -->  small step alternating pushdown system
		 
		APS2STAPS();
		Logger.debug("");
		
		Logger.debug("***********small step alternating pushdown system rules start*******************");
		Logger.debug("rules:" + rules.size() + ";introRules:" + introRules.size() + ";elimRules" + elimRules.size() + ";neutralRules" + neutralRules.size());
		for(Rule rule : rules){
			
			System.out.println(rule);
			System.out.println();
		}
		Logger.debug("***********small step alternating pushdown system rules end********");
		saturation();
		complementation();
		Logger.debug("***********Complementation rules start*******************");
//		Logger.debug("rules:" + rules.size() + ";introRules:" + introRules.size() + ";elimRules" + elimRules.size() + ";neutralRules" + neutralRules.size());
		for(Rule rule : complementationRules){
			
			System.out.println(rule);
			System.out.println();
		}
		Logger.debug("***********Complementation rules end********");
	}

	public static void APS2STAPS() {
		
		for( Rule rule : rules){
			if(!(rule.getForm() > 0)){
				for(Configuration cf : rule.getPremise()){
					TransformCf2SmallStepCf(cf);
				}
				TransformCf2SmallStepCf(rule.getConfiguration());
			}
		}
		if(addedConfigurations != null)
		    configurations.addAll(addedConfigurations);
		
		if(addedRules != null)
			rules.addAll(addedRules);
		
		for(Rule rule : rules){
			rule.toSmallStep();
			switch(rule.getForm()){
			case Constants.ELIMINATION:
				elimRules.add(rule);
				break;
			case Constants.INTRODUCTION:
				introRules.add(rule);
				String key = rule.getConfiguration().getWord()[0];
				if(wordIntroRuleMap.containsKey(key)){
					wordIntroRuleMap.get(key).add(rule);
				}else{
					Set<Rule> temp = new HashSet<Rule>();
					temp.add(rule);
					wordIntroRuleMap.put(rule.getConfiguration().getWord()[0], temp);
				}
				break;
			case Constants.NEUTRAL:
				neutralRules.add(rule);
				if(rule.getPremise().size() == 0){
					specialNeutralRule.add(rule);
				}
				break;
			default:
				Logger.debug("规则:\n" + rule +"不能转换成 small step");
			}
		}
	}

	public static void TransformCf2SmallStepCf(Configuration cf) {
		if(null != cf){
			if(cf.getWord().length >= 0){
				Logger.debug("************ " + cf.toString()+ " ****************");
				if(null == addedConfigurations){
					addedConfigurations = new HashSet<Configuration>();
				}
				if(addedRules == null){
					addedRules = new HashSet<Rule>();
				}
				String[] word = cf.getWord();
				String state = cf.getState();
				if(word.length != 1){
				//不是small step的形式，需要转换
	
					StringBuilder preState = new StringBuilder(state);
					for(int i = 0; i < word.length - 1;i++){
				
						Configuration premiseC = new Configuration(preState + word[i],"");
						addedConfigurations.add(premiseC);
						Configuration conclusionC = new Configuration(preState.toString(),word[i]);
						addedConfigurations.add(conclusionC);
						
						Rule ruleIntroduciton = new Rule(premiseC,conclusionC);
						ruleIntroduciton.setForm(Constants.INTRODUCTION);
						introRules.add(ruleIntroduciton);
						addedRules.add(ruleIntroduciton);
						
						Logger.debug("added Introduction rule:\n" + ruleIntroduciton.toString());
						
						Rule ruleElimination = new Rule(conclusionC,premiseC);
						ruleElimination.setForm(Constants.ELIMINATION);
						elimRules.add(ruleElimination);
						addedRules.add(ruleElimination);
						Logger.debug("added Elimination rule:\n" + ruleElimination.toString());
					
						preState.append(word[i]);
					}
					cf.setState(preState.toString());
					cf.setWord("x");
					Logger.debug("new Configuration:\n" + cf.toString());
				}
				Logger.debug("***********************************************************");
			}
		}
	}


	public static void saturation1 () {
		Logger.debug("saturation 1 elimRule------------------------- ");
		for(Rule elimRule : elimRules){
			Logger.debug(elimRule.toString());
		}
		for(Rule elimRule : elimRules){
			Set<Configuration> premise = elimRule.getPremise();
			Configuration target = null;
			for(Configuration c : premise){
				if(c.getWord().length == 2){
					target = c;
				}
			}
			Logger.debug("target:" + target);
			if(null != target){
				for(Rule introRule : introRules){
					Logger.debug(introRule.getConfiguration().toString());
					if(target.equals(introRule.getConfiguration())){
						Rule tempNeutralRule = new Rule();
						tempNeutralRule.setForm(Constants.NEUTRAL);
						tempNeutralRule.setConfiguration(elimRule.getConfiguration());
						Set<Configuration> tempPremise = new HashSet<Configuration>();
						for(Configuration c : premise){
							if(!c.equals(target)){
								tempPremise.add(c);
							}
						}
						tempPremise.addAll(introRule.getPremise());
						tempNeutralRule.setPremise(tempPremise);
						
						
						neutralRules.add(tempNeutralRule);
						rules.add(tempNeutralRule);
						addNeutralRules.add(tempNeutralRule);
						Logger.debug("saturation 1:added Neutral rule:\n "+ tempNeutralRule);
					}
				}
			}
			
		}
	}
		
	
	public static void saturation2 () {
			for(String key : wordIntroRuleMap.keySet()){
				if(!"#".equals(key)){
					Logger.debug("saturation2 key:" + key);
					Set<String> introStateSet = new HashSet<String>();
					
					HashMap<String,Rule> tempStateRuleMap = new HashMap<String,Rule>();
					for(Rule r : wordIntroRuleMap.get(key)){
						Logger.debug(r.toString());
						introStateSet.add(r.getConfiguration().getState());
						tempStateRuleMap.put(r.getConfiguration().getState(), r);
					}
					
					Logger.debug("saturation 2 neutralRule----------------------------");
					for(Rule neutralRule : neutralRules){
						Logger.debug(neutralRule.toString());
					}
					for(Rule neutralRule : neutralRules){
						Set<String> neutralStateSet = new HashSet<String>();
						for(Configuration c : neutralRule.getPremise()){
							neutralStateSet.add(c.getState());
						}
						Logger.debug("introStateSet:" + introStateSet +"\n neutralStateSet:" + neutralStateSet);
						if(introStateSet.containsAll(neutralStateSet)){
							Logger.debug("true");
							Rule rule = new Rule();
							rule.setForm(Constants.INTRODUCTION);
							Configuration c = new Configuration(neutralRule.getConfiguration().getState(),key);
							Set<Configuration> tempPremise = new HashSet<Configuration>();
							for(Map.Entry<String,Rule> entry : tempStateRuleMap.entrySet()){
								if(neutralStateSet.contains(entry.getKey())){
									tempPremise.addAll(entry.getValue().getPremise());
								}
							}
							rule.setConfiguration(c);
							rule.setPremise(tempPremise);
							
							rules.add(rule);
							addIntroRules.add(rule);
							Logger.debug("saturation 2:added inroduction rule:\n "+ rule);
						}
					}
				}
					
			}
			
			Logger.debug("saturation2 specialNeutralRule --------------------------");
			for(Rule rule : specialNeutralRule){
				Logger.debug("" + rule);
				for(String symbol :symbols ){
					//2
					if(!symbol.equals("#")){
						Rule r1 = new Rule();
						r1.setForm(Constants.INTRODUCTION);
						Configuration c = new Configuration();
						c.setState(rule.getConfiguration().getState());
						c.setWord(symbol);
						r1.setConfiguration(c);
						r1.setPremise(rule.getPremise());
						
						rules.add(r1);
						addIntroRules.add(r1);
						Logger.debug("saturation 2:added inroduction rule:\n "+ r1);
					}
				}
				
				//3
				Rule r2 = new Rule();
				r2.setForm(Constants.INTRODUCTION);
				Configuration c2 = new Configuration();
				c2.setState(rule.getConfiguration().getState());
				c2.setWord("#");
				r2.setConfiguration(c2);
				r2.setPremise(rule.getPremise());
				
				rules.add(r2);
				addIntroRules.add(r2);
				Logger.debug("saturation 3:added inroduction rule:\n "+ r2);
			}
			
			
		}
		
	
	public static void saturation3 () {
			String key = "#";
			Set<String> introStateSet = new HashSet<String>();
			
			if(null != wordIntroRuleMap.get(key)){
				for(Rule r : wordIntroRuleMap.get(key)){
					introStateSet.add(r.getConfiguration().getState());
				}	
			}else{
//				try {
//					throw new WordIntroRuleMapException("wordIntroRuleMap " + key + "key 对应的值为空");
//				} catch (WordIntroRuleMapException e) {
//					e.printStackTrace();
//				}
			}
			
			
			for(Rule neutralRule : neutralRules){
				Logger.debug("neutralRule " + neutralRule);
				Set<String> neutralStateSet = new HashSet<String>();
				for(Configuration c : neutralRule.getPremise()){
					neutralStateSet.add(c.getState());
				}
				if(introStateSet.equals(neutralStateSet)){
					Rule rule = new Rule();
					rule.setForm(Constants.INTRODUCTION);
					Configuration c = new Configuration(neutralRule.getConfiguration().getState(),key);
					Set<Configuration> tempPremise = new HashSet<Configuration>();
					
					rule.setConfiguration(c);
					rule.setPremise(tempPremise);
					
					rules.add(rule);
					addIntroRules.add(rule);
					Logger.debug("saturation 3:added inroduction rule:\n "+ rule);
				}
			}
					
	}
		
	
	public static void saturation(){
		int oldSize = -1;
		int newSize = rules.size();
		while(oldSize != newSize){
			Logger.debug("oldSize:" + oldSize + ";newSize:" + newSize);
			Logger.debug("rules:" + rules.size() + ";introRules:" + introRules.size() + ";elimRules" + elimRules.size() + ";neutralRules" + neutralRules.size());
			saturation1();
			saturation2();
			saturation3();
			
			Logger.debug("**********addIntroRules**************");
			for(Rule rule : addIntroRules){
				Logger.debug(rule.toString());
			}
			
			Logger.debug("**********addElimRules**************");
			for(Rule rule : addElimRules){
				Logger.debug(rule.toString());
			}

			Logger.debug("**********addNeutralRules**************");
			for(Rule rule : addNeutralRules){
				Logger.debug(rule.toString());
			}
			introRules.addAll(addIntroRules);
			elimRules.addAll(addElimRules);
			neutralRules.addAll(addNeutralRules);
			
			Logger.debug("after introRules");
			for(Rule r : introRules){
				Logger.debug(r.toString());
			}
			
			Logger.debug("after elimRules");
			for(Rule r : elimRules){
				Logger.debug(r.toString());
			}
			
			Logger.debug("after neutralRules");
			for(Rule r : neutralRules){
				Logger.debug(r.toString());
			}
			
			for(Rule rule :introRules){
				String key = rule.getConfiguration().getWord()[0];
				if(wordIntroRuleMap.containsKey(key)){
					wordIntroRuleMap.get(key).add(rule);
					Logger.debug("add to wordIntroRuleMap:" + rule);
				}else{
					Set<Rule> temp = new HashSet<Rule>();
					temp.add(rule);
					wordIntroRuleMap.put(rule.getConfiguration().getWord()[0], temp);
					Logger.debug("put to wordIntroRuleMap:" + rule);
				}
			}
			
			oldSize = newSize;
			newSize = rules.size();
			
		}
		
		Logger.debug("***********alternating multi-automaton start*******************");
		introRules.addAll(addIntroRules);
		for(Rule rule : rules){
			if(rule.getForm() == Constants.INTRODUCTION){
				multiRules.add(rule);
				if(conRulesMap.containsKey(rule.getConfiguration())){
					if(!conRulesMap.get(rule.getConfiguration()).contains(rule)){
						conRulesMap.get(rule.getConfiguration()).add(rule);
					}
				}else{
					List<Rule> tempList = new ArrayList<Rule>();
					tempList.add(rule);
					conRulesMap.put(rule.getConfiguration(),tempList);
				}
				Logger.debug(rule.toString());
				Logger.debug("");
			}
		}
		Logger.debug("***********alternating multi-automaton end********");
	}
	public static List<Set<Configuration>> deepFirst(List<Set<Configuration>> list){
		List<Set<Configuration>> retList = new ArrayList<Set<Configuration>>();
		Stack<Configuration> stack = new Stack<Configuration>();
		int i = 0;
		dfs(list,i,stack,list.size(),retList);
		
		return retList;
	}
	
	private static void dfs(List<Set<Configuration>> list,int level,Stack<Configuration> stack,int n,List<Set<Configuration>> retlist) {
		for(Configuration Configuration : list.get(level)){
			stack.push(Configuration);
//			System.out.println("push:" + Configuration);
			if(level == n - 1){
				Set<Configuration> tempSet = new HashSet<Configuration>();
				tempSet.addAll(stack);
				retlist.add(tempSet);
			}
			if( level + 1 < n){
				dfs(list,level+1,stack,n,retlist);
			}
			stack.pop();
		}
		
	}
	public static void complementation(){
		Logger.debug("-------------------------complementation ------------------");
		for(Configuration key : conRulesMap.keySet()){
			Logger.debug("key: \n" + key);
			List<Rule> tempRules = conRulesMap.get(key);
			List<Set<Configuration>> list = new ArrayList<Set<Configuration>>();
			for(Rule rule : tempRules){
				if(rule .getPremise() != null && rule.getPremise().size() > 0){
					list.add(rule.getPremise());
				}
			}
			List<Set<Configuration>> retList = null;
			if(list.size() > 0){
				retList = deepFirst(list);
			}
			if(null != retList){
				for(Set<Configuration> tempPremise :retList){
					Rule tempCompleRule = new Rule();
					
					tempCompleRule.setConfiguration(key);
					tempCompleRule.setPremise(tempPremise);	
					complementationRules.add(tempCompleRule);
					Logger.debug("tempCompleRule:\n" + tempCompleRule);
				}
			}
			
		}
	}
	

}
