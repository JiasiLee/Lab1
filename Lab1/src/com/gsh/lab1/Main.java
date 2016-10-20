package com.gsh.lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//李佳思大好人！
//LJS 特别牛逼！

public class Main
{
	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		String expressionCache = "";
		while(true)
		{
			System.out.print(">");
			String input = scanner.nextLine();
			input = input.trim();
			input.replaceAll("\\t", "");
			input.replaceAll("\\s+", " ");
			if(input.matches("^([0-9]+|[a-zA-Z]+)((\\+|\\*)([0-9]+|[a-zA-Z]+))+$"))
			{
				expressionCache = input;
				System.out.println(expressionCache);
			}
			else if(input.matches("^\\!simplify(\\s[a-zA-Z]+=[0-9]+)*$"))
			{
				System.out.println(Main.simplify(expressionCache, input));
			}
			//姹傚
			else if(input.matches("^\\!d/d[a-zA-Z]+$"))
			{
				System.out.println(Main.derivative(expressionCache, input));
			}
			//閫�鍑�
			else if(input.matches("!exit"))
			{
				break;
			}
			//閿欒杈撳叆
			else
			{
				System.out.println("Error: Unrecognized input");
				expressionCache = "";
			}
			
		}
		scanner.close();
	}
	
	public static String simplify(String expression, String command)
	{
		//鍙橀噺鏇挎崲 鍖归厤command涓殑閿�煎
		Pattern pattern = Pattern.compile("[a-zA-Z]+=[0-9]+");
		Matcher matcher = pattern.matcher(command);
		while(matcher.find())
		{
			String result = matcher.group();
			int equalIndex = result.indexOf("=");
			if(!expression.contains(result.substring(0, equalIndex)))
			{
				return "Error: undifined variable";
			}
			expression = expression.replaceAll(result.substring(0, equalIndex), result.substring(equalIndex + 1));
		}
		//涔樻硶鍖栫畝 鍖归厤鎵�鏈変箻娉曞瓙寮�
		pattern = Pattern.compile("([0-9]+|[a-zA-Z]+)(\\*([0-9]+|[a-zA-Z]+))+");
		matcher = pattern.matcher(expression);
		while(matcher.find())
		{
			String result = matcher.group();
			expression = expression.replace(result, Main.computeMulti(result));
		}
		//鍔犳硶鍖栫畝
		expression = Main.computeAdd(expression);
		
		return expression;
	}
	
	public static String computeMulti(String expression)
	{
		Pattern pattern1 = Pattern.compile("[0-9]+");
		Pattern pattern2 = Pattern.compile("[a-zA-Z]+");
		Matcher matcher1 = pattern1.matcher(expression);
		Matcher matcher2 = pattern2.matcher(expression);
		int numResult = 1;
		List<String> vars = new ArrayList<>();
		while(matcher1.find())
		{
			numResult = numResult * Integer.valueOf(matcher1.group());
		}
		while(matcher2.find())
		{
			vars.add(matcher2.group());
		}
		StringBuffer result = new StringBuffer("" + numResult);
		for(String var : vars)
		{
			result.append("*" + var);
		}
		return result.toString();
	}
	
	public static String computeAdd(String expression)
	{
		Pattern pattern = Pattern.compile("(|\\+)[a-zA-Z0-9|\\*]+");
		Matcher matcher = pattern.matcher(expression);
		List<String> subExpressions = new ArrayList<>();
		int numResult = 0;
		while(matcher.find())
		{
			String subExpression = matcher.group();
			if(subExpression.startsWith("+"))
			{
				subExpression = subExpression.substring(1);
			}
			if(!subExpression.contains("*"))
			{
				numResult = numResult + Integer.valueOf(subExpression);
			}
			else
			{
				subExpressions.add(subExpression);
			}
		}
		StringBuffer stringBuffer = new StringBuffer();
		if(numResult != 0)
		{
			stringBuffer.append(numResult);
			for(String subExpression : subExpressions)
			{
				stringBuffer.append("+" + subExpression);
			}
		}
		else
		{
			stringBuffer.append(subExpressions.remove(0));
			for(String subExpression : subExpressions)
			{
				stringBuffer.append("+" + subExpression);
			}
		}
		return stringBuffer.toString();
	}
		
	public static String derivative(String expression, String command)
	{
		String var = command.substring(4);
		if(!expression.contains(var))
		{
			return "Error: undifined variable";
		}
		String[] subExpressions = expression.split("\\+");
		String result = "";
		for(String subExpression : subExpressions)
		{
			if(subExpression.contains(var))
			{
				int index = subExpression.indexOf(var);
				int startIndex = index - 1;
				int endIndex = index + var.length();
				int count = 0;
				Pattern pattern = Pattern.compile(var);
				Matcher matcher = pattern.matcher(subExpression);
				while(matcher.find())
				{
					matcher.group();
					count++;
				}
				if(startIndex < 0)
				{
					startIndex = 0;
				}
				if(endIndex > subExpression.length())
				{
					endIndex = subExpression.length();
				}
				String temp = subExpression.substring(0, startIndex) + subExpression.substring(endIndex);
				if(temp.startsWith("*") || temp.equals(""))
				{
					temp = count + temp;
				}
				else
				{
					temp = count + "*" + temp;
				}
				result = result + "+" + temp;
			}
		}
		result = Main.simplify(result, "!simplify");
		return result;
	}
}
