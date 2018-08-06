import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Pre_Process_test {
    private static final String DATASET_DIR = "D:/IDEA/DP-FTexperiment/dataSets/";
    private static  final String RESULT_DIR="D:/synthesisGraph";
    public static void main(String[] args) throws IOException {
        SimCompute sc=new SimCompute();
        boolean directer,withAttr,privately;
        System.out.print("输入数据集名称：");
        Scanner scan = new Scanner(System.in);
        String dataSet = scan.nextLine();
        File DATASET_PATH=new File(DATASET_DIR+dataSet);
        File RESULT_PATH=new File(RESULT_DIR+"\\/"+dataSet);

        System.out.println("原始数据集路径："+DATASET_PATH);
        System.out.print("是否进行差分隐私扰动？请输入boolean值(true/false)：");
        privately=scan.nextBoolean()?true:false;
        System.out.print("是否为有向图？请输入boolean值(true/false)：");
        directer=scan.nextBoolean()?true:false;
        File output_dir;
        double epsilon1=0;
        int nattribute=0;
        if (privately){
            System.out.print("请输入社交关系隐私预算：");
            epsilon1=scan.nextDouble();
            double epsilon2=0.0;
            withAttr=sc.preProcess(DATASET_PATH,privately,directer,epsilon1);     //初始化过程，包含差分隐私扰动
            if(withAttr){
                System.out.print("请输入属性个数：");
                nattribute=scan.nextInt();
                System.out.print("请输入属性隐私预算：");
                epsilon2=Double.valueOf(scan.nextLine());
                output_dir=new File(RESULT_PATH,"nattributes_"+ nattribute+"/FT_attr"+String.valueOf(epsilon2));
                sc.generateAttribute(output_dir,dataSet,epsilon2);		//属性
            }
        }
        else
            withAttr=sc.preProcess(DATASET_PATH,privately,directer,epsilon1);     //初始化过程，不包含差分隐私扰动
        output_dir=new File(RESULT_PATH,"nattributes_"+ nattribute+"/FT_"+String.valueOf(epsilon1));
        if(directer){
            sc.generateDLink(output_dir,dataSet);               //有向图
        }
        else
            sc.generateUDLink(output_dir,dataSet);
        if(withAttr){
            sc.computeErrorProcess();
        }



//        SimCompute sc=new SimCompute();
//        boolean directer,withAttr,privately;
////        System.out.print("输入数据集名称：");
//        Scanner scan = new Scanner(System.in);
////        String dataSet = scan.nextLine();
//        String dataSet="health";
//        File DATASET_PATH=new File(DATASET_DIR+dataSet);
//        File RESULT_PATH=new File(RESULT_DIR+"\\/"+dataSet);
//
////        System.out.println("原始数据集路径："+DATASET_PATH);
////        System.out.print("是否进行差分隐私扰动？请输入boolean值(true/false)：");
////        privately=scan.nextBoolean()?true:false;
//        privately=false;
////        System.out.print("是否为有向图？请输入boolean值(true/false)：");
////        directer=scan.nextBoolean()?true:false;
//        directer=true;
//        File output_dir;
//        double epsilon1=0;
//        int nattribute=0;
//        if (privately){
////            System.out.print("请输入社交关系隐私预算：");
////            epsilon1=scan.nextDouble();
//            epsilon1=1;
//            double epsilon2=0.0;
//            withAttr=sc.preProcess(DATASET_PATH,privately,directer,epsilon1);     //初始化过程，包含差分隐私扰动
//            if(withAttr){
//                System.out.print("请输入属性个数：");
//                nattribute=scan.nextInt();
//                System.out.print("请输入属性隐私预算：");
//                epsilon2=Double.valueOf(scan.nextLine());
//                output_dir=new File(RESULT_PATH,"nattributes_"+ nattribute+"/FT_attr"+String.valueOf(epsilon2));
//                sc.generateAttribute(output_dir,dataSet,epsilon2);		//属性
//            }
//        }
//        else
//            withAttr=sc.preProcess(DATASET_PATH,privately,directer,epsilon1);     //初始化过程，不包含差分隐私扰动
//        output_dir=new File(RESULT_PATH,"nattributes_"+ nattribute+"/FT_"+String.valueOf(epsilon1));
//        if(directer){
//            sc.generateDLink(output_dir,dataSet);               //有向图
//        }
//        else
//            sc.generateUDLink(output_dir,dataSet);
//        if(withAttr){
//            sc.computeErrorProcess();
//        }



//        //mat
//        float[][] a1  = {{2.3f,2,1,3},{2,1,1,3},{2,0.5f,1,3},{2,0.5f,1,3}};
//        float[][] a2 = {{2.3f,2,1,3},{2,1,1,3},{2,0.5f,1,3},{2,0.5f,1,3}};
//        UserList u=new UserList();
//        float[] [] re = u.mmltiple(a1,a2);
//
//        for(int i = 0;i<a1.length;i++) {
//            for (int j = 0; j < a2.length; j++)
//                System.out.println(re[i][j] + " ");
//            System.out.println("\n");
//        }



//        AttributeInfo ul=new AttributeInfo("123");
//        ul.addMember("5");
//        ul.addMember("6");
//        System.out.print(ul.getNum_member());
//
//        AttributeInfo ul1=new AttributeInfo("12");
//        ul1.addMember("15");
//        ul1.addMember("62");
//        ul1.addMember("16");
//        System.out.print(ul1.getNum_member());
//
//        AttributeInfo ul2=new AttributeInfo("152");
//        ul2.addMember("15");
//
//        System.out.print(ul2.getNum_member());
//
//
//
//        AttributeList al=new AttributeList();
//        al.addGroup("1",ul);
//        al.addGroup("2", ul1);
//        al.addGroup("3", ul2);
//
//
//        System.out.print(al.AllAttribute());
    }

}
