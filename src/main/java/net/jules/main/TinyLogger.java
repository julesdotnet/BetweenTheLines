package net.jules.main;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TinyLogger {
    public static final String RESET  = "\u001B[0m";
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String CYAN   = "\u001B[36m";
    public static final String GRAY   = "\u001B[90m";
	
	public static void log(String msg) {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
		
		System.out.println(
				YELLOW +
			    caller.getClassName() + "." +
			    caller.getMethodName() + "() in " +
			    caller.getFileName() + ":" +
			    caller.getLineNumber() +
			    RESET
			);
		System.out.println(CYAN + "'" + msg + "'" + RESET);
		
		DateTimeFormatter fmt =
			    DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

			System.out.println(GREEN + LocalTime.now().format(fmt) + RESET);
			
		System.out.println("------------------------------");
	}
}
