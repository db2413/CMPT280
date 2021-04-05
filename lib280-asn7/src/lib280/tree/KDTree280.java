/**
 * Duncan Boyes
 * dhb021
 * 11084342
 */
package lib280.tree;

import lib280.base.NDPoint280;

import java.util.*;

public class KDTree280 extends LinkedSimpleTree280<NDPoint280> {

    int k; // dimensionality of the tree

    /**
     *
     * @param a Minimum bound
     * @param b Maximum bound
     * @precond ai <=bi for all i=1...k. a and b must be same dimensionality
     * @return A set of all the points (c1,c2,...,ck) such that a1<=c1<=b1,...ak<=ck<=bk
     */
    public Set<NDPoint280> searchRange(NDPoint280 a, NDPoint280 b,int depth){
        if (isEmpty()) return new HashSet<NDPoint280>();
        // Check a vs b precond
        for (int i = 0; i < a.dim(); i++) {
            if (a.idx(i)>b.idx(i)) throw new IllegalArgumentException("a must be smaller than b across all dimensions");
        }

        int d = depth % a.dim();
        double splitVal = rootItem().idx(d);
        double min = a.idx(d);
        double max = b.idx(d);

        if (splitVal<min){
            return ((KDTree280)rootRightSubtree()).searchRange(a,b,depth+1);
        }
        else if(splitVal>max){
            return ((KDTree280)rootLeftSubtree()).searchRange(a,b,depth+1);
        }
        else {
            // In range points can exist in all subtreez
            Set<NDPoint280> leftSet = ((KDTree280) rootLeftSubtree()).searchRange(a, b, depth + 1);
            Set<NDPoint280> rightSet = ((KDTree280) rootRightSubtree()).searchRange(a, b, depth + 1);
            boolean rootInRange = true;
            for (int i = 0; i < a.dim(); i++) {
                if (a.idx(i)>rootItem().idx(i) || b.idx(i)<rootItem().idx(i)) rootInRange =false;
            }
            Set<NDPoint280> result = new HashSet<>();
            result.addAll(leftSet);
            result.addAll(rightSet);
            if (rootInRange) result.add(rootItem());
            return result;
        }
    }

    public KDTree280(){
        super();
    }

    public KDTree280(NDPoint280[] pointsArray, int k){
        super();
        this.k = k;
        rootNode = kdTree(pointsArray,0,pointsArray.length-1,0);
    }

    /**
     *
     * @param pointsArray: array of NDPoint elements
     * @param left offset of start of subarray for which we want the median element
     * @param right offset of end of subarray for which we want the median element
     * @param depth the current depth in the under-construction tree. Used to select which
     *              dimension (k) of the points will be partitioned
     * @return The root of the newly built kdTree
     */
    public KDNode280 kdTree(NDPoint280[] pointsArray,int left, int right, int depth){
        if (subArrayEmpty(pointsArray, left, right)) return null;

        // First first valid node and grab it's dimensionality
        NDPoint280 firstValidNode = pointsArray[0];
        for (int i = 0; i < pointsArray.length; i++) {
            if (firstValidNode == null) firstValidNode = pointsArray[i];
            else break;
        }
        int d = depth % k;
        int medianOffset = (left+right)/2;

        // Put median element in the correct position
        jSmallest(pointsArray,left,right,medianOffset,d);

        KDNode280 node = new KDNode280(pointsArray[medianOffset]);
        node.setLeftNode(kdTree(pointsArray,left,medianOffset-1,depth+1));
        node.setRightNode(kdTree(pointsArray, medianOffset+1, right, depth+1));
        return node;
    }

    /**
     * @param list array of comparable elements
     * @param left offset of start of subarray for which we want the median element
     * @param right offset of end of subarray for which we want the median element
     * @param j we want to find the element that belongs at array index j
     * @param d dimension with which to sort subarray
     * To find the median of the subarray between arra`y indices ’ left ’ and ’ right ’ ,
     * pass in j = ( right + left )/2.
     *
     * @precond left <= j <= right
     * @precond all items in list are unique
     * @precond d >=0
     * @postcond the element x that belongs at offset j, if the subarray were sorted, is at offset j.
     * Elements in the subarray smaller than x are to the left of offset j and the elements in the
     * subarray larger than x are to the right of offset j
     */
    private void jSmallest(NDPoint280[] list,int left,int right,int j, int d){
        if (!(left<=j)) throw new IllegalArgumentException("j must be greater than left");
        if (!(j<=right)) {
            throw new IllegalArgumentException("j must be less than right");
        }
        if (d<0) throw new IllegalArgumentException("d must be positive");

        if (right<=left) return; // Dont want to alter list in this case
        int pivotIndex = partition(list,left,right,d);
        // If the pivot index is equal to j, then we have found the j-th smallest element
        // and is now in the right place

        // If the pivot is less than the pivot index, we know the j-th smallest element must be
        // between left and pivot index -1. So keep looking
        if (j<pivotIndex){
            jSmallest(list,left,pivotIndex-1,j,d);
        }
        // Else the position must be larger than the pivot index. Keep searching
        else if (j>pivotIndex){
            jSmallest(list,pivotIndex+1,right,j,d);
        }
        // Otherwise , the pivot ended up at list [j] , and the pivot *is* the
        // j-th smallest element and we ’re done .
    }

