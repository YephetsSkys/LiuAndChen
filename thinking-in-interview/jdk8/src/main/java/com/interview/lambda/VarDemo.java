package com.interview.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @Author liuman
 * @Descirption: Consumer的使用
 */
public class VarDemo {

	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
		list.add("test");
		Consumer<String> consumer = s -> System.out.println(s + list);
		consumer.accept("1211");
	}

}
