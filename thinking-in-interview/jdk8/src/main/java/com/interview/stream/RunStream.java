package com.interview.stream;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * ��֤stream���л���
 * 
 * 1. ���в�������ʽ����, һ��Ԫ��ֻ����һ�� 
 * 2. ÿһ���м��������һ���µ���. ��������һ������sourceStage 
 *     ָ��ͬһ�� �ط�,����Head 
 * 3. Head->nextStage->nextStage->... -> null
 * 4. ��״̬���������״̬�����׶�,��������
 * 5. ���л�����, ��״̬���м������һ���ܲ��в���.
 * 
 * 6. parallel/ sequetial ��2������Ҳ���м����(Ҳ�Ƿ���stream)
 * 		�������ǲ�������, ����ֻ�޸� Head�Ĳ��б�־
 * 
 * @author ������
 *
 */
public class RunStream {

	public static void main(String[] args) {
		Random random = new Random();
		// �����������
		Stream<Integer> stream = Stream.generate(() -> random.nextInt())
				// ����500�� ( ��������Ҫ��·����. )
				.limit(500)
				// ��1����״̬����
				.peek(s -> print("peek: " + s))
				// ��2����״̬����
				.filter(s -> {
					print("filter: " + s);
					return s > 1000000;
				})
				// ��״̬����
				.sorted((i1, i2) -> {
					print("����: " + i1 + ", " + i2);
					return i1.compareTo(i2);
				})
				// ��һ����״̬����
				.peek(s -> {
					print("peek2: " + s);
				}).parallel();

		// ��ֹ����
		stream.count();
	}

	/**
	 * ��ӡ��־��sleep 5 ����
	 * 
	 * @param s
	 */
	public static void print(String s) {
		// System.out.println(s);
		// ���߳���(���Բ������)
		System.out.println(Thread.currentThread().getName() + " > " + s);
		try {
			TimeUnit.MILLISECONDS.sleep(5);
		} catch (InterruptedException e) {
		}
	}

}
