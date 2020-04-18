package com.interview.binaryTree;

/**
 * 手写平衡二叉树
 *
 */
public class 平衡二叉树 {
	
	public static class AVLNode {
		public int data;//保存节点数据
		public int depth;//保存节点深度
		public int balance;//是否平衡
		public AVLNode parent;//指向父节点
		public AVLNode left;//指向左子树
		public AVLNode right;//指向右子树
		
		public AVLNode(int data) {
			this.data = data;
			this.depth = 1;
			balance = 0;
			left = right = parent = null;
		}

		@Override
		public String toString() {
			return String.valueOf(data);
		}
	}
	
	public static void insert(AVLNode root, int data) {
		//如果插入的数据小于根节点，往左边递归插入
		if(data < root.data) {
			if(root.left != null) {
				insert(root.left, data);
			} else {
				root.left = new AVLNode(data);
				root.left.parent = root;
			}
		} else {//否则插入到右边
			if(root.right != null) {
				insert(root.right, data);
			} else {
				root.right = new AVLNode(data);
				root.right.parent = root;
			}
		}
		
		//插入后计算平衡因子
		root.balance = calcBalance(root);
		System.out.println("插入"+data+"，节点"+root+"的平衡因子="+root.balance);
		//左子树高，右旋
		if(root.balance >= 2) {
			//右孙高，先左旋
			if(root.left.balance == -1) {
				//左旋
				left_rotate(root.left);
			}
			//右旋
			right_rotate(root);
		} else if(root.balance <= -2) {//右子树高，左旋
			//左孙高，先右旋
			if(root.right.balance == 1) {
				//右旋
				right_rotate(root.right);
			}
			//左旋
			left_rotate(root);
		}
		//调整之后，重新计算平衡因子和树的深度
		root.balance = calcBalance(root);
		root.depth = calcDepth(root);
	}
	
	/**
	 * 右旋
	 * @param p - 根节点
	 */
	private static void right_rotate(AVLNode p) {
		System.out.println("节点:" + p + "进行右旋");
		// 一次旋转涉及到节点包括祖父、父亲、右儿子
		AVLNode pParent = p.parent;//父节点
		AVLNode pLeftSon = p.left;//左儿子
		AVLNode pLeftRightGrandSon = pLeftSon != null ? pLeftSon.right : null;//左孩子的右孩子
		if(pLeftSon != null) {
			// 左子的父节点指向p的父节点
			pLeftSon.parent = pParent;
			// p的父节点原p的位置改成左子
			if(pParent != null) {
				if(p == pParent.left) {
					pParent.left = pLeftSon;
				} else if(p == pParent.right) {
					pParent.right = pLeftSon;
				}
			}
			//左子的右孩子改成p
			pLeftSon.right = p;
		}
		//p的父节点改成左子
		p.parent = pLeftSon;
		// 将左孩子的右孩子变为p的左孩子
		p.left = pLeftRightGrandSon;
		if(pLeftRightGrandSon != null) {
			pLeftRightGrandSon.parent = p;
		}
		
		p.depth = calcDepth(p);
		p.balance = calcBalance(p);
		if(pLeftSon != null) {
			pLeftSon.depth = calcDepth(pLeftSon);
			pLeftSon.balance = calcBalance(pLeftSon);
		}
	}
	
	/**
	 * 左旋
	 * @param p - AVLNode
	 */
	private static void left_rotate(AVLNode p) {
		System.out.println("节点:" + p + "进行左旋");
		AVLNode pParent = p.parent;//父节点
		AVLNode pRightSon = p.right;//右儿子
		AVLNode pRightLeftGrandSon = pRightSon != null ? pRightSon.left : null;//右孩子的左孩子
		if(pRightSon != null) {
			// 右子变父
			pRightSon.parent = pParent;
			if(pParent != null) {
				if(p == pParent.right) {
					pParent.right = pRightSon;
				} else if(p == pParent.left) {
					pParent.left = pRightSon;
				}
			}
			//右子的左孩子改成p
			pRightSon.left = p;
		}
		//p的父节点改成右子
		p.parent = pRightSon;
		// 将右孩子的左孩子变成p的右孩子
		p.right = pRightLeftGrandSon;
		if(pRightLeftGrandSon != null) {
			pRightLeftGrandSon.parent = p;
		}
		
		p.depth = calcDepth(p);
		p.balance = calcBalance(p);
		if(pRightSon != null) {
			pRightSon.depth = calcDepth(pRightSon);
			pRightSon.balance = calcBalance(pRightSon);
		}
	}
	
	/**
	 * 计算平衡因子
	 * @param p - AVLNode
	 * @return int
	 */
	private static int calcBalance(AVLNode p) {
		//计算左子树深度-右子树深度的差值
		int left_depth = 0, right_depth = 0;
		//左子树深度
		if(p.left != null) {
			left_depth = p.left.depth;
		}
		if(p.right != null) {
			right_depth = p.right.depth;
		}
		return left_depth - right_depth;
	}
	
	/**
	 * 计算节点的深度
	 * @param p - AVLNode
	 * @return int
	 */
	private static int calcDepth(AVLNode p) {
		int depth = 0;
		if(p.left != null) {
			depth = p.left.depth;
		}
		if(p.right != null && depth < p.right.depth) {
			depth = p.right.depth;
		}
		return ++depth;
	}
	
	/**
	 * 按照层序遍历
	 * @param root - TreeNode
	 */
	public static void printLeftRightTree(AVLNode p) {
		//首先找到根节点
		AVLNode root = p;
		while(root.parent != null) {
			root = root.parent;
		}
		
		//计算最大深度
		int depth = calcDepth(root);
		System.out.println("maxDepth : " + depth+"，root : " + root.data);
		for(int i=0;i<=depth;i++) {
			_printLeftRightTree(root, i);
		}
		System.out.println();
	}
	
	private static void _printLeftRightTree(AVLNode p, int i) {
		if(p == null || i == 0) {
			return;
		}
		if(i == 1) {
			System.out.print(p.data+",");
			return;
		}
		//依次打印出左节点和右节点
		_printLeftRightTree(p.left, i-1);
		_printLeftRightTree(p.right, i-1);
	}
	
	public static void main(String[] args) {
		int[] array = {3, 2 , 1, 4, 5};
		AVLNode root = new AVLNode(array[0]);
		for(int i=1;i<array.length;i++) {
			insert(root, array[i]);
			printLeftRightTree(root);
			System.out.println("===================================");
		}
	}

}
