import java.io.*;
import java.util.List;

//import org.slf4j.Logger;

public class FileUtil {
//    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if(file.exists()) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件已存在,准备删除重建！");
            file.delete();
            file = new File(destFileName);

        }
        if (destFileName.endsWith(File.separator)) {
            System.out.println("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在
        if(!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if(!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                System.out.println("创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                System.out.println("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }

    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            System.out.println("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        // 创建目录
        if (dir.mkdirs()) {
            System.out.println("创建目录" + destDirName + "成功！");
            return true;
        } else {
            System.out.println("创建目录" + destDirName + "失败！");
            return false;
        }
    }

    public static void writeTextFile(String fileName, List<String> set)
            throws IOException {
        createFile(fileName);
//        logger.debug("Writing text file " + fileName + "...");
        PrintWriter writer = null;
        System.out.println("开始创建" + fileName);
        try {
            writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(fileName)));

            for (int i = 0; i < set.size(); i++) {
                String data = set.get(i);
                writer.print(data);
                writer.println();
            }
            System.out.println("创建完毕" + fileName);
        } finally {
            if (writer != null) {
                writer.close();

            }
        }
    }
}