    /**
     * partitions a subarray using its last element as a pivot
     * @param list: array of NDPoint comparable elements
     * @param left: lower limit on subarray to be partitioned
     * @param right: upper limit on subarray to be partitioned
     * @param d: dimension of NDPoint to compare against
     * @precond: All elements in the list are unique
     * @postcond: All elements smaller than the pivot appear in the leftmost
     * part of the subarray, then the pivot element , followed by
     * the elements larger than the pivot. There is no guarantee
     * about the ordering of the elements before and after the
     * pivot.
     * @return: The offset at which the pivot element ended up
     */
    private int partition(NDPoint280[] list,int left, int right, int d){
        NDPoint280 pivot = list[right];
        int swapOffset = left;

        for (int i = left; i < right; i++) {
            if (list[i].compareByDim(d,pivot) <= 0){
                NDPoint280 temp = list[i];
                list[i] = list[swapOffset];
                list[swapOffset] = temp;
                swapOffset += 1;
            }
        }
        NDPoint280 temp = list[right];
        list[right] = list[swapOffset];
        list[swapOffset] = temp;
        return swapOffset;
    }

    /**
     * Function is for testing only. Compares two lists of NDPoints
     * @param a
     * @param b
     * @return
     */
    boolean compareListsOfNodes(NDPoint280[] a, NDPoint280[] b){
        for (int i = 0; i < a.length; i++) {
            if (a[i]!=b[i]) return false;
        }
        return true;
    }

    /**
     * For making a string of each of the elements of a NDPoint array
     * @param a: Array to print of NDPoints
     * @return String
     */
    public String nodesListToString(NDPoint280[] a){
        String result = "";
        for (int i = 0; i < a.length; i++) {
            result += a[i].toString();
        }
        return result;
    }

