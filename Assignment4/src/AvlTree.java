/**
 * CMPT 280 Assignment 4
 * Duncan Boyes
 * 11084342
 * dhb021
 */

import lib280.exception.NoCurrentItem280Exception;
import lib280.tree.BinaryNode280;
import lib280.tree.OrderedSimpleTree280;

/**
 * This AVL tree is implemented as an extension of the binary tree. It maintains the
 * below invariants
 *
 * Invariants: Signed Imbalance of all nodes in tree will not have a magnitude greater than 1
 * All nodes extend binary nodes
 * @param <I> type of elements stored in the tree. I or it's base class extends Comparable
 */
public class AvlTree<I extends Comparable<? super I>> extends OrderedSimpleTree280<I> {

    // Constructor delegates to super
    public AvlTree(){
        super();
    }

    /**
     * Creates a new node
     * @param item    The item to be placed in the new node
     * @return The newly generated node
     * @timing O(1)
     */
    @Override
    protected BinaryNode280<I> createNewNode(I item) {
        return new AvlNode<>(item);
    }

    /**
     * Derives the height of the AVL tree rooted at R
     * @param R the root node of the tree
     * @return The height of the tree rooted at R
     * @precond R must be non null
     * @postcond none
     * @timing O(1)
     */
    public int height(AvlNode<I> R){
        if (R == null) return 0;
        return 1+ Math.max(R.leftHeight,R.rightHeight);
    }

    /**
     * The weight imbalance of the left and right nodes of R
     * @param R Root node
     * @return Imbalance of left and right subtree. Positive indicates a left heavy root node
     * @precond None
     * @postcond None
     * @timing O(1)
     */
    protected int signedImbalance(AvlNode<I> R){
        return R.leftHeight - R.rightHeight;
    }

    /**
     * Updates the left height of N
     * @param N AVL node to update
     * @return None
     * @precond None
     * @postcond Updates the left height of N
     * @timing O(1)
     */
    protected void updateNodeLeftHeight(BinaryNode280<I> N){
        ((AvlNode<I>)N).leftHeight = height((AvlNode<I>) N.leftNode());
    }

    /**
     * Updates the right height of N
     * @param N AVL node to update
     * @return None
     * @precond None
     * @postcond Updates the right height of N
     * @timing O(1)
     */
    protected void updateNodeRightHeight(BinaryNode280<I> N){
        ((AvlNode<I>)N).rightHeight = height((AvlNode<I>) N.rightNode());
    }

    /**
     * Inserts a new item as in BinaryTree, and then restores the AVL property after the insertion
     * @param x The item to insert
     * @return None
     * @precond None
     * @postcond A new node with item x is balanced into the AVL tree. A wide variety of
     * sideeffects occur through calls to RotateLeft and RotateRight
     * @timing O(log(n))
     */
    @Override
    public void insert(I x) {
        if (isEmpty())
            rootNode = createNewNode(x);
        else if (x.compareTo(rootItem()) < 0)
        {
            AvlTree<I> leftTree =(AvlTree<I>)rootLeftSubtree();
            leftTree.insert(x);
            setRootLeftSubtree(leftTree);

            updateNodeLeftHeight(rootNode); // Update left height
        }
        else
        {
            AvlTree<I> rightTree = (AvlTree<I>)rootRightSubtree();
            rightTree.insert(x);
            setRootRightSubtree(rightTree);

            updateNodeRightHeight(rootNode); // Update right height
        }
        restoreAvlProperty((AvlNode<I>) rootNode); //Restore the AVL property

    }

    /**
     * Delete the current item, making its replacement the current item
     * @return None
     * @precond itemExists()
     * @postcond Removes the current item, making its replacement the current item
     * @timing O(logn)
     * @throws NoCurrentItem280Exception
     */
    @Override
    public void deleteItem() throws NoCurrentItem280Exception {
        if(!itemExists())
            throw new NoCurrentItem280Exception("No current item to delete");

        I data = cur.item();
        parent = null;
        cur = rootNode;
        deleteItemHelper(data, (AvlNode<I>) cur);
    }

