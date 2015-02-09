package com.util;

public class Logger {
	private static boolean debug = true;
	private static boolean info = true;
	public static void debug(String bug){
		if(debug){
			System.out.println(bug);
		}
	}
	public static void info(String message){
		if(info){
			System.out.println(message);
		}
	}
}
