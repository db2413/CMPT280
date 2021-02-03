import lib280.base.Container280;
import lib280.exception.ContainerEmpty280Exception;
import lib280.tree.SimpleTree280;

public class LinkedSimpleTree280<I> implements SimpleTree280<I> {

    BinaryNode280<I> rootNode;
    LinkedSimpleTree280<I> rootLeftTree;
    LinkedSimpleTree280<I> rootRightTree;

    /* Container interface */
    @Override
    public boolean isEmpty() {
        return rootNode == null;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public void clear() {
        rootNode = null;
    }

    /* Simple tree interface */
    @Override
    public I rootItem() throws ContainerEmpty280Exception {
        if (isEmpty()) throw new ContainerEmpty280Exception("Error: Empty container");
        return rootNode.item();
    }

    @Override
    public SimpleTree280<I> rootRightSubtree() throws ContainerEmpty280Exception {
        if (isEmpty()) throw new ContainerEmpty280Exception("Error: Empty container");
        return rootRightTree;
    }

    @Override
    public SimpleTree280<I> rootLeftSubtree() throws ContainerEmpty280Exception {
        if (isEmpty()) throw new ContainerEmpty280Exception("Error: Empty container");
        return rootLeftTree;
    }

    /* Class methods */
    public LinkedSimpleTree280(){
        this.rootNode = null;
    }

    public LinkedSimpleTree280(LinkedSimpleTree280<I> leftTree, I rootItem, LinkedSimpleTree280<I> rightTree) {
        rootNode = new BinaryNode280<I>(rootItem);
        rootLeftTree = leftTree;
        rootRightTree = rightTree;
    }

    public void setRootLeftTree(LinkedSimpleTree280<I> rootLeftTree) throws ContainerEmpty280Exception {
        if (isEmpty()) throw new ContainerEmpty280Exception("Error: Empty container");
        this.rootLeftTree = rootLeftTree;
    }

    public void setRootRightTree(LinkedSimpleTree280<I> rootRightTree) throws ContainerEmpty280Exception{
        if (isEmpty()) throw new ContainerEmpty280Exception("Error: Empty container");
        this.rootRightTree = rootRightTree;
    }
}
