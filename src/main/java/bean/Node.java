package bean;

import ERR.ParamWrong;
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
    protected Map<Integer,Short> permisions=new HashMap<>();//00000000->00|000|000|write,read,delete

    //=============================================================================

    public String getPermision(USER user){
        String res="";
        short ii= 0;
        try {
            ii = permisions.get(user.getUser_id());
        } catch (Exception e) {
            permisions.put(user.getUser_id(),(short)0b00000000);
        }
        for (int i = 0; i < 8; i++) {
            res=((ii&(1<<i))==0?0:1)+res;
        }
        return res;
    }


    public boolean canBeDel(USER user){
        return (permisions.get(user.getUser_id())&0b00001001)>0?true:false;
    }

    public String getParentPath(){
        return path.substring(0,path.lastIndexOf("/"));
    }



    public void chmod(USER user,Short per){
        if(permisions.containsKey(user.getUser_id())){
            permisions.put(user.getUser_id(), (short) (permisions.get(user.getUser_id())|per));
        }else{

        }

    }

    public String getPath() {
        return path;
    }

    public void chmod(USER user, String per)throws ParamWrong{
        if(permisions.containsKey(user.getUser_id())){
            short p=permisions.get(user.getUser_id());
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
                p|=(1<<5);
                if(method==0){
                    p-=(1<<(3+pro));
                }
            }else if(u==1){
                p|=((1<<(3+pro))+(1<<pro));
                if(method==0){
                    p-=((1<<(3+pro))+(1<<pro));
                }

            }
            permisions.put(user.getUser_id(), p);
        }else{

        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Node(USER user,String name,String parentPath){
        permisions.put(user.getUser_id(), (short) 0b00111111);
        this.path=parentPath+"/"+name;
        this.name=name;
    }
}
