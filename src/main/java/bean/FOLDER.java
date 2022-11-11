package bean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author 齐鲁工业大学_数据科学20-2_夏增荃
 * @version 1.0
 * @date 2022/11/9 13:04
 */
public class FOLDER extends Node{
    private Map<String,Node> nexts=new HashMap<>();
    public FOLDER(USER user,String name,String parentPath){
        super(user,name,parentPath);
    }

    public Map<String, Node> getNexts() {
        return nexts;
    }

    public void setNexts(Map<String, Node> nexts) {
        this.nexts = nexts;
    }

}
