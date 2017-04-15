/**
 * 
 */
package com.softisland.common.utils;

/**
 * @author Administrator
 *
 */
public class TestPwdDe {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		
		System.out.println(TokenUtil.jweDecryption("DYB2Dtck0ZqxxaV-g7KcdQ.3xfhWgU2G3ZZxumZA0owtA.QwhdCr5BNhdyBKTXMb69Rg"));
		try {
			System.out.println(TokenUtil.jweEncryption("4UGFnyKR"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
