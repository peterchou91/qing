package com;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;


import com.entity.Configuration;
import com.entity.Rule;
import com.util.Constants;

public class Test {
	
	public static List<Integer> l1 = new ArrayList<Integer>();
	static List<Set<Integer>> list = new ArrayList<Set<Integer>>();
	
	static{
		for(int i = 0; i < 100 ;i++){
			l1.add(i);
		}
		Set<Integer> s1 = new HashSet<Integer>();
		s1.add(1);
		s1.add(2);
		list.add(s1);
		
		Set<Integer> s2 = new HashSet<Integer>();
		s2.add(3);
		s2.add(4);
		list.add(s2);
		
		Set<Integer> s3 = new HashSet<Integer>();
		s3.add(5);
		s3.add(6);
		list.add(s3);
	}
	
	public static List<Set<Integer>> deepFirst(List<Set<Integer>> list){
		List<Set<Integer>> retList = new ArrayList<Set<Integer>>();
		Stack<Integer> stack = new Stack<Integer>();
		int i = 0;
		dfs(list.get(i),i,stack,list.size(),retList);
		
		return retList;
	}
	
	private static void dfs(Set<Integer> s,int level,Stack<Integer> stack,int n,List<Set<Integer>> retlist) {
		for(Integer integer : s){
			stack.push(integer);
			System.out.println("push:" + integer);
			if(level == n - 1){
				Set<Integer> tempSet = new HashSet<Integer>();
				tempSet.addAll(stack);
				retlist.add(tempSet);
			}
			if( level + 1 < n){
				dfs(list.get(level+1),level+1,stack,n,retlist);
			}
			int tempInt = stack.pop();
			System.out.println("pop:" + tempInt);
		}
		
	}

	public static void main(String[] args) {
		List<Set<Integer>> retList =  deepFirst(list);
		for(Set<Integer> s : retList){
			System.out.println(s);
		}
	}
}