    /**
     * Recursive helper for delete item. Recursivly searches for data and removes it. As the recursion unwinds, restore
     * the AVL property
     * @param data Item to delete
     * @param R root node
     * @precond itemExists()
     * @postcond AVL property maintained with data removed
     * @timing O(log(n))
     */
    protected void deleteItemHelper(I data, AvlNode<I> R){
        if (!itemExists())
            throw new RuntimeException("Error: Nothing to delete");

        if (data.compareTo(R.item())==0){
            BinaryNode280<I> replaceNode = null;
            boolean foundReplacement = false;

            /*	Test if there is only one child so it can replace the root. */
            if (R.rightNode() == null)
            {
                replaceNode = R.leftNode();
                foundReplacement = true;
            }
            else if (R.leftNode() == null)
            {
                replaceNode = R.rightNode();
                foundReplacement = true;
            }
            // If the node has 0 or 1 children
            if (foundReplacement){
                if (parent == null){
                    setRootNode(replaceNode);
                }
                else if(parent.leftNode() == R){
                    parent.setLeftNode(replaceNode);
                    updateNodeLeftHeight(parent);
                }
                else{
                    parent.setRightNode(replaceNode);
                    updateNodeRightHeight(parent);
                }
                cur = replaceNode;
            }
            // Else we must have a node with 2 children and must make use of AVL rotations
            else{
                /*  Find next in-order node and move its contents of item node we are deleting*/
                BinaryNode280<I> replaceCur = R.rightNode();
                while (replaceCur.leftNode()!=null){
                    replaceCur = replaceCur.leftNode();
                }
                R.setItem(replaceCur.item());
                /*  Delete that in-order node */
                BinaryNode280<I> saveCur = cur;
                BinaryNode280<I> saveParent = parent;
                parent = R;
                cur = R.rightNode();
                deleteItemHelper(R.item(), (AvlNode<I>) R.rightNode());
                parent = saveParent;
                cur = saveCur;
            }
        }
        // Haven't found the data yet so recursively keep looking
        else
        {
            if (data.compareTo(R.item())<0){
                parent = R;
                this.cur = R.leftNode();
                deleteItemHelper(data, (AvlNode<I>) cur);
                updateNodeLeftHeight(R);
            }
            else{
                parent = R;
                this.cur = R.rightNode();
                deleteItemHelper(data, (AvlNode<I>) cur);
                updateNodeRightHeight(R);
            }
        }
        // Restore the AVL property as the recursion rewinds
        restoreAvlProperty(R);
    }

    /**
     * Shifts the children branches in such a way that maintains the balance of node A
     * @param A Critical Node (imbalanced AVL node)
     * @return None
     * @precond signedUnbalance(A) less than -1
     * @postcond signedBalance(A) > -1
     * @timing O(1)
     */
    private void rotateLeft(AvlNode<I> A){
        // A is root node. It helps to draw the tree to understand the relationship changes.
        // Parent does not need to be updated because we simply modify the item at root and at C
        BinaryNode280<I> B = A.leftNode();
        BinaryNode280<I> C = A.rightNode();
        BinaryNode280<I> D = C.leftNode();
        BinaryNode280<I> E = C.rightNode();
        I oldRootItem = A.item();
        A.setItem(C.item());
        A.setLeftNode(C);
        A.setRightNode(E);
        C.setLeftNode(B);
        C.setRightNode(D);
        C.setItem(oldRootItem);

        // Since we straight swapped the item values on root and C , we need to update the tree heights of the nodes
        // root and C. C first as root heights depend on C
        ((AvlNode<I>)C).rightHeight = height((AvlNode<I>) C.rightNode());
        //((AvlNode<I>)C).rightHeight = ((AvlTree<I>) rootLeftSubtree().rootRightSubtree()).height();
        ((AvlNode<I>)C).leftHeight = height((AvlNode<I>) C.leftNode());
        A.rightHeight = height((AvlNode<I>) A.rightNode());
        A.leftHeight = height((AvlNode<I>) A.leftNode());
    }


