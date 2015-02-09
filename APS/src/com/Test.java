package com;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Test {
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		
		for(String l :list){
			System.out.println(l);
		}
		for(String str : "sd(".split("\\(")){
			System.out.println("aaaa");
			System.out.println(str);
		}
		
//		System.out.println("x".split(",")[1]);
		list.addAll(null);
	}
}
