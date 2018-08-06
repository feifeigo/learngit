import java.util.LinkedList;


public class AttributeInfo {
    public String name;
    public float coefficent;
    public float perCoefficent;
    public int num_member; //
    public int num_perturb;
    public int link_member;
    public int perturb_link_menber;
    public LinkedList<String> perturb_Member;
    public LinkedList<String> member; //qun

    public AttributeInfo(String name) {
        this.name = name;
        member = new LinkedList<String>();
        perturb_Member = new LinkedList<String>();
    }

    public void addMember(String value) {
        member.add(value);
    }

    public void addPerMember(String value) {
        perturb_Member.add(value);
    }

    public void removeAllMember() {
        member.retainAll(member);
    }

    public void setNum_member() {
        this.num_member = member.size();
    }

    public void setNum_perturbmember() {
        this.num_perturb = perturb_Member.size();
    }

    public void setLink_member(int value) {
        this.link_member = value;
    }

    public void setPerLink_member(int value) {
        this.perturb_link_menber = value;
    }

    public int getNum_member() {
        return member.size();
    }

    public void setCoefficent(int value) {
        if (getNum_member() > 1)
            this.coefficent = value / ((float) (getNum_member() - 1) * getNum_member());
        else
            this.coefficent = 0;

    }

    public void setPerCoefficent(int value) {
        int size = this.perturb_Member.size();
        //	System.out.print("拥有该属性的节点数量"+size);
        if (size > 1) {
            this.perCoefficent = value / ((float) (size - 1) * size);
        } else
            this.perCoefficent = 0;
        System.out.println("perCoefficent" + perCoefficent);

    }

    public String toString() {
        String info = name + "聚集系数" + coefficent;
        return info;

    }


}
