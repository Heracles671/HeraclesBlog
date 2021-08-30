import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class BinaryTree {

    class TreeNode {
        int data;
        TreeNode leftNode;
        TreeNode rightNode;
    }

    /**
     * 二叉树层序遍历
     * @param root 二叉树根结点
     */
    public static void levelOrderTraversal(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<TreeNode>();
        queue.offer(root);
        while(!queue.isEmpty()){
            TreeNode node = queue.poll();
            System.out.println(node.data);
            if(node.leftNode != null) {
                queue.offer(node.leftNode);
            }
            if(node.rightNode != null) {
                queue.offer(node.rightNode);
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = new int[] { 4, 4, 6, 5, 3, 2, 8, 1 };
        System.out.println(Arrays.toString(arr));
    }
}