    /**
     * Shifts the children branches in such a way that maintains the balance of node A
     * @param A Critical Node (imbalanced AVL node)
     * @return None
     * @precond signedUnbalance(A) greater than 1. Left heavy node
     * @postcond Node A will be balanced
     * @timing O(1)
     */
    private void rotateRight(AvlNode<I> A){
        // A is root node. It helps to draw the tree to understand the relationship changes.
        // Parent does not need to be updated because we simply modify the item at root and at C
        BinaryNode280<I> B = A.rightNode();
        BinaryNode280<I> C = A.leftNode();
        BinaryNode280<I> D = C.rightNode();
        BinaryNode280<I> E = C.leftNode();
        I oldRootItem = A.item();
        A.setItem(C.item());
        A.setRightNode(C);
        A.setLeftNode(E);
        C.setRightNode(B);
        C.setLeftNode(D);
        C.setItem(oldRootItem);

        // Since we straight swapped the item values on root and C , we need to update the tree heights of the nodes
        // root and C. C first as root heights depend on C
        ((AvlNode<I>)C).rightHeight = height((AvlNode<I>) C.rightNode());
        ((AvlNode<I>)C).leftHeight = height((AvlNode<I>) C.leftNode());
        A.rightHeight = height((AvlNode<I>) A.rightNode());
        A.leftHeight = height((AvlNode<I>) A.leftNode());
    }

    /**
     * Restores the balance of the node R (if needed)
     * @param R AvlNode that may or may not be imbalanced
     * @precond Imbalance of R must be less than 3 and greater than -3
     * @postcond If R is unbalanced, restores it's AVL property
     */
    private void restoreAvlProperty(AvlNode<I> R){
        int imbalance = signedImbalance(R);
        if (Math.abs(imbalance)<=1) return;     // Imbalance is 0 or 1. Node is not critical

        if (imbalance== 2){
            if (signedImbalance((AvlNode<I>) R.leftNode())>=0) {
                rotateRight(R);                                                 // LL Imbalance
            }
            else{
                rotateLeft((AvlNode<I>) R.leftNode());                          // LR Imbalance
                R.leftHeight = height((AvlNode<I>) R.leftNode());
                rotateRight(R);                                                 // LL Imbalance
            }
        }
        else if (imbalance == -2){
            if (signedImbalance((AvlNode<I>) R.rightNode())<=0)   {
                rotateLeft(R);                                                  // RR Imbalance
            }
            else {
                rotateRight((AvlNode<I>) R.rightNode());                        // RL Imbalance
                R.rightHeight = height((AvlNode<I>) R.rightNode());
                rotateLeft(R);                                                  // RR Imbalance
            }
        }
        else throw new RuntimeException("Error: Should not get here in restoreAVLProperty. Imbalance: " + imbalance);
    }


