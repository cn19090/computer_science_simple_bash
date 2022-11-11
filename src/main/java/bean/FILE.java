package bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 齐鲁工业大学_数据科学20-2_夏增荃
 * @version 1.0
 * @date 2022/11/9 9:31
 */
public class FILE extends Node{
    private byte[] bytes;
    private int length;
    public byte[] read(USER user) {
        if(canBeRead(user)){
            return this.bytes;
        }
        return null;
    }
    public boolean canBeWrite(USER user){
        return ((permisions.get(user.getUser_id())+(all_user_permision<<3))&0b00100100)>0?true:false;
    }

    public boolean canBeRead(USER user){
        return ((permisions.get(user.getUser_id())+(all_user_permision<<3))&0b00010010)>0?true:false;
    }

    public void write(USER user,byte[] bytes,short contro) {
        if(canBeWrite(user)){
            if(contro==0){
                ByteArrayOutputStream out=new ByteArrayOutputStream();
                try {
                    out.write(this.bytes);
                    out.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.bytes=out.toByteArray();
            }else{
                this.bytes = bytes;
            }
            this.length=this.bytes.length;
        }else{
            
        }


    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public FILE(USER user,String name,String parentPath){
        super(user,name,parentPath);
        user.setUserFileCount(user.getUserFileCount()+1);

    }
}
