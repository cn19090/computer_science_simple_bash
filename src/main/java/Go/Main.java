package Go;

import ERR.*;
import bean.FILE;
import bean.FOLDER;
import bean.Node;
import bean.USER;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Set;

/**
 * @author 齐鲁工业大学_数据科学20-2_夏增荃
 * @version 1.0
 * @date 2022/11/9 9:30
 */
public class Main {
    public static int s,m,k;
    public static USER now_user=new USER();

    public final static FOLDER root=new FOLDER(now_user,"","");
    public final static FOLDER user_root=new FOLDER(now_user,"root","");
    static {
        now_user.setUser_root(user_root);
        root.getNexts().put("root",user_root);
    }
    public static FOLDER now=user_root;
    public static String[] code=new String[]{
            "mkdir",//0
            "cd",//1
            "ls",//2
            "chmod",//3
            "rm",//4
            "cat",//5
            "echo",//6
            "login",//7
            "lgout",//8
            "register",//9
            "open",//10
            "close",//11
            "touch"
    };

    public static Scanner scanner=new Scanner(System.in);

    public static Node getNode(String path) throws PathNotFound {
        if(path.equals("")){
            return root;
        }
        Node n;
        if(path.charAt(0)=='/'){
            n=root;
        }else{
            //if(path.charAt(0)=='.'){
            //    path=path.substring(1);
            //}
            n=now;
        }

        String[] pa=path.split("/");
        for (int i = 0; i < pa.length; i++) {
           if(pa[i].equals("")){
               continue;
           }else{
               if(pa[i].equals("..")){
                   try {
                       n=(FOLDER) getNode(n.getPath().substring(0,n.getPath().lastIndexOf("/")));
                   } catch (PathNotFound e) {
                       e.printStackTrace();
                   }
               }else{
                   if(((FOLDER)n).getNexts().containsKey(pa[i])){
                       if(((FOLDER)n).getNexts().get(pa[i]) instanceof FOLDER){
                           n=((FOLDER)n).getNexts().get(pa[i]);
                       }else{
                           if(i==pa.length-1){
                               n=((FOLDER)n).getNexts().get(pa[i]);
                               break;
                           }
                           throw new PathNotFound();
                       }
                   }else{
                       throw new PathNotFound();
                   }
               }
           }
        }
        return n;
    }

    public static void mkdir(String path){
        FOLDER n;
        if(path.charAt(0)=='/'){
            n=user_root;
        }else{
            if(path.charAt(0)=='.'){
                path=path.substring(1);
            }
            n=now;
        }
        String[] pa=path.split("/");
        for (int i = 0; i < pa.length; i++) {
           if(pa[i].equals("")){
               continue;
           }else{
               if(n.getNexts().containsKey(pa[i])){
                   if(n.getNexts().get(pa[i]) instanceof FOLDER){
                       n=(FOLDER) n.getNexts().get(pa[i]);
                   }else{
                       FOLDER folder=new FOLDER(now_user,pa[i],n.getPath());
                       n.getNexts().put(pa[i],folder);
                       n=folder;
                   }
               }else{
                   FOLDER folder=new FOLDER(now_user,pa[i],n.getPath());
                   n.getNexts().put(pa[i],folder);
                   n=folder;
               }
           }
        }

    }

    public static void ls(String path)throws PathNotFound{

            Node n=getNode(path);
            if(n instanceof FOLDER){
                Set<String> strings = ((FOLDER) n).getNexts().keySet();
                for (String string : strings) {
                    System.out.print(((FOLDER) n).getNexts().get(string).getPermision(now_user)+"\t"+ string+"\t");
                    System.out.println();
                }

            }

    }

    public static void cd(String path)throws PathNotFound{

            Node node=getNode(path);
            if(node instanceof FOLDER){
                now=(FOLDER) node;
            }else{
                throw new PathNotFound();
            }

    }

    public static void rm(String path)throws PathNotFound{

            Node n=getNode(path);
            if(n.canBeDel(now_user)){
                FOLDER p=(FOLDER) getNode(n.getParentPath());
                p.getNexts().remove(n.getName());
            }else{
                System.out.println("permision denied");
            }


    }

    public static void cat(String path)throws PathNotFound{
            Node node=getNode(path);
            if(node instanceof FILE){
                if(((FILE)node).canBeRead(now_user)){
                    System.out.println(new String(((FILE)node).read(now_user)));
                }else{
                    System.out.println("permision denied");
                }
            }else{
                System.out.println("path is not a file");
            }
    }

    public static void touch(String path)throws UserFileCountLimit{
        if(now_user.getUserFileCount()>= s){
            throw new UserFileCountLimit();
        }
        FOLDER n;
        if(path.charAt(0)=='/'){
            n=user_root;
        }else{
            if(path.charAt(0)=='.'){
                path=path.substring(1);
            }
            n=now;
        }
        String[] pa=path.split("/");
        for (int i = 0; i < pa.length; i++) {
            if(pa[i].equals("")){
                continue;
            }else{
                if(n.getNexts().containsKey(pa[i])){
                    if(n.getNexts().get(pa[i]) instanceof FOLDER){
                        n=(FOLDER) n.getNexts().get(pa[i]);
                    }else{
                        break;
                    }
                }else{
                    if(i==pa.length-1){
                        n.getNexts().put(pa[i],new FILE(now_user,pa[i],n.getPath()));
                    }else{
                        FOLDER folder=new FOLDER(now_user,pa[i],n.getPath());
                        n.getNexts().put(pa[i],folder);
                        n=folder;
                    }

                }
            }
        }



    }