    public static void main(String[] args) {

        System.out.println("AVL tree implementation testing.");
        // Testing insert()
        AvlTree<Integer> avl = new AvlTree<>();
        OrderedSimpleTree280<Integer> testAgainst = new OrderedSimpleTree280<>();


        // Test LL Imbalance caused by insert() to Small Tree
        avl.insert(20);
        System.out.println("Test LL imbalance on small tree. ");
        System.out.println("Empty tree insert 20. Result: " + avl.toStringByLevel());
        avl.insert(4);
        System.out.println("Insert 4. Result: " + avl.toStringByLevel());
        avl.insert(1);
        testAgainst.clear();
        System.out.println("Insert 1. Causes right rotation. Result: " + avl.toStringByLevel());
        testAgainst.insert(4);
        testAgainst.insert(20);
        testAgainst.insert(1);
        if (!testAgainst.toString().equals(avl.toString())){
            System.out.println("Error: Expected: \n" + testAgainst.toStringByLevel()
                    + " \n    but got: \n"+ avl.toStringByLevel());
        }

        // Test LR Imbalance caused by insert() to Small Tree
        System.out.println("Test LR imbalance on small tree. ");
        avl.clear();
        avl.insert(20);
        System.out.println("\nNew tree. Insert 20. Result: " + avl.toStringByLevel());
        avl.insert(4);
        System.out.println("Insert 4. Result: " + avl.toStringByLevel());
        avl.insert(15);
        System.out.println("Insert 15. Causes LR Imbalance. Result: " + avl.toStringByLevel());
        testAgainst.clear();
        testAgainst.insert(15);
        testAgainst.insert(20);
        testAgainst.insert(4);
        if (!testAgainst.toString().equals(avl.toString())){
            System.out.println("Error: Expected: \n" + testAgainst.toStringByLevel()
                    + " \n    but got: \n"+ avl.toStringByLevel());
        }


        // Test RR Imbalance caused by insert() to Small Tree
        System.out.println("\nTest RR imbalance on small tree. ");
        avl.clear();
        avl.insert(20);
        System.out.println("New tree. Insert 20. Result: " + avl.toStringByLevel());
        avl.insert(40);
        System.out.println("Insert 40. Result: " + avl.toStringByLevel());
        avl.insert(80);
        System.out.println("Insert 80. Causes RR Imbalance. Result: " + avl.toStringByLevel());
        testAgainst.clear();
        testAgainst.insert(40);
        testAgainst.insert(20);
        testAgainst.insert(80);
        if (!testAgainst.toString().equals(avl.toString())){
            System.out.println("Error: Expected: \n" + testAgainst.toStringByLevel()
                    + " \n    but got: \n"+ avl.toStringByLevel());
        }

        // Test RL Imbalance caused by insert() to Small Tree
        avl.clear();
        System.out.println("\nTest RL imbalance on small tree. ");
        avl.insert(20);
        System.out.println("New tree. Insert 20. Result: " + avl.toStringByLevel());
        avl.insert(40);
        System.out.println("Insert 40. Result: " + avl.toStringByLevel());
        avl.insert(30);
        System.out.println("Insert 30. Causes RL imbalance. Result: " + avl.toStringByLevel());
        testAgainst.clear();
        testAgainst.insert(30);
        testAgainst.insert(20);
        testAgainst.insert(40);
        if (!testAgainst.toString().equals(avl.toString())){
            System.out.println("Error: Expected: \n" + testAgainst.toStringByLevel()
                    + " \n    but got: \n"+ avl.toStringByLevel());
        }

        // Test LR Imbalance caused by insert() to Larger Tree
        avl.clear();
        System.out.println("\nTest LR imbalance on larger tree. New tree ");
        avl.insert(20);
        avl.insert(4);
        avl.insert(26);
        avl.insert(21);
        avl.insert(30);
        avl.insert(3);
        avl.insert(9);
        avl.insert(2);
        avl.insert(7);
        avl.insert(11);
        System.out.println("Insert: 20,4,26,21,30,3,9,2,7,11. Result: " + avl.toStringByLevel());
        avl.insert(15); // This one causes the first imbalance
        System.out.println("Insert 15. This insertion should be the first imbalance. Result: " + avl.toStringByLevel());
        testAgainst.clear();
        testAgainst.insert(9);
        testAgainst.insert(4);
        testAgainst.insert(20);
        testAgainst.insert(3);
        testAgainst.insert(7);
        testAgainst.insert(2);
        testAgainst.insert(11);
        testAgainst.insert(26);
        testAgainst.insert(15);
        testAgainst.insert(21);
        testAgainst.insert(30);
        if (!testAgainst.toString().equals(avl.toString())){
            System.out.println("Error: Expected: \n" + testAgainst.toStringByLevel()
                    + " \n    but got: \n"+ avl.toStringByLevel());
        }


        // Test deleteItem() trivial nodes(1 or 0 children). Delete a number of times and check against expected
        avl.clear();
        avl.insert(1);
        avl.insert(2);
        avl.insert(3);
        avl.insert(4);
        avl.insert(5);
        System.out.println("\nTesting delete(). Test tree: " + avl.toStringByLevel());
        avl.search(3);
        avl.deleteItem();
        System.out.println("Delete 3. Result " + avl.toStringByLevel());
        avl.search(4);
        avl.deleteItem();
        System.out.println("Delete 4. Result " + avl.toStringByLevel());
        avl.search(5);
        avl.deleteItem();
        System.out.println("Delete 5. Result " + avl.toStringByLevel());
        avl.search(2);      // Should target the root node of 2 node tree
        avl.deleteItem();
        System.out.println("Delete 2. Result " + avl.toStringByLevel());
        if (avl.item() != 1 && avl.height((AvlNode<Integer>) avl.rootNode)!= 1){
            System.out.println("Error: Tree should only have a single item of 1, but got: " + avl.toString());
        }
        // Test deleting from a single item tree
        avl.search(1);
        avl.deleteItem();
        System.out.println("Delete 1. Result " + avl.toStringByLevel());
        if (avl.itemExists()){
            System.out.println("Error: Tree should be empty, but got: " + avl.toString());
        }
        //Test delete from empty tree
        try {
            avl.deleteItem();
            System.out.println("Error: Can't delete from empty tree");
        }catch (NoCurrentItem280Exception e){
            //Good
        }

        // Test deleting nodes with 2 children
        avl.insert(0);
        avl.insert(1);
        avl.insert(2);
        avl.search(1);
        avl.deleteItem();

        testAgainst = new OrderedSimpleTree280<>();
        testAgainst.insert(2);
        testAgainst.insert(0);

        if (avl.cur.item() != 2 && avl.parent != null && !avl.equals(testAgainst)){
            System.out.println("Error: Expected");
        }

        // Test Delete() on large tree. Causes a RR imbalance at 2, which then causes an RR imbalance at 5. Test
        // against expected result
        avl.clear();
        avl.insert(5);
        avl.insert(2);
        avl.insert(8);
        avl.insert(1);
        avl.insert(3);
        avl.insert(7);
        avl.insert(10);
        avl.insert(4);
        avl.insert(6);
        avl.insert(9);
        avl.insert(12);
        avl.insert(44);
        System.out.println("\nTest delete on larger tree. Test tree initial state: " + avl.toStringByLevel());
        avl.search(1);
        avl.deleteItem();
        System.out.println("\nDelete 1. Causes a RR imbalance at 2, which then causes an RR imbalance at 5. Result: " + avl.toStringByLevel());


        testAgainst.clear();
        testAgainst.insert(8);
        testAgainst.insert(5);
        testAgainst.insert(3);
        testAgainst.insert(7);
        testAgainst.insert(2);
        testAgainst.insert(4);
        testAgainst.insert(6);
        testAgainst.insert(10);
        testAgainst.insert(9);
        testAgainst.insert(12);
        testAgainst.insert(44);
        if (!testAgainst.toString().equals(avl.toString())){
            System.out.println("Error: Expected: \n" + testAgainst.toStringByLevel()
                    + " \n    but got: \n"+ avl.toStringByLevel());
        }
        avl.search(3);
        avl.deleteItem();
        System.out.println("\nDelete 3. Simple deletion with no imbalance. Result: " + avl.toStringByLevel());
        avl.search(44);
        avl.deleteItem();
        avl.search(9);
        avl.deleteItem();
        avl.search(2);
        avl.deleteItem();
        avl.search(12);
        avl.deleteItem();
        System.out.println("\nDelete 44,9,2,12. Causes a LL imbalance at 8. Result: " + avl.toStringByLevel());


        // Test Multiple insert and delete caused imbalances against expected result
        avl.clear();
        avl.insert(16);
        avl.insert(32);
        avl.insert(64);
        avl.insert(24);
        avl.insert(20);
        avl.insert(22);
        avl.insert(21);
        avl.insert(98);

        testAgainst.clear();
        testAgainst.insert(24);
        testAgainst.insert(20);
        testAgainst.insert(64);
        testAgainst.insert(16);
        testAgainst.insert(22);
        testAgainst.insert(21);
        testAgainst.insert(32);
        testAgainst.insert(98);

        if (!avl.toString().equals(testAgainst.toString())){
            System.out.println("Error: Generated test AVL tree does not match expected result: \n Result AVL tree: " + avl.toString() + "\n Expected Result:" + testAgainst.toString());
        }

        // Test deleting causing a RL imbalance
        avl.clear();

        avl.insert(37);
        avl.insert(40);
        avl.insert(91);
        avl.insert(-10);
        avl.insert(14);
        avl.insert(100);
        avl.insert(24);
        // Deleting 91 should cause a RL imbalance
        avl.search(91);
        avl.deleteItem();

        testAgainst.clear();
        testAgainst.insert(37);
        testAgainst.insert(14);
        testAgainst.insert(-10);
        testAgainst.insert(24);
        testAgainst.insert(40);
        testAgainst.insert(100);

        if (!testAgainst.toString().equals(avl.toString())){
            System.out.println("Error: Delete() caused irregularity. Expected: \n" + testAgainst.toStringByLevel()
                    + " \n    but got: \n"+ avl.toStringByLevel());
        }

        avl.search(37);
        avl.deleteItem();
        testAgainst.search(37);
        testAgainst.deleteItem();
        if (!testAgainst.toString().equals(avl.toString())){
            System.out.println("Error: Delete() caused irregularity. Expected: \n" + testAgainst.toStringByLevel()
                    + " \n    but got: \n"+ avl.toStringByLevel());
        }
        // Test deletion until Empty
        avl.deleteItem();
        avl.deleteItem();
        avl.deleteItem();
        avl.deleteItem();
        avl.deleteItem();
        if (avl.itemExists()){
            System.out.println("Error: Tree should be empty after these deletions. Got: "+avl.toStringByLevel());
        }

        System.out.println("\nTest String AVL tree.");
        // Test with strings
        AvlTree<String> stringAVL = new AvlTree<String>();
        stringAVL.insert("Hello");
        stringAVL.insert("World");
        System.out.println("\nInsert \"Hello\",\"World\". Result: " + stringAVL.toStringByLevel());
        stringAVL.insert("Universe");
        System.out.println("\nInsert \"Universe\". Causes RL imbalance. Result: " + stringAVL.toStringByLevel());

        AvlTree<String> stringTestAgainst = new AvlTree<>();
        stringTestAgainst.insert("Universe");
        stringTestAgainst.insert("Hello");
        stringTestAgainst.insert("World");
        if (!stringAVL.toString().equals(stringTestAgainst.toString()))
        {
            System.out.println("Error with string AVL tree test: Expected: \n" + stringTestAgainst.toStringByLevel()
                    + " \n    but got: \n"+ stringAVL.toStringByLevel());
        }
        stringAVL.search("Hello");
        stringAVL.deleteItem();
        stringAVL.search("Universe");
        stringAVL.deleteItem();
        stringAVL.search("World");
        stringAVL.deleteItem();
        System.out.println("\nDelete \"Hello\",\"Universe\",\"World\". Result: " + stringAVL.toStringByLevel());

        stringAVL.insert("!!!!Good bye!!!!");
        System.out.println("\nInsert \"!!!!Good bye!!!!\". Result: " + stringAVL.toStringByLevel());
        System.out.println("Regression test completed (Ya there are a bunch of other tests running silent)");
    }
}
