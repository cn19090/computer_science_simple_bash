package bean;

import ERR.*;
import Go.Main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author 齐鲁工业大学_数据科学20-2_夏增荃
 * @version 1.0
 * @date 2022/11/9 9:34
 */
public class USER {
    public static  Map<Integer, List<FILE>> AFD=new HashMap<>();//当前每个用户打开的文件列表
    private int userFileCount=0;
    private static Map<Integer,USER> userlist=new HashMap<>();
    private int user_id;
    private String username;
    private FOLDER user_root;
    private Map<String,FILE> userfiles=new HashMap<>();

    public int getUserFileCount() {
        return userFileCount;
    }

    public void openFile(FILE file)throws UserOpenFileLimit,UserHasOpenedThisFile{
        if(AFD.containsKey(this.getUser_id())){

        }else{
            AFD.put(this.getUser_id(),new LinkedList<FILE>());
        }
        List<FILE> files = AFD.get(this.getUser_id());
        if(files.contains(files)){
            throw new UserHasOpenedThisFile();
        }else{
            if(files.size()>=Main.k){
                throw new UserOpenFileLimit();
            }else{
                files.add(file);
            }
        }

    }

    public void closeFile(FILE file){
        List<FILE> files = AFD.get(this.getUser_id());
        if(files.contains(file)){
            files.remove(file);
        }else{
            System.out.println("user havent opened this file");
        }

    }

    public void setUserFileCount(int userFileCount) {
        this.userFileCount = userFileCount;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, FILE> getUserfiles() {
        return userfiles;
    }

    public void setUserfiles(Map<String, FILE> userfiles) {
        this.userfiles = userfiles;
    }

    public static USER getUser(String name)throws UserNotFound{
        for (Integer integer : userlist.keySet()) {
            if(userlist.get(integer).getUsername().equals(name)){
                return userlist.get(integer);
            }
        }
        throw new UserNotFound();
    }

    public USER(String name)throws UsernameHasBeenUsed, UserListFull {
        if(userlist.size()>= Main.m){
            throw new UserListFull();
        }
        this.user_id=(int)(Math.random()*1000000);
        while(userlist.containsKey(user_id)){
            this.user_id=(int)(Math.random()*1000000);
        }
        for (Integer integer : userlist.keySet()) {
            if(userlist.get(integer).getUsername().equals(name)){
                throw new UsernameHasBeenUsed();
            }
        }
        this.username=name;
        userlist.put(this.user_id,this);
    }

    public FOLDER getUser_root() {
        return user_root;
    }

    public void setUser_root(FOLDER user_root) {
        this.user_root = user_root;
    }

    public USER(){
        this.user_id=1;
        this.username="root";
        userlist.put(1,this);

    }
}
