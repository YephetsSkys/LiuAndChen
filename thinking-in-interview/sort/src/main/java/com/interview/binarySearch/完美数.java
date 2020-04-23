package com.interview.binarySearch;

/**
 *
 * 对于一个 正整数，如果它和除了它自身以外的所有正因子之和相等，我们称它为“完美数”。
 * 给定一个 整数 n， 如果他是完美数，返回 True，否则返回 False
 * 
 * 示例：
 * 输入: 28
 * 输出: True
 * 解释: 28 = 1 + 2 + 4 + 7 + 14
 * 
 * 提示：
 * 输入的数字 n 不会超过 100,000,000. (1e8)
 *
 */
public class 完美数 {
	
	public static void main(String[] args) {
		int num = 28;
		System.out.println(num+(checkPerfectNumber(num) ? "是" : "不是")+"一个完美数");
	}
	
	private static boolean checkPerfectNumber(int num) {
		if(num == 0)
			return false;
		int sum = 0;
		//若num能被一个数整除，那么其中一个数必<=sqrt(num)，另一个数>=sqrt(num)，所以循环到sqrt(num)就足够了
		for(int i = 1; i < Math.sqrt(num); ++i) {
			if(num%i == 0) {
				sum += i;
				sum += num/i;
			}
		}
		return sum - num == num;
	}

}
