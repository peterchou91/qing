package com;
import java.util.ArrayList;
import java.util.List;

import com.entity.Configuration;
import com.entity.Rule;
import com.util.Constants;
import com.util.Logger;
import com.util.Parse;


public class Aps2Multi {
	
	private static List<Rule> rules = null;
	private static List<Rule> addedRules = null;
	private static List<Configuration> configurations = null;
	private static List<Configuration> addedConfigurations = null;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		System.out.println("start");
		Parse.init();
		rules = Parse.getRules();
		configurations = Parse.getConfigurations();
		Logger.debug(configurations.size()+"");
		Logger.debug("***********alternating pushdown system rules*******************");
		for(Rule rule : rules){
			System.out.println(rule);
			System.out.println();
		}
		Logger.debug("***************************************************************");
		
//		Alternating pushdown system  -->  small step alternating pushdown system
		 
		APS2STAPS();
		Logger.debug("");
		
		Logger.debug("***********alternating pushdown system rules*******************");
		for(Rule rule : rules){
			
			System.out.println(rule);
			System.out.println();
		}
		Logger.debug("****************************************************************");
	}

	public static void APS2STAPS() {
		for( Configuration cf : configurations){
			TransformCf2SmallStepCf(cf);
		}
		if(addedConfigurations != null)
		    configurations.addAll(addedConfigurations);
		for(Rule rule : rules){
			rule.toSmallStep();
		}
		if(addedRules != null)
			rules.addAll(addedRules);
	}

	public static void TransformCf2SmallStepCf(Configuration cf) {
		if(null != cf){
			Logger.debug("************ " + cf.toString()+ " ****************");
			if(null == addedConfigurations){
				addedConfigurations = new ArrayList<Configuration>();
			}
			if(addedRules == null){
				addedRules = new ArrayList<Rule>();
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
					addedRules.add(ruleIntroduciton);
					Logger.debug("added Introduction rule:\n" + ruleIntroduciton.toString());
					
					Rule ruleElimination = new Rule(conclusionC,premiseC);
					ruleElimination.setForm(Constants.ELIMINATION);
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
	
	public static void saturation(){
		
	}
	

}
