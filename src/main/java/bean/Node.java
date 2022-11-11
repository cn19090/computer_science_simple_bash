package bean;

import ERR.ParamWrong;
import ERR.PermisionDenied;
import ERR.UserNotFound;
import Go.Main;

import java.util.*;

/**
 * @author 齐鲁工业大学_数据科学20-2_夏增荃
 * @version 1.0
 * @date 2022/11/9 13:05
 */
public class Node {
    protected String name;
    protected String path;
    protected int owner_id;
    protected short all_user_permision=0b000; //000|write,read,delete
    protected Map<Integer,Short> permisions=new HashMap<>();//权限设置 00000000->00|000|000|write,read,delete

    //=============================================================================

    public String getPermision(USER user){
        String res="";
        short ii= 0;
        try {
            ii = (short)(permisions.get(user.getUser_id())+(all_user_permision<<3));
        } catch (Exception e) {
            permisions.put(user.getUser_id(),(short)0b00000000);
        }
        for (int i = 0; i < 8; i++) {
            res=((ii&(1<<i))==0?0:1)+res;
        }
        return res;
    }


    public boolean canBeDel(USER user) {
        if ((all_user_permision & 0b001) > 0) {
            return true;
        }
        if (permisions.containsKey(user.getUser_id())) {
            if (permisions.get(user.getUser_id() & 0b001) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public String getParentPath(){
        return path.substring(0,path.lastIndexOf("/"));
    }



    public void chmod(String username,Short per)throws UserNotFound,PermisionDenied {
        if(Main.now_user.getUser_id()!=this.owner_id){
            throw new PermisionDenied();
        }
        USER user=USER.getUser(username);
        all_user_permision=(short) ((per&0b00111000)>>3);
        short user_permis=(short)((per&0b00000111));
        if(permisions.containsKey(user.getUser_id())){
            permisions.put(user.getUser_id(), user_permis);
        }else{

        }

    }

    public String getPath() {
        return path;
    }

    public void chmod(String username, String per) throws ParamWrong, UserNotFound,PermisionDenied {
        if(Main.now_user.getUser_id()!=this.owner_id){
            throw new PermisionDenied();
        }
        USER user=USER.getUser(username);
        short p=0;
        if(permisions.containsKey(user.getUser_id())){
            p=(short)(permisions.get(user.getUser_id())+(all_user_permision<<3));
        }else{

        }
            int u=-1,method=-1,pro=-1;
            switch (per.charAt(0)){
                case 'a':
                    u=1;
                    break;
                case 'p':
                    u=2;
                    break;
            }
            switch (per.charAt(1)){
                case '+':
                    method=1;
                    break;
                case '-':
                    method=0;
                    break;
            }
            switch (per.charAt(2)){
                case 'w':
                    pro=2;
                    break;
                case 'r':
                    pro=1;
                    break;
                case 'd':
                    pro=0;
                    break;
            }
            if(u<0||method<0||pro<0){
                throw new ParamWrong();
            }
            if(u==2){
                p|=(1<<(pro));
                if(method==0){
                    p-=(1<<(pro));
                }
                permisions.put(user.getUser_id(), (short)((p&0b00000111)));
            }else if(u==1){
                p|=((1<<(3+pro)));
                if(method==0){
                    p-=((1<<(3+pro)));
                }
                all_user_permision=(short)((p&0b00111000)>>3);
            }



    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node(USER user,String name,String parentPath){
        permisions.put(user.getUser_id(), (short) 0b00000111);
        this.owner_id=Main.now_user.getUser_id();
        this.path=parentPath+"/"+name;
        this.name=name;
    }
}
