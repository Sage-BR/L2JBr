package org.l2jbr.gameserver.util.exp4j;

/**
 * This exception is being thrown whenever {@link Tokenizer} finds unknown function or variable.
 * @author Bartosz Firyn (sarxos)
 */
public class UnknownFunctionOrVariableException extends IllegalArgumentException
{
	private final String message;
	private final String expression;
	private final String token;
	private final int position;
	
	public UnknownFunctionOrVariableException(String expression, int position, int length)
	{
		this.expression = expression;
		token = token(expression, position, length);
		this.position = position;
		message = "Unknown function or variable '" + token + "' at pos " + position + " in expression '" + expression + "'";
	}
	
	private static String token(String expression, int position, int length)
	{
		
		int len = expression.length();
		int end = (position + length) - 1;
		
		if (len < end)
		{
			end = len;
		}
		
		return expression.substring(position, end);
	}
	
	@Override
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * @return Expression which contains unknown function or variable
	 */
	public String getExpression()
	{
		return expression;
	}
	
	/**
	 * @return The name of unknown function or variable
	 */
	public String getToken()
	{
		return token;
	}
	
	/**
	 * @return The position of unknown function or variable
	 */
	public int getPosition()
	{
		return position;
	}
}