    /**
     * This function is for testing purposes only
     * @param pointsArray
     * @param left
     * @param right
     * @return
     */
    boolean subArrayEmpty(NDPoint280[] pointsArray, int left, int right){
        if(right<left) return true;
        if(left>right) return true;
        for (int i = left; i < right; i++) {
            if (pointsArray[i]==null) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        KDTree280 testTree = new KDTree280();
        Random r = new Random();
        NDPoint280 n1 = new NDPoint280(new double[]{1.0,7.0});
        NDPoint280 n2 = new NDPoint280(new double[]{2.0,6.0});
        NDPoint280 n3 = new NDPoint280(new double[]{3.0,5.0});
        NDPoint280 n4 = new NDPoint280(new double[]{4.0,4.0});
        NDPoint280 n5 = new NDPoint280(new double[]{5.0,3.0});
        NDPoint280 n6 = new NDPoint280(new double[]{6.0,2.0});
        NDPoint280 n7 = new NDPoint280(new double[]{7.0,1.0});

        // Test Partition()
        // Test Single node list, should get no change
        NDPoint280[] testNodes = new NDPoint280[]{n2};
        testTree.partition(testNodes,0,0,0);
        if (!testNodes[0].toString().equals("(2.0, 6.0)")) System.out.print("Error, single item list should not change");

        // Swap first dimension of two item list
        testNodes = new NDPoint280[]{n2,n1};
        testTree.partition(testNodes,0,1,0);
        if (!testNodes[0].toString().equals("(1.0, 7.0)")) System.out.print("Error, expected a swap here");
        // try again, expect no change
        testTree.partition(testNodes,0,1,0);
        if (!testNodes[0].toString().equals("(1.0, 7.0)")) System.out.print("Error, expected no change to first item");
        // try with 2nd dimension. should swap to: n2,n1
        testTree.partition(testNodes,0,1,1);
        if (!testNodes[0].toString().equals("(2.0, 6.0)")) System.out.print("Error, expected a swap to : n2,n1");

        // test on larger list
        testNodes = new NDPoint280[]{n7,n6,n5,n3,n2,n1};
        testTree.partition(testNodes,0,1,1); // Should get no swaps
        if (!testTree.compareListsOfNodes(testNodes,new NDPoint280[]{n7,n6,n5,n3,n2,n1})) System.out.println("Expected same old lists");
        testTree.partition(testNodes,0,3,0); // Should get partial swaps
        if (!testTree.compareListsOfNodes(testNodes,new NDPoint280[]{n3,n6,n5,n7,n2,n1})) System.out.println("Partial swap error. Got:" + testTree.nodesListToString(testNodes) );
        //full range test
        testTree.partition(testNodes,0,5,0); // Full range partition, should not disturb order other than swapping back and front item since back item is smallest
        if (!testTree.compareListsOfNodes(testNodes,new NDPoint280[]{n1,n6,n5,n7,n2,n3})) System.out.println("Full range swap error 0. Got:" + testTree.nodesListToString(testNodes) );
        testTree.partition(testNodes,0,5,0); // Full range partition, should not disturb order other than swapping back and front item since back item is smallest
        if (!testTree.compareListsOfNodes(testNodes,new NDPoint280[]{n1,n2,n3,n7,n6,n5})) System.out.println("Full range swap error 1. Got:" + testTree.nodesListToString(testNodes) );
        testTree.partition(testNodes,0,5,0); // Full range partition, should not disturb order other than swapping back and front item since back item is smallest
        if (!testTree.compareListsOfNodes(testNodes,new NDPoint280[]{n1,n2,n3,n5,n6,n7})) System.out.println("Full range swap error 2. Got:" + testTree.nodesListToString(testNodes) );
        // Should be fully sorted to first dimension smallest to largest
        //Partition second dimension
        testNodes = new NDPoint280[]{n5,n6,n7,n1,n2,n3};
        testTree.partition(testNodes,0,5,1); // Full range partition, should not disturb order other than swapping back and front item since back item is smallest
        if (!testTree.compareListsOfNodes(testNodes,new NDPoint280[]{n5,n6,n7,n3,n2,n1})) System.out.println("Second dimension Full range swap error 2. Got:" + testTree.nodesListToString(testNodes) );


        // Test jSmallest
        // Test on small list
        testNodes = new NDPoint280[]{n5,n1};
        testTree.jSmallest(testNodes,0,1,0,0);
        if (!testTree.compareListsOfNodes(testNodes,new NDPoint280[]{n1,n5})) System.out.println("jSmallest 2 item list Error 1. Got:" + testTree.nodesListToString(testNodes) );
        testTree.jSmallest(testNodes,0,1,0,1);
        if (!testTree.compareListsOfNodes(testNodes,new NDPoint280[]{n5,n1})) System.out.println("jSmallest 2 item list Error 2. Got:" + testTree.nodesListToString(testNodes) );
        testTree.jSmallest(testNodes,0,1,1,0);
        if (!testTree.compareListsOfNodes(testNodes,new NDPoint280[]{n1,n5})) System.out.println("jSmallest 2 item list Error 2. Got:" + testTree.nodesListToString(testNodes) );

        // Test on large list full range
        testNodes = new NDPoint280[]{n3,n5,n7,n4,n6,n2,n1};
        testTree.jSmallest(testNodes,0,6,4,0);
        if (testNodes[4] != n5) System.out.println("jSmallest large list Error 1. Got:" + testNodes[4] );
        // Test on large list part range
        testNodes = new NDPoint280[]{n3,n5,n7,n4,n6,n2,n1};
        testTree.jSmallest(testNodes,1,4,3,0);
        if (testNodes[3] != n6) System.out.println("jSmallest large list Error 1. Got:" + testNodes[3] );

        // Testing kdTree builder function
        KDTree280 kdt = new KDTree280();
        if (!kdt.isEmpty()) System.out.println("kD tree should be empty");
        //Build a tree from points
        n1 = new NDPoint280(new double[]{5.0,2.0});
        n2 = new NDPoint280(new double[]{9.0,10.0});
        n3 = new NDPoint280(new double[]{11.0,1.0});
        n4 = new NDPoint280(new double[]{4.0,3.0});
        n5 = new NDPoint280(new double[]{2.0,12.0});
        n6 = new NDPoint280(new double[]{3.0,7.0});
        n7 = new NDPoint280(new double[]{1.0,5.0});
        // Same as example given in specifications
        testNodes = new NDPoint280[]{n1,n2,n3,n4,n5,n6,n7};
        System.out.println("Input Points:");
        for (int i = 0; i < testNodes.length; i++) {
            System.out.println(testNodes[i].toString());
        }


        kdt = new KDTree280(testNodes,2);
        if (kdt.isEmpty()) System.out.println("kDTree should not be empty");
        if (kdt.rootItem()!=n4) System.out.println("Incorrect root node, should be n4, got: " + kdt.rootItem().toString() );
        if (kdt.rootLeftSubtree().rootItem()!=n6) System.out.println("Incorrect root node, should be n6, got: " + kdt.rootLeftSubtree().rootItem() );
        String expected ="\n" +
                "          3: (9.0, 10.0)\n" +
                "     2: (5.0, 2.0)\n" +
                "          3: (11.0, 1.0)\n" +
                "1: (4.0, 3.0)\n" +
                "          3: (2.0, 12.0)\n" +
                "     2: (3.0, 7.0)\n" +
                "          3: (1.0, 5.0)";

        if (!kdt.toStringByLevel().equals(expected)) System.out.println("Expected:\n" +expected+" \n But got:\n" + kdt.toStringByLevel());
        System.out.println("The tree built from this is: ");
        System.out.println(kdt.toStringByLevel());

        n1 = new NDPoint280(new double[]{1.0,12.0,0.0});
        n2 = new NDPoint280(new double[]{18.0,1.0,2.0});
        n3 = new NDPoint280(new double[]{2.0,13.0,16.0});
        n4 = new NDPoint280(new double[]{7.0,3.0,3.0});
        n5 = new NDPoint280(new double[]{3.0,7.0,5.0});
        n6 = new NDPoint280(new double[]{16.0,4.0,4.0});
        n7 = new NDPoint280(new double[]{4.0,6.0,1.0});
        NDPoint280 n8 = new NDPoint280(new double[]{5.0,5.0,17.0});
        testNodes = new NDPoint280[]{n1,n2,n3,n4,n5,n6,n7,n8};
        System.out.println("Input 3D points");
        for (int i = 0; i < testNodes.length; i++) {
            System.out.println(testNodes[i].toString());
        }
        kdt = new KDTree280(testNodes,3);
        System.out.println(kdt.toStringByLevel());

        System.out.println("Looking for points between (0.0,1.0.0,0.0) and (4.0, 6.0, 3.0).\nFound:");
        for (NDPoint280 ndPoint280 : kdt.searchRange(new NDPoint280(new double[]{0.0, 1.0, 0.0}),
                new NDPoint280(new double[]{4.0, 6.0, 3.0}), 0)) {
            System.out.println(ndPoint280.toString());
        }

        System.out.println("Looking for points between (0.0,1.0.0,0.0) and (8.0, 7.0, 4.0).\nFound:");
        for (NDPoint280 ndPoint280 : kdt.searchRange(new NDPoint280(new double[]{0.0, 1.0, 0.0}),
                new NDPoint280(new double[]{8.0, 7.0, 4.0}), 0)) {
            System.out.println(ndPoint280.toString());
        }

        System.out.println("Looking for points between (0.0,1.0.0,0.0) and (17.0, 9.0, 10.0).\nFound:");
        for (NDPoint280 ndPoint280 : kdt.searchRange(new NDPoint280(new double[]{0.0, 1.0, 0.0}),
                new NDPoint280(new double[]{17.0, 9.0, 10.0}), 0)) {
            System.out.println(ndPoint280.toString());
        }

        System.out.println("Looking for points between (100.0,100.0,100.0) and (101.0,101.0,101.0).\nFound (Should be none):");
        for (NDPoint280 ndPoint280 : kdt.searchRange(new NDPoint280(new double[]{100.0,100.0,100.0}),
                new NDPoint280(new double[]{101.0,101.0,101.0}), 0)) {
            System.out.println(ndPoint280.toString());
        }
        System.out.println("Looking for points between (0,0,100.0) and (101.0,101.0,101.0).\nFound (Should be none):");
        for (NDPoint280 ndPoint280 : kdt.searchRange(new NDPoint280(new double[]{0.0,0.0,100.0}),
                new NDPoint280(new double[]{101.0,101.0,101.0}), 0)) {
            System.out.println(ndPoint280.toString());
        }
    }
}