    public static void register(String name)throws UsernameHasBeenUsed,UserListFull{
            USER user=new USER(name);
            user.setUser_root(new FOLDER(user,name,""));
            root.getNexts().put(name,user.getUser_root());
            System.out.println("user register completed!");

    }

    public static void login(String name)throws UserNotFound{
            USER user = USER.getUser(name);
            now_user=user;
            now=user.getUser_root();
    }
    
    public static void echo(String path,String str,short contro){
        try {
            touch(path);
            Node node=getNode(path);

            if(node instanceof FILE){
                FILE n=(FILE) node;
                if(n.canBeWrite(now_user)){
                    n.write(now_user,str.getBytes(StandardCharsets.UTF_8),contro);
                }else{
                    System.out.println("permision denied");
                }

            }else{
                System.out.println("path is not a file");
            }
        } catch (PathNotFound|UserFileCountLimit e) {
            e.printStackTrace();
        }

    }

    public static void open(String path) throws PathNotFound,UserOpenFileLimit,UserHasOpenedThisFile {
        Node o=getNode(path);
        if(o instanceof FILE){
            now_user.openFile((FILE)o);
        }else{
            System.out.println("path is not a file");
        }
    }

    public static void close(String path)throws PathNotFound{
        Node o=getNode(path);
        if(o instanceof FILE){
            now_user.closeFile((FILE)o);
        }else{
            System.out.println("path is not a file");

        }

    }

    public static void mainC(){
        while(true){

            String s[]=scanner.nextLine().split(" ");
            try {
                if(s.length==4){
                    int cho=-1;
                    for (int i = 0; i < code.length; i++) {
                        if(s[0].equals(code[i])){
                            cho=i;
                            break;
                        }
                    }
                    switch (cho){
                        case 3:
                            Node n=getNode(s[3]);
                            if(s[2].matches("[\\d]+")){
                                n.chmod(s[1],Short.parseShort(s[2]));
                            }else{
                                if(s[2].charAt(0)=='a'){
                                    throw new ParamWrong();
                                }
                                n.chmod(s[1],s[2]);
                            }

                            break;
                        case 6:
                            echo(s[3],s[1],(short) (s[2].equals(">>")?0:1));
                            break;
                        default:
                            System.out.println("command not found");
                    }
                }
                else if(s.length==3){
                    int cho=-1;
                    for (int i = 0; i < code.length; i++) {
                        if(s[0].equals(code[i])){
                            cho=i;
                            break;
                        }
                    }

                    switch (cho){
                        case 3:
                            try {
                                Node n=getNode(s[2]);
                                if(s[1].matches("[\\d]+")){
                                    n.chmod(now_user.getUsername(),Short.parseShort(s[1]));
                                }else{
                                    try {
                                        n.chmod(now_user.getUsername(),s[1]);
                                    } catch (ParamWrong e) {
                                        e.printStackTrace();
                                    }


                                }

                            } catch (PathNotFound e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            System.out.println("command not found");
                    }
                }
                else if(s.length==2){
                     int cho=-1;
                    for (int i = 0; i < code.length; i++) {
                        if(s[0].equals(code[i])){
                            cho=i;
                            break;
                        }
                    }

                    switch (cho){
                        case 0:
                            mkdir(s[1]);
                            break;
                        case 1:
                            cd(s[1]);
                            break;
                        case 2:
                            ls(s[1]);
                            break;
                        case 4:
                            rm(s[1]);
                            break;
                        case 5:
                            cat(s[1]);
                            break;
                        case 7:
                            login(s[1]);
                            break;
                        case 9:
                            register(s[1]);
                            break;
                        case 10:
                            open(s[1]);
                            break;
                        case 11:
                            close(s[1]);
                            break;
                        case 12:
                            touch(s[1]);
                            break;
                        default:
                            System.out.println("command not found");
                    }
                }
                else if(s.length==1){
                    int cho=-1;
                    for (int i = 0; i < code.length; i++) {
                        if(s[0].equals(code[i])){
                            cho=i;
                            break;
                        }
                    }
                    switch (cho){
                        case 2:
                            ls(now.getPath());
                            break;
                    }
                }
                else{

                }
            } catch (Exception e) {
              e.printStackTrace();
            }
            finally {
                System.out.println();
                System.out.print(now_user.getUsername()+"@"+now.getName()+">");
            }
        }
    }
    public static void main(String[] args) {
        System.out.println("input user count limit:");
        m= scanner.nextInt();
        System.out.println("input user file count limit:");
        s =scanner.nextInt();
        System.out.println("input user open file count limit:");
        k= scanner.nextInt();
        mainC();
    }
}
