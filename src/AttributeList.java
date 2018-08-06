

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;


public class AttributeList {
//    private static Logger logger = LoggerFactory.getLogger(AttributeList.class);
    private Map<String, AttributeInfo> attributeList;

    public AttributeList() {
        attributeList = new LinkedHashMap<String, AttributeInfo>();
    }

    public int Size() {
        return attributeList.size();
    }

    public LinkedList<String> AllAttribute() {
        Collection<String> keyset = attributeList.keySet();
        LinkedList<String> list = new LinkedList<String>(keyset);
        Collections.sort(list, new SortAttList());
        return list;
    }

    public void setGroupList(Map<String, AttributeInfo> groupList) {
        this.attributeList = groupList;
    }

    public AttributeInfo getGroup(String key) {
        return attributeList.get(key);
    }

    public void addGroup(String key, AttributeInfo value) {
        attributeList.put(key, value);
    }

    public void createAttList(File fileName) throws IOException {
//        logger.debug("Reading text file " + fileName + "...");

        String pltLine;
        String[] elementStrings;
        RandomAccessFile plt = new RandomAccessFile(fileName, "r");
        while ((pltLine = plt.readLine()) != null) {
            elementStrings = pltLine.split(" ");
            addGroup(elementStrings[0], new AttributeInfo(elementStrings[0]));
        }
        try {
            plt.close();
            System.out.println("创建属性列表成功");
        } catch (IOException e) {
            // TODO: handle exception while trying to close a
            // *.plt
            e.printStackTrace();
        }

    }

    public void createMember(File fileName) throws IOException {
//        logger.debug("Reading text file " + fileName + "...");
        String pltLine;
        String[] elementStrings;
        RandomAccessFile plt = new RandomAccessFile(fileName, "r");
        while ((pltLine = plt.readLine()) != null) {
            elementStrings = pltLine.split(" ");
            //	if (attributeList.containsKey(elementStrings[1]))
            getGroup(elementStrings[1]).addMember(elementStrings[0]);


        }
        try {
            plt.close();
            // writer.close();
            System.out.println("创建属性拥有者成功");
            //initAttributeInfo(); // ��ʼ�����Ա�
        } catch (IOException e) {
            // TODO: handle exception while trying to close a
            // *.plt
            e.printStackTrace();
        }
        int ii = 0;
        int length = AllAttribute().size();
        int sum = 0;
        for (String a : AllAttribute()) {
            ii++;
            AttributeInfo a1 = attributeList.get(a);
            System.out.println(a + "num:" + a1.member.size());
            sum += a1.member.size();
            if (ii > Math.sqrt(length / 1009)) {

                System.out.println("geshu" + ii + "zonghe" + sum);
                break;
            }

        }
    }

    class SortAttList implements Comparator<String> {
        public int compare(String o1, String o2) {
            AttributeInfo a1 = attributeList.get(o1);
            AttributeInfo a2 = attributeList.get(o2);
            int num1 = a1.member.size();
            int num2 = a2.member.size();
            if (num1 > num2)
                return -1;
            if (num1 == num2)
                return 0;
            else
                return 1;
        }

    }
}
	